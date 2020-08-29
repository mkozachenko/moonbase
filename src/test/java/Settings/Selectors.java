package Settings;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.pagefactory.ByChained;

import java.util.List;

public class Selectors extends BrowserSettings{
    private WebElement element;
    public static WebElement lastElement;
    private List<WebElement> elements;

    public WebElement selector(By selector) {
        element = null;
        try {
            element = getDriver().findElement(selector);
            lastElement = element;
        } catch (NoSuchElementException | NullPointerException noEl) {
            System.err.println("Элемент отсутствует: " + selector);
        } catch (StaleElementReferenceException stEx){
            System.err.println("Элемент не актуален (stale element reference): " + selector);
        }
        return element;
    }
    public WebElement selector(By locator1, By locator2){
        element = null;
        try {
            element = getDriver().findElement(new ByChained(locator1, locator2));
            lastElement = element;
        } catch (NoSuchElementException | NullPointerException noEl) {
            System.err.println("Элементы отсутствуют: \n1) "+locator1+"\n2) "+locator2);
        } catch (StaleElementReferenceException stEx){
            System.err.println("Элементы не актуальны (stale element reference): \n1) " + locator1+"\n2) "+locator2);
        }
        return element;
    }
    public WebElement selector(WebElement webElement, By locator){
        element = null;
        try {
            element = webElement.findElement(locator);
            lastElement = element;
        } catch (NoSuchElementException | NullPointerException noEl) {
            System.err.println("Элементы отсутствуют: \n1) "+webElement+"\n2) "+locator);
        } catch (StaleElementReferenceException stEx){
            System.err.println("Элементы не актуальны (stale element reference): \n1) "+webElement+"\n2) "+locator);
        }
        return element;
    }

    public List<WebElement> multiSelector(By selector) {
        elements = null;
        try {
            elements = getDriver().findElements(selector);
            lastElement = (elements!=null)?(!elements.isEmpty()?elements.get(0) : null):null;
        } catch (NoSuchElementException | IndexOutOfBoundsException exc) {
            System.err.println("Элементы отсутствуют: " + selector);
            System.err.println("Ошибка: \n" + exc);
        } catch (StaleElementReferenceException stEx){
            System.err.println("Элемент не актуален (stale element reference): " + selector);
        }
        return elements;
    }
    public List<WebElement> multiSelector(WebElement webElement, By locator) {
        elements = null;
        try {
            elements = webElement.findElements(locator);
            lastElement = (elements!=null)?(!elements.isEmpty()?elements.get(0) : null):null;
        } catch (NoSuchElementException | IndexOutOfBoundsException exc) {
            System.err.println("Элементы отсутствуют: \n1) "+webElement+"\n2) "+locator);
            System.err.println("Ошибка: \n" + exc);
        } catch (StaleElementReferenceException stEx){
            System.err.println("Элемент не актуален (stale element reference): \n1) "+webElement+"\n2) "+locator);
        }
        return elements;
    }
    public List<WebElement> multiSelector(By locator1, By locator2) {
        elements = null;
        try {
            elements = getDriver().findElements(new ByChained(locator1, locator2));
            lastElement = (elements!=null)?(!elements.isEmpty()?elements.get(0) : null):null;
        } catch (NoSuchElementException | IndexOutOfBoundsException exc) {
            System.err.println("Элементы отсутствуют: \n1) "+locator1+"\n2) "+locator2);
            System.err.println("Ошибка: \n" + exc);
        } catch (StaleElementReferenceException stEx){
            System.err.println("Элемент не актуален (stale element reference): \n1) "+locator1+"\n2) "+locator2);
        }
        return elements;
    }

