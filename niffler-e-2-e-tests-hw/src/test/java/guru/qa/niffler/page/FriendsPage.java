package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage extends BasePage<FriendsPage> {

  public final static String URL = "http://localhost:3000/people/friends";

  private final SelenideElement peopleTab = $("a[href='/people/friends']");
  private final SelenideElement allTab = $("a[href='/people/all']");
  private final SelenideElement requestsTable = $("#requests");
  private final SelenideElement friendsTable = $("#friends");

  @Override
  public FriendsPage checkThatPageLoaded() {
    return null;
  }

  public FriendsPage checkExistingFriends(String... expectedUsernames) {
    friendsTable.$$("tr").shouldHave(textsInAnyOrder(expectedUsernames));
    return this;
  }

  public FriendsPage checkNoExistingFriends() {
    friendsTable.$$("tr").shouldHave(size(0));
    return this;
  }

  public FriendsPage checkExistingInvitations(String... expectedUsernames) {
    requestsTable.$$("tr").shouldHave(textsInAnyOrder(expectedUsernames));
    return this;
  }

  public FriendsPage open() {
    Selenide.open(URL);
    return this;
  }

}
