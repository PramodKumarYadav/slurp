package slurp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import org.openqa.selenium.WebDriver;

import slurp.pages.DhruvPage;
import slurp.webdriver.DriverFactory;
import java.lang.invoke.MethodHandles;
import static slurp.PageActions.closeDriver;
import static slurp.pages.DhruvPage.convertImagesToPDF;

@Slf4j
public class TestDhruv {
    private WebDriver driver = DriverFactory.getDriver();
    private static String className = MethodHandles.lookup().lookupClass().getSimpleName();

    private DhruvPage dhruvPage = new DhruvPage(driver);

    @BeforeAll
    static void initializeTestResults() {
        log.info("Creating pdfs for comics");
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

    @Test
    public void convertImagesToPDFTest(){
        convertImagesToPDF("111-end-game");
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
    public void getAllDhruvComics() {
        dhruvPage.getAllDhruvComics();
    }
}
