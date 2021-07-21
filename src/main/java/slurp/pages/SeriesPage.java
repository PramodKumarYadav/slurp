package slurp.pages;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import slurp.PageActions;

import java.util.ArrayList;
import java.util.List;

import static slurp.TestConfig.getConfig;

@Slf4j
public class SeriesPage extends PageActions {
    //Page URL
    private static Config config = getConfig();
    private static final String SERIES_PAGE_URL = config.getString("seriesPageUrl");

    public SeriesPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // Locators
    @FindBy(css = "ul[class*='main version-chap'] > li > a")
    private List<WebElement> comics;

    public void navigateToSeriesPageURL() {
        driver.get(SERIES_PAGE_URL);
        webDriverWaitLong.until(ExpectedConditions.urlToBe(SERIES_PAGE_URL));
    }

    public void listAllComicURLs(){
        for (WebElement comic: comics) {
            log.info(comic.getAttribute("href"));
        }
    }

    public List<String> getAllComicURLsOnThePage(){
        List<String> comicURLs = new ArrayList();

        for (WebElement comic: comics) {
            String comicURL = comic.getAttribute("href");
            log.info("comicURL: "+ comicURL);

            comicURLs.add(comicURL);
        }

        return comicURLs;
    }

    public Integer getComicsCount(){
        log.info("Total comics in series: " + String.valueOf(comics.size()));
        return comics.size();
    }

    public void getAllComicsFromASeries(){
        navigateToSeriesPageURL();

        List<String> comicURLs = getAllComicURLsOnThePage();
        for (String comicURL: comicURLs) {
            driver.get(comicURL);
            webDriverWaitLong.until(ExpectedConditions.urlToBe(comicURL));

            ComicPage comicPage = new ComicPage(driver);
            comicPage.saveAllImagesInAComic(comicURL);
            comicPage.convertImagesToPDF(comicPage.getComicName(comicURL));
        }
    }
}
