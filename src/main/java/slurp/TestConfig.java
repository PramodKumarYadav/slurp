package slurp;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class TestConfig {
    public static Config getConfig() {
        Config appConfig = ConfigFactory.load();

        // See if any seriesName is passed from command line
        String seriesName = System.getProperty("series");
        log.debug("seriesName passed from command line: {}", seriesName);

        // If series name is not passed from command line, choose it from application.config
        if (StringUtils.isEmpty(seriesName)) {
            seriesName = appConfig.getString("series");
        }

        log.debug("seriesName used to load config: {}", seriesName);

        // Load properties specific for chosen series.
        Config seriesConfig = ConfigFactory.load(seriesName);

        // Merge properties from common properties (application.conf) and series specific properties (from series file)
        Config mergedConfig = seriesConfig.withFallback(appConfig);

        return mergedConfig;
    }
}

