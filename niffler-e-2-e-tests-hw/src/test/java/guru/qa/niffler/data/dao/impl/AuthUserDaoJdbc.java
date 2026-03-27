package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoJdbc implements AuthUserDao {

  private final Connection connection;

  public AuthUserDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    try (PreparedStatement ps = connection.prepareStatement(
        "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
            "VALUES (?, ?, ?, ?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getPassword());
      ps.setBoolean(3, user.getEnabled());
      ps.setBoolean(4, user.getAccountNonExpired());
      ps.setBoolean(5, user.getAccountNonLocked());
      ps.setBoolean(6, user.getCredentialsNonExpired());

      ps.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can`t find id in ResultSet");
        }
      }
      user.setId(generatedKey);
      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    try (PreparedStatement ps = connection.prepareStatement(
        "SELECT u.id, u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, u.credentials_non_expired, " +
            "a.id AS authority_id, a.authority " +
            "FROM \"user\" u " +
            "LEFT JOIN \"authority\" a ON a.user_id = u.id " +
            "WHERE u.id = ?"
    )) {
      ps.setObject(1, id);
      ps.execute();
      return extractUser(ps.getResultSet());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    try (PreparedStatement ps = connection.prepareStatement(
        "SELECT u.id, u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, u.credentials_non_expired, " +
            "a.id AS authority_id, a.authority " +
            "FROM \"user\" u " +
            "LEFT JOIN \"authority\" a ON a.user_id = u.id " +
            "WHERE u.username = ?"
    )) {
      ps.setString(1, username);
      ps.execute();
      return extractUser(ps.getResultSet());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<AuthUserEntity> findAll() {
    try (PreparedStatement ps = connection.prepareStatement(
        "SELECT u.id, u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, u.credentials_non_expired, " +
            "a.id AS authority_id, a.authority " +
            "FROM \"user\" u " +
            "LEFT JOIN \"authority\" a ON a.user_id = u.id " +
            "ORDER BY u.username"
    )) {
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        List<AuthUserEntity> result = new ArrayList<>();
        UUID currentId = null;
        AuthUserEntity current = null;
        while (rs.next()) {
          UUID userId = rs.getObject("id", UUID.class);
          if (!userId.equals(currentId)) {
            current = mapUserRow(rs);
            result.add(current);
            currentId = userId;
          }
          String authority = rs.getString("authority");
          if (authority != null) {
            AuthAuthorityEntity ae = new AuthAuthorityEntity();
            ae.setId(rs.getObject("authority_id", UUID.class));
            ae.setAuthority(Authority.valueOf(authority));
            ae.setUser(current);
            current.getAuthorities().add(ae);
          }
        }
        return result;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(AuthUserEntity user) {
    if (user == null || user.getId() == null) {
      throw new IllegalArgumentException("User or user id must not be null");
    }
    try (PreparedStatement ps = connection.prepareStatement(
        "DELETE FROM \"user\" WHERE id = ?"
    )) {
      ps.setObject(1, user.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private Optional<AuthUserEntity> extractUser(ResultSet rs) throws SQLException {
    try (rs) {
      AuthUserEntity user = null;
      while (rs.next()) {
        if (user == null) {
          user = mapUserRow(rs);
        }
        String authority = rs.getString("authority");
        if (authority != null) {
          AuthAuthorityEntity ae = new AuthAuthorityEntity();
          ae.setId(rs.getObject("authority_id", UUID.class));
          ae.setAuthority(Authority.valueOf(authority));
          ae.setUser(user);
          user.getAuthorities().add(ae);
        }
      }
      return Optional.ofNullable(user);
    }
  }

  private AuthUserEntity mapUserRow(ResultSet rs) throws SQLException {
    AuthUserEntity user = new AuthUserEntity();
    user.setId(rs.getObject("id", UUID.class));
    user.setUsername(rs.getString("username"));
    user.setPassword(rs.getString("password"));
    user.setEnabled(rs.getBoolean("enabled"));
    user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
    user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
    user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
    return user;
  }
}






