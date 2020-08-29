package DriverFactory;

import io.qameta.allure.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.logging.*;
import org.openqa.selenium.remote.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

import static Data.CommonData.resourcesPath;

public class ChromeDriverManager extends DriverManager {
    private ChromeDriverService chService;
    private boolean fullscreen;
    private boolean headless;
    private long implicitWait;
    private HashMap<String, Object> chromePrefs;
    private List<String> chromeOptions;
    private HashMap<String, Level> loggingPreferences;
    private Point coordinates;
    private Proxy proxy;

    @Override
    public void startService() {
        if (null == chService) {
            try {
                chService = new ChromeDriverService.Builder()
                    .usingDriverExecutable(new File(resourcesPath+"chromedriver.exe"))
                    .usingAnyFreePort()
                    .build();
                chService.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stopService() {
        if (null != chService && chService.isRunning()) {
            chService.stop();
        }
    }

    @Override
    public void createDriver() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logPrefs = new LoggingPreferences();
        HashMap<String, Object> chromePreferences = new HashMap<String, Object>();
        /**setDriverLogs*/
        for(Map.Entry<String, Level> logPrefEntry : this.loggingPreferences.entrySet()){
            logPrefs.enable(logPrefEntry.getKey(), logPrefEntry.getValue());
        }
        options.setCapability("goog:loggingPrefs", logPrefs);
        /**/
        /**setChromePreferences*/
        for(Map.Entry<String, Object> prefEntry : this.chromePrefs.entrySet()){
            chromePreferences.put(prefEntry.getKey(), prefEntry.getValue());
        }
        options.setExperimentalOption("prefs", chromePreferences);
        /**/
        /**addChromeOptionsArguments*/
        for(String chromeOption : this.chromeOptions){
            options.addArguments(chromeOption);
        }
        /**/
        /**setHeadless*/
        options.setHeadless(this.headless);
        /**/
        /**withProxy*/
        capabilities.setCapability(CapabilityType.PROXY, this.proxy);
        /**/
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        options.merge(capabilities);
        driver = new ChromeDriver(chService, options);
        /**setTimeout*/
        driver.manage().timeouts().implicitlyWait(this.implicitWait, TimeUnit.SECONDS);
        /**/
        /**setBrowserPosition*/
        if(!this.headless){
            driver.manage().window().setPosition(this.coordinates);
        }
        /**/
        /**setFullscreen*/
        if(this.fullscreen){
            driver.manage().window().maximize();
        }
        driver.manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS);
        /**/
    }

    @Step("Билд локального сервиса Appium")
    private static void buildAppium() {
//        AppiumServiceBuilder builder;
//        //Set Capabilities
//        dc = new DesiredCapabilities();
//        //This Flag is used when we don't want to reset app state between sessions (Android: don’t uninstall app before new session). Its default Value is False
//        dc.setCapability("noReset", "false");
//        builder = new AppiumServiceBuilder();
//        builder.withIPAddress(host);
//        builder.usingPort(port);
//        builder.withCapabilities(dc);
//        builder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);
//        builder.withArgument(GeneralServerFlag.LOG_LEVEL, "error");
//        service = AppiumDriverLocalService.buildService(builder);
//        System.out.println("APPIUM built");
    }

    /**********/
    /**BUILDER*/
    /**********/

    private ChromeDriverManager(DriverBuilder builder) {
        this.chromeOptions = builder.chromeOptions;
        this.chromePrefs = builder.chromePrefs;
        this.implicitWait = builder.implicitWait;
        this.loggingPreferences = builder.loggingPreferences;
        this.coordinates = builder.coordinates;
        this.fullscreen = builder.fullscreen;
        this.headless = builder.headless;
        this.proxy = builder.proxy;
    }

    public static class DriverBuilder {
        private boolean fullscreen;
        private boolean headless;
        private long implicitWait;
        private Point coordinates;
        private List<String> chromeOptions = new ArrayList<>();
        private HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        private HashMap<String, Level> loggingPreferences = new HashMap<>();
        private Proxy proxy;

        public DriverBuilder setFullscreen(){
            this.fullscreen = true;
            return this;
        }

        public DriverBuilder setHeadless(String resolution) {
            String res;
            this.headless = true;
            res = (resolution != null)?resolution:"";
            this.chromeOptions.add(res);
            return this;
        }

        public DriverBuilder setTimeout(long time){
            this.implicitWait = time;
            return this;
        }

        public DriverBuilder setDriverLogs(HashMap<String, Level> logging) {
            this.loggingPreferences.putAll(logging);
            return this;
        }

        public DriverBuilder setChromePreferences(HashMap<String, Object> prefs) {
            this.chromePrefs.putAll(prefs);
            return this;
        }

        public DriverBuilder setChromeOptionsArguments(List<String> args) {
            this.chromeOptions.addAll(args);
            return this;
        }

        public DriverBuilder setBrowserPosition(Point coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public DriverBuilder withProxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public ChromeDriverManager build(){
            return new ChromeDriverManager(this);
        }
    }

}
