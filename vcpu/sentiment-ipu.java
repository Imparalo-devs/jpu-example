import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.HashMap;
import java.util.Map;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * Sentiment iPU: Analyze sentiment from a chat.
 * 
 * Input iMEM: chatInput (any)
 * Output iMEM: sentiment (String), confidence (float)
 */
public class SentimentIPU {
    private static final String PROVIDER_SLUG = "UNKNOWN";
    private static final String BASE_URL = "https://api.example.com/sentiment";
    private static final String MODEL = "sentiment-analysis";

    private volatile AtomicReference<Status> status = new AtomicReference<>(new Status(Status.State.IDLE, null, 0));
    private Object chatInput = null;
    private String sentiment = null;
    private Float confidence = null;

    private static class Status {
        enum State { IDLE, RUNNING, OK, ERR }
        private State state;
        private String errorMessage;
        private long duration;

        public Status(State state, String errorMessage, long duration) {
            this.state = state;
            this.errorMessage = errorMessage;
            this.duration = duration;
        }
    }

    public void writeInput(Map<String, Object> input) {
        // Write input values into their iMEM variables
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            if ("chatInput".equals(entry.getKey())) {
                this.chatInput = entry.getValue();
            }
        }
        // Fire the runner asynchronously
        CompletableFuture.runAsync(this::runSentimentiPU);
    }

    public Map<String, Object> readOutput() {
        // Get the output iMEM values
        Map<String, Object> output = new HashMap<>();
        output.put("sentiment", sentiment);
        output.put("confidence", confidence);
        return output;
    }

    private void runSentimentiPU() {
        long startTime = System.currentTimeMillis();
        status.set(new Status(Status.State.RUNNING, null, 0));
        try {
            // Execute the pipeline following the EXECUTION PLAN
            Sentiment_Analyzer_iLU();
            // Mark status as OK + duration
            status.set(new Status(Status.State.OK, null, System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            // Mark status as ERR + message + duration
            status.set(new Status(Status.State.ERR, e.getMessage(), System.currentTimeMillis() - startTime));
        }
    }

    private void Sentiment_Analyzer_iLU() throws Exception {
        // Get the chat string from the chatInput variable
        String text = (String) chatInput;
        // Sanitize the input using OWASP library
        PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
        text = policy.sanitize(text);

        // Call the AI provider at base URL using model
        String apiKey = System.getenv("VCPU_" + PROVIDER_SLUG + "_API_KEY");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"text\": \"" + text + "\"}"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse the JSON response
        String responseBody = response.body();
        // Assuming the response body is a JSON object with fields "sentiment" and "confidence"
        // For simplicity, we use a simple JSON parser
        int sentimentIndex = responseBody.indexOf("\"sentiment\":");
        int confidenceIndex = responseBody.indexOf("\"confidence\":");
        String sentimentValue = responseBody.substring(sentimentIndex + 12, responseBody.indexOf("\"", sentimentIndex + 12));
        String confidenceValue = responseBody.substring(confidenceIndex + 14, responseBody.indexOf("\"", confidenceIndex + 14));
        sentiment = sentimentValue;
        confidence = Float.parseFloat(confidenceValue);

        // Transform via Type_Validator_iSBU_ilu-1775828734058_imem-1775828862904
        Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(sentiment);
        Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(confidence);
    }

    private void Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(String value) {
        // Must contain a string
        if (value == null || value.isEmpty()) {
            throw new RuntimeException("Invalid sentiment value");
        }
    }

    private void Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(Float value) {
        // Must be a number between 0.0 and 1.0, if invalid default to 0.5
        if (value == null || value < 0.0 || value > 1.0) {
            confidence = 0.5f;
        }
    }

    private void Custom_iSBU_imem_1775828537403_ilu_1775828734058(String value) {
        // Data must be plain text and not contain any harmful content like SQL injection, XSS or any other related security issue that can cause service disruption
        // Use an adequate library to implement all the needed controls
        PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
        value = policy.sanitize(value);
    }
}