package Settings;

import com.jayway.jsonpath.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.lightbody.bmp.core.har.*;
import org.openqa.selenium.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.*;
import java.text.*;
import java.util.*;

import javax.mail.*;
import javax.mail.search.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;

import Data.CommonData;
import org.openqa.selenium.NoSuchElementException;

import static Data.CommonData.*;
import static Settings.WaitsAsserts.hardAssert;
import static org.apache.commons.lang.StringEscapeUtils.unescapeHtml;

public class Utility extends Selectors{
    WaitsAsserts Waits = new WaitsAsserts();
    Selectors Selectors = new Selectors();

    public void alertAccept() {
        Waits.waitAlertIsPresent(10);
        Alert alert = getDriver().switchTo().alert();
        alert.accept();
    }

    public void alertDismiss() {
        Waits.waitAlertIsPresent(25);
        Alert alert = getDriver().switchTo().alert();
        alert.dismiss();
    }

    public void switchToiFrame(WebElement element){
        getDriver().switchTo().frame(element);
    }

    public void switchToDefaultContent(){
        getDriver().switchTo().defaultContent();
    }

    public int returnCountHandles(){
        ArrayList<String> tabs = new ArrayList<>(getDriver().getWindowHandles());
        return tabs.size();
    }

    public void alertAssertText(String s) {
        String alText;
        Waits.waitAlertIsPresent(25);
        try {
            Alert alert = getDriver().switchTo().alert();
            alText = alert.getText();
//            alert.accept(); //Закрываем алерт перед проверкой, так как при открытом алерте невозможно сделать скриншот
            hardAssert.assertEquals(alText, s, "Текст в алерте неверный!");
            alert.accept();
        } catch (NoAlertPresentException e) {
            System.err.print("Алерта не обнаружено:\n"+ e);
        }
    }

    public void assertTitlePage(String title){
        String titlePage = getDriver().getTitle();
        hardAssert.assertEquals(titlePage, title, "Тайтл не совпадает");
    }

    public void openNewTab(String url){
        ((JavascriptExecutor) getDriver()).executeScript("window.open();");
        ArrayList<String> tabs = new ArrayList<>(getDriver().getWindowHandles());
        getDriver().switchTo().window(tabs.get(tabs.size()-1));
        getDriver().get(url);
    }

    public void switchTab(int index) {
        ArrayList<String> tabs = new ArrayList<>(getDriver().getWindowHandles());
        getDriver().switchTo().window(tabs.get(index));
    }

    public void switchTab(){
        ArrayList<String> tabs = new ArrayList<>(getDriver().getWindowHandles());
        int sizeTabs = tabs.size();
        getDriver().switchTo().window(tabs.get(sizeTabs - 1));
    }

    public static void assertDownloadName(String downloadPartialName, String format){
        filename = null;
        int cnt =0;
        File file, dir = new File(UserSettings.downloadPath);
        FilenameFilter filter = new FilenameFilter(){public boolean accept(File dir, String name) {return name.matches(".*"+downloadPartialName+".*/."+format);}};
        String[] children;
        while(filename == null && cnt<30){
            children = dir.list(filter);
            if (children != null && children.length > 0) {
                for (int i = 0; i < children.length; i++) {
                    file = new File(UserSettings.downloadPath + children[i]);
                    if (file.lastModified() > (System.currentTimeMillis() - 60 * 1000)) {
                        filename = children[i];
                    }
                }
            }
            new WaitsAsserts().sleep(1000);
            cnt++;
        }
        hardAssert.assertNotNull(filename, "Файл " + downloadPartialName + "[...]." + format + " не найден в папке "+ UserSettings.downloadPath);
    }

    public static void assertDownloadSize(String filepath, int lowBound, int highBound){
        int size_b, size_kb, size_mb;
        File file = new File (UserSettings.downloadPath+filename);
        size_b = (int)file.length();
        size_kb = (int)Math.ceil(size_b/1024);
        hardAssert.assertTrue(size_kb>=lowBound, "Файл "+filepath+" меньше, чем "+lowBound+" кб\nРазмер: "+size_b+" байт\n"+size_kb+" килобайт\n");
        hardAssert.assertTrue(size_kb<=highBound, "Файл "+filepath+" больше, чем "+highBound+" кб\nРазмер: "+size_b+" байт\n"+size_kb+" килобайт\n");
    }

