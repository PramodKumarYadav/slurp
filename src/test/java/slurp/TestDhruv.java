package slurp;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.WebDriver;
import slurp.pages.DhruvPage;
import slurp.webdriver.DriverFactory;

import java.lang.invoke.MethodHandles;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static slurp.PageActions.closeDriver;
import static slurp.PageActions.sleepForSeconds;
import static slurp.utils.DirectoryUtils.createDirectory;

@Slf4j
public class TestDhruv {
    private WebDriver driver = DriverFactory.getDriver();
    private static String className = MethodHandles.lookup().lookupClass().getSimpleName();

    private DhruvPage dhruvPage = new DhruvPage(driver);
    @BeforeAll
    static void initializeTestResults() {
        createDirectory(className);
    }

    @BeforeEach
    void setUp() {
        // Navigate to home page url for dhruv page
        dhruvPage.navigateToHomePageURL();
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
        dhruvPage.getComicsCount();
    }

    @Test
    void getComicsURLs() {
        dhruvPage.listAllComicURLs();
    }
}
