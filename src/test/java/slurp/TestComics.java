package slurp;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import org.openqa.selenium.WebDriver;

import slurp.pages.ComicPage;
import slurp.webdriver.DriverFactory;
import java.lang.invoke.MethodHandles;
import static slurp.PageActions.closeDriver;
import static slurp.TestConfig.getConfig;

@Slf4j
public class TestComics {
    private WebDriver driver = DriverFactory.getDriver();
    private static String className = MethodHandles.lookup().lookupClass().getSimpleName();

    private ComicPage comicPage = new ComicPage(driver);

    private static Config config = getConfig();
    private final static String SINGLE_COMIC_URL = config.getString("singleComicUrl");

    @BeforeAll
    static void initializeTestResults() {
        log.info("Testing comic: " + SINGLE_COMIC_URL);
    }

    @BeforeEach
    void setUp() {
        log.info("Each test has specific steps. No common steps here.");
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
    public void getComicNameFromURL() {
        comicPage.getComicName(SINGLE_COMIC_URL);
    }

    @Test
    @Disabled
    public void getASingleComicAsPDF() {
        comicPage.getASingleComic(SINGLE_COMIC_URL);
    }
}
