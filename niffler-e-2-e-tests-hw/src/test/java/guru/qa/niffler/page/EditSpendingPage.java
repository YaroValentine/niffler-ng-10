package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage extends BasePage<EditSpendingPage> {
  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement saveBtn = $("#save");

  @Override
  public EditSpendingPage checkThatPageLoaded() {
    return null;
  }

  public EditSpendingPage setNewSpendingDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    return this;
  }

  public void save() {
    saveBtn.click();
  }
}
