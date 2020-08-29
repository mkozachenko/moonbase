package Settings;

import net.lightbody.bmp.core.har.HarEntry;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.*;
import org.testng.asserts.*;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WaitsAsserts extends BrowserSettings {

    public static Assertion hardAssert = new Assertion();
    private static Selectors Selectors = new Selectors();
    private Duration duration = Duration.of(explicitWaitTimeout, ChronoUnit.SECONDS);

    public void waitForClickable(By selector) {
        getDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriver())
            .withTimeout(duration)
            .pollingEvery(Duration.ofSeconds(1))
            .ignoring(StaleElementReferenceException.class)
            .ignoring(NoSuchElementException.class)
            .ignoring(UnreachableBrowserException.class);
        wait.until(ExpectedConditions.elementToBeClickable(selector));
        getDriver().manage().timeouts().implicitlyWait(implicitWaitTimeout, TimeUnit.SECONDS);
    }
    public void waitForClickable(WebElement element) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    public void waitForClickable(By selector, int timeout) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.of(timeout, ChronoUnit.SECONDS));
        wait.until(ExpectedConditions.elementToBeClickable(selector));
    }

    public void waitForTextToBe(By selector, String newText) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.textToBe(selector, newText));
    }
    public void waitForTextToBe(WebElement element, String text) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    public void waitForTextChange(By selector, String oldText, int timeout) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.of(timeout, ChronoUnit.SECONDS));
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBe(selector, oldText)));
    }
    public void waitForTextChange(By selector, String oldText) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBe(selector, oldText)));
    }
    public void waitForTextChange(WebElement element, String oldText) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.refreshed(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(element, oldText))));
    }

    public void waitForStaleness(WebElement element) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.stalenessOf(element));
    }

    public void attributeToBe(By selector, String attribute, String value) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.attributeToBe(selector, attribute, value));
    }
    public void attributeToBe(WebElement element, String attribute, String value) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.attributeToBe(element, attribute, value));
    }
    public void attributeNotToBe(WebElement element, String attribute, String value) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.not(ExpectedConditions.attributeToBe(element, attribute, value)));
    }
    public void attributeNotToBe(By selector, String attribute, String value) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.not(ExpectedConditions.attributeToBe(selector, attribute, value)));
    }
    public void attributeToBeNotEmpty(WebElement element, String attribute) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.attributeToBeNotEmpty(element, attribute));
    }
    public void attributeContains(By selector, String attribute, String value) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.attributeContains(selector, attribute, value));
    }
    public void attributeContains(WebElement element, String attribute, String value) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.attributeContains(element, attribute, value));
    }
    public void attributeNotContains(By selector, String attribute, String value) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.not(ExpectedConditions.attributeContains(selector, attribute, value)));
    }
    public void attributeNotContains(WebElement element, String attribute, String value) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.not(ExpectedConditions.attributeContains(element, attribute, value)));
    }

    public void waitForUrlContains(String URL) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.urlContains(URL));
    }

    public void textToBePresentInElementLocated(By selector, String text) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(selector, text));
    }

    public void waitForVisibility(By selector, int timeout) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.of(timeout, ChronoUnit.SECONDS));
        wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
    }
    public void waitForVisibility(By selector) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
    }
    public void waitForVisibility(WebElement element) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void waitForNotVisible(By selector, int timeout) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.of(timeout, ChronoUnit.SECONDS));
        wait.until(ExpectedConditions.invisibilityOf(getDriver().findElement(selector)));
    }
    public void waitForNotVisible(By selector) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.invisibilityOf(getDriver().findElement(selector)));
    }
    public void waitForNotVisible(WebElement element, int timeout) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.of(timeout, ChronoUnit.SECONDS));
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    public void visibilityOfAllElementsLocatedBy(By selector) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(selector));
    }

    public void waitAlertIsPresent(int timeout) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.of(timeout, ChronoUnit.SECONDS));
        wait.until(ExpectedConditions.alertIsPresent());
    }

    public void waitForDisappearElementWithText(By selector, String text) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.invisibilityOfElementWithText(selector, text));
    }
    public void waitForDisappear(By selector, int timeout) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.of(timeout, ChronoUnit.SECONDS));
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(selector));
        }  catch (StaleElementReferenceException stale){
            System.err.println("Элемента "+selector+" больше нет на странице\n"+stale);
        }
    }
    public void waitForDisappear(By selector) {
        getDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriver())
            .withTimeout(duration)
            .pollingEvery(Duration.ofSeconds(1))
            .ignoring(StaleElementReferenceException.class)
            .ignoring(NoSuchElementException.class);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(selector));
        getDriver().manage().timeouts().implicitlyWait(implicitWaitTimeout, TimeUnit.SECONDS);
    }
    public void waitForDisappear(WebElement element) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.invisibilityOf(element));
    }
    public void waitForDisappear(WebElement element, int timeout) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.of(timeout, ChronoUnit.SECONDS));
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    public void waitForNumberOfElementsToBe(By selector, int count) {
        getDriver().manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.numberOfElementsToBe(selector, count));
        getDriver().manage().timeouts().implicitlyWait(implicitWaitTimeout, TimeUnit.SECONDS);
    }
    public void waitForNumberOfElementsMoreThan(By selector, int count) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(selector, count));
    }
    public void waitForNumberOfElementsLessThan(By selector, int count) {
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.numberOfElementsToBeLessThan(selector, count));
    }

    public void waitForAPIResponse(String requestRegex, String requestMethod, int pollingEvery) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriver())
            .withTimeout(duration)
            .pollingEvery(Duration.ofSeconds(pollingEvery));
        wait.until(expectedApi(requestRegex, requestMethod));
    }
    public void waitForAPIResponse(String requestRegex, String requestMethod) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriver())
            .withTimeout(duration)
            .pollingEvery(Duration.ofSeconds(1));
        wait.until(expectedApi(requestRegex, requestMethod));
    }



    public void fluentWaitToAppear(By selector) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriver())
            .withTimeout(Duration.ofSeconds(explicitWaitTimeout))
            .pollingEvery(Duration.ofSeconds(1))
            .ignoring(NullPointerException.class)
            .ignoring(NoSuchElementException.class);
        wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
    }
    public void fluentWaitToAppear(WebElement element) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriver())
            .withTimeout(Duration.ofSeconds(explicitWaitTimeout))
            .pollingEvery(Duration.ofSeconds(1))
            .ignoring(NullPointerException.class)
            .ignoring(NoSuchElementException.class);
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    public void fluentWaitToAppear(By selector, int pollingEvery) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriver())
                .withTimeout(Duration.ofSeconds(explicitWaitTimeout))
                .pollingEvery(Duration.ofSeconds(pollingEvery))
                .ignoring(NoSuchElementException.class);
        wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
    }
    public void fluentWaitToAppear(WebElement element, int pollingEvery) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriver())
                .withTimeout(Duration.ofSeconds(explicitWaitTimeout))
                .pollingEvery(Duration.ofSeconds(pollingEvery))
                .ignoring(NoSuchElementException.class);
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void waitForFrameToLoad(By selector){
        WebDriverWait wait = new WebDriverWait(getDriver(), duration);
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(selector));
    }

    public void waitForFilePresent(File file, int timeout){
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.of(timeout, ChronoUnit.SECONDS));
        wait.until((ExpectedCondition<Boolean>) webDriver -> file.exists());
    }

    public void sleep(long millisToWait) {
        try {
            Thread.sleep(millisToWait);
        } catch (InterruptedException e) {
            System.err.println("Ожидание было прервано: \n"+e);
        }
    }

    public boolean assertTitlePage(String title){
        return getDriver().getTitle().equals(title);
    }




    //<editor-fold desc="Custom Waits">

    public static ExpectedCondition<Boolean> expectedApi(final String requestRegex, final String requestMethod){
        return new ExpectedCondition<Boolean>() {
            boolean url, method;
            int responseCode;
            List<HarEntry> harList;
            @Override
            public Boolean apply(WebDriver driver) {
                harList = new BrowserSettings().getProxyServer().getHar().getLog().getEntries();
                for (HarEntry harEntry : harList) {
                    url = harEntry.getRequest().getUrl().matches(requestRegex);
                    method = harEntry.getRequest().getMethod().toUpperCase().equals(requestMethod.toUpperCase());
                    responseCode = harEntry.getResponse().getStatus();
                    if (harEntry.getRequest().getUrl().matches(requestRegex) &&
                        harEntry.getRequest().getMethod().toUpperCase().equals(requestMethod.toUpperCase()) &&
                        harEntry.getResponse().getStatus() != 0) {
                        return Boolean.TRUE;
                    }
                }
                return Boolean.FALSE;
            }

            @Override
            public String toString() {
                return String.format("API Response \"%s\" with method \"%s\"", requestRegex, requestMethod);
            }
        };
    }


    //</editor-fold>
}
