import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Sentiment iPU: Analyze sentiment from a chat.
 * 
 * Input iMEM: chatInput (any)
 * Output iMEM: sentiment (String), confidence (float)
 */
public class SentimentIPU {
    private static volatile String status = "IDLE";
    private static volatile String errorMessage = "";
    private static volatile long duration = 0;
    private static volatile long startTime = 0;

    private static Object chatInput = null;
    private static String sentiment = null;
    private static Float confidence = null;

    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) {
        // Example usage:
        writeInput("chatInput", "I love this product!");
    }

    public static void writeInput(String key, Object value) {
        if ("chatInput".equals(key)) {
            chatInput = value;
            CompletableFuture.runAsync(SentimentIPU::runSentimentiPU);
        }
    }

    public static void runSentimentiPU() {
        try {
            status = "RUNNING";
            startTime = System.currentTimeMillis();

            // Write input values into their iMEM variables
            Object text = chatInput;

            // Execute the pipeline following the EXECUTION PLAN
            CompletableFuture<SentimentResponse> sentimentFuture = Sentiment_Analyzer_iLU(text);
            SentimentResponse sentimentResponse = sentimentFuture.get();

            // Transform via Type_Validator_iSBU_ilu-1775828734058_imem-1775828862904
            sentiment = Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(sentimentResponse.getSentiment());
            confidence = Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(sentimentResponse.getConfidence());

            // Mark status as OK and record duration
            status = "OK";
            duration = System.currentTimeMillis() - startTime;
        } catch (Exception e) {
            // Mark status as ERR and record error message and duration
            status = "ERR";
            errorMessage = e.getMessage();
            duration = System.currentTimeMillis() - startTime;
        }
    }

    public static CompletableFuture<SentimentResponse> Sentiment_Analyzer_iLU(Object text) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get API key from environment variable
                String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");

                // Set up HTTP request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.example.com/sentiment"))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"text\": \"" + text + "\"}"))
                        .build();

                // Send HTTP request and get response
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Parse JSON response
                SentimentResponse sentimentResponse = new SentimentResponse();
                sentimentResponse.setSentiment(response.body().split("\"sentiment\":\"")[1].split("\",")[0]);
                sentimentResponse.setConfidence(Float.parseFloat(response.body().split("\"confidence\":")[1].split("}")[0]));

                return sentimentResponse;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static String Type_Validator_iSBU_imem_1775828537403_ilu_1775828734058(Object data) {
        // Custom logic: data must be plain text and not contain any harmful content
        if (data instanceof String) {
            String text = (String) data;
            if (text.contains("<script>") || text.contains("SELECT") || text.contains("INSERT") || text.contains("UPDATE") || text.contains("DELETE")) {
                return "Invalid input";
            } else {
                return text;
            }
        } else {
            return "Invalid input";
        }
    }

    public static String Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(Object data) {
        // Custom logic: must contain a string
        if (data instanceof String) {
            return (String) data;
        } else {
            return "Invalid input";
        }
    }

    public static Float Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(Object data) {
        // Custom logic: must be a number between 0.0 and 1.0, if invalid default to 0.5
        if (data instanceof Float) {
            Float confidence = (Float) data;
            if (confidence >= 0.0 && confidence <= 1.0) {
                return confidence;
            } else {
                return 0.5f;
            }
        } else {
            return 0.5f;
        }
    }

    public static class SentimentResponse {
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