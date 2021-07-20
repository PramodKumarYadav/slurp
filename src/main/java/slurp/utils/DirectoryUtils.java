package slurp.utils;

import com.typesafe.config.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static slurp.TestConfig.getConfig;

public class DirectoryUtils {
    private static Config config = getConfig();
    private final Integer WAIT_IN_SECONDS = config.getInt("webDriverWaitInSeconds");
    private final Integer WAIT_IN_SECONDS_LONG = config.getInt("webDriverWaitInSecondsLong");
    private final static String PATH_RESULTS_DIR = config.getString("resultsDir");

    public static void createDirectory(String dirName) {
        try {
            Path path = Paths.get(String.format("./%s/%s", PATH_RESULTS_DIR, dirName));
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initializeResultsDir(String className) {
        try {
            Path path = Paths.get(String.format("./%s/%s", PATH_RESULTS_DIR, className));
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File dir = new File(String.format("./%s/%s", PATH_RESULTS_DIR, className));
        File[] listFiles = dir.listFiles();
        for (File file : listFiles) {
            System.out.println("Deleting " + file.getName());
            file.delete();
        }
    }
}
