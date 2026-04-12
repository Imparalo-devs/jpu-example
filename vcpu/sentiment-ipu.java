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
    // iMEM variables
    private Object chatInput = null;
    private String sentiment = null;
    private Float confidence = null;

    // Status object
    private volatile Status status = new Status();

    // iLU functions
    public void Sentiment_Analyzer_iLU(String text) {
        // Get API key from environment variable
        String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");

        // Set up HTTP client and request
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("https://api.example.com/sentiment"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString("{\"text\": \"" + text + "\"}"))
                .build();

        // Send request and get response
        client.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    // Parse JSON response
                    org.json.JSONObject jsonObject = new org.json.JSONObject(response.body());
                    String sentiment = jsonObject.getString("sentiment");
                    double confidence = jsonObject.getDouble("confidence");

                    // Update iMEM variables
                    this.sentiment = sentiment;
                    this.confidence = (float) confidence;

                    return null;
                })
                .join();
    }

    // iSBU functions
    public boolean Custom_iSBU_imem_1775828537403_ilu_1775828734058(String data) {
        // Check for harmful content
        if (data.contains("SELECT") || data.contains("INSERT") || data.contains("UPDATE") || data.contains("DELETE")) {
            return false;
        }
        if (data.contains("<script>") || data.contains("</script>")) {
            return false;
        }
        return true;
    }

    public boolean Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(String data) {
        // Check if data is a string
        return data instanceof String;
    }

    public boolean Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(Float data) {
        // Check if data is a number between 0.0 and 1.0
        if (data == null) {
            return false;
        }
        if (data < 0.0 || data > 1.0) {
            return false;
        }
        return true;
    }

    // Main runner function
    public void runSentimentiPU(Object chatInput) {
        // Mark status as RUNNING and record start time
        status.setState(Status.State.RUNNING);
        long startTime = System.currentTimeMillis();

        try {
            // Write input values into iMEM variables
            this.chatInput = chatInput;

            // Execute pipeline
            Sentiment_Analyzer_iLU((String) chatInput);

            // Mark status as OK and record duration
            status.setState(Status.State.OK);
            status.setDuration(System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            // Mark status as ERR and record duration
            status.setState(Status.State.ERR);
            status.setErrorMessage(e.getMessage());
            status.setDuration(System.currentTimeMillis() - startTime);
        }
    }

    // Write input trigger function
    // To write data and trigger the execution, call this function with the key and value, 
    // for example: writeInput("chatInput", "Hello, how are you?");
    public void writeInput(String key, Object value) {
        if ("chatInput".equals(key)) {
            this.chatInput = value;
        }
        // Fire runner asynchronously
        java.util.concurrent.CompletableFuture.runAsync(() -> runSentimentiPU(chatInput));
    }

    // Read output function
    // To get the output data, call this function and it will return a map of the variables and their values.
    // For example: Map<String, Object> output = readOutput();
    public java.util.Map<String, Object> readOutput() {
        java.util.Map<String, Object> output = new java.util.HashMap<>();
        output.put("sentiment", sentiment);
        output.put("confidence", confidence);
        return output;
    }

    // Status class
    private static class Status {
        public enum State {
            IDLE,
            RUNNING,
            OK,
            ERR
        }

        private State state = State.IDLE;
        private String errorMessage = null;
        private long duration = 0;

        public void setState(State state) {
            this.state = state;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public State getState() {
            return state;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public long getDuration() {
            return duration;
        }
    }
}