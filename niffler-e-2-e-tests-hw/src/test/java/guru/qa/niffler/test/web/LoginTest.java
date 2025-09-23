package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;
import static guru.qa.niffler.RandomDataUtils.randomUsername;

@WebTest
public class LoginTest {

  private static final Config CFG = Config.getInstance();

  @Test
  void mainPageShouldBeDisplayedAfterSuccessLogin() {
    final String username = randomUsername();
    final String password = "secret";

    open(CFG.frontUrl(), LoginPage.class)
        .clickCreateNewAccount()
        .fillRegistrationForm(username, password, password)
        .submitRegistration()
        .clickSignIn()
        .login(username, password)

        .mainPage().checkThatPageLoaded();
  }

  @Test
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
    final String username = randomUsername();
    final String password = "secret";
    final String wrongPassword = "terces";

    open(CFG.frontUrl(), LoginPage.class)
        .clickCreateNewAccount()
        .fillRegistrationForm(username, password, password)
        .submitRegistration()
        .clickSignIn()
        .login(username, wrongPassword)

        .checkBadCredentialsIsDisplayed();
  }
}
