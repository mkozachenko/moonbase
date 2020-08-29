package DriverFactory;

import io.qameta.allure.*;
import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.logging.*;
import org.openqa.selenium.remote.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

import static Data.CommonData.resourcesPath;

/*UNDER CONSTRUCTION**/
public class FirefoxDriverManager extends DriverManager {
//    private FirefoxDriverService fxService;
    private GeckoDriverService fxService;
    private boolean fullscreen;
    private boolean headless;
    private long implicitWait;
    private HashMap<String, Object> firefoxProfile;
    private List<String> chromeOptions;
    private HashMap<String, Level> loggingPreferences;
    private Point coordinates;


    @Override
    public void startService() {
        if (null == fxService) {
            try {
                fxService = new GeckoDriverService.Builder()
                    .usingDriverExecutable(new File(resourcesPath+"geckodriver.exe"))
                    .usingAnyFreePort()
                    .build();
                fxService.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stopService() {
        if (null != fxService && fxService.isRunning())
            fxService.stop();
    }



    /**/

    @Override
    public void createDriver() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        FirefoxOptions fxOptions = new FirefoxOptions();
        FirefoxProfile fxProfile = new FirefoxProfile();
        LoggingPreferences logPrefs = new LoggingPreferences();
        /**setDriverLogs*/
        for(Map.Entry<String, Level> logPrefEntry : this.loggingPreferences.entrySet()){
            logPrefs.enable(logPrefEntry.getKey(), logPrefEntry.getValue());
        }
//        options.setCapability("goog:loggingPrefs", logPrefs);
        /**/
        /**setFirefoxProfile*/
        for(Map.Entry<String, Object> profile : this.firefoxProfile.entrySet()){
//            fxProfile.setPreference(profile.getKey(), profile.getValue());
        }
        /**/
        fxOptions.setProfile(fxProfile);
        driver = new FirefoxDriver(fxService, fxOptions);
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
        /**/
    }

    /**********/
    /**BUILDER*/
    /**********/

    private FirefoxDriverManager(DriverBuilder builder){

        this.implicitWait = builder.implicitWait;
        this.loggingPreferences = builder.loggingPreferences;

        this.coordinates = builder.coordinates;
        this.fullscreen = builder.fullscreen;
        this.headless = builder.headless;
    }

    public static class DriverBuilder{
        private boolean fullscreen;
        private boolean headless;
        private long implicitWait;
        private Point coordinates;
        private List<String> chromeOptions = new ArrayList<>();
        private HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        private HashMap<String, Level> loggingPreferences = new HashMap<>();

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

        public FirefoxDriverManager build(){
            return new FirefoxDriverManager(this);
        }
    }

}
