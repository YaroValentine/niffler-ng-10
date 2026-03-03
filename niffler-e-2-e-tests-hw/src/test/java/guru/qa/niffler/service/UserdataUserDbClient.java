package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;

public class UserdataUserDbClient {

  private static final Config CFG = Config.getInstance();

  public UserEntity createUser(UserEntity user) {
    return transaction(connection -> {
          return new UserdataUserDaoJdbc(connection).create(user);
        },
        CFG.userdataJdbcUrl()
    );
  }

  public UserEntity findUserById(UUID id) {
    return transaction(connection -> {
          return new UserdataUserDaoJdbc(connection).findById(id)
              .orElseThrow(() -> new RuntimeException("Failed to find user with id: " + id));
        },
        CFG.userdataJdbcUrl()
    );
  }

  public UserEntity findUserByUsername(String username) {
    return transaction(connection -> {
          return new UserdataUserDaoJdbc(connection).findByUsername(username)
              .orElseThrow(() -> new RuntimeException("Failed to find user with username: " + username));
        },
        CFG.userdataJdbcUrl()
    );
  }

  public void deleteUser(UserEntity user) {
    transaction(connection -> {
          if (user.getId() == null) {
            throw new IllegalArgumentException("User id must not be null");
          }
          new UserdataUserDaoJdbc(connection).delete(user);
        },
        CFG.userdataJdbcUrl()
    );
  }
}