    public static void allureReport(boolean generate, boolean open, boolean delete) {
        ProcessBuilder pb = new ProcessBuilder();
        Process process;
        List<String> resultsDelete = new ArrayList<>();
        File allureEnv = new File("environment.properties"),
            allureCategories = new File("categories.json");
        String resultRoot = UserSettings.allureFolder+"results\\",
                reportRoot = UserSettings.allureFolder+"reports\\",
                reportFolder = CommonData.date+"/"+CommonData.time;
        String[] copyEnvironment = {"cmd", "/c", "copy", "/y", allureEnv.getAbsolutePath(), resultRoot};
        String[] copyCategories = {"cmd", "/c", "copy", "/y", allureCategories.getAbsolutePath(), resultRoot};
        String[] copyHistory = {"cmd", "/c", "xcopy", "/y", reportRoot+reportFolder+"\\history", resultRoot+"history\\"};
        String[] reportGenerate = {"cmd", "/c", "allure", "generate", "-c", resultRoot, "--output", reportRoot+reportFolder};
        String[] reportOpen = {"cmd", "/c", "allure", "open", reportRoot+reportFolder};
        try {
            process = pb.command(copyEnvironment).start();
            process.waitFor();
            process.destroy();
            process = pb.command(copyCategories).start();
            process.waitFor();
            process.destroy();
            if (generate){  //формирование отчета
                process = pb.command(reportGenerate).start();
                process.waitFor();
                process.destroy();
                /*перенос истории тестов в папку результатов*/
                process = pb.command(copyHistory).start();
                process.waitFor();
                process.destroy();
                System.out.println("Отчет сформирован и сохранен в папке: "+reportRoot+reportFolder);
            }
            if(delete){ //очистка папки с результатами
                resultsDelete.add(resultRoot+"*.json");
                resultsDelete.add(resultRoot+"*-attachment");
                clearFiles(resultsDelete);
            }
            if (open){  //открытие сгенерированного отчета
                pb.command(reportOpen).start();
            }
        } catch (IOException | InterruptedException exp) {
            exp.printStackTrace();
        }
    }

    public static void clearFiles(List arg){
        ProcessBuilder pb = new ProcessBuilder();
        Process process;
        List<String> delete = new ArrayList<>();
        delete.add("cmd");
        delete.add("/c");
        delete.add("del /q");
        delete.addAll(arg);
        System.out.println(delete.toString());
        try {
            process = pb.command(delete).start();
            cmdReader(process);
            process.waitFor();
            process.destroy();
        } catch (IOException | InterruptedException exp) {
            exp.printStackTrace();
        }
    }

    public static void adb_dev(){
        ProcessBuilder pb = new ProcessBuilder();
        Process process;
        String[] probe = {"cmd", "/c", "adb", "devices"};
        try {
            pb.command(probe).start();
            process = pb.command(probe).start();
            cmdReader(process);
            process.waitFor();
            process.destroy();
            System.out.println("ADB started");
        } catch (IOException | InterruptedException exp) {
            exp.printStackTrace();
        }
    }

    private static void cmdReader(Process process) throws IOException {
        String s;
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        System.out.println("Сообщение из командной строки:\n");
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
    }

    public void scrollTo(WebElement element, int adjust){
        try {
            if(element!=null && element.isDisplayed()){
                ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
                ((JavascriptExecutor) getDriver()).executeScript("window.scrollBy(0, "+adjust+");");
                ((JavascriptExecutor) getDriver()).executeScript("arguments[0].style.outline='3px dashed red'", element);
                new WaitsAsserts().sleep(500);
            } else {System.err.println("Нельзя проскроллить к элементу");}
        } catch (StaleElementReferenceException | NullPointerException | NoSuchSessionException | UnhandledAlertException e) {
            System.err.println("Произошла ошибка:\n"+e);
        }
    }

    public int parseInteger(By element){
        return Integer.parseInt(new Selectors().selector(element).getText().replaceAll("\\D+", ""));
    }
    public int parseInteger(String line){
        return Integer.parseInt(line.replaceAll("\\D+", ""));
    }

    public float parseFloat(String text){
        return Float.parseFloat(text.replaceAll("[^\\d+\\.\\,]", ""));
    }
    public float parseFloat(By locator){
        return Float.parseFloat(new Selectors().selector(locator).getText().replaceAll("[^\\d+\\.\\,]", ""));
    }
    public float parseFloat(WebElement element){
        return Float.parseFloat(element.getText().replaceAll("[^\\d+\\.\\,]", ""));
    }

    public double parseDouble(String text){
        return Double.parseDouble(text.replaceAll("[^\\d+\\.\\,]", ""));
    }
    public double parseDouble(By locator){
        return Double.parseDouble(new Selectors().selector(locator).getText().replaceAll("[^\\d+\\.\\,]", ""));
    }
    public double parseDouble(WebElement element){
        return Double.parseDouble(element.getText().replaceAll("[^\\d+\\.\\,]", ""));
    }

