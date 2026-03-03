package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient {

  private static final Config CFG = Config.getInstance();

  public SpendJson createSpend(SpendJson spend) {
    return transaction(connection -> {
          SpendEntity spendEntity = SpendEntity.fromJson(spend);
          if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = new CategoryDaoJdbc(connection)
                .create(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
          }
          return SpendJson.fromEntity(
              new SpendDaoJdbc(connection).create(spendEntity)
          );
        },
        CFG.spendJdbcUrl()
    );
  }

  public SpendJson findSpendById(UUID id) {
    return transaction(connection -> {
          return new SpendDaoJdbc(connection).findSpendById(id)
              .map(SpendJson::fromEntity)
              .orElseThrow(() -> new RuntimeException("Failed to find spend with id: " + id));
        },
        CFG.spendJdbcUrl()
    );
  }

  public List<SpendJson> findAllByUsername(String username) {
    return transaction(connection -> {
          return new SpendDaoJdbc(connection).findAllByUsername(username).stream()
              .map(SpendJson::fromEntity)
              .toList();
        },
        CFG.spendJdbcUrl()
    );
  }

  public void deleteSpend(SpendJson spend) {
    transaction(connection -> {
          SpendEntity entity = SpendEntity.fromJson(spend);
          if (entity.getId() == null) {
            throw new IllegalArgumentException("Spend id must not be null");
          }
          new SpendDaoJdbc(connection).deleteSpend(entity);
        },
        CFG.spendJdbcUrl()
    );
  }
}
