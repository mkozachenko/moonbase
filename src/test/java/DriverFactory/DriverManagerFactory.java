package DriverFactory;

import Settings.UserSettings;
import org.openqa.selenium.Point;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.logging.LogType;

import java.util.*;
import java.util.logging.Level;

public class DriverManagerFactory {

    public DriverManager getManager(String driverType, List<String> options, int implicitWait, Point browserPosition, boolean headless, String headlessResolution, Proxy proxyServer) {
        DriverManager driverManager;
        ChromeDriverManager.DriverBuilder builder = new ChromeDriverManager.DriverBuilder();
        HashMap<String, Level> loggingPreferences = new HashMap<>();
        loggingPreferences.put(LogType.BROWSER, Level.ALL);
        loggingPreferences.put(LogType.PERFORMANCE, Level.ALL);
        loggingPreferences.put(LogType.DRIVER, Level.ALL);

        /**Наполнение билдера*/
        builder = builder.setDriverLogs(loggingPreferences).setTimeout(implicitWait);
        switch(driverType){
            case "chrome":
            default:
                HashMap<String, Object> chromePrefsMap = new HashMap<>();
                chromePrefsMap.put("download.default_directory", UserSettings.downloadPath);
                builder = builder.setChromePreferences(chromePrefsMap).setChromeOptionsArguments(options);
                if(headless){
                    builder = builder.setHeadless(headlessResolution);
                } else {
                    builder = builder.setBrowserPosition(browserPosition).setFullscreen();
                }
                if(proxyServer != null){
                    builder = builder.withProxy(proxyServer);
                }
                break;
            case "firefox":
                HashMap<String, Object> firefoxProfile = new HashMap<>();
                firefoxProfile.put("browser.download.folderList", 2); //загружать в директорию, указанную пользователем
                firefoxProfile.put("browser.download.dir", UserSettings.downloadPath); //изменение директории для загрузки файлов
                firefoxProfile.put("browser.helperApps.neverAsk.saveToDisk", "text/csv, application/vnd.ms-excel, application/octet-stream, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/excel"); //не отображать диалог подтверждения загрузки для этих типов файлов
//                if (UserSettings.useProxy) {
//                    firefoxProfile.put("network.proxy.type", 1);
//                    firefoxProfile.put("network.proxy.http", proxyIP);
//                    firefoxProfile.put("network.proxy.socks", proxyIP);
//                    firefoxProfile.put("network.proxy.http_port", proxyPort);
//                    firefoxProfile.put("network.proxy.socks_port", proxyPort);
//                }
                if(headless){
                    builder = builder.setHeadless(headlessResolution);
                } else {
                    builder = builder.setBrowserPosition(browserPosition).setFullscreen();
                }
                break;
            case "safari":
                //   driverManager = new SafariDriverManager();
                break;
        }

        /**********/

        driverManager = builder.build();
        return driverManager;
    }
}
