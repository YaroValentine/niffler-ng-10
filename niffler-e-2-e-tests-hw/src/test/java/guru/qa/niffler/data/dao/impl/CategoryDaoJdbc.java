package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbc implements CategoryDao {

  private final Connection connection;

  public CategoryDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @Override
  public CategoryEntity create(CategoryEntity category) {
    try (PreparedStatement ps = connection.prepareStatement(
        "INSERT INTO category (username, name, archived) " +
            "VALUES (?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, category.getUsername());
      ps.setString(2, category.getName());
      ps.setBoolean(3, category.isArchived());

      ps.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can`t find id in ResultSet");
        }
      }
      category.setId(generatedKey);
      return category;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    try (PreparedStatement ps = connection.prepareStatement(
        "SELECT * FROM category WHERE id = ?"
    )) {
      ps.setObject(1, id);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          CategoryEntity ce = new CategoryEntity();
          ce.setId(rs.getObject("id", UUID.class));
          ce.setUsername(rs.getString("username"));
          ce.setName(rs.getString("name"));
          ce.setArchived(rs.getBoolean("archived"));
          return Optional.of(ce);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
    try (PreparedStatement ps = connection.prepareStatement(
        "SELECT * FROM category WHERE username = ? AND name = ?"
    )) {
      ps.setObject(1, username);
      ps.setObject(2, categoryName);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          CategoryEntity ce = new CategoryEntity();
          ce.setId(rs.getObject("id", UUID.class));
          ce.setUsername(rs.getString("username"));
          ce.setName(rs.getString("name"));
          ce.setArchived(rs.getBoolean("archived"));
          return Optional.of(ce);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<CategoryEntity> findAllByUsername(String username, String categoryName) {
    final String baseSql = "SELECT * FROM category WHERE username = ?";
    final boolean filterByName = categoryName != null && !categoryName.isEmpty();
    final String sql = filterByName ? baseSql + " AND name = ? ORDER BY name" : baseSql + " ORDER BY name";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.setString(1, username);
      if (filterByName) {
        ps.setString(2, categoryName);
      }
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        List<CategoryEntity> result = new ArrayList<>();
        while (rs.next()) {
          CategoryEntity ce = new CategoryEntity();
          ce.setId(rs.getObject("id", UUID.class));
          ce.setUsername(rs.getString("username"));
          ce.setName(rs.getString("name"));
          ce.setArchived(rs.getBoolean("archived"));
          result.add(ce);
        }
        return result;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public CategoryEntity update(CategoryEntity categoryEntity) {
    if (categoryEntity == null || categoryEntity.getId() == null) {
      throw new IllegalArgumentException("Category or category id must not be null");
    }
    try (PreparedStatement ps = connection.prepareStatement(
        "UPDATE category SET name = ?, username = ?, archived = ? WHERE id = ?"
    )) {
      ps.setString(1, categoryEntity.getName());
      ps.setString(2, categoryEntity.getUsername());
      ps.setBoolean(3, categoryEntity.isArchived());
      ps.setObject(4, categoryEntity.getId());
      int updated = ps.executeUpdate();
      if (updated == 0) {
        throw new RuntimeException("No category updated for id=" + categoryEntity.getId());
      }
      return categoryEntity;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteCategoryById(CategoryEntity categoryEntity) {
    if (categoryEntity == null || categoryEntity.getId() == null) {
      throw new IllegalArgumentException("Category or category id must not be null");
    }
    try (PreparedStatement ps = connection.prepareStatement(
        "DELETE FROM category WHERE id = ?"
    )) {
      ps.setObject(1, categoryEntity.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
