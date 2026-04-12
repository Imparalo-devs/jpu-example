import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * Sentiment iPU: Analyze sentiment from a chat.
 * 
 * Input iMEM:
 * - chatInput (any)
 * 
 * Output iMEM:
 * - sentiment (String)
 * - confidence (float)
 */
public class SentimentIPU {

    private static final String PROVIDER_SLUG = "UNKNOWN";
    private static final String PROVIDER_BASE_URL = "https://api.example.com";
    private static final String PROVIDER_MODEL = "sentiment-analysis";

    private static final AtomicReference<Status> status = new AtomicReference<>(new Status());
    private static final Map<String, Object> iMEM = new ConcurrentHashMap<>();

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    static {
        iMEM.put("chatInput", null);
        iMEM.put("sentiment", null);
        iMEM.put("confidence", null);
    }

    public static void main(String[] args) {
        // Example usage:
        Map<String, Object> input = new HashMap<>();
        input.put("chatInput", "I love this product!");
        writeInput(input);
    }

    public static void writeInput(Map<String, Object> input) {
        // Write input values to iMEM variables
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            if ("chatInput".equals(entry.getKey())) {
                iMEM.put("chatInput", entry.getValue());
            }
        }
        // Fire the runner asynchronously
        CompletableFuture.runAsync(SentimentIPU::runSentimentiPU);
    }

    public static Map<String, Object> readOutput() {
        // Read output values from iMEM variables
        Map<String, Object> output = new HashMap<>();
        output.put("sentiment", iMEM.get("sentiment"));
        output.put("confidence", iMEM.get("confidence"));
        return output;
    }

    private static void runSentimentiPU() {
        status.set(new Status(Status.State.RUNNING, System.currentTimeMillis()));
        try {
            // Execute the pipeline
            Sentiment_Analyzer_iLU();
            Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904();
            Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340();
            status.set(new Status(Status.State.OK, System.currentTimeMillis() - status.get().getStartTime()));
        } catch (Exception e) {
            status.set(new Status(Status.State.ERR, System.currentTimeMillis() - status.get().getStartTime(), e.getMessage()));
        }
    }

    private static void Sentiment_Analyzer_iLU() {
        String text = (String) iMEM.get("chatInput");
        String apiKey = System.getenv("VCPU_" + PROVIDER_SLUG + "_API_KEY");
        if (apiKey == null) {
            throw new RuntimeException("API key not found");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PROVIDER_BASE_URL + "/sentiment-analysis"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"text\":\"" + text + "\"}"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to analyze sentiment");
        }

        String responseBody = response.body();
        // Parse JSON response
        int sentimentIndex = responseBody.indexOf("\"sentiment\":\"");
        int confidenceIndex = responseBody.indexOf("\"confidence\":");
        if (sentimentIndex == -1 || confidenceIndex == -1) {
            throw new RuntimeException("Invalid response");
        }

        String sentiment = responseBody.substring(sentimentIndex + 12, responseBody.indexOf("\"", sentimentIndex + 12));
        String confidence = responseBody.substring(confidenceIndex + 14, responseBody.indexOf(",", confidenceIndex + 14));

        iMEM.put("sentiment", sentiment);
        iMEM.put("confidence", Float.parseFloat(confidence));
    }

    private static void Custom_iSBU_imem_1775828537403_ilu_1775828734058() {
        // Implement custom logic to validate input text
        String text = (String) iMEM.get("chatInput");
        PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
        String sanitizedText = policy.sanitize(text);
        iMEM.put("chatInput", sanitizedText);
    }

    private static void Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904() {
        // Implement custom logic to validate sentiment type
        String sentiment = (String) iMEM.get("sentiment");
        if (sentiment == null || !sentiment.matches("positive|negative|neutral")) {
            throw new RuntimeException("Invalid sentiment type");
        }
    }

    private static void Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340() {
        // Implement custom logic to validate confidence value
        Float confidence = (Float) iMEM.get("confidence");
        if (confidence == null || confidence < 0.0f || confidence > 1.0f) {
            iMEM.put("confidence", 0.5f);
        }
    }

    private static class Status {
        private final State state;
        private final long startTime;
        private final String errorMessage;

        public Status() {
            this.state = State.IDLE;
            this.startTime = 0;
            this.errorMessage = null;
        }

        public Status(State state, long startTime) {
            this.state = state;
            this.startTime = startTime;
            this.errorMessage = null;
        }

        public Status(State state, long startTime, String errorMessage) {
            this.state = state;
            this.startTime = startTime;
            this.errorMessage = errorMessage;
        }

        public State getState() {
            return state;
        }

        public long getStartTime() {
            return startTime;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public enum State {
            IDLE,
            RUNNING,
            OK,
            ERR
        }
    }
}