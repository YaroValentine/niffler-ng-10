package guru.qa.niffler.service;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.GhApi;
import guru.qa.niffler.config.Config;
import lombok.SneakyThrows;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.Objects;

public class GhApiClient {

  private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";

  private final Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(Config.getInstance().ghUrl())
    .addConverterFactory(JacksonConverterFactory.create())
    .build();

  private final GhApi ghApi = retrofit.create(GhApi.class);

  @SneakyThrows
  public String issueState(String issueNumber) {
    JsonNode responseBody = ghApi.issue(
      "Bearer " + System.getenv(GH_TOKEN_ENV),
      issueNumber
    ).execute().body();
    System.out.println("ENV GITHUB_TOKEN=" + System.getenv("GITHUB_TOKEN"));
    System.out.println("PROP github.token=" + System.getProperty("github.token"));
    return Objects.requireNonNull(responseBody).get("state").asText();
  }
}
