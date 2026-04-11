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
    private volatile String status = "IDLE";
    private volatile long startTime;
    private volatile String errorMessage;
    private volatile long duration;

    private Object chatInput = null;
    private String sentiment = null;
    private Float confidence = null;

    private HttpClient httpClient = HttpClient.newHttpClient();

    public void writeInput(String key, Object value) {
        if ("chatInput".equals(key)) {
            this.chatInput = value;
        }
        CompletableFuture.runAsync(this::runSentimentiPU);
    }

    private void runSentimentiPU() {
        try {
            status = "RUNNING";
            startTime = System.currentTimeMillis();

            sentiment = Sentiment_Analyzer_iLU((String) chatInput);
            confidence = Confidence_Analyzer_iLU((String) chatInput);

            sentiment = Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(sentiment);
            confidence = Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(confidence);

            status = "OK";
            duration = System.currentTimeMillis() - startTime;
        } catch (Exception e) {
            status = "ERR";
            errorMessage = e.getMessage();
            duration = System.currentTimeMillis() - startTime;
        }
    }

    private String Sentiment_Analyzer_iLU(String text) throws Exception {
        String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");
        String baseUrl = "https://api.example.com/sentiment";
        String requestBody = "{\"text\": \"" + text + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Failed to analyze sentiment");
        }

        String sentiment = response.body().split("\"sentiment\":\"")[1].split("\"")[0];
        return sentiment;
    }

    private Float Confidence_Analyzer_iLU(String text) throws Exception {
        String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");
        String baseUrl = "https://api.example.com/confidence";
        String requestBody = "{\"text\": \"" + text + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Failed to analyze confidence");
        }

        String confidence = response.body().split("\"confidence\":")[1].split(",")[0];
        return Float.parseFloat(confidence);
    }

    private String Type_Validator_iSBU_ilu_1775828734058_imem_1775828537403(String data) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        if (data.contains("SELECT") || data.contains("INSERT") || data.contains("UPDATE") || data.contains("DELETE")) {
            return "";
        }
        if (data.contains("<script>") || data.contains("</script>")) {
            return "";
        }
        return data;
    }

    private String Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(String data) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        if (!data.getClass().equals(String.class)) {
            return "";
        }
        return data;
    }

    private Float Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(Float data) {
        if (data == null) {
            return 0.5f;
        }
        if (data < 0.0 || data > 1.0) {
            return 0.5f;
        }
        return data;
    }
}