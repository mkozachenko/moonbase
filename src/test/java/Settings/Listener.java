package Settings;

import io.qameta.allure.*;
import net.lightbody.bmp.core.har.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.http.HttpStatus;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.logging.*;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.testng.*;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Listener extends BrowserSettings implements ITestListener{
    Set<Cookie> cookies;
    int testCntCurrent, testCntSum;

    private String getTestMethodName(ITestResult iTestResult) {
        return iTestResult.getMethod().getConstructorOrMethod().getName();
    }

    private Map<String, String> getUpdatedIssues(ITestContext iTestContext){
        Map <String, String> resultIssues = new HashMap<>();
        Method realMethod;
        Collection<ITestNGMethod> methods1 = iTestContext.getPassedTests().getAllMethods();
        for (ITestNGMethod testMethod : methods1) {
            String realMethodName = testMethod.getMethodName();
            try {
                realMethod = testMethod.getRealClass().getMethod(realMethodName);
                if (realMethod.isAnnotationPresent(Issue.class)) {
                    Issue issueNumber = realMethod.getAnnotation(Issue.class);
                    resultIssues.put(realMethodName, issueNumber.value());
                }
            } catch (NoSuchMethodException | NullPointerException noMethod){
                noMethod.printStackTrace();
            }
        }
        return resultIssues;
    }

    private Map<String, Integer> getErrorCodeConsole(){
        Map<String, Integer> errorCodes = new HashMap<>();
        Har testHar = new BrowserSettings().getProxyServer().getHar();
        try {
            for(HarEntry harEntry : testHar.getLog().getEntries()){
                String apiURL = harEntry.getRequest().getUrl();
                int responseCode = harEntry.getResponse().getStatus();
                if (responseCode >= HttpStatus.SC_BAD_REQUEST) {
                    errorCodes.put(apiURL, responseCode);
                }
            }
        } catch (NullPointerException np){
            np.printStackTrace();
        }
        return errorCodes;
    }

    private void setTestErrors(ITestResult tResult){
        Map<String, Integer> errCodes = getErrorCodeConsole();
        if(!errCodes.isEmpty()){
            Throwable throwable = tResult.getThrowable();
            StringBuilder newMessage = new StringBuilder(throwable.getMessage());
            newMessage.append("\n\n*****\nStatusCodes==>");
            for (Entry code : errCodes.entrySet()){
                newMessage.append("<<").append(code.getKey()).append(" |::| ").append(code.getValue()).append(">>");
            }
            newMessage.append("\n\n*****\n");
            try {
                FieldUtils.writeField(throwable, "detailMessage", newMessage.toString(), true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Attachment(value = "Скриншот", type = "image/png")
    private byte[] saveScreenshotAsFile(WebDriver driver, String screenshotName) {
        byte[] screenBytes = null;
        try {
            new Utility().scrollTo(Selectors.lastElement, -50);
            File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destination = new File(UserSettings.screenshotFolder + screenshotName + ".png");
            FileUtils.copyFile(screen, destination);
            screenBytes = Files.readAllBytes(destination.toPath());
            System.out.println("Скриншот сделан: "+destination.toURI().toString().replace("/", "///"));
        } catch (NoSuchElementException | UnhandledAlertException | IOException | NullPointerException | UnreachableBrowserException e) {
            //при открытом алерте скриншот можно сделать роботом, но он заскринит экран по факту, а не окно браузера
            System.err.println("При попытке сделать скриншот произошла ОШИБКА:\n"+e);
        }
        return screenBytes;
    }

    @Attachment(value = "Лог консоли браузера", type = "text/plain")
    private String webConsole(int logLevel, String testName) {
        String log = "";
        Predicate<LogEntry> filter = entry -> entry.getLevel().intValue() >= logLevel;
        LogEntries entries = getDriver().manage().logs().get(LogType.BROWSER);
        List<LogEntry> entryList = entries.getAll().stream().filter(filter).collect(Collectors.toList());
        switch (cap.getBrowserName()) {
            case "chrome":
                if (entryList.size() > 0) {
                    for(Object logEntry : entryList){
                        log = log+"\n"+logEntry.toString();
                    }
                }
                break;
            case "firefox":
                break;
            case "edge":
            case "MicrosoftEdge":
                break;
            default:
                break;
        }
        if(!log.equals("")) {
            new Utility().writeFileAppend(UserSettings.screenshotFolder + "WebConsole-"+testName+".log", log);
            System.out.println("Лог консоли браузера:\n" + log);
        }
        return log;
    }

    @Attachment(value = "Адрес страницы", type = "text/plain")
    public String getPageAddress(WebDriver driver) {
        String url;
        url = driver.getCurrentUrl();
        System.out.println("URL страницы сохранен");
        return url;
    }

    @Attachment(value = "Cookies", type = "text/plain")
    private String getCookies(String testName) {
        String cookie_="";
        cookies = getDriver().manage().getCookies();
        Iterator<Cookie> itr = cookies.iterator();
        while (itr.hasNext()) {
            Cookie cookie = itr.next();
            cookie_ = cookie_+"Domain: "+cookie.getDomain() + "\n" + "Path: "+cookie.getPath()
                    + "\n" + "Name: "+cookie.getName() + "\n" + "Value: "+cookie.getValue()
                    + "\n" + "Expires: "+cookie.getExpiry()+
                    "\n==============\n";
        }
        if(!cookie_.equals("")){
            new Utility().writeFileAppend(UserSettings.screenshotFolder+"Cookies-Persistent.log", "Test: "+testName+"\n"+cookie_+"\n[......]\n");
        }
        System.out.println("Cookies сохранены");
        return cookie_;
    }

    @Attachment(value = "Лог запросов API", type = "text/plain")
    private String apiLog(String testName, boolean filterByStatus, boolean filterByURL) {
        String fullLog = "", filteredLog = "";
        String apiURL;
        int responseCode;
        Har testHar = new BrowserSettings().getProxyServer().getHar();
        try {
            for(HarEntry harEntry : testHar.getLog().getEntries()){
                    apiURL = harEntry.getRequest().getUrl();
                    responseCode = harEntry.getResponse().getStatus();
                    if(apiURL.contains(runURL) && apiURL.contains("/api/")){
                        for (HarNameValuePair header : harEntry.getResponse().getHeaders()){
                            if(header.getName().equals("Date")){
                                filteredLog = filteredLog + "\n\n" +header.getValue();
                                fullLog = fullLog + "\n\n" +header.getValue();
                            }
                        }
                        fullLog = fullLog + "\n[" + responseCode + " - " + harEntry.getRequest().getMethod() + "] " + apiURL + "\nAPI RESPONSE:\n" + harEntry.getResponse().getContent().getText();
                        /*Фильтр по коду ответа*/
                        if (filterByStatus) {
                            if (responseCode != 200 && responseCode != 301 && responseCode != 204 && responseCode != 0) {
                                filteredLog = filteredLog + "\n[" + responseCode + " - " + harEntry.getRequest().getMethod() + "] " + apiURL + "\nAPI RESPONSE:\n" + harEntry.getResponse().getContent().getText();
                            }
                        }
                        /*Фильтр по API Endpoint*/
                        if (filterByURL) {
                            if (!apiURL.contains("/search/query")) {
                                filteredLog = filteredLog + "\n[" + responseCode + " - " + harEntry.getRequest().getMethod() + "] " + apiURL + "\nAPI RESPONSE:\n" + harEntry.getResponse().getContent().getText();
                            }
                        }
                    }
            }
        } catch (NullPointerException np){
            np.printStackTrace();
        }
        new Utility().writeFileAppend(UserSettings.screenshotFolder+"API-Persistent.log", "Test: "+testName+"\n"+fullLog+"\n[END OF LOG]\n[......]\n");
        return filteredLog;
    }

    @Override
    public void onStart(ITestContext iTestContext) {
        new UserSettings().getUserSettings();
        System.out.println("Запуск " + iTestContext.getName());
        iTestContext.setAttribute("WebDriver", getDriver());
        testCntSum = iTestContext.getSuite().getAllMethods().size();
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        Map<String, String> issuesMap;
        System.out.println("Прогон " + iTestContext.getName() + " закончен");
        issuesMap = getUpdatedIssues(iTestContext);
        for (Entry<String, String> entry : issuesMap.entrySet()){
            String entryResult = entry.getKey() + " <:::> " + entry.getValue();
            new Utility().writeFileAppend(UserSettings.screenshotFolder + "UpdatedIssues.log", entryResult);
        }
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
        testCntCurrent++;
        int cntFailed, cntSuccess;
        cntFailed = iTestResult.getTestContext().getFailedTests().size();
        cntSuccess = iTestResult.getTestContext().getPassedTests().size();
        System.out.println("Тест " + getTestMethodName(iTestResult) + " ("+testCntCurrent+ "/"+ testCntSum + ")" + " стартовал");
        System.out.println("Failed/Success rate: ("+cntFailed+ "/"+ cntSuccess + ")" + " of "+testCntSum);
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        System.out.println("[+++] -> Тест " +  getTestMethodName(iTestResult) + " прошел успешно");
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        System.err.println("[!!!] -> В тесте "+getTestMethodName(iTestResult) + " произошла ошибка!");
        try {
            setTestErrors(iTestResult);
        } catch (UnhandledAlertException | NullPointerException alEx) {
            System.err.println("Ошибка при сохранении данных о тесте: добавление кодов ошибок\n" + alEx.getMessage());
            alEx.printStackTrace();
        }
        try {
            webConsole(Level.SEVERE.intValue(), getTestMethodName(iTestResult));
        } catch (UnhandledAlertException | NullPointerException alEx) {
            System.err.println("Ошибка при сохранении данных о тесте: сохранение консоли браузера\n" + alEx.getMessage());
            alEx.printStackTrace();
        }
        try {
            saveScreenshotAsFile(getDriver(), getTestMethodName(iTestResult));
        } catch (UnhandledAlertException | NullPointerException alEx) {
            System.err.println("Ошибка при сохранении данных о тесте: сохранение скриншота\n" + alEx.getMessage());
            alEx.printStackTrace();
        }
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        System.err.println("[???] -> Тест "+getTestMethodName(iTestResult)+" пропущен");
//        driverLog(Level.ALL.intValue(), getTestMethodName(iTestResult));
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        System.out.println("[~~~] -> " + getTestMethodName(iTestResult));
    }

}
