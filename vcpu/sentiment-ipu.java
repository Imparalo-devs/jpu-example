import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Sentiment iPU: Analyze sentiment from a chat.
 * 
 * Input iMEM:
 * - chatInput (variable): any
 * - testMap (map): Record<string, any>
 * 
 * Output iMEM:
 * - sentiment (variable): String
 * - confidence (variable): float
 */
public class SentimentIPU {
    private volatile String status = "IDLE";
    private volatile String errorMessage = "";
    private volatile long duration = 0;
    private volatile long startTime = 0;

    private Object chatInput = null;
    private java.util.Map<String, Object> testMap = new java.util.HashMap<>();

    private String sentiment = null;
    private Float confidence = null;

    private HttpClient client = HttpClient.newHttpClient();

    public void writeInput(java.util.Map<String, Object> inputMap) {
        // Write input values into their iMEM variables
        for (java.util.Map.Entry<String, Object> entry : inputMap.entrySet()) {
            if ("chatInput".equals(entry.getKey())) {
                this.chatInput = entry.getValue();
            }
            if ("testMap".equals(entry.getKey())) {
                this.testMap = (java.util.Map<String, Object>) entry.getValue();
            }
        }
        // Fire the runner asynchronously
        CompletableFuture.runAsync(this::runSentimentiPU);
    }

    public java.util.Map<String, Object> readOutput() {
        // Read the output iMEM values
        java.util.Map<String, Object> outputMap = new java.util.HashMap<>();
        outputMap.put("sentiment", this.sentiment);
        outputMap.put("confidence", this.confidence);
        return outputMap;
    }

    private void runSentimentiPU() {
        try {
            this.status = "RUNNING";
            this.startTime = System.currentTimeMillis();

            // Execute the pipeline following the EXECUTION PLAN
            this.Sentiment_Analyzer_iLU();

            this.status = "OK";
            this.duration = System.currentTimeMillis() - this.startTime;
        } catch (Exception e) {
            this.status = "ERR";
            this.errorMessage = e.getMessage();
            this.duration = System.currentTimeMillis() - this.startTime;
        }
    }

    private void Sentiment_Analyzer_iLU() throws Exception {
        // Get the API key from the environment variable
        String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");

        // Create the HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.example.com/sentiment"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"text\": \"" + this.chatInput + "\"}"))
                .build();

        // Send the HTTP request and get the response
        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse the JSON response
        java.util.Map<String, Object> resultMap = new java.util.HashMap<>();
        resultMap = parseJson(response.body());

        // Update the output iMEM variables
        this.sentiment = (String) resultMap.get("sentiment");
        this.confidence = (Float) resultMap.get("confidence");

        // Apply the iSBU functions
        this.sentiment = Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(this.sentiment);
        this.confidence = Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(this.confidence);
    }

    private java.util.Map<String, Object> parseJson(String json) {
        // Implement a simple JSON parser or use a library like Jackson
        // For simplicity, assume the JSON is in the format: {"sentiment": "positive", "confidence": 0.8}
        java.util.Map<String, Object> resultMap = new java.util.HashMap<>();
        String[] parts = json.substring(1, json.length() - 1).split(",");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue[0].trim().equals("\"sentiment\"")) {
                resultMap.put("sentiment", keyValue[1].trim().replace("\"", ""));
            } else if (keyValue[0].trim().equals("\"confidence\"")) {
                resultMap.put("confidence", Float.parseFloat(keyValue[1].trim()));
            }
        }
        return resultMap;
    }

    private String Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(String input) {
        // Custom logic: must contain a string
        if (input == null || input.isEmpty()) {
            return "";
        }
        return input;
    }

    private Float Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(Float input) {
        // Custom logic: must be a number between 0.0 and 1.0, if invalid default to 0.5
        if (input == null || input < 0.0 || input > 1.0) {
            return 0.5f;
        }
        return input;
    }

    private Object Passthrough_iSBU_imem_1775977935330_ilu_1775828734058(Object input) {
        // Passthrough logic: return the input unchanged
        return input;
    }

    private Object Custom_iSBU_imem_1775828537403_ilu_1775828734058(Object input) {
        // Custom logic: data must be plain text and not contain any harmful content
        if (input == null || input.toString().isEmpty()) {
            return "";
        }
        // Implement security checks for SQL injection, XSS, etc.
        return input;
    }
}