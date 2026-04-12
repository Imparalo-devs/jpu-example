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

        public synchronized void setState(String state) {
            this.state = state;
        }

        public synchronized void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public synchronized void setDuration(long duration) {
            this.duration = duration;
        }

        public synchronized String getState() {
            return state;
        }

        public synchronized String getErrorMessage() {
            return errorMessage;
        }

        public synchronized long getDuration() {
            return duration;
        }
    }

    public void writeInput(java.util.Map<String, Object> input) {
        // To write data and trigger the execution, simply call this function with a map containing the input values.
        // For example: writeInput(Collections.singletonMap("chatInput", "Hello, world!"));
        for (java.util.Map.Entry<String, Object> entry : input.entrySet()) {
            if ("chatInput".equals(entry.getKey())) {
                this.chatInput = entry.getValue();
            }
        }
        java.util.concurrent.CompletableFuture.runAsync(this::runSentimentiPU);
    }

    public java.util.Map<String, Object> readOutput() {
        // To get/read the data, simply call this function and it will return a map containing the output values.
        // For example: Map<String, Object> output = readOutput();
        java.util.Map<String, Object> output = new java.util.HashMap<>();
        output.put("sentiment", sentiment);
        output.put("confidence", confidence);
        return output;
    }

    private void runSentimentiPU() {
        long startTime = System.currentTimeMillis();
        status.setState("RUNNING");
        try {
            sentiment = null;
            confidence = null;
            java.util.Map<String, Object> output = Sentiment_Analyzer_iLU(chatInput);
            sentiment = (String) output.get("sentiment");
            confidence = (Float) output.get("confidence");
            sentiment = Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(sentiment);
            confidence = Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(confidence);
            status.setState("OK");
        } catch (Exception e) {
            status.setErrorMessage(e.getMessage());
            status.setState("ERR");
        } finally {
            status.setDuration(System.currentTimeMillis() - startTime);
        }
    }

    private java.util.Map<String, Object> Sentiment_Analyzer_iLU(Object text) throws Exception {
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("https://api.example.com/sentiment"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + System.getenv("VCPU_UNKNOWN_API_KEY"))
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString("{\"text\":\"" + text + "\"}"))
                .build();
        java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Failed to analyze sentiment: " + response.body());
        }
        java.util.Map<String, Object> output = new java.util.HashMap<>();
        org.json.JSONObject jsonObject = new org.json.JSONObject(response.body());
        output.put("sentiment", jsonObject.getString("sentiment"));
        output.put("confidence", jsonObject.getDouble("confidence"));
        return output;
    }

    private String Type_Validator_iSBU_imem_1775828537403_ilu_1775828734058(String text) {
        // Custom logic to validate plain text and prevent SQL injection, XSS, etc.
        // For simplicity, this example just checks for null or empty strings.
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text;
    }

    private String Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(String text) {
        // Custom logic to validate string type.
        if (text == null || !(text instanceof String)) {
            return "";
        }
        return text;
    }

    private Float Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(Float confidence) {
        // Custom logic to validate confidence value between 0.0 and 1.0.
        if (confidence == null || confidence < 0.0 || confidence > 1.0) {
            return 0.5f;
        }
        return confidence;
    }
}