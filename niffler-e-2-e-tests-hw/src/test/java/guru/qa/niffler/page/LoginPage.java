package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {
  private final SelenideElement
      usernameInput = $("input[name='username']"),
      passwordInput = $("input[name='password']"),
      submitButton = $("button[type='submit']"),
      createNewAccountBtn = $("a[href*='/register']"),
      formError = $(".form__error");

  @Override
  public LoginPage checkThatPageLoaded() {
    usernameInput.shouldBe(visible);
    return this;
  }

  public LoginPage login(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    submitButton.click();
    return this;
  }

  public RegistrationPage clickCreateNewAccount() {
    createNewAccountBtn.click();
    return new RegistrationPage();
  }

  public LoginPage checkBadCredentialsIsDisplayed() {
    formError.shouldHave(text("Bad credentials"));
    return this;
  }
}
