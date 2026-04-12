/**
 * Sentiment iPU: Analyze sentiment from a chat.
 * 
 * Input iMEM:
 *   - chatInput (any): this queue holds chat conversations
 * 
 * Output iMEM:
 *   - sentiment (String): this variable holds a string containing current sentiment
 *   - confidence (float): this variable holds a sentiment score 0.0->1.0
 */
public class SentimentIPU {
    private volatile Status status = new Status();
    private Object chatInput = null;
    private String sentiment = null;
    private Float confidence = null;

    private static class Status {
        private volatile String state = "IDLE";
        private volatile String errorMessage = null;
        private volatile long duration = 0;
    }

    public void writeInput(String key, Object value) {
        // To write data and trigger the execution, simply call this function with the key and value.
        // For example: writeInput("chatInput", "Hello, how are you?");
        if ("chatInput".equals(key)) {
            this.chatInput = value;
            CompletableFuture.runAsync(this::runSentimentiPU);
        }
    }

    public java.util.Map<String, Object> readOutput() {
        // To get/read the data, simply call this function and it will return a map of the variables and their values.
        // For example: Map<String, Object> output = readOutput();
        java.util.Map<String, Object> output = new java.util.HashMap<>();
        output.put("sentiment", sentiment);
        output.put("confidence", confidence);
        return output;
    }

    private void runSentimentiPU() {
        long startTime = System.currentTimeMillis();
        status.state = "RUNNING";
        try {
            sentiment = Sentiment_Analyzer_iLU(chatInput.toString());
            confidence = (float) 0.5; // default confidence
            Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(sentiment);
            Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(confidence);
            status.state = "OK";
        } catch (Exception e) {
            status.state = "ERR";
            status.errorMessage = e.getMessage();
        } finally {
            status.duration = System.currentTimeMillis() - startTime;
        }
    }

    private String Sentiment_Analyzer_iLU(String text) {
        String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");
        if (apiKey == null) {
            throw new RuntimeException("API key is not set");
        }
        String baseUrl = "https://api.example.com/sentiment";
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(baseUrl))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString("{\"text\":\"" + text + "\"}"))
                .build();
        try {
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to analyze sentiment: " + response.body());
            }
            org.json.JSONObject jsonObject = new org.json.JSONObject(response.body());
            return jsonObject.getString("sentiment");
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze sentiment", e);
        }
    }

    private void Custom_iSBU_imem_1775828537403_ilu_1775828734058(String text) {
        // data must be plain text and not contain any harmful content like sql injection, XSS or any other related security issue that can cause service disruption
        if (text == null || !text.getClass().equals(String.class)) {
            throw new RuntimeException("Input must be a string");
        }
        if (text.contains("SELECT") || text.contains("INSERT") || text.contains("UPDATE") || text.contains("DELETE")) {
            throw new RuntimeException("Input contains SQL injection");
        }
        if (text.contains("<script>") || text.contains("</script>")) {
            throw new RuntimeException("Input contains XSS");
        }
    }

    private void Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(String sentiment) {
        // must contain a string
        if (sentiment == null || sentiment.isEmpty()) {
            throw new RuntimeException("Sentiment must be a non-empty string");
        }
    }

    private void Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(Float confidence) {
        // must be a number between 0.0 and 1.0, if invalid default to 0.5
        if (confidence == null || confidence < 0.0 || confidence > 1.0) {
            confidence = 0.5f;
        }
    }
}