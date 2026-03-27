package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.Authority;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

  private final Connection connection;

  public AuthAuthorityDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void create(AuthAuthorityEntity... authorities) {
    try (PreparedStatement ps = connection.prepareStatement(
        "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)"
    )) {
      for (AuthAuthorityEntity authority : authorities) {
        ps.setObject(1, authority.getUser().getId());
        ps.setString(2, authority.getAuthority().name());
        ps.addBatch();
      }
      ps.executeBatch();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<AuthAuthorityEntity> findAllByUserId(UUID userId) {
    try (PreparedStatement ps = connection.prepareStatement(
        "SELECT id, user_id, authority FROM \"authority\" WHERE user_id = ?"
    )) {
      ps.setObject(1, userId);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        List<AuthAuthorityEntity> result = new ArrayList<>();
        while (rs.next()) {
          AuthAuthorityEntity ae = new AuthAuthorityEntity();
          ae.setId(rs.getObject("id", UUID.class));
          ae.setAuthority(Authority.valueOf(rs.getString("authority")));
          result.add(ae);
        }
        return result;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(AuthAuthorityEntity... authorities) {
    try (PreparedStatement ps = connection.prepareStatement(
        "DELETE FROM \"authority\" WHERE id = ?"
    )) {
      for (AuthAuthorityEntity authority : authorities) {
        ps.setObject(1, authority.getId());
        ps.addBatch();
      }
      ps.executeBatch();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}




