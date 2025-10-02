package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage extends BasePage<MainPage> {
  private final ElementsCollection tableRows = $("#spendings tbody").$$("tr");
  private final SelenideElement canvas = $x("//canvas");

  @Override
  public LoginPage loginPage() {
    return super.loginPage();
  }

  public MainPage checkThatPageLoaded() {
    canvas.shouldBe(visible);
    return this;
  }

  public EditSpendingPage editSpending(String spendingDescription) {
    tableRows.find(text(spendingDescription)).$$("td").get(5).click();
    return new EditSpendingPage();
  }

  public void checkThatTableContainsSpending(String spendingDescription) {
    tableRows.find(text(spendingDescription)).should(visible);
  }
}
