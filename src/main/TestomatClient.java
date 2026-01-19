import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestomatClient {
    private final String apiKey;

    public TestomatClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public String createTestRun(String title) {
        try {
            URL url = new URL("https://app.testomat.io/api/reporter?api_key=" + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String body = new JSONObject().put("title", title).toString();

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            String response = readResponse(conn);
            return new JSONObject(response).getString("uid");

        } catch (Exception e) {
            throw new RuntimeException("Error creating test run", e);
        }
    }

    public void finishTestRun(String runUid, double duration) {
        try {
            URL url = new URL("https://app.testomat.io/api/reporter/" + runUid + "?api_key=" + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();
            body.put("status_event", "finish");
            body.put("duration", duration);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            readResponse(conn);

        } catch (Exception e) {
            System.err.println("Failed to finish test run: " + e.getMessage());
        }
    }

    public void reportTest(
            String runUid,
            String title,
            String testId,
            String suite,
            String file,
            String status,
            String message,
            String stack
    ) {
        try {
            ensureTestExists(testId, title);

            URL url = new URL("https://app.testomat.io/api/reporter/" + runUid + "/testrun?api_key=" + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();
            body.put("title", title);
            body.put("test_id", testId);
            body.put("suite_title", suite);
            body.put("file", file);
            body.put("status", status);
            if (message != null) body.put("message", message);
            if (stack != null) body.put("stack", stack);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            readResponse(conn);

        } catch (Exception e) {
            System.err.println("Failed to report test: " + e.getMessage());
        }
    }

    private void ensureTestExists(String testId, String title) {
        try {
            URL url = new URL("https://app.testomat.io/api/reporter/tests?api_key=" + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            String response = readResponse(conn);
            JSONArray tests = new JSONObject(response).getJSONArray("tests");

            for (int i = 0; i < tests.length(); i++) {
                JSONObject t = tests.getJSONObject(i);
                if (testId.equals(t.getString("test_id"))) {
                    return;
                }
            }

            URL createUrl = new URL("https://app.testomat.io/api/reporter?api_key=" + apiKey);
            HttpURLConnection createConn = (HttpURLConnection) createUrl.openConnection();
            createConn.setRequestMethod("POST");
            createConn.setRequestProperty("Content-Type", "application/json");
            createConn.setDoOutput(true);

            JSONObject body = new JSONObject();
            body.put("test_id", testId);
            body.put("title", title);

            try (OutputStream os = createConn.getOutputStream()) {
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            readResponse(createConn);

        } catch (Exception e) {
            System.err.println("Failed to ensure test exists: " + e.getMessage());
        }
    }

    private String readResponse(HttpURLConnection conn) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }
}

