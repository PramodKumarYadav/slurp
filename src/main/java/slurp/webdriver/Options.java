/* NOTE: Taken from comments of Anthony. If not needed, remove them in future.
    these lines were not needed,but keep here for the moment for reference
    chromeOptions.addArguments("--verbose");
    chromeOptions.addArguments("--whitelisted-ips=''");
    chromeOptions.addArguments("--no-sandbox");
    chromeOptions.addArguments("--disable-dev-shm-usage");
    System.setProperty("webdriver.chrome.whitelistedIps", "");
    //add this line for verbose
    System.setProperty("webdriver.chrome.verboseLogging", "true");
    chromeOptions.addArguments("--proxy-server='direct://'");
    chromeOptions.addArguments("--proxy-bypass-list=*");
        chromeOptions.setProxy(null);
    https://www.selenium.dev/documentation/en/webdriver/page_loading_strategy/
    */

    /* these lines enable debugging
    System.setProperty("webdriver.chrome.logfile", "/usr/src/chromedrivergrid.log");
    System.setProperty("webdriver.chrome.verboseLogging", "true");
    // WebDriverManager.globalConfig().setProxy("https://server:8080");
*/

package slurp.webdriver;

import com.typesafe.config.Config;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.CapabilityType;

import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;

import static java.lang.Boolean.parseBoolean;
import static slurp.TestConfig.getConfig;

public class Options {
    static Config config = getConfig();

    static String host = config.getString("host");
    static boolean headlessFlag = parseBoolean(config.getString("headless"));
    static boolean acceptInsecureCertsFlag = parseBoolean(config.getString("acceptInsecureCertificates"));
    static String seleniumLogLevel = config.getString("seleniumLogLevel");

    public static ChromeOptions getChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setAcceptInsecureCerts(acceptInsecureCertsFlag);
        chromeOptions.setHeadless(headlessFlag);
//        chromeOptions.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        chromeOptions.addArguments("--no-sandbox"); // overcome limited resource problems and a must-have step to run tests in docker pipeline
        chromeOptions.addArguments("--window-size=1280,1380");
        chromeOptions.addArguments("start-maximized");
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        chromeOptions.addArguments("--enable-javascript");

        // To get error console logs
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.parse(seleniumLogLevel));
        chromeOptions.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        return chromeOptions;
    }

    public static FirefoxOptions getFirefoxOptions() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setAcceptInsecureCerts(acceptInsecureCertsFlag);
        firefoxOptions.setHeadless(headlessFlag);
        return firefoxOptions;
    }

    public static OperaOptions getOperaOptions() {
        return new OperaOptions();
    }
}
