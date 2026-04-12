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
    private volatile AtomicReference<Status> status = new AtomicReference<>(new Status("IDLE", 0, null));
    private Object chatInput = null;
    private String sentiment = null;
    private Float confidence = null;

    private static class Status {
        private String state;
        private long duration;
        private String errorMessage;

        public Status(String state, long duration, String errorMessage) {
            this.state = state;
            this.duration = duration;
            this.errorMessage = errorMessage;
        }
    }

    public void writeInput(Map<String, Object> inputMap) {
        // Write input values into their iMEM variables, then fire the runner asynchronously
        // To write data and trigger the execution, simply call writeInput() with a map containing the input values
        // Example: writeInput(Map.of("chatInput", "Hello, world!"));
        for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
            if ("chatInput".equals(entry.getKey())) {
                this.chatInput = entry.getValue();
            }
        }
        CompletableFuture.runAsync(this::runSentimentiPU);
    }

    public Map<String, Object> readOutput() {
        // To get/read the data, simply call readOutput() and access the returned map
        // Example: Map<String, Object> output = readOutput();
        Map<String, Object> outputMap = new HashMap<>();
        outputMap.put("sentiment", sentiment);
        outputMap.put("confidence", confidence);
        return outputMap;
    }

    private void runSentimentiPU() {
        long startTime = System.currentTimeMillis();
        status.set(new Status("RUNNING", 0, null));
        try {
            // Execute the pipeline following the EXECUTION PLAN
            Sentiment_Analyzer_iLU();
            Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904();
            Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340();
            status.set(new Status("OK", System.currentTimeMillis() - startTime, null));
        } catch (Exception e) {
            status.set(new Status("ERR", System.currentTimeMillis() - startTime, e.getMessage()));
        }
    }

    private void Sentiment_Analyzer_iLU() throws Exception {
        // Get the API key from the environment variable
        String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");
        if (apiKey == null) {
            throw new Exception("API key not found");
        }

        // Sanitize the input text using OWASP library
        PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
        String sanitizedText = policy.sanitize((String) chatInput);

        // Create the HTTP request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.example.com/sentiment"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"text\": \"" + sanitizedText + "\"}"))
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse the JSON response
        String responseBody = response.body();
        // Assuming the response body is a JSON object with "sentiment" and "confidence" fields
        // You may need to use a JSON parsing library like Jackson or Gson to parse the response
        // For simplicity, this example assumes the response body is a simple JSON object
        int sentimentIndex = responseBody.indexOf("\"sentiment\":\"");
        int confidenceIndex = responseBody.indexOf("\"confidence\":");
        if (sentimentIndex != -1 && confidenceIndex != -1) {
            int sentimentEndIndex = responseBody.indexOf("\"", sentimentIndex + 12);
            int confidenceEndIndex = responseBody.indexOf(",", confidenceIndex + 13);
            if (sentimentEndIndex != -1 && confidenceEndIndex != -1) {
                sentiment = responseBody.substring(sentimentIndex + 12, sentimentEndIndex);
                confidence = Float.parseFloat(responseBody.substring(confidenceIndex + 13, confidenceEndIndex));
            }
        }
    }

    private void Custom_iSBU_imem_1775828537403_ilu_1775828734058() {
        // This function is not used in the current implementation
    }

    private void Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904() {
        // Validate if the sentiment is a string
        if (sentiment != null && !(sentiment instanceof String)) {
            throw new RuntimeException("Sentiment must be a string");
        }
    }

    private void Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340() {
        // Validate if the confidence is a number between 0.0 and 1.0
        if (confidence != null && !(confidence instanceof Float)) {
            throw new RuntimeException("Confidence must be a float");
        }
        if (confidence < 0.0f || confidence > 1.0f) {
            confidence = 0.5f; // Default to 0.5 if invalid
        }
    }
}