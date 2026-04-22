package guru.qa.niffler.test.db;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.db.CategoryDbClient;
import guru.qa.niffler.service.db.SpendDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcTests {
  @Test
  void daoJdbcCanCreateSpendTest() {
    SpendDbClient spendDbClient = new SpendDbClient();
    String username = "yaro_" + System.currentTimeMillis();
    String categoryName = "test-category-1-" + System.currentTimeMillis();

    SpendJson spend = spendDbClient.createSpend(
      new SpendJson(
        null,
        new Date(),
        new CategoryJson(
          null,
          categoryName,
          username,
          false
        ),
        CurrencyValues.USD,
        111.1,
        "test dao jdbc spend",
        username
      )
    );
    try {
      assertNotNull(spend.id());
      assertEquals(username, spend.username());
      assertEquals(categoryName, spend.category().name());
    } finally {
      // cleanup the created spend
      spendDbClient.deleteSpend(spend);
    }
  }

  @Test
  void daoJdbcCanFindSpendByIdTest() {
    SpendDbClient spendDbClient = new SpendDbClient();

    UUID spendId = UUID.fromString("cd21d9b9-8604-4136-b263-0b5859197389");
    SpendJson spend = spendDbClient.findSpendById(spendId);
    System.out.println(spend);
  }

  @Test
  void daoJdbcCanFindAllByUsernameTest() {
    SpendDbClient spendDbClient = new SpendDbClient();
    String username = "jdbc_usr_" + System.currentTimeMillis();

    // create two spends for the same user
    spendDbClient.createSpend(new SpendJson(
        null,
        new Date(),
        new CategoryJson(null, "cat-a", username, false),
        CurrencyValues.USD,
        10.0,
        "spend-a",
        username
    ));
    spendDbClient.createSpend(new SpendJson(
        null,
        new Date(),
        new CategoryJson(null, "cat-b", username, false),
        CurrencyValues.EUR,
        20.0,
        "spend-b",
        username
    ));

    List<SpendJson> spends = spendDbClient.findAllByUsername(username);
    assertNotNull(spends);
    assertTrue(spends.size() >= 2);
    assertTrue(spends.stream().allMatch(s -> username.equals(s.username())));
  }

  @Test
  void daoJdbcCanDeleteSpendTest() {
    SpendDbClient spendDbClient = new SpendDbClient();
    String username = "jdbc_del_" + System.currentTimeMillis();

    SpendJson created = spendDbClient.createSpend(new SpendJson(
        null,
        new Date(),
        new CategoryJson(null, "cat-del", username, false),
        CurrencyValues.USD,
        33.3,
        "to be deleted",
        username
    ));

    spendDbClient.deleteSpend(created);

    assertThrows(RuntimeException.class, () -> spendDbClient.findSpendById(created.id()));
  }

  @Test
  void categoryDaoCanFindByUsernameAndNameTest() {
    CategoryDbClient categoryDbClient = new CategoryDbClient();
    String username = "cat_user_" + System.currentTimeMillis();
    String name = "cat-name-1";

    CategoryJson created = categoryDbClient.createCategory(new CategoryJson(null, name, username, false));

    CategoryJson found = categoryDbClient.findCategoryByUsernameAndCategoryName(username, name);
    assertNotNull(found);
    assertEquals(created.name(), found.name());
    assertEquals(created.username(), found.username());
    assertFalse(found.archived());
  }

  @Test
  void categoryDaoCanFindAllByUsernameTest() {
    CategoryDbClient categoryDbClient = new CategoryDbClient();
    String username = "cat_user_list_" + System.currentTimeMillis();

    categoryDbClient.createCategory(new CategoryJson(null, "name-a", username, false));
    categoryDbClient.createCategory(new CategoryJson(null, "name-b", username, true));

    List<CategoryJson> categories = categoryDbClient.findAllByUsername(username);
    assertNotNull(categories);
    assertTrue(categories.size() >= 2);
    assertTrue(categories.stream().allMatch(c -> username.equals(c.username())));
  }

  @Test
  void categoryDaoCanDeleteByIdTest() {
    CategoryDbClient categoryDbClient = new CategoryDbClient();
    String username = "cat_user_del_" + System.currentTimeMillis();
    String name = "to-delete";

    CategoryJson created = categoryDbClient.createCategory(new CategoryJson(null, name, username, false));

    categoryDbClient.deleteCategoryById(created);

    assertThrows(RuntimeException.class, () -> categoryDbClient.findCategoryByUsernameAndCategoryName(username, name));
  }

  @Test
  void categoryDaoCanFindAllByUsernameAndNameTest() {
    CategoryDbClient categoryDbClient = new CategoryDbClient();
    String username = "cat_user_filter_" + System.currentTimeMillis();

    // create two categories for same user
    CategoryJson catA = categoryDbClient.createCategory(new CategoryJson(null, "name-a", username, false));
    categoryDbClient.createCategory(new CategoryJson(null, "name-b", username, true));

    List<CategoryJson> filtered = categoryDbClient.findAllByUsernameAndName(username, "name-a");
    assertNotNull(filtered);
    assertEquals(1, filtered.size());
    assertEquals("name-a", filtered.get(0).name());
    assertEquals(username, filtered.get(0).username());
    // cleanup
    categoryDbClient.deleteCategoryById(catA);
  }

  @Test
  void categoryDaoCanFindCategoryByIdTest() {
    CategoryDbClient categoryDbClient = new CategoryDbClient();
    String username = "cat_user_by_id_" + System.currentTimeMillis();
    String name = "by-id";

    CategoryJson created = categoryDbClient.createCategory(new CategoryJson(null, name, username, false));

    CategoryJson found = categoryDbClient.findCategoryById(created.id());
    assertNotNull(found);
    assertEquals(created.id(), found.id());
    assertEquals(name, found.name());
    assertEquals(username, found.username());

    // cleanup
    categoryDbClient.deleteCategoryById(created);
  }

  @Test
  void categoryDaoCanUpdateCategoryTest() {
    CategoryDbClient categoryDbClient = new CategoryDbClient();
    String username = "cat_user_upd_" + System.currentTimeMillis();

    CategoryJson created = categoryDbClient.createCategory(new CategoryJson(null, "old-name", username, false));

    CategoryJson updatedRequest = new CategoryJson(created.id(), "new-name", username, true);
    CategoryJson updated = categoryDbClient.updateCategory(updatedRequest);
    assertNotNull(updated);
    assertEquals(created.id(), updated.id());
    assertEquals("new-name", updated.name());
    assertTrue(updated.archived());

    CategoryJson fetched = categoryDbClient.findCategoryById(created.id());
    assertEquals("new-name", fetched.name());
    assertTrue(fetched.archived());

    // cleanup
    categoryDbClient.deleteCategoryById(updated);
  }
}
