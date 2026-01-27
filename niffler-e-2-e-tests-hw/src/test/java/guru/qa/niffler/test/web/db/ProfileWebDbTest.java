package guru.qa.niffler.test.web.db;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.UserDb;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class ProfileWebDbTest {

  private static final Config CFG = Config.getInstance();

  @UserDb(
      username = "yaro",
      categories = @Category(
          archived = true
      )
  )
  @Test
  void archivedCategoryShouldPresentInCategoriesList(CategoryJson category) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .login("yaro", "secret")
        .mainPage().checkThatPageLoaded()
        .profilePage().open()
        .clickShowArchivedCategories()
        .verifyCategoryExists(category.name());
  }

  @UserDb(
      username = "yaro",
      categories = @Category(
          archived = false
      )
  )
  @Test
  void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .login("yaro", "secret")
        .mainPage().checkThatPageLoaded()
        .profilePage().open()
        .clickShowArchivedCategories()
        .verifyCategoryExists(category.name());
  }


}
