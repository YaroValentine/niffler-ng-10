package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.model.CategoryJson;

import java.util.List;
import java.util.UUID;

public class CategoryDbClient {

  private final CategoryDao categoryDao = new CategoryDaoJdbc();

  public CategoryJson createCategory(CategoryJson category) {
    CategoryEntity entity = CategoryEntity.fromJson(category);
    return CategoryJson.fromEntity(categoryDao.create(entity));
  }

  public CategoryJson findCategoryById(UUID id) {
    return categoryDao.findCategoryById(id)
        .map(CategoryJson::fromEntity)
        .orElseThrow(() -> new RuntimeException("Failed to find category with id: " + id));
  }

  public CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName) {
    return categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName)
        .map(CategoryJson::fromEntity)
        .orElseThrow(() -> new RuntimeException("Failed to find category by username=" + username + ", name=" + categoryName));
  }

  public List<CategoryJson> findAllByUsername(String username) {
    return categoryDao.findAllByUsername(username, null).stream()
        .map(CategoryJson::fromEntity)
        .toList();
  }

  public List<CategoryJson> findAllByUsernameAndName(String username, String categoryName) {
    return categoryDao.findAllByUsername(username, categoryName).stream()
        .map(CategoryJson::fromEntity)
        .toList();
  }

  public CategoryJson updateCategory(CategoryJson category) {
    CategoryEntity entity = CategoryEntity.fromJson(category);
    return CategoryJson.fromEntity(categoryDao.update(entity));
  }

  public void deleteCategoryById(CategoryJson category) {
    CategoryEntity entity = CategoryEntity.fromJson(category);
    if (entity.getId() == null) {
      throw new IllegalArgumentException("Category id must not be null");
    }
    categoryDao.deleteCategoryById(entity);
  }
}