    public double roundPrice(double input, int precision){
        double result, tmp, factor = Math.pow(10, precision);
        result = input * factor;
        tmp = Math.round(result);
        result = tmp / factor;
        return result;
    }

    public boolean checkPictureDisplay(WebElement picture){
        //Вебэлементы должны указывать на сам  img, а не один из родительских элементов
        boolean imageDisplay = false;
        try {
            imageDisplay = (Boolean) ((JavascriptExecutor) getDriver()).executeScript("return arguments[0].complete && "+
                "typeof arguments[0].naturalWidth != \"undefined\" && "+
                "arguments[0].naturalWidth > 0", picture);
        } catch (StaleElementReferenceException | NullPointerException | NoSuchSessionException | JavascriptException | UnhandledAlertException e) {
            System.err.println("Произошла ошибка:\n"+e);
        }
        return imageDisplay;
    }

    private static String convertDate(String incorrectDate){
        StringBuffer dateForTimestamp = new StringBuffer();
        List<String> items = null;
        items = new ArrayList<>(Arrays.asList(incorrectDate.split(" ", 0)));
        items.remove(4);
        for (String i : items) {
            dateForTimestamp.append(i + " ");
//            dateForTimestamp.append("\t");
        }
        dateForTimestamp.deleteCharAt(24);
        return dateForTimestamp.toString();
    }

    public static String nowTime(String formatDate, String locale){
        SimpleDateFormat formatter = new SimpleDateFormat(formatDate, new Locale(locale));
        Date date = new Date();
        return formatter.format(date.getTime());
    }

    public static boolean compareDate(String formatDate, String locale, String systemTime, String mailTime) {
        //Сравнение дат. True = если systemTime < mailTime. False = если systemTime >= mailTime
        boolean result;
        Date date1 = null;
        Date date2 = null;
        SimpleDateFormat formatter = new SimpleDateFormat(formatDate, new Locale(locale));
        try {
            date1 = formatter.parse(systemTime);
            date2 = formatter.parse(mailTime);
        }catch (ParseException e){
            System.err.println(e);
        }
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        result = cal1.before(cal2) || cal1.compareTo(cal2) == 0;
        return result;
    }

    private static Store connectToMail(String mail, String password){
        Store store = null;
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(props, null);
        try {
            store = session.getStore("imaps");
        } catch (NoSuchProviderException e) {
            System.err.println(e);
        }
        try {
            if (store != null) {
                store.connect("imap.gmail.com", mail, password);
            }else{
                System.err.println("Неверный протокол");
            }
        } catch (MessagingException e) {
            System.err.println(e);
        }
        return store;
    }

    public Message getMessage(String mail, String password, String folderName, boolean read, String subject, String systemTimeStamp){
        Message[] messages;
        String convertD;
        boolean isMailFound = false;
        int sleepTime = 0;
        try{
            while (sleepTime < 22) {
                messages = getMessages(connectToMail(mail, password), folderName, read);
                for (Message message : messages) {
                    convertD = convertDate(message.getSentDate().toString());
                    if (message.getSubject().contains(subject) && compareDate("E MMM dd HH:mm:ss yyyy", "en", systemTimeStamp, convertD)) {
                        return message;
                    }
                }
                sleepTime += 1;
                Waits.sleep(10000);
            }
            hardAssert.assertTrue(isMailFound, "Сообщение не найдено: " + subject);
        }catch (MessagingException e){
            System.err.println(e);
        }
        return null;
    }

    private static Message[] getMessages(Store connect, String folderName, boolean read){
        Folder folder;
        Message[] messages = null;
        try {
            folder = connect.getFolder(folderName);
            folder.open(Folder.READ_WRITE);
                messages = folder.search(new FlagTerm(new Flags(
                        Flags.Flag.SEEN), read));
        } catch (MessagingException e) {
            System.err.println(e);
        }
        return messages;
    }

    public void cleanUpMail(String mail, String password, String folderName, boolean read) {
        Store connect;
        Message[] messages;
        connect = connectToMail(mail, password);
        messages = getMessages(connect, folderName, read);
        for (Message message : messages) {
            try {
                message.setFlag(Flags.Flag.DELETED, true);
            } catch (MessagingException e) {
                System.err.println(e);
            }
        }
    }

