package guru.qa.niffler.test.rest;

import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.service.api.AuthApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;

public class RegistrationRestTest {

  private final AuthApiClient authApiClient = new AuthApiClient();

  @Test
//  @Disabled
  void newUserShouldRegisteredByApiCall() throws IOException {
    final Response<Void> response = authApiClient.register(RandomDataUtils.randomUsername(), "12345");
    Assertions.assertEquals(201, response.code());
  }
}
