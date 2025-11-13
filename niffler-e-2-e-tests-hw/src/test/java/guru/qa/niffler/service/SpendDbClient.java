package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.SpendJson;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
public class SpendDbClient {

  private final SpendDao spendDao = new SpendDaoJdbc();
  private final CategoryDao categoryDao = new CategoryDaoJdbc();

  public SpendJson createSpend(SpendJson spend) {
    SpendEntity spendEntity = SpendEntity.fromJson(spend);
    if (spendEntity.getCategory().getId() == null) {
      CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
      spendEntity.setCategory(categoryEntity);
    }
    return SpendJson.fromEntity(
      spendDao.create(spendEntity));
  }

  public SpendJson findSpendById(UUID id) {
    return spendDao.findSpendById(id)
      .map(SpendJson::fromEntity)
      .orElseThrow(() -> new RuntimeException("Failed to find spend with id: " + id));
  }

  public List<SpendJson> findAllByUsername(String username) {
    return spendDao.findAllByUsername(username).stream()
        .map(SpendJson::fromEntity)
        .toList();
  }

  public void deleteSpend(SpendJson spend) {
    SpendEntity entity = SpendEntity.fromJson(spend);
    if (entity.getId() == null) {
      throw new IllegalArgumentException("Spend id must not be null");
    }
    spendDao.deleteSpend(entity);
  }
}
