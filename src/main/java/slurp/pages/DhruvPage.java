package slurp.pages;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import slurp.PageActions;

import java.util.List;

import static slurp.TestConfig.getConfig;

@Slf4j
public class DhruvPage extends PageActions {
    //Page URL
    private static Config config = getConfig();
    private static final String HOME_PAGE_URL = config.getString("homePageUrl");

    public DhruvPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // Locators
    @FindBy(css = "ul[class='main version-chap active'] > li > a")
    private List<WebElement> comics;

    public void navigateToHomePageURL() {
        driver.get(HOME_PAGE_URL);
        webDriverWaitLong.until(ExpectedConditions.urlToBe(HOME_PAGE_URL));
    }

    public void navigateAllComics(){
        log.info("size is: " + String.valueOf(comics.size()));
    }
}
