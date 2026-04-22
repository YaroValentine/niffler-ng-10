package guru.qa.niffler.test.db;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.service.AuthUserDbClient;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AuthUserDbClientTest {

  private final AuthUserDbClient authUserDbClient = new AuthUserDbClient();

  private AuthUserEntity buildUser(String suffix) {
    AuthUserEntity user = new AuthUserEntity();
    user.setUsername("test_user_" + suffix);
    user.setPassword("password_" + suffix);
    user.setEnabled(true);
    user.setAccountNonExpired(true);
    user.setAccountNonLocked(true);
    user.setCredentialsNonExpired(true);
    return user;
  }

  @Test
  void createUserShouldPersistUserWithEncodedPasswordAndAuthorities() {
    AuthUserEntity user = buildUser(String.valueOf(System.currentTimeMillis()));
    String rawPassword = user.getPassword();

    AuthUserEntity created = authUserDbClient.createUser(user);
    try {
      assertNotNull(created.getId());
      // password must be encoded (not stored in plain text)
      assertNotEquals(rawPassword, created.getPassword());
      assertTrue(created.getPassword().startsWith("{bcrypt}") ||
          created.getPassword().startsWith("{pbkdf2}") ||
          created.getPassword().contains("}")); // delegating encoder prefix

      // verify persisted user is readable with both authorities
      Optional<AuthUserEntity> found = authUserDbClient.findById(created.getId());
      assertTrue(found.isPresent());
      assertEquals(created.getUsername(), found.get().getUsername());

      List<AuthAuthorityEntity> authorities = found.get().getAuthorities();
      assertEquals(2, authorities.size());
      assertTrue(authorities.stream().anyMatch(a -> a.getAuthority() == Authority.read));
      assertTrue(authorities.stream().anyMatch(a -> a.getAuthority() == Authority.write));
    } finally {
      authUserDbClient.deleteUser(created);
    }
  }

  @Test
  void findByUsernameShouldReturnUserWithAuthorities() {
    AuthUserEntity user = buildUser("find_" + System.currentTimeMillis());
    AuthUserEntity created = authUserDbClient.createUser(user);
    try {
      Optional<AuthUserEntity> found = authUserDbClient.findByUsername(created.getUsername());
      assertTrue(found.isPresent());
      assertEquals(created.getUsername(), found.get().getUsername());
      assertEquals(2, found.get().getAuthorities().size());
    } finally {
      authUserDbClient.deleteUser(created);
    }
  }

  @Test
  void findByUsernameForNonExistingUserShouldReturnEmpty() {
    Optional<AuthUserEntity> found = authUserDbClient.findByUsername("non_existing_user_" + System.currentTimeMillis());
    assertTrue(found.isEmpty());
  }

  @Test
  void findAllShouldReturnAtLeastCreatedUser() {
    AuthUserEntity user = buildUser("all_" + System.currentTimeMillis());
    AuthUserEntity created = authUserDbClient.createUser(user);
    try {
      List<AuthUserEntity> all = authUserDbClient.findAll();
      assertNotNull(all);
      assertFalse(all.isEmpty());
      assertTrue(all.stream().anyMatch(u -> u.getUsername().equals(created.getUsername())));
    } finally {
      authUserDbClient.deleteUser(created);
    }
  }

  @Test
  void deleteUserShouldRemoveUserAndAuthorities() {
    AuthUserEntity user = buildUser("del_" + System.currentTimeMillis());
    AuthUserEntity created = authUserDbClient.createUser(user);

    authUserDbClient.deleteUser(created);

    Optional<AuthUserEntity> found = authUserDbClient.findById(created.getId());
    assertTrue(found.isEmpty());
  }

  @Test
  void xaTransactionShouldRollbackOnError() {
    // Create a user normally first so we have a valid id to cause a duplicate-key error
    AuthUserEntity base = buildUser("xa_" + System.currentTimeMillis());
    AuthUserEntity created = authUserDbClient.createUser(base);
    try {
      // Try to create a second user with the same username — should fail with a constraint violation
      // and the XA transaction must roll back cleanly (no partial data)
      AuthUserEntity duplicate = buildUser("xa_already_exists");
      duplicate.setUsername(created.getUsername()); // force duplicate username

      assertThrows(RuntimeException.class, () -> authUserDbClient.createUser(duplicate));

      // Only the original user must exist, not a half-inserted duplicate
      List<AuthUserEntity> all = authUserDbClient.findAll();
      long count = all.stream()
          .filter(u -> u.getUsername().equals(created.getUsername()))
          .count();
      assertEquals(1, count, "Rolled-back duplicate must not be present in the DB");
    } finally {
      authUserDbClient.deleteUser(created);
    }
  }
}

