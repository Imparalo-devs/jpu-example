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
    private volatile AtomicReference<Status> status = new AtomicReference<>(new Status());
    private Object chatInput = null;
    private java.util.Map<String, Object> testMap = new java.util.HashMap<>();
    private String sentiment = null;
    private Float confidence = null;

    private static class Status {
        private String state = "IDLE";
        private String errorMessage = "";
        private long duration = 0;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
    }

    public void writeInput(java.util.Map<String, Object> input) {
        // To write data and trigger the execution, call this function with a map containing the input values.
        // For example: writeInput(java.util.Map.of("chatInput", "Hello, world!", "testMap", new java.util.HashMap<>()));
        input.forEach((key, value) -> {
            if ("chatInput".equals(key)) this.chatInput = value;
            if ("testMap".equals(key)) this.testMap = (java.util.Map<String, Object>) value;
        });
        CompletableFuture.runAsync(this::runSentimentiPU);
    }

    public java.util.Map<String, Object> readOutput() {
        // To get/read the data, call this function after the execution has completed.
        // It returns a map containing the output values.
        return java.util.Map.of("sentiment", sentiment, "confidence", confidence);
    }

    private void runSentimentiPU() {
        long startTime = System.currentTimeMillis();
        status.get().setState("RUNNING");
        try {
            Sentiment_Analyzer_iLU();
            Custom_iSBU_imem_1775828537403_ilu_1775828734058();
            Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904();
            Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340();
            Passthrough_iSBU_imem_1775977935330_ilu_1775828734058();
            status.get().setState("OK");
        } catch (Exception e) {
            status.get().setState("ERR");
            status.get().setErrorMessage(e.getMessage());
        } finally {
            status.get().setDuration(System.currentTimeMillis() - startTime);
        }
    }

    private void Sentiment_Analyzer_iLU() throws Exception {
        String text = (String) chatInput;
        String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");
        String baseUrl = "https://api.example.com";
        String model = "sentiment-analysis";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/sentiment"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"text\": \"" + text + "\"}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Failed to analyze sentiment");
        }

        java.util.Map<String, Object> result = java.util.Map.of(
                "sentiment", response.body().split("\"sentiment\":\"")[1].split("\",\"confidence\"")[0],
                "confidence", Float.parseFloat(response.body().split("\"confidence\":")[1].split("}")[0])
        );

        sentiment = (String) result.get("sentiment");
        confidence = (Float) result.get("confidence");
    }

    private void Custom_iSBU_imem_1775828537403_ilu_1775828734058() {
        // Custom logic: data must be plain text and not contain any harmful content like SQL injection, XSS or any other related security issue that can cause service disruption
        if (chatInput != null && !(chatInput instanceof String)) {
            throw new RuntimeException("Chat input must be a string");
        }
    }

    private void Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904() {
        // Custom logic: must contain a string
        if (sentiment == null || !(sentiment instanceof String)) {
            throw new RuntimeException("Sentiment must be a string");
        }
    }

    private void Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340() {
        // Custom logic: must be a number between 0.0 and 1.0, if invalid default to 0.5
        if (confidence == null || !(confidence instanceof Float) || confidence < 0.0f || confidence > 1.0f) {
            confidence = 0.5f;
        }
    }

    private void Passthrough_iSBU_imem_1775977935330_ilu_1775828734058() {
        // Custom logic: foreach K,V concatenate in the prompt key:k, value:v
        java.util.Map<String, Object> prompt = new java.util.HashMap<>();
        prompt.put("chatInput", chatInput);
        prompt.put("testMap", testMap);
        prompt.put("sentiment", sentiment);
        prompt.put("confidence", confidence);
        // Use the prompt map as needed
    }
}