package DriverFactory;

import org.openqa.selenium.remote.RemoteWebDriver;

public abstract class DriverManager {
    protected RemoteWebDriver driver;
    protected abstract void startService();
    protected abstract void stopService();
    protected abstract void createDriver();

    public void quitDriver() {
        if (null != driver) {
            driver.quit();
            stopService();
            driver = null;
        }
    }

    public RemoteWebDriver startDriver() {
        if (null == driver) {
            startService();
            createDriver();
        }
        return driver;
    }


}
