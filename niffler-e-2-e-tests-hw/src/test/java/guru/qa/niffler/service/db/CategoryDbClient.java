package guru.qa.niffler.service.db;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.model.CategoryJson;

import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;

public class CategoryDbClient {

  private static final Config CFG = Config.getInstance();

  public CategoryJson createCategory(CategoryJson category) {
    return transaction(connection -> {
          return CategoryJson.fromEntity(
              new CategoryDaoJdbc(connection).create(CategoryEntity.fromJson(category))
          );
        },
        CFG.spendJdbcUrl()
    );
  }

  public CategoryJson findCategoryById(UUID id) {
    return transaction(connection -> {
          return new CategoryDaoJdbc(connection).findCategoryById(id)
              .map(CategoryJson::fromEntity)
              .orElseThrow(() -> new RuntimeException("Failed to find category with id: " + id));
        },
        CFG.spendJdbcUrl()
    );
  }

  public CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName) {
    return transaction(connection -> {
          return new CategoryDaoJdbc(connection).findCategoryByUsernameAndCategoryName(username, categoryName)
              .map(CategoryJson::fromEntity)
              .orElseThrow(() -> new RuntimeException("Failed to find category by username=" + username + ", name=" + categoryName));
        },
        CFG.spendJdbcUrl()
    );
  }

  public List<CategoryJson> findAllByUsername(String username) {
    return transaction(connection -> {
          return new CategoryDaoJdbc(connection).findAllByUsername(username, null).stream()
              .map(CategoryJson::fromEntity)
              .toList();
        },
        CFG.spendJdbcUrl()
    );
  }

  public List<CategoryJson> findAllByUsernameAndName(String username, String categoryName) {
    return transaction(connection -> {
          return new CategoryDaoJdbc(connection).findAllByUsername(username, categoryName).stream()
              .map(CategoryJson::fromEntity)
              .toList();
        },
        CFG.spendJdbcUrl()
    );
  }

  public CategoryJson updateCategory(CategoryJson category) {
    return transaction(connection -> {
          return CategoryJson.fromEntity(
              new CategoryDaoJdbc(connection).update(CategoryEntity.fromJson(category))
          );
        },
        CFG.spendJdbcUrl()
    );
  }

  public void deleteCategoryById(CategoryJson category) {
    transaction(connection -> {
          CategoryEntity entity = CategoryEntity.fromJson(category);
          if (entity.getId() == null) {
            throw new IllegalArgumentException("Category id must not be null");
          }
          new CategoryDaoJdbc(connection).deleteCategoryById(entity);
        },
        CFG.spendJdbcUrl()
    );
  }
}
