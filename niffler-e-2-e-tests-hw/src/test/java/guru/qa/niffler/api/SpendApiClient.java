package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;

import static org.apache.hc.core5.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient {

  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(Config.getInstance().spendUrl())
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final SpendApi spendApi = retrofit.create(SpendApi.class);

  @SneakyThrows
  public SpendJson createSpend(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.addSpend(spend).execute();
    } catch (IOException e) {
      throw new AssertionError("Failed to create spend", e);
    }
    assertEquals(SC_CREATED, response.code());
    return response.body();
  }

  @SneakyThrows
  public SpendJson editSpend(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.editSpend(spend).execute();
    } catch (IOException e) {
      throw new AssertionError("Failed to edit spend", e);
    }
    assertEquals(SC_OK, response.code());
    return response.body();
  }

  @SneakyThrows
  public SpendJson getSpend(String id, String username) {
    final Response<SpendJson> response;
    try {
      response = spendApi.getSpend(id, username).execute();
    } catch (IOException e) {
      throw new AssertionError("Failed to get spend", e);
    }
    assertEquals(SC_OK, response.code());
    return response.body();
  }

  @SneakyThrows
  public List<SpendJson> getAllSpends(String username, CurrencyValues filterCurrency, String from, String to) {
    final Response<List<SpendJson>> response;
    try {
      response = spendApi.getAllSpends(username, filterCurrency, from, to).execute();
    } catch (IOException e) {
      throw new AssertionError("Failed to get all spends", e);
    }
    assertEquals(SC_OK, response.code());
    return response.body();
  }

  @SneakyThrows
  public void removeSpend(String username, List<String> ids) {
    final Response<Void> response;
    try {
      response = spendApi.deleteSpend(username, ids).execute();
    } catch (IOException e) {
      throw new AssertionError("Failed to remove spend", e);
    }
    assertEquals(SC_ACCEPTED, response.code());
  }

  @SneakyThrows
  public CategoryJson addCategory(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.addCategory(category).execute();
    } catch (IOException e) {
      throw new AssertionError("Failed to add category", e);
    }
    assertEquals(SC_OK, response.code());
    return response.body();
  }

  @SneakyThrows
  public CategoryJson updateCategory(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.updateCategory(category).execute();
    } catch (IOException e) {
      throw new AssertionError("Failed to update category", e);
    }
    assertEquals(SC_OK, response.code());
    return response.body();
  }

  @SneakyThrows
  public List<CategoryJson> getAllCategories(String username, boolean excludeArchived) {
    final Response<List<CategoryJson>> response;
    try {
      response = spendApi.getAllCategories(username, excludeArchived).execute();
    } catch (IOException e) {
      throw new AssertionError("Failed to get all categories", e);
    }
    assertEquals(SC_OK, response.code());
    return response.body();
  }

}