    /*Требует точного соответствия в тексте указанного элемента, кроме регистра*/
    public WebElement selectFromListByText(By listSelector, String text){
        element = null;
        for(WebElement item : multiSelector(listSelector)){
            if (item.getText().equalsIgnoreCase(text)) {
                element = item;
                lastElement = element;
                break;
            }
        }
        if(element == null){
            throw new NoSuchElementException("Элемент отсутствует:\nСелектор: " + listSelector+"\nТекст: "+text);
        }
        return element;
    }
    public WebElement selectFromListByText(By listSelector, By relativeSelector, String text){
        element = null;
        for(WebElement item : multiSelector(listSelector, relativeSelector)){
            if(item.getText().equalsIgnoreCase(text)){
                element = item;
                lastElement = element;
                break;
            }
        }
        if(element == null){
            throw new NoSuchElementException("Элемент отсутствует:\nСелекторы\n1) " + listSelector+"\n2) "+relativeSelector+"\nТекст: "+text);
        }
        return element;
    }
    public WebElement selectParentFromListByText(By listSelector, By relativeSelector, String text){
        element = null;
        for(WebElement item : multiSelector(listSelector)){
            if(item.findElement(relativeSelector).getText().equalsIgnoreCase(text)){
                element = item;
                lastElement = element;
                break;
            }
        }
        if(element == null){
            throw new NoSuchElementException("Элемент отсутствует:\nСелекторы\n1) " + listSelector+"\n2) "+relativeSelector+"\nТекст: "+text);
        }
        return element;
    }
    public WebElement selectFromListByAttribute(By listSelector, String attrName, String attrValue){
        element = null;
        for(WebElement item : multiSelector(listSelector)){
            if (item.getAttribute(attrName).equalsIgnoreCase(attrValue)) {
                element = item;
                lastElement = element;
                break;
            }
        }
        if(element == null){
            throw new NoSuchElementException("Элемент отсутствует:\nСелектор: " + listSelector+"\nАттрибут: "+attrName+"\nЗначение: "+attrValue);
        }
        return element;
    }
    public WebElement selectFromListByAttribute(By listSelector, By relativeSelector, String attrName, String attrValue) throws NoSuchElementException{
        element = null;
        for(WebElement item : multiSelector(listSelector, relativeSelector)){
            if(item.getAttribute(attrName).equalsIgnoreCase(attrValue)){
                element = item;
                lastElement = element;
                break;
            }
        }
        if(element == null){
            throw new NoSuchElementException("Элемент отсутствует:\nСелектор 1: " + listSelector+"\nСелектор 2: "+relativeSelector+"\nАттрибут: "+attrName+"\nЗначение: "+attrValue);
        }
        return element;
    }

    public WebElement selectParentByAttribute(By parentSelector, By relativeSelector, String attrName, String attrValue){
        element = null;
        for(WebElement item : multiSelector(parentSelector)){
            if(item.findElement(relativeSelector).getAttribute(attrName).contains(attrValue)){
                element = item;
                lastElement = element;
                break;
            }
        }
        if(element == null){
            throw new NoSuchElementException("Элемент отсутствует:\nСелекторы\n1) " + parentSelector+"\n2) "+relativeSelector+"\nАттрибут: "+attrName+" = "+attrValue);
        }
        return element;
    }

    public void inputText(By locator, String input, long delay){
        try {
            for(int i = 0; i < input.length(); i++){
                selector(locator).sendKeys(input.substring(i, i+1));
                new WaitsAsserts().sleep(delay);
            }
        } catch (Exception ex){
            System.err.println("Cannot perform action: \n");
            ex.printStackTrace();
        }
    }

    public void actionMouseOver(By selector){
        Actions action = new Actions(getDriver());
        try {
            action.moveToElement(selector(selector)).build().perform();
        } catch (Exception ex){
            System.err.println("Cannot perform action: \n");
            ex.printStackTrace();
        }
    }
    public void actionMouseOver(By selector, By relativeSelector){
        Actions action = new Actions(getDriver());
        try {
            action.moveToElement(selector(selector, relativeSelector)).build().perform();
        } catch (Exception ex){
            System.err.println("Cannot perform action: \n");
            ex.printStackTrace();
        }
    }

    public void actionMouseOver(WebElement element){
        Actions action = new Actions(getDriver());
        try {
            action.moveToElement(element).build().perform();
        } catch (Exception ex){
            System.err.println("Cannot perform action: \n");
            ex.printStackTrace();
        }
    }
    public void actionMouseOver(WebElement element, By relativeSelector){
        Actions action = new Actions(getDriver());
        try {
            action.moveToElement(selector(element, relativeSelector)).build().perform();
        } catch (Exception ex){
            System.err.println("Cannot perform action: \n");
            ex.printStackTrace();
        }
    }

    public void actionInput(WebElement element, String input){
        Actions action = new Actions(getDriver());
        try {
            action.sendKeys(element, input).build().perform();
        } catch (Exception ex){
            System.err.println("Cannot perform action: \n");
            ex.printStackTrace();
        }
    }
}
