package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;
import static guru.qa.niffler.RandomDataUtils.randomUsername;

@WebTest
public class RegistrationWebTests {

  private static final Config CFG = Config.getInstance();

  @Test
  void shouldRegisterNewUser() {
    final String username = randomUsername();
    final String password = "secret";

    open(CFG.frontUrl(), LoginPage.class)
        .clickCreateNewAccount()
        .fillRegistrationForm(username, password, password)
        .submitRegistration()
        .checkYouHaveRegisteredIsDisplayed();
  }

  @Test
  void shouldNotRegisterUserWithExistingUsername() {
    final String username = randomUsername();
    final String password = "secret";

    // Register First User
    open(CFG.frontUrl(), LoginPage.class)
        .clickCreateNewAccount()
        .fillRegistrationForm(username, password, password)
        .submitRegistration()
        .checkYouHaveRegisteredIsDisplayed();

    // Register Second User
    open(CFG.frontUrl(), LoginPage.class)
        .clickCreateNewAccount()
        .fillRegistrationForm(username, password, password)
        .submitRegistration()
        .checkUsernameAlreadyExistsErrorIsDisplayed(username);
  }

  @Test
  void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
    final String username = randomUsername();
    final String password1 = "secret";
    final String password2 = "terces";

    open(CFG.frontUrl(), LoginPage.class)
        .clickCreateNewAccount()
        .fillRegistrationForm(username, password1, password2)
        .submitRegistration()
        .checkPasswordsShouldBeEqualIsDisplayed();
  }

}
