package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent<Header> {

    private final SelenideElement mainPageBtn = $("a[href*='main']");

    private final SelenideElement
            friendsPageBtn = $("a[href*='friends']"),
            profileBtn = $(".header__avatar"),
            peopleBtn = $("a[href*='people']"),
            friendsBtn = $("a[href*='friends']");
    ;

    public Header() {
        super($(".header"));
    }

    public Header(SelenideElement self) {   // This example is useful for something dynamic, like a calendar
        super(self);                        // which could be located anywhere on the page.
    }

    @Override
    public Header checkThatComponentDisplayed() {
        self.$(".header__title").shouldHave(text("Niffler. The coin keeper."));
        return this;
    }

    @Step("Go to Fiends page")
    public FriendsPage goToFriendsPage() {
        friendsPageBtn.click();
        return new FriendsPage();
    }

    @Step("Go to Main page")
    public MainPage goToMainPage() {
        mainPageBtn.click();
        return new MainPage();
    }

    @Step("Go to Profile page")
    public ProfilePage goToProfilePage() {
        profileBtn.click();
        return new ProfilePage();
    }

    @Step("Go to People page")
    public PeoplePage goToPeoplePage() {
        peopleBtn.click();
        return new PeoplePage();
    }

}
