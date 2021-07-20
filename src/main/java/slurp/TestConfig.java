package slurp;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class TestConfig {
    public static Config getConfig() {
        Config appConfig = ConfigFactory.load();

        // See if any hostName is passed from command line
        String hostName = System.getProperty("host");
        log.debug("hostName passed from command line: {}", hostName);

        // If host name is not passed from command line, choose it from application.config
        if (StringUtils.isEmpty(hostName)) {
            hostName = appConfig.getString("host");
        }

        log.debug("hostName used to load config: {}", hostName);

        // Load properties specific for chosen host.
        Config hostConfig = ConfigFactory.load(hostName);

        // Merge properties from common properties (application.conf) and host specific properties (from host file)
        Config mergedConfig = hostConfig.withFallback(appConfig);

        return mergedConfig;
    }
}

