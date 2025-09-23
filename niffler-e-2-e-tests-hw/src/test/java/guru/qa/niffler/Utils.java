package guru.qa.niffler;

import com.github.javafaker.Faker;

public class Utils {

  public static String generateRandomString(int length) {
    if (length <= 0) {
      throw new IllegalArgumentException("Length must be greater than 0");
    }

    StringBuilder sb = new StringBuilder(length);
    Faker faker = new Faker();
    String randomString = faker.lorem().characters(length - 1);
    sb.append(Character.toUpperCase(randomString.charAt(0)));
    sb.append(randomString.substring(1));

    return sb.toString();
  }
}
