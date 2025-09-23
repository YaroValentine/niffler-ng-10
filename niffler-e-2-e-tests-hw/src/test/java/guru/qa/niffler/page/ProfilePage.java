package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CurrencyValues;
import io.qameta.allure.Step;

import java.io.File;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class ProfilePage extends BasePage<ProfilePage> {

  public final static String URL = "http://127.0.0.1:3000/profile";

  final SelenideElement
      profileAvatar = $("profile__avatar"),
      firstNameFld = $("input[name='firstname']"),
      sureNameFld = $("input[name='surname']"),
      submitBtn = $("button[type='submit']"),
      currencyDdl = $(" css-1hb7zxy-IndicatorsContainer"),
      categoryNameFld = $(byText("Add new category")),
      createBtn = $(byText("Create")),
      chooseFileForAvatarBtn = $("edit-avatar__input"),
      spendingCategoriesTbl = $(".main-content__section-categories ul"),
      showArchivedTgl = $x("//input[@type='checkbox']");


  public ProfilePage open() {
    Selenide.open(URL);
    return this;
  }

  public ProfilePage checkThatPageLoaded() {
    profileAvatar.should(visible);
    return this;
  }

  @Step("Type Name: {name}")
  public ProfilePage typeName(String name) {
    firstNameFld.setValue(name);
    return this;
  }

  @Step("Type Surname: {name}")
  public ProfilePage typeSurname(String name) {
    sureNameFld.setValue(name);
    return this;
  }

  @Step("Select Currency: {currency}")
  public ProfilePage selectCurrency(CurrencyValues currency) {
    currencyDdl.setValue(currency.toString());
    return this;
  }

  @Step("Click Submit button")
  public ProfilePage clickSubmitBtn() {
    submitBtn.click();
    return this;
  }

  @Step("Type Category Name: {name}")
  public ProfilePage typeCategoryName(String name) {
    categoryNameFld.setValue(name);
    return this;
  }

  @Step("Click Create button")
  public ProfilePage clickCreateBtn() {
    createBtn.click();
    return this;
  }

  @Step("Click on Avatar")
  public ProfilePage clickOnAvatar() {
    profileAvatar.click();
    return this;
  }

  @Step("Upload Photo: {file}")
  public ProfilePage uploadPhoto(File file) {
    chooseFileForAvatarBtn.uploadFile(file);
    return this;
  }

  @Step("Verify Spending Category exists in All Your Spending Categories table")
  public ProfilePage verifySpendingExistsInAllYourSpendingCategoriesTable(String spend) {
    spendingCategoriesTbl.shouldHave(text(spend));
    return this;
  }

  @Step("Verify Name equals: {name}")
  public ProfilePage verifyNameEquals(String name) {
    firstNameFld.shouldHave(value(name));
    return this;
  }

  @Step("Verify Surname equals: {name}")
  public ProfilePage verifySurnameEquals(String name) {
    sureNameFld.shouldHave(value(name));
    return this;
  }

  @Step("Verify Currency selected: {name}")
  public ProfilePage verifyCurrencyEquals(CurrencyValues currency) {
    SelenideElement selectedCurrency = $("div.css-1dimb5e-singleValue");
    selectedCurrency.shouldHave(text(currency.toString()));
    return this;
  }

  public ProfilePage clickShowArchivedCategories() {
    showArchivedTgl.click();
    return this;
  }

  public ProfilePage verifyCategoryExists(String category) {
    $x(String.format("//span[text()='%s']", category)).should(exist);
    return this;
  }

}
