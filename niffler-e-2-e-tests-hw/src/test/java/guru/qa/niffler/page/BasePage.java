package guru.qa.niffler.page;

import io.qameta.allure.Step;

public abstract class BasePage<T extends BasePage> {

  private LoginPage loginPage;
  private MainPage mainPage;
  private ProfilePage profilePage;
  private RegistrationPage registrationPage;
  private EditSpendingPage editSpendingPage;
  private FriendsPage friendsPage;
  private PeoplePage peoplePage;

  @Step("Check that Page loaded")
  public abstract T checkThatPageLoaded();

  public LoginPage loginPage() {
    return loginPage == null ? loginPage = new LoginPage() : loginPage;
  }

  public MainPage mainPage() {
    return mainPage == null ? mainPage = new MainPage() : mainPage;
  }

  public ProfilePage profilePage() {
    return profilePage == null ? profilePage = new ProfilePage() : profilePage;
  }

  public RegistrationPage registrationPage() {
    return registrationPage == null ? registrationPage = new RegistrationPage() : registrationPage;
  }

  public EditSpendingPage editSpendingPage() {
    return editSpendingPage == null ? editSpendingPage = new EditSpendingPage() : editSpendingPage;
  }

  public FriendsPage friendsPage() {
    return friendsPage == null ? friendsPage = new FriendsPage() : friendsPage;
  }

  public PeoplePage allPeoplePage() {
    return peoplePage == null ? peoplePage = new PeoplePage() : peoplePage;
  }
}
