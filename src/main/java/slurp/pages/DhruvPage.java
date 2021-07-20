package slurp.pages;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import slurp.PageActions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static slurp.TestConfig.getConfig;
import static slurp.utils.DirectoryUtils.createDirectory;

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

    @FindBy(css = "img[id*='image-']")
    private List<WebElement> images;

    public void navigateToHomePageURL() {
        driver.get(HOME_PAGE_URL);
        webDriverWaitLong.until(ExpectedConditions.urlToBe(HOME_PAGE_URL));
    }

    public void listAllComicURLs(){
        for (WebElement comic: comics) {
            log.info(comic.getAttribute("href"));
        }
    }

    public Integer getComicsCount(){
        log.info("Total Dhruv comics: " + String.valueOf(comics.size()));
        return comics.size();
    }

    public String getComicName(String comicURL) {
        String[] urlPaths = comicURL.split("/");
        String comicName = String.valueOf(urlPaths[urlPaths.length - 1]);
        log.info("comicName: " + comicName);
        return comicName;
    }

    public void saveAllImagesInAComic(String comicURL){
        driver.get(comicURL);

        String comicName = getComicName(comicURL);
        createDirectory(comicName);

        int pageNr = 1;
        for (WebElement image: images) {
            String srcURL = image.getAttribute("data-src");
            log.info(srcURL);

            try {
                URL imageURL = new URL(srcURL);
                BufferedImage saveImage = ImageIO.read(imageURL);
                ImageIO.write(saveImage, "png", new File("./results/" + comicName + "/" + pageNr + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            pageNr++;
        }
    }
}
