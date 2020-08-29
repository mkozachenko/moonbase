package Settings;

import java.util.*;
import java.util.List;

import io.qameta.allure.*;
import net.lightbody.bmp.BrowserMobProxyServer;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.remote.*;
import org.testng.annotations.*;
import DriverFactory.*;
import org.testng.annotations.Optional;

import static Data.CommonData.*;

@Listeners({Listener.class})
public class BrowserSettings {

    public static Capabilities cap;
    private static final String defaultURL = siteDomains.productionMain,//Дефолтные переменные, если тесты запускаются не по строке из консоли или из консоли не все данные приходят
        defaultPlatform = "desktop", //android or desktop
        defaultBrowser= "chrome",   //chrome, edge, firefox
        defaultLanguage= "RUS",
        defaultHeadless = "false",
        defaultExplicit = "30",
        defaultImplicit = "15";
    public static String runURL, runPlatform, runBrowser, runLanguage;   //Переменные для запуска, куда вносятся либо данные из консоли, либо дефолтные
    public static boolean headlessToggle;
    public static int implicitWaitTimeout;
    public static long explicitWaitTimeout;
    private static ThreadLocal<WebDriver> driverLocal = new ThreadLocal<>();
    private static ThreadLocal<DriverManager> manLocal = new ThreadLocal<>();
    private static ThreadLocal<BrowserMobProxyServer> proxyLocal = new ThreadLocal<>();
    private static ThreadLocal<Proxy> proxyLocalSelenium = new ThreadLocal<>(), fiddlerProxy = new ThreadLocal<>();
    public static Map<String, String> allureEnvMap = new HashMap<>(){{
        put("URL", "[no data]");
        put("BranchVersion", "[no data]");
        put("Browser", "[no data]");
        put("Platform", "[no data]");
    }};

    @BeforeSuite(alwaysRun = true)
    @Parameters({"platform", "browser", "sitePage", "siteLanguage", "headlessMode", "waitImplicit", "waitExplicit"})
    public void initSetup(@Optional String platform, @Optional String browser, @Optional String page, @Optional String language, @Optional String headlessMode, @Optional String waitImplicit, @Optional String waitExplicit){
        /*Проверяем, что передаваемые переменные не null и в них передаются настоящие данные.
        Иначе подставляем дефолтные значения*/
        runPlatform = (platform != null)?(platform.equals("${platform}")?defaultPlatform:platform):defaultPlatform;
        runBrowser = (browser != null)?(browser.equals("${browser}")?defaultBrowser:browser):defaultBrowser;
        runURL = ((page !=null)?(page.equals("${site}")?defaultURL:page):defaultURL).replaceAll("/$", "");
        runLanguage = (language !=null)?(language.equals("${lang}")?defaultLanguage:language):defaultLanguage;
        headlessToggle = Boolean.parseBoolean((headlessMode !=null)?(headlessMode.equals("${headlessMode}")?defaultHeadless:headlessMode):defaultHeadless);
        implicitWaitTimeout = Integer.parseInt((waitImplicit !=null)?(waitImplicit.equals("${waitImplicit}")?defaultImplicit:waitImplicit):defaultImplicit);
        explicitWaitTimeout = Long.parseLong((waitExplicit !=null)?(waitExplicit.equals("${waitExplicit}")?defaultExplicit:waitExplicit):defaultExplicit);
        new UserSettings().getUserSettings();
        TextStrings.GetStrings(runLanguage);
        //удаление старых скриншотов из папки со скриншотами перед началом тестов
        String screenshots = UserSettings.screenshotFolder + "*.png", logs = UserSettings.screenshotFolder + "*.log";
        List<String> files = new ArrayList<>();
        files.add(screenshots);
        files.add(logs);
        if(UserSettings.clearScreenshots) {
            Utility.clearFiles(files);
        }
    }

    @AfterSuite
    public void report() {
        allureEnvMap.put("URL", runURL);
        allureEnvMap.put("BranchVersion", new Utility().getSiteVersion());
        allureEnvMap.put("Browser", cap.getBrowserName()+" v. "+cap.getVersion());
        allureEnvMap.put("Platform", System.getProperty("os.name").toLowerCase() + " (v. "+System.getProperty("os.version")+")");
        new UserSettings().setEnvironment(allureEnvMap);
//        Utility.allureReport(true, true, true);
    }

    @BeforeMethod(alwaysRun = true)
    public void driverRun(){
        Point browserPosition = new Point(2000, 1);
        new UserSettings().getUserSettings();
        List<String> chromeOptions = new ArrayList<>();
        chromeOptions.add("use-fake-ui-for-media-stream");
        chromeOptions.add("--ignore-certificate-errors");
        chromeOptions.add("enable-logging");
        proxyLocal.set(new ProxyServerBrowserMob().startProxy());
        proxyLocalSelenium.set(new ProxyServerBrowserMob().setSeleniumProxy(getProxyServer()));
//        chromeOptions.add("--proxy-server="+proxyLocalSelenium.get().getHttpProxy());
        manLocal.set(new DriverManagerFactory().getManager(runBrowser, chromeOptions, implicitWaitTimeout, browserPosition, headlessToggle, "--window-size=1920,1200", proxyLocalSelenium.get()));
//        fiddlerProxy.set(new Proxy().setHttpProxy("127.0.0.1:8888").setSslProxy("127.0.0.1:8888"));
        driverLocal.set(getManager().startDriver());
        getProxyServer().newHar("testHAR");
        cap = ((RemoteWebDriver) getDriver()).getCapabilities();
        getDriver().get(runURL);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        driverClose();
        new ProxyServerBrowserMob().stopBrowserMobProxy(getProxyServer());
    }

    @Step("Завершение работы")
    public void driverClose() {
        getManager().quitDriver();
    }

    public WebDriver getDriver(){
        return driverLocal.get();
    }

    public static DriverManager getManager(){
        return manLocal.get();
    }

    public BrowserMobProxyServer getProxyServer(){
        return proxyLocal.get();
    }
}