    public String getLinkFromMessage(Message message){
        String text = null;
        String line;
        Pattern linkPattern;
        Matcher pageMatcher;
        StringBuilder buffer = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(message.getInputStream()));
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            ArrayList<String> links = new ArrayList<>();
            String registrationURL = buffer.toString();

            linkPattern = Pattern.compile("<a.*?href=['\\\"](.*?)['\\\"].*?>");
            pageMatcher = linkPattern.matcher(registrationURL);
            while (pageMatcher.find()) {
                links.add(pageMatcher.group(0));
            }
            text = unescapeHtml(links.get(0).substring(8).replaceAll("<", "").replaceAll(">", "").replaceAll("\"", ""));
        }catch (MessagingException | IOException e){
            System.err.println(e);
        }
        return text;
    }

    public void writeFileAppend(String filePath, Object input){
        BufferedWriter logFile = null;
        try {
            logFile = new BufferedWriter(new FileWriter(filePath, true));
            logFile.write(input.toString());
            logFile.newLine();
            logFile.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (logFile != null) try {
                logFile.close();
            } catch (IOException ioe2) {
                // just ignore it
            }
        }
    }

    public Object getRandomElement(List list){
        Object retValue = null;
        if (list.size() > 0){
            retValue = list.get(new Random().nextInt(list.size()));
        }
        return retValue;
    }

    public String getApiResponse(String requestURL, String requestMethod){
        boolean url, method;
        int attempt = 0, responseCode, noRespCnt = 1;
        List<String> responses = new ArrayList<>();
        List<HarEntry> harList;
        while (noRespCnt != 0 && attempt < 60) {
            noRespCnt = 0;
            harList = new BrowserSettings().getProxyServer().getHar().getLog().getEntries();
            for (HarEntry harEntry : harList) {
                if (harEntry.getResponse().getStatus() == 0) {
                    noRespCnt++;
                }
                url = harEntry.getRequest().getUrl().equals(requestURL);
                method = harEntry.getRequest().getMethod().toUpperCase().equals(requestMethod.toUpperCase());
                responseCode = harEntry.getResponse().getStatus();
                if(url && method && responseCode != 0){
                    responses.add(harEntry.getResponse().getContent().getText());
                }
            }
            attempt++;
            Waits.sleep(1000);
        }
        hardAssert.assertNotEquals(responses.size(), 0, "Не удалось получить тело API запроса "+requestURL);
        return responses.get(responses.size()-1);
    }

    public String parseJSON(String input, String query) {
        String output = null;
        DocumentContext jsonContext;
        try {
            jsonContext = JsonPath.parse(input);
            output = jsonContext.read(query).toString();
        } catch (PathNotFoundException | NullPointerException nullEx){
            nullEx.printStackTrace();
        }
        return output;
    }

    public String parseRegex(String input, String query){
        String result = "NO RESULT";
        Pattern pattern = Pattern.compile(query);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find())
        {
            result = matcher.group(1);
        }
        return result;
    }

    public boolean isLetter(String name) {
        char[] chars = name.toCharArray();
        for (char c : chars) {
            if(Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDigit(String name) {
        char[] chars = name.toCharArray();
        for (char c : chars) {
            if(Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    public void clearInput(WebElement element){
        element.click();
        element.sendKeys(Keys.CONTROL+"a");
        element.sendKeys(Keys.DELETE);
    }

    public Object getClipboardContent(DataFlavor flavor){
        Object clipboardData = null;
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            clipboardData = cb.getData(flavor);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
        return clipboardData;
    }

    public boolean assertPresent(By selector){
        boolean isPresent;
        getDriver().manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        try{
            isPresent = !Selectors.multiSelector(selector).isEmpty() && Selectors.multiSelector(selector).get(0).isDisplayed();
        } catch(NoSuchElementException | StaleElementReferenceException | NullPointerException | IndexOutOfBoundsException ex){
            isPresent = false;
        }
        getDriver().manage().timeouts().implicitlyWait(implicitWaitTimeout, TimeUnit.SECONDS);
        return isPresent;
    }
    public boolean assertPresent(By parentSelector, By childSelector){
        boolean ret;
        getDriver().manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        try{
            ret = !Selectors.multiSelector(parentSelector, childSelector).isEmpty() && Selectors.multiSelector(parentSelector, childSelector).get(0).isDisplayed();
        } catch(NoSuchElementException | StaleElementReferenceException | NullPointerException ex){
            ret=false;
        }
        getDriver().manage().timeouts().implicitlyWait(implicitWaitTimeout, TimeUnit.SECONDS);
        return ret;
    }
    public boolean assertPresent(By selector, int duration){
        boolean ret = true;
        getDriver().manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        try{
            Waits.waitForVisibility(selector, duration);
        } catch(NoSuchElementException | StaleElementReferenceException | NullPointerException | TimeoutException ex){
            ret=false;
        }
        getDriver().manage().timeouts().implicitlyWait(implicitWaitTimeout, TimeUnit.SECONDS);
        return ret;
    }
    public boolean assertPresent(WebElement element){
        boolean ret=true;
        try{
            element.isDisplayed();
        } catch(NoSuchElementException | StaleElementReferenceException | NullPointerException ex){
            ret = false;
        }
        return ret;
    }
    public boolean assertPresent(WebElement parentElement, By childSelector){
        boolean ret;
        getDriver().manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        try{
            ret = !Selectors.multiSelector(parentElement, childSelector).isEmpty() && Selectors.multiSelector(parentElement, childSelector).get(0).isDisplayed();
        } catch(NoSuchElementException | StaleElementReferenceException | NullPointerException ex){
            ret=false;
        }
        getDriver().manage().timeouts().implicitlyWait(implicitWaitTimeout, TimeUnit.SECONDS);
        return ret;
    }

    public boolean assertNotPresent(By selector){
        boolean ret;
        getDriver().manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        try{
            ret = Selectors.multiSelector(selector).isEmpty();
        } catch(NoSuchElementException | StaleElementReferenceException | NullPointerException ex){
            ret=false;
        }
        getDriver().manage().timeouts().implicitlyWait(implicitWaitTimeout, TimeUnit.SECONDS);
        return ret;
    }
    public boolean assertNotPresent(By parentSelector, By childSelector){
        boolean ret;
        getDriver().manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        try{
            ret = Selectors.multiSelector(parentSelector, childSelector).isEmpty();
        } catch(NoSuchElementException | StaleElementReferenceException | NullPointerException ex){
            ret=false;
        }
        getDriver().manage().timeouts().implicitlyWait(implicitWaitTimeout, TimeUnit.SECONDS);
        return ret;
    }
    public boolean assertNotPresent(WebElement parentElement, By childSelector){
        boolean ret;
        getDriver().manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        try{
            ret = Selectors.multiSelector(parentElement, childSelector).isEmpty();
        } catch(NoSuchElementException | StaleElementReferenceException | NullPointerException ex){
            ret=false;
        }
        getDriver().manage().timeouts().implicitlyWait(implicitWaitTimeout, TimeUnit.SECONDS);
        return ret;
    }
    public boolean assertNotPresent(WebElement element){
        /*Для этого метода будет задержка, есть передавать в него элемент, найденный селектором, потому что селектор всегда учитывает implicitlyWait*/
        boolean ret=false;
        try{
            element.isDisplayed();
        } catch(NoSuchElementException | StaleElementReferenceException | NullPointerException ex){
            ret = true;
        }
        return ret;
    }

    public void setCookie(String name, String value){
        Cookie cookieFound = null;
        try {
            getDriver().manage().addCookie(new Cookie(name, value));
        } catch (UnableToSetCookieException | InvalidCookieDomainException ckExc){
            System.err.println("--->!!!!! Error while adding cookie: ");
            ckExc.printStackTrace();
        } finally {
            for(Cookie cookie : getDriver().manage().getCookies()){
                if(cookie.getName().equals(name))
                {
                    cookieFound = cookie;
                    break;
                }
            }
        }
        if (cookieFound != null){
            System.out.println("---> +++ Cookie added:\n[<"+cookieFound.getName()+">;<"+cookieFound.getValue()+">;<"+cookieFound.getDomain()+">"+"]");
        } else {
            System.out.println("---> !!! Cookie not added: ["+name+" : "+value+"]");
        }
    }

    public Map<String, String> getCookie(String name){
        Map <String, String> cookie = new HashMap<>();
        cookie.put("DEFAULT_KEY", "DEFAULT_VALUE");
        for(Cookie availableCookie : getDriver().manage().getCookies()){
            if(availableCookie.getName().equalsIgnoreCase(name)){
                cookie.put(availableCookie.getName(), availableCookie.getValue());
                break;
            }
        }
        return cookie;
    }

    public String getSiteVersion(){
        String version = "UNKNOWN VERSION";
        RequestSpecification httpRequest;
        Response response;
        try{
            httpRequest = RestAssured.given().relaxedHTTPSValidation();
            response = httpRequest.request(Method.GET, runURL + "/api/public/site/version");
            version = response.asString();
        } catch (Exception apiExc) {
            apiExc.printStackTrace();
        }
        version = parseJSON(version, "$.ProductVersion");
        return version;
    }

}
