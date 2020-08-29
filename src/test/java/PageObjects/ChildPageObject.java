package PageObjects;

import Settings.Selectors;
import Settings.Utility;
import Settings.WaitsAsserts;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.asserts.SoftAssert;

public class ChildPageObject extends MainPageObject {
    WaitsAsserts Waits = new WaitsAsserts();
    Settings.Selectors Selectors = new Selectors();
    Settings.Utility Utility = new Utility();
    private SoftAssert softAssertion;

    public ChildPageObject(SoftAssert sAssert){
        this.softAssertion = sAssert;
    }

    public ChildPageObject(){
    }

    By actionButton = By.xpath("//*[@class='site-header__logo']/a");
    By inputField = By.xpath("//*[@class='showContent']/a");
    By replyLabel = By.xpath("//*[@id='replyLabel']");


    @Step("Отправить текст")
    public void sendTextReply(String reply) {
        Selectors.selector(inputField).sendKeys(reply);
        Selectors.selector(actionButton).click();
    }

    @Step("Проверить текст ответа")
    public void assertReply(String reply) {
        softAssertion.assertEquals(Selectors.selector(replyLabel).getText(), reply, "Текст ответа неправильный");
        softAssertion.assertAll("Ошибки при отправке сообщения");
    }

}
