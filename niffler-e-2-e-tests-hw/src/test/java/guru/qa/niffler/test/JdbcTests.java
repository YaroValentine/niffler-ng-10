package guru.qa.niffler.test;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcTests {
  @Test
  void daoJdbcCanCreateTest() {
    SpendDbClient spendDbClient = new SpendDbClient();

    SpendJson spend = spendDbClient.createSpend(
      new SpendJson(
        null,
        new Date(),
        new CategoryJson(
          null,
          "test-category-1",
          "yaro",
          false
        ),
        CurrencyValues.USD,
        111.1,
        "test dao jdbc spend",
        "yaro"
      )
    );
    System.out.println(spend);
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
}
