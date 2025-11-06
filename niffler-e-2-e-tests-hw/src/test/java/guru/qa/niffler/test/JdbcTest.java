package guru.qa.niffler.test;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class JdbcTest {
  @Test
  void daoTest() {
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
}
