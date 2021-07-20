// Details on how to run test scripts with different browsers from maven command line (and thus from CI)
// https://seleniumjava.com/2017/05/21/how-to-run-scripts-in-a-specific-browser-with-maven/amp/
// Driver should work with whatever browser passed to them (either via CI, CMD line or in rare cases - from tests(not recommended way)
// Thus browser is abstracted outside driver class.
// Driver constructor makes sure that it can work with both cases (both with default browser or when browser is passed to it)

package slurp.webdriver;

import com.typesafe.config.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static java.lang.Boolean.parseBoolean;

import static slurp.TestConfig.getConfig;
import static slurp.webdriver.Options.getChromeOptions;
import static slurp.webdriver.Options.getFirefoxOptions;
import static slurp.webdriver.Options.getOperaOptions;

// Note: For executing in containers, the option MUST be set to headless. Else driver fails,
// So do not pass the headless parameter as false when choosing container as option.

public class DriverFactory {
    private DriverFactory() {
        // Do not want people to create an instance of Factory but use its static getDriver method to get the driver
        // using private WebDriver driver = DriverFactory.getDriver();
    }

    public static WebDriver getDriver() {
        WebDriver driver;
        Config config = getConfig();

        boolean remote = parseBoolean(config.getString("remote"));
        if (remote) {
            driver = createRemoteWebDriver(config);
        } else {
            driver = createWebDriver(config);
        }

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }

    private static WebDriver createRemoteWebDriver(Config config) {
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(config.getString("proxyAddress"));
        proxy.setSslProxy(config.getString("proxyAddress"));

        WebDriver remoteWebDriver = null;
        try {
            URL remoteUrl = new URL(config.getString("remoteUrl"));
            String browser = config.getString("browser").toLowerCase();
            switch (browser) {
                case "firefox":
                    remoteWebDriver = new RemoteWebDriver(remoteUrl, getFirefoxOptions());
                case "edge":
                    //ToDo
                case "ie":
                    //ToDo
                case "opera":
                    remoteWebDriver = new RemoteWebDriver(remoteUrl, getOperaOptions());
                case "phantom":
                    //ToDo
                default:
                    remoteWebDriver = new RemoteWebDriver(remoteUrl, getChromeOptions());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return remoteWebDriver;
    }

    private static WebDriver createWebDriver(Config config) {
//        WebDriverManager.globalConfig().setProxy(config.getString("proxyAddress"));
        String browser = config.getString("browser").toLowerCase();

        switch (browser) {
            // todo
            case "firefox":
                /**
                 * Some of the drivers (e.g. geckodriver or operadriver) are hosted on GitHub. When several consecutive
                 * requests are made by WebDriverManager, GitHub servers return an HTTP 403 error response.
                 * In that case there would be a need to set githubTokenName and gitHubTokenSecret properties.
                 * For additional information refer to https://github.com/bonigarcia/webdrivermanager#known-issues
                 */
                WebDriverManager.firefoxdriver()
                        // .gitHubTokenName("wd")
                        // .gitHubTokenSecret("e45e4480ab5a5c4333e5c8efcae73424445f064d")
                        .setup();
                return new FirefoxDriver(getFirefoxOptions());
            case "edge":
                WebDriverManager.edgedriver().setup();
                return new EdgeDriver();
            case "opera":
                WebDriverManager.operadriver().setup();
                return new OperaDriver();
//           As of Spring 2017, PhantomJS is not supported anymore.
//           On August 17, 2021 IE 11 will not be supported as well -
//           created ticket to monitor whether we should support it till that time (P4441-2432)
//           For Safari support additional ticket has been created P4441-2363.

//            case "ie":
//                WebDriverManager.iedriver().setup();
//                return new InternetExplorerDriver();

            default:
                WebDriverManager.chromedriver().setup();
                // To get rid of selenium logs
                java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.parse(config.getString("seleniumLogLevel")));
                // To get rid of chrome driver logs.
                System.setProperty("webdriver.chrome.silentOutput", config.getString("silentDriverLogs"));
                return new ChromeDriver(getChromeOptions());
        }
    }
}