package slurp.pages;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import slurp.PageActions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static slurp.TestConfig.getConfig;
import static slurp.utils.DirectoryUtils.createDirectory;

@Slf4j
public class DhruvPage extends PageActions {
    //Page URL
    private static Config config = getConfig();
    private static final String HOME_PAGE_URL = config.getString("homePageUrl");
    private final static String COMICS_DIR = config.getString("comicsDir");

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

            String paddedPageNr = StringUtils.leftPad(String.valueOf(pageNr), 3, "0");
            log.info(paddedPageNr);
            try {
                URL imageURL = new URL(srcURL);
                BufferedImage bufferedImage = ImageIO.read(imageURL);
                ImageIO.write(bufferedImage, "png", new File(String.format("./%s/%s/%s.png", COMICS_DIR, comicName, paddedPageNr)));
            } catch (IOException e) {
                e.printStackTrace();
            }

            pageNr++;
        }
    }

    public static void convertImagesToPDF(String imageDirName){
        try{
            // Instantiate Document Object
            Document document=new Document();

            // Access image files in the folder
            // String imagesDirectory = "D:/Images/";
            String imagesDirectory = String.format("./%s/%s", COMICS_DIR, imageDirName);
            log.info(imagesDirectory);
            File file = new File(imagesDirectory);
            String[] fileList = file.list();

            //Create PdfWriter for Document to hold physical file
            String pathFinalPDF = String.format("./%s/%s.pdf", COMICS_DIR, imageDirName);
            PdfWriter.getInstance(document, new FileOutputStream(pathFinalPDF));
            document.open();

            for (String fileName : fileList) {
                String pathJPG = String.format("%s/%s", imagesDirectory, fileName);
                log.info(pathJPG);

                //Get the input image to Convert to PDF
                Image image=Image.getInstance(pathJPG);
                image.scaleToFit(PageSize.A4);

                //Add image to Document
                document.add(image);
            }

            //Close Document
            document.close();
            System.out.println("Successfully Converted JPG to PDF using iText");
        }
        catch (Exception i1){
            i1.printStackTrace();
        }
    }

    public void getAllDhruvComics(){
        navigateToHomePageURL();

        List<String> comicURLs = getAllComicURLsOnThePage();
        for (String comicURL: comicURLs) {
            driver.get(comicURL);
            webDriverWaitLong.until(ExpectedConditions.urlToBe(comicURL));

            saveAllImagesInAComic(comicURL);
            convertImagesToPDF(getComicName(comicURL));
        }
    }
}
