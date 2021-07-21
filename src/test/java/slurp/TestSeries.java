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
import slurp.pages.SeriesPage;
import slurp.webdriver.DriverFactory;

import java.lang.invoke.MethodHandles;

import static slurp.PageActions.closeDriver;
import static slurp.TestConfig.getConfig;

@Slf4j
public class TestSeries {
    private WebDriver driver = DriverFactory.getDriver();
    private static String className = MethodHandles.lookup().lookupClass().getSimpleName();

    private SeriesPage seriesPage = new SeriesPage(driver);

    private static Config config = getConfig();
    private final static String SERIES = config.getString("series");

    @BeforeAll
    static void initializeTestResults() {
        log.info("Testing series: " + SERIES);
    }

    @BeforeEach
    void setUp() {
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
    void getComicsCountInSeries() {
        seriesPage.getComicsCount();
    }

    @Test
    void getComicsURLsInSeries() {
        seriesPage.listAllComicURLs();
    }

    @Test
    @Disabled
    public void getAllComicsFromASeriesAsPDFs() {
        seriesPage.getAllComicsFromASeries();
    }
}
