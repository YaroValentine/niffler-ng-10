package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class SpendDaoJdbc implements SpendDao {

  private static final Config CFG = Config.getInstance();

  @Override
  public SpendEntity create(SpendEntity spend) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
              "VALUES ( ?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
      )) {
        ps.setString(1, spend.getUsername());
        ps.setDate(2, spend.getSpendDate());
        ps.setString(3, spend.getCurrency().name());
        ps.setDouble(4, spend.getAmount());
        ps.setString(5, spend.getDescription());
        ps.setObject(6, spend.getCategory().getId());

        ps.executeUpdate();

        final UUID generatedKey;
        try (ResultSet rs = ps.getGeneratedKeys()) {
          if (rs.next()) {
            generatedKey = rs.getObject("id", UUID.class);
          } else {
            throw new SQLException("Can`t find id in ResultSet");
          }
        }
        spend.setId(generatedKey);
        return spend;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
        "SELECT " +
        "s.id AS spend_id, " +
        "s.username AS spend_username, " +
        "s.spend_date, " +
        "s.currency, " +
        "s.amount, " +
        "s.description, " +
        "s.category_id, " +
        "c.name AS category_name, " +
        "c.username AS category_username, " +
        "c.archived AS category_archived " +
        "FROM spend s " +
        "JOIN category c ON c.id = s.category_id " +
        "WHERE s.id = ?"
      )) {
        ps.setObject(1, id);

        ps.execute();

        try (ResultSet rs = ps.getResultSet()) {
          if (rs.next()) {
            CategoryEntity ce = new CategoryEntity();
            ce.setId(rs.getObject("category_id", UUID.class));
            ce.setName(rs.getString("category_name"));
            ce.setUsername(rs.getString("category_username"));
            ce.setArchived(rs.getBoolean("category_archived"));

            SpendEntity se = new SpendEntity();
            se.setId(rs.getObject("spend_id", UUID.class));
            se.setUsername(rs.getString("spend_username"));
            se.setSpendDate(rs.getDate("spend_date"));
            se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
            se.setAmount(rs.getDouble("amount"));
            se.setDescription(rs.getString("description"));
            se.setCategory(ce);
            return Optional.of(se);
          } else {
            return Optional.empty();
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<SpendEntity> findAllByUsername(String username) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "SELECT " +
              "s.id AS spend_id, " +
              "s.username AS spend_username, " +
              "s.spend_date, " +
              "s.currency, " +
              "s.amount, " +
              "s.description, " +
              "s.category_id, " +
              "c.name AS category_name, " +
              "c.username AS category_username, " +
              "c.archived AS category_archived " +
              "FROM spend s " +
              "JOIN category c ON c.id = s.category_id " +
              "WHERE s.username = ? " +
              "ORDER BY s.spend_date DESC"
      )) {
        ps.setString(1, username);
        ps.execute();

        try (ResultSet rs = ps.getResultSet()) {
          ArrayList<SpendEntity> result = new ArrayList<>();
          while (rs.next()) {
            CategoryEntity ce = new CategoryEntity();
            ce.setId(rs.getObject("category_id", UUID.class));
            ce.setName(rs.getString("category_name"));
            ce.setUsername(rs.getString("category_username"));
            ce.setArchived(rs.getBoolean("category_archived"));

            SpendEntity se = new SpendEntity();
            se.setId(rs.getObject("spend_id", UUID.class));
            se.setUsername(rs.getString("spend_username"));
            se.setSpendDate(rs.getDate("spend_date"));
            se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
            se.setAmount(rs.getDouble("amount"));
            se.setDescription(rs.getString("description"));
            se.setCategory(ce);
            result.add(se);
          }
          return result;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteSpend(SpendEntity spend) {
    if (spend == null || spend.getId() == null) {
      throw new IllegalArgumentException("Spend or spend id must not be null");
    }
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "DELETE FROM spend WHERE id = ?"
      )) {
        ps.setObject(1, spend.getId());
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
