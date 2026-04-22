package guru.qa.niffler.service.db;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.XaConsumer;
import static guru.qa.niffler.data.Databases.XaFunction;
import static guru.qa.niffler.data.Databases.xaTransaction;

public class AuthUserDbClient {

  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  public AuthUserEntity createUser(AuthUserEntity user) {
    user.setPassword(pe.encode(user.getPassword()));
    xaTransaction(
        new XaConsumer(
            connection -> {
              AuthUserEntity created = new AuthUserDaoJdbc(connection).create(user);

              AuthAuthorityEntity read = new AuthAuthorityEntity();
              read.setUser(created);
              read.setAuthority(Authority.read);

              AuthAuthorityEntity write = new AuthAuthorityEntity();
              write.setUser(created);
              write.setAuthority(Authority.write);

              new AuthAuthorityDaoJdbc(connection).create(read, write);
            },
            CFG.authJdbcUrl()
        )
    );
    return user;
  }

  public Optional<AuthUserEntity> findById(UUID id) {
    return xaTransaction(
        new XaFunction<>(
            connection -> new AuthUserDaoJdbc(connection).findById(id),
            CFG.authJdbcUrl()
        )
    );
  }

  public Optional<AuthUserEntity> findByUsername(String username) {
    return xaTransaction(
        new XaFunction<>(
            connection -> new AuthUserDaoJdbc(connection).findByUsername(username),
            CFG.authJdbcUrl()
        )
    );
  }

  public List<AuthUserEntity> findAll() {
    return xaTransaction(
        new XaFunction<>(
            connection -> new AuthUserDaoJdbc(connection).findAll(),
            CFG.authJdbcUrl()
        )
    );
  }

  public void deleteUser(AuthUserEntity user) {
    // authorities are deleted first due to FK constraint, then the user
    xaTransaction(
        new XaConsumer(
            connection -> {
              List<AuthAuthorityEntity> authorities =
                  new AuthAuthorityDaoJdbc(connection).findAllByUserId(user.getId());
              new AuthAuthorityDaoJdbc(connection)
                  .delete(authorities.toArray(new AuthAuthorityEntity[0]));
              new AuthUserDaoJdbc(connection).delete(user);
            },
            CFG.authJdbcUrl()
        )
    );
  }
}


