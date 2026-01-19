import annotations.TestId;
import annotations.Title;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

public class ReportingExtension implements BeforeAllCallback, AfterAllCallback, TestWatcher {
    private static TestomatClient client;
    public static String RUN_UID;
    private long startTime;

    @Override
    public void beforeAll(ExtensionContext context) {
        startTime = System.currentTimeMillis();
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("TESTOMATIO");

        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("TESTOMATIO env variable is not set");
        }

        client = new TestomatClient(apiKey);
        RUN_UID = client.createTestRun("JUnit Test Run");
        System.out.println("Created Test Run UID: " + RUN_UID);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        double duration = (System.currentTimeMillis() - startTime) / 1000.0;
        client.finishTestRun(RUN_UID, duration);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        report(context, "passed", null);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        report(context, "failed", cause);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        report(context, "skipped", cause);
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        report(context, "skipped", null);
    }

    private void report(ExtensionContext context, String status, Throwable error) {
        String suite = context.getRequiredTestClass().getSimpleName();
        String file = suite + ".java";

        Optional<java.lang.reflect.Method> testMethod = context.getTestMethod();

        String testId = testMethod
                .flatMap(m -> Optional.ofNullable(m.getAnnotation(TestId.class)))
                .map(TestId::value)
                .orElse("UNKNOWN");

        String title = testMethod
                .flatMap(m -> Optional.ofNullable(m.getAnnotation(Title.class)))
                .map(Title::value)
                .orElse(context.getDisplayName());

        client.reportTest(
                RUN_UID,
                title,
                testId,
                suite,
                file,
                status,
                error != null ? error.getMessage() : null,
                error != null ? getStackTrace(error) : null
        );
    }

    private String getStackTrace(Throwable t) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement e : t.getStackTrace()) {
            sb.append(e.toString()).append("\n");
        }
        return sb.toString();
    }
}
