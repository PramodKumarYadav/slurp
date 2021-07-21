package slurp;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import org.openqa.selenium.WebDriver;

import slurp.pages.ComicPage;
import slurp.pages.SeriesPage;
import slurp.webdriver.DriverFactory;
import java.lang.invoke.MethodHandles;
import static slurp.PageActions.closeDriver;
import static slurp.TestConfig.getConfig;

@Slf4j
public class TestComics {
    private WebDriver driver = DriverFactory.getDriver();
    private static String className = MethodHandles.lookup().lookupClass().getSimpleName();

    private SeriesPage seriesPage = new SeriesPage(driver);
    private ComicPage comicPage = new ComicPage(driver);

    private static Config config = getConfig();
    private final static String SERIES = config.getString("series");
    private final static String SINGLE_COMIC_URL = config.getString("singleComicUrl");
    private final static String COMIC_NAME = config.getString("comicName");

    @BeforeAll
    static void initializeTestResults() {
        log.info("Creating pdfs for series: " + SERIES);
        log.info("Single comic URL: " + SINGLE_COMIC_URL);
        log.info("Single comic name: " + COMIC_NAME);
    }

    @BeforeEach
    void setUp() {
        // Navigate to home page url for dhruv page
        seriesPage.navigateToSeriesPageURL();
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        closeDriver(driver);
    }

    @AfterAll
    static void logTestDetails() {
        log.info("Tests completed for: " + className);
    }

    @Test
    void getComicsCount() {
        seriesPage.getComicsCount();
    }

    @Test
    void getComicsURLs() {
        seriesPage.listAllComicURLs();
    }

    @Test
    public void getComicName() {
        comicPage.getComicName(SINGLE_COMIC_URL);
    }

    @Test
    public void saveImages() {
        comicPage.saveAllImagesInAComic(SINGLE_COMIC_URL);
    }

    @Test
    public void convertImagesToPDFTest(){
        comicPage.convertImagesToPDF(COMIC_NAME);
    }

    @Test
    public void testPadding(){
        int pageNr = 1;
        for(int i=0; i< 20; i++){
            String paddedPageNr = StringUtils.leftPad(String.valueOf(pageNr), 3, "0");
            log.info(paddedPageNr);
            pageNr++;
        }
    }

    @Test
    public void getAllComicsFromASeries() {
        seriesPage.getAllComicsFromASeries();
    }
}
