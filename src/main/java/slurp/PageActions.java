// https://github.com/SeleniumHQ/selenium/wiki/PageFactory
// https://www.toptal.com/selenium/test-automation-in-selenium-using-page-object-model-and-page-factory

package slurp;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;
import static slurp.TestConfig.getConfig;

@Slf4j
public class PageActions {
    public WebDriver driver;
    public static WebDriverWait webDriverWait;
    public static WebDriverWait webDriverWaitLong;

    private static Config config = getConfig();
    private final Integer WAIT_IN_SECONDS = config.getInt("webDriverWaitInSeconds");
    private final Integer WAIT_IN_SECONDS_LONG = config.getInt("webDriverWaitInSecondsLong");

    public PageActions(WebDriver driver) {
        this.driver = driver;
        webDriverWait = new WebDriverWait(driver, WAIT_IN_SECONDS);
        webDriverWaitLong = new WebDriverWait(driver, WAIT_IN_SECONDS_LONG);
    }

    // In our application, when we say "addApplicant" then application refocuses.
    // Chrome driver cannot handle this: https://chromedriver.chromium.org/help/clicking-issues
    // Also the javascript scrollIntoView method does not always work, so we have another method
    // that is not as efficient but that works to scroll page into center.
    // Thus when things are not clickable, because page did not scroll or did not scroll enough
    // hide behind a ribbon, than we try again to recenter and click element. This seems to have
    // fixed the flakiness issue I encountered early on after migration to Json Schema.
    public void scrollIntoViewAndClick(WebElement webElement) {
        try {
            scrollIntoView(webElement);
            webElement.click();
        } catch (Exception exception) {
            log.debug("scrollIntoView failed. Now trying with scrollIntoViewAndCenter");
            log.debug("webElement Location: " + webElement.getLocation().toString());
            scrollIntoViewAndCenter(webElement);
            webElement.click();
        }
    }

    public void getPage(String page) {
        driver.get(page);
        webDriverWaitLong.until(ExpectedConditions.urlToBe(page));
    }


