import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Sentiment iPU: Analyze sentiment from a chat.
 * 
 * Input iMEM:
 * - chatInput (any): this queue holds chat conversations
 * 
 * Output iMEM:
 * - sentiment (String): this variable holds a string containing current sentiment
 * - confidence (float): this variable holds a sentiment score 0.0->1.0
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

    public void writeInput(Map<String, Object> inputMap) {
        // Write input values into their iMEM variables, then fire the runner asynchronously
        // To write data and trigger the execution, simply call this function with a map containing the input values
        // For example: writeInput(Map.of("chatInput", "Hello, world!"));
        for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
            if ("chatInput".equals(entry.getKey())) {
                this.chatInput = entry.getValue();
            }
        }
        CompletableFuture.runAsync(this::runSentimentiPU);
    }

    public Map<String, Object> readOutput() {
        // To get/read the data, simply call this function and it will return a map containing the output values
        // For example: Map<String, Object> output = readOutput();
        Map<String, Object> outputMap = new HashMap<>();
        outputMap.put("sentiment", sentiment);
        outputMap.put("confidence", confidence);
        return outputMap;
    }

    private void runSentimentiPU() {
        try {
            status = "RUNNING";
            startTime = System.currentTimeMillis();
            Sentiment_Analyzer_iLUResponse response = Sentiment_Analyzer_iLU((String) chatInput);
            sentiment = response.getSentiment();
            confidence = response.getConfidence();
            Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(sentiment);
            Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(confidence);
            status = "OK";
            duration = System.currentTimeMillis() - startTime;
        } catch (Exception e) {
            status = "ERR";
            errorMessage = e.getMessage();
            duration = System.currentTimeMillis() - startTime;
        }
    }

    private Sentiment_Analyzer_iLUResponse Sentiment_Analyzer_iLU(String text) throws Exception {
        String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");
        String baseUrl = "https://api.example.com/sentiment";
        String model = "sentiment-analyzer";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"text\": \"" + text + "\"}"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Failed to analyze sentiment: " + response.body());
        }

        Sentiment_Analyzer_iLUResponse sentimentResponse = new Sentiment_Analyzer_iLUResponse();
        sentimentResponse.setSentiment(response.json().getString("sentiment"));
        sentimentResponse.setConfidence(response.json().getFloat("confidence"));
        return sentimentResponse;
    }

    private void Custom_iSBU_imem_1775828537403_ilu_1775828734058(String text) {
        // Custom logic to validate plain text and prevent security issues
        // For example, you can use a library like OWASP ESAPI to validate and sanitize the input text
    }

    private void Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(String text) {
        if (text == null || !text.getClass().equals(String.class)) {
            throw new RuntimeException("Invalid sentiment type: " + text);
        }
    }

    private void Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(Float confidence) {
        if (confidence == null || confidence < 0.0 || confidence > 1.0) {
            confidence = 0.5f;
        }
    }

    private static class Sentiment_Analyzer_iLUResponse {
        private String sentiment;
        private Float confidence;

        public String getSentiment() {
            return sentiment;
        }

        public void setSentiment(String sentiment) {
            this.sentiment = sentiment;
        }

        public Float getConfidence() {
            return confidence;
        }

        public void setConfidence(Float confidence) {
            this.confidence = confidence;
        }
    }
}