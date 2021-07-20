package slurp;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static slurp.TestConfig.getConfig;

// https://www.baeldung.com/junit-testwatcher

@Slf4j
public class ResultLoggerExtension implements TestWatcher, AfterAllCallback, BeforeAllCallback {
    private List<TestResultStatus> testResultsStatus = new ArrayList<>();
    private static ArrayList<String> classNames = new ArrayList<String>();

    private static Config config = getConfig();
    private final static String PATH_RESULTS_DIR = config.getString("resultsDir");

    private enum TestResultStatus {
        SUCCESSFUL, ABORTED, FAILED, DISABLED;
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        log.info("Test Disabled for test {}: with reason :- {}",
                context.getDisplayName(),
                reason.orElse("No reason"));

        testResultsStatus.add(TestResultStatus.DISABLED);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        log.info("Test Successful for test {}: ", context.getDisplayName());
        deleteFiles(context.getDisplayName());
        testResultsStatus.add(TestResultStatus.SUCCESSFUL);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        log.info("Test Aborted for test {}: ", context.getDisplayName());
        testResultsStatus.add(TestResultStatus.ABORTED);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        log.info("Test Failed for test {}: ", context.getDisplayName());
        testResultsStatus.add(TestResultStatus.FAILED);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        Map<TestResultStatus, Long> summary = testResultsStatus.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        log.info("Test result summary for {} {}", context.getDisplayName(), summary.toString());
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        log.info("In test class: {}", context.getDisplayName());
        setClassName(context.getDisplayName());
    }

    private void setClassName(String className) {
        classNames.add(className);
    }

    private String getClassName() {
        return classNames.get(0);
    }

    private void deleteFiles(String fileName) {
        try {
            Path path = Paths.get(String.format("./%s/%s/%s.png", PATH_RESULTS_DIR, getClassName(), fileName));
            Files.delete(path);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
    }
}