    // ToDo: Make this method private after attachments and party section is refactored.
    public void scrollIntoView(WebElement webElement) {
        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", webElement);
        } else {
            throw new RuntimeException("The driver used doesn't implement JavascriptExecutor");
        }
    }

    private void scrollIntoViewAndCenter(WebElement webElement) {
        String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
                + "var elementTop = arguments[0].getBoundingClientRect().top;"
                + "window.scrollBy(0, elementTop-(viewPortHeight/2));";

        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) driver).executeScript(scrollElementIntoMiddle, webElement);
            // Selenium cannot click on moving elements. So you have to let the page settle down before clicking.
            waitForPageToRecenter(2);
        } else {
            throw new RuntimeException("The driver used doesn't implement JavascriptExecutor");
        }
    }

    // The selenium click method does not work for elements hidden behind ribbon (say confirm button),
    // thus this method can be used in such cases.
    public void scrollIntoViewAndClickViaJavaScript(WebElement webElement) {
        scrollIntoViewAndCenter(webElement);
        clickViaJavaScript(webElement);
    }

    public void clickViaJavaScript(WebElement webElement) {
        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", webElement);
        } else {
            throw new RuntimeException("The driver used doesn't implement JavascriptExecutor");
        }
    }

    public void setCheckbox(WebElement webElement) {
        scrollIntoView(webElement);
        if (! webElement.isSelected()) {
            webElement.click();
        }
    }

    public void setTextField(WebElement webElement, String value) {
        waitUntilElementIsVisible(webElement);
        scrollIntoViewAndClick(webElement);
        webElement.clear();
        webElement.sendKeys(value);
    }

    public void setListItem(List<WebElement> webElementList, String attributeName, String attributeValue) {
        // Below step is required for list items. Else the test gets flaky.
        waitUntilAllElementsAreDisplayed(webElementList);
        WebElement webElement = webElementList
                .stream()
                .filter(element -> element.getAttribute(attributeName).equals(attributeValue))
                .findFirst().get();
        clickViaJavaScript(webElement);
    }

    // Below step is required for list items. Else the test gets flaky.
    public void waitUntilAllElementsAreDisplayed(List<WebElement> webElements) {
        webDriverWait.until(ExpectedConditions.visibilityOfAllElements(webElements));
    }

    public Boolean waitUntilElementDisappears(By locator) {
        return webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public WebElement waitUntilElementIsClickable(WebElement webElement) {
        return webDriverWait.until(ExpectedConditions.elementToBeClickable(webElement));
    }

    public WebElement waitUntilElementIsVisible(WebElement webElement) {
        return webDriverWait.until(ExpectedConditions.visibilityOf(webElement));
    }


    public void waitUntilElementIsDisplayed(WebElement webElement) {
        waitUntilElementIsVisible(webElement).isDisplayed();
    }

    public boolean isElementDisplayed(WebElement webElement) {
        return waitUntilElementIsVisible(webElement).isDisplayed();
    }

    public boolean isTextPresentInWebElement(WebElement webElement, String text) {
        return webDriverWait.until(ExpectedConditions.textToBePresentInElement(webElement, text));
    }

    public boolean doesElementHaveAttributeWithValue(WebElement webElement, String attribute, String text) {
        return webDriverWait.until(ExpectedConditions.attributeToBe(webElement, attribute, text));
    }

    public String getTextFromElement(WebElement webElement) {
        return waitUntilElementIsVisible(webElement).getText();
    }

    // To close the home page (at the end of tests if required)
    public static void closeDriver(WebDriver driver) {
        driver.close();

        // we should generally not run 'close' and 'quit' commands together
        // as it could result in NoSuchSessionException exception
        try {
            driver.quit();
        } catch (NoSuchSessionException exception) {
            log.warn("Attempt to quit webDriver for already closed connection");
        }
    }

    public void refreshPage() {
        driver.navigate().refresh();
    }

    //ToDo: Delete later this method (Use setListItem method (above) more than using this method (getListItem)
    public WebElement getListItem(WebElement webElementList, String selector, String item) {
        String cssSelector = String.format(selector, item);
        return webElementList.findElement(By.cssSelector(cssSelector));
    }

    public static void waitBeforeCheckingDatabase(Integer seconds) {
        sleepForSeconds(seconds);
    }

    public static void waitForCardToBeVisibleAfterCarouselClicks(Integer seconds) {
        sleepForSeconds(seconds);
    }

    public static void waitForProgressToUpdate(Integer seconds) {
        sleepForSeconds(seconds);
    }

    public static void waitForPageToLoad(Integer seconds) {
        sleepForSeconds(seconds);
    }

    public static void waitForPageToRecenter(Integer seconds) {
        sleepForSeconds(seconds);
    }

    public static void waitToSeeIfThereAreAnyTechnicalErrors(Integer seconds) {
        sleepForSeconds(seconds);
    }

    public static void sleepForSeconds(Integer seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // We want to extend these methods but not to show them in intellisense as domain methods.
    protected void findElementAndSetTextField(String cssSelector, String textToSet) {
        WebElement element = findElementByCssSelector(cssSelector);
        setTextField(element, textToSet);
    }

    protected void findElementsAndSetAListItem(String cssSelector, String listItem) {
        List<WebElement> elements = findElementsByCssSelector(cssSelector);
        setListItem(elements, "data-value", listItem);
    }

    protected List<WebElement> findElementsByCssSelector(String cssSelector) {
        return driver.findElements(By.cssSelector(cssSelector));
    }

    protected void findElementAndClick(String cssSelector) {
        WebElement webElement = findElementByCssSelector(cssSelector);
        scrollIntoViewAndClick(webElement);
    }

    protected boolean isElementAvailableOnThePage(By locator) {
        List<WebElement> addButton = findElements(locator);
        return addButton.size() > 0;
    }

    public void findElementAndClick(By selector) {
        WebElement webElement = driver.findElement(selector);
        scrollIntoViewAndClick(webElement);
    }

    protected WebElement findElementByCssSelector(String cssSelector) {
        return driver.findElement(By.cssSelector(cssSelector));
    }

    public WebElement findElement(By selector) {
        return driver.findElement(selector);
    }

    public List<WebElement> findElements(By selector) {
        return driver.findElements(selector);
    }

    protected String findElementAndGetText(String cssSelector) {
        WebElement element = findElementByCssSelector(cssSelector);
        return element.getText();
    }

    public List<String> findElementsAndReturnAllListItemsDataValues(String cssSelector) {
        List<WebElement> elements = findElementsByCssSelector(cssSelector);

        List<String> dataValues = new ArrayList<>();
        for (WebElement element : elements) {
            dataValues.add(element.getAttribute("data-value"));
        }

        return dataValues;
    }

    public static LogEntries getBrowserLogs(WebDriver driver) {
        return driver.manage().logs().get(LogType.BROWSER);
    }

    public static void assertBrowserLogs(LogEntries logs) {
        Boolean errors = false;
        if (logs.getAll().size() > 0) {
            for (LogEntry entry : logs) {
                if (entry.getLevel().toString().equalsIgnoreCase("SEVERE")) {
                    errors = true;
                    log.info(entry.getLevel() + " " + entry.getMessage());
                }
            }
        }

        if (errors == true) {
            fail("Errors logs found. \n");
        }
    }

    public static void cleanUp(WebDriver driver, String draftId) {
        LogEntries logs = getBrowserLogs(driver);
        closeDriver(driver);
        assertBrowserLogs(logs);
    }

    public static void clearConsole(WebDriver driver) {
        String script = "console.clear();";
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(script);
    }

    public void printElementAttributes(WebElement webElement) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        Object debug = executor.executeScript("var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) " +
                "{ items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;", webElement);
        log.info(debug.toString());
    }

    public void mouseHover(WebElement element) {
        Actions action = new Actions(driver);
        action.moveToElement(element).perform();
    }

    public void setTextFieldAndPressEnter(WebElement webElement, String text) {
        webElement.sendKeys(text);
        webElement.sendKeys(Keys.ENTER);
    }

    public void waitForUrlContaining(String url) {
        new WebDriverWait(driver, WAIT_IN_SECONDS).until(ExpectedConditions.urlContains(url));
    }

    public WebElement waitForElementToBeClickable(WebElement element){
        return webDriverWait.until(ExpectedConditions.elementToBeClickable(element));
    }
//    Type this in console of chrome browser and hover to any point to get the page coordinates
//    https://stackoverflow.com/questions/12888584/is-there-a-way-to-tell-chrome-web-debugger-to-show-the-current-mouse-position-in
//    document.onmousemove = function(e) {
//        var x = e.pageX;
//        var y = e.pageY;
//        e.target.title = "X is " + x + " and Y is " + y;
//    };
}
