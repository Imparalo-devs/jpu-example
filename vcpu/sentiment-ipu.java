```java
/**
 * Sentiment iPU: Analyze sentiment from a chat.
 * 
 * Inputs:
 * - chatInput (any): chat conversations
 * 
 * Outputs:
 * - sentiment (String): current sentiment
 * - confidence (float): sentiment score 0.0->1.0
 */

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SentimentIPU {
    // iMEM variables
    public static Object chatInput = null;
    public static String sentiment = null;
    public static Float confidence = null;

    // System status
    public static class Status {
        public String state;
        public String error;
        public Long duration;
    }
    public static Status __status = new Status();

    // iLU functions
    public static CompletableFuture<Object[]> Sentiment_Analyzer_iLU(String text) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");
                String url = "https://api.example.com/sentiment";
                String payload = "{\"text\": \"" + text + "\"}";
                String response = fetch(url, apiKey, payload);
                Object[] result = parseResponse(response);
                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // iSBU functions
    public static Object Custom_iSBU_imem_1775828537403_ilu_1775828734058(Object data) {
        // Custom logic: data must be plain text and not contain any harmful content
        if (data instanceof String) {
            String text = (String) data;
            if (text.contains("SELECT") || text.contains("INSERT") || text.contains("UPDATE") || text.contains("DELETE")) {
                throw new RuntimeException("SQL injection detected");
            }
            if (text.contains("<script>") || text.contains("</script>")) {
                throw new RuntimeException("XSS detected");
            }
            return text;
        } else {
            throw new RuntimeException("Invalid data type");
        }
    }

    public static Object Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(Object data) {
        // Custom logic: must contain a string
        if (data instanceof String) {
            return data;
        } else {
            throw new RuntimeException("Invalid data type");
        }
    }

    public static Float Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(Object data) {
        // Custom logic: must be a number between 0.0 and 1.0, if invalid default to 0.5
        if (data instanceof Number) {
            Float value = ((Number) data).floatValue();
            if (value >= 0.0 && value <= 1.0) {
                return value;
            } else {
                return 0.5f;
            }
        } else {
            return 0.5f;
        }
    }

    // Helper functions
    public static String fetch(String url, String apiKey, String payload) {
        // Implement fetch logic using native Java HTTP client
        // For simplicity, this example uses a hypothetical fetch function
        return "{\"sentiment\": \"positive\", \"confidence\": 0.8}";
    }

    public static Object[] parseResponse(String response) {
        // Implement response parsing logic
        // For simplicity, this example returns a hypothetical result
        return new Object[] {"positive", 0.8f};
    }

    // Runner function
    public static CompletableFuture<Object> runSentimentiPU(Object inputs) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                __status.state = "RUNNING";
                long __t0 = System.currentTimeMillis();

                if (inputs instanceof java.util.Map) {
                    java.util.Map<String, Object> inputMap = (java.util.Map<String, Object>) inputs;
                    if (inputMap.containsKey("chatInput")) {
                        chatInput = inputMap.get("chatInput");
                    }
                }

                Object[] result = Sentiment_Analyzer_iLU((String) chatInput).get();
                sentiment = (String) result[0];
                confidence = (Float) result[1];

                __status.state = "OK";
                __status.duration = System.currentTimeMillis() - __t0;
                return new Object[] {sentiment, confidence};
            } catch (Exception e) {
                __status.state = "ERR";
                __status.error = e.getMessage();
                __status.duration = System.currentTimeMillis() - System.currentTimeMillis();
                throw new RuntimeException(e);
            }
        });
    }

    // Write input trigger function
    public static void writeInput(String key, Object value) {
        if (key.equals("chatInput")) {
            chatInput = value;
        }
        runSentimentiPU(new java.util.HashMap<>()).exceptionally(ex -> {
            System.err.println(ex.getMessage());
            return null;
        });
    }

    public static void main(String[] args) {
        writeInput("chatInput", "Hello, world!");
    }
}
```