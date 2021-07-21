package slurp.utils;

import com.typesafe.config.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static slurp.TestConfig.getConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DirectoryUtils {
    private static Config config = getConfig();
    private final static String COMICS_DIR = config.getString("comicsDir");
    private final static String SERIES_DIR = config.getString("series");

    public static void createDirectory(String dirName) {
        try {
            Path path = Paths.get(String.format("./%s/%s/%s", COMICS_DIR, SERIES_DIR, dirName));
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initializeComicsDir(String className) {
        try {
            Path path = Paths.get(String.format("./%s/%s/%s", COMICS_DIR, SERIES_DIR, className));
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File dir = new File(String.format("./%s/%s", COMICS_DIR, className));
        File[] listFiles = dir.listFiles();
        for (File file : listFiles) {
            System.out.println("Deleting " + file.getName());
            file.delete();
        }
    }
}
