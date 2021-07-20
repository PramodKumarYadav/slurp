package slurp;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import slurp.pages.DhruvPage;
import slurp.webdriver.DriverFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

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

    @Test
    public void getComicName() {
        dhruvPage.getComicName("https://comicsworld.in/manga/super-commando-dhruv/111-end-game/");
    }

    @Test
    public void saveImages() {
        dhruvPage.saveAllImagesInAComic("https://comicsworld.in/manga/super-commando-dhruv/111-end-game/");
    }
}
