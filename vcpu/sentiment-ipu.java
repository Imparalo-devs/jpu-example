import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Sentiment iPU: Analyze sentiment from a chat.
 * 
 * Input iMEM: chatInput (any)
 * Output iMEM: sentiment (String), confidence (float)
 */
public class SentimentIPU {
    private volatile String status = "IDLE";
    private volatile String errorMessage = "";
    private volatile long duration = 0;
    private volatile long startTime = 0;

    private Object chatInput = null;
    private String sentiment = null;
    private Float confidence = null;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void writeInput(Map<String, Object> input) {
        // Write input values into their iMEM variables, then fire the runner asynchronously
        // To write data and trigger the execution, simply call this function with a map containing the input values
        // Example: writeInput(Collections.singletonMap("chatInput", "Hello, world!"));
        input.forEach((key, value) -> {
            if ("chatInput".equals(key)) {
                this.chatInput = value;
            }
        });
        CompletableFuture.runAsync(this::runSentimentiPU);
    }

    public Map<String, Object> readOutput() {
        // To get/read the data, simply call this function and retrieve the output values from the returned map
        // Example: Map<String, Object> output = readOutput();
        Map<String, Object> output = new HashMap<>();
        output.put("sentiment", sentiment);
        output.put("confidence", confidence);
        return output;
    }

    private void runSentimentiPU() {
        try {
            status = "RUNNING";
            startTime = System.currentTimeMillis();
            if (chatInput != null) {
                Sentiment_Analyzer_iLU(chatInput.toString());
                Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(sentiment);
                Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(confidence);
            }
            status = "OK";
            duration = System.currentTimeMillis() - startTime;
        } catch (Exception e) {
            status = "ERR";
            errorMessage = e.getMessage();
            duration = System.currentTimeMillis() - startTime;
        }
    }

    private void Sentiment_Analyzer_iLU(String text) throws Exception {
        // Get API key from environment variable
        String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");
        if (apiKey == null) {
            throw new Exception("API key not found");
        }

        // Set up HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.example.com/sentiment"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"text\": \"" + text + "\"}"))
                .build();

        // Send HTTP request and get response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse JSON response
        String responseBody = response.body();
        // Assuming the response body is a JSON object with "sentiment" and "confidence" fields
        // You may need to use a JSON parsing library like Jackson or Gson to parse the response body
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

    private String Type_Validator_iSBU_imem_1775828537403_ilu_1775828734058(String text) {
        // Use OWASP ESAPI to validate and sanitize the input text
        // For simplicity, this example assumes the input text is plain text and does not contain any harmful content
        return text;
    }

    private String Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(String text) {
        // Validate if the text contains a string
        if (text != null && !text.isEmpty()) {
            return text;
        } else {
            throw new RuntimeException("Invalid input: text must contain a string");
        }
    }

    private Float Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(Float confidence) {
        // Validate if the confidence is a number between 0.0 and 1.0
        if (confidence != null && confidence >= 0.0 && confidence <= 1.0) {
            return confidence;
        } else {
            return 0.5f; // Default to 0.5 if invalid
        }
    }
}