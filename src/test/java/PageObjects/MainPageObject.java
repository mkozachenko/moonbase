package PageObjects;

import Settings.BrowserSettings;
import Settings.Selectors;
import Settings.Utility;
import Settings.WaitsAsserts;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.asserts.SoftAssert;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

import static Data.CommonData.textStringsMap;
import static Settings.WaitsAsserts.hardAssert;

public class MainPageObject extends BrowserSettings {
    WaitsAsserts Waits = new WaitsAsserts();
    Utility Utility = new Utility();
    Selectors Selectors = new Selectors();
    private SoftAssert softAssertion;

    public MainPageObject(SoftAssert sAssert) {
        this.softAssertion = sAssert;
    }

    public MainPageObject() {
    }

    By loginInput = By.xpath("//div[@id='auth-form-container']//form//input[@name='Login']");
    By passwordInput = By.xpath("//div[@id='auth-form-container']//form//input[@name='Password']");
    By loginSubmit = By.xpath("//div[@id='auth-form-container']//form//button[@type='submit']");
    By logoutButton = By.xpath("//*[@id='personal-menu']//li/a[@class='logout']");
    By upperMenuLink = By.xpath("//*[@class='uppermenu']/a");
    By upperMenuContent = By.xpath("//*[@class='showContent']/a");

    @Step("Логин")
    public void login(String login, String password) {
        Waits.waitForClickable(loginInput);
        Selectors.selector(loginInput).sendKeys(login);
        Selectors.selector(passwordInput).sendKeys(password);
        Selectors.selector(loginSubmit).click();
        Waits.waitForClickable(logoutButton);
    }

    @Step("Выход")
    public void logout() {
        Waits.waitForClickable(logoutButton);
        Selectors.selector(logoutButton).click();
        Waits.waitForClickable(loginInput);
    }

    @Step("Переход на главную страницу")
    public void openUpperMenu() {
        Selectors.selector(upperMenuLink).click();
        Waits.waitForClickable(upperMenuContent);
    }

    @Step("Перейти по ссылке")
    public void gotoLink(String url) {
        getDriver().get(url);
    }


}
