import java.io.IOException;
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
 * Input iMEM: chatInput (any)
 * Output iMEM: sentiment (String), confidence (float)
 */
public class SentimentIPU {
    // iMEM variables
    private Object chatInput = null;
    private String sentiment = null;
    private Float confidence = null;

    // Status object
    private enum Status { IDLE, RUNNING, OK, ERR }
    private AtomicReference<Status> status = new AtomicReference<>(Status.IDLE);
    private String errorMessage = null;
    private long duration = 0;

    // API key
    private String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");

    // iLU functions
    private CompletableFuture<Map<String, Object>> Sentiment_Analyzer_iLU(Object text) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Create HTTP client and request
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.example.com/sentiment"))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"text\": \"" + text + "\"}"))
                        .build();

                // Send request and get response
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Parse JSON response
                String sentiment = response.body().split("\"sentiment\":\"")[1].split("\",\"confidence\"")[0];
                Float confidence = Float.parseFloat(response.body().split("\"confidence\":")[1].split("}")[0]);

                // Return output slots
                Map<String, Object> output = new HashMap<>();
                output.put("sentiment", sentiment);
                output.put("confidence", confidence);
                return output;
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // iSBU functions
    private Object Custom_iSBU_imem_1775828537403_ilu_1775828734058(Object data) {
        // Implement custom logic to validate plain text and prevent security issues
        // For example, using the OWASP ESAPI library
        // return ESAPI.validator().isValidInput("text", data.toString(), "^[a-zA-Z0-9\\s]+$", false);
        return data; // TO DO: implement custom logic
    }

    private Object Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(Object data) {
        // Implement custom logic to validate string type
        if (data instanceof String) {
            return data;
        } else {
            throw new RuntimeException("Invalid data type. Expected String.");
        }
    }

    private Object Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(Object data) {
        // Implement custom logic to validate number between 0.0 and 1.0
        if (data instanceof Float || data instanceof Double) {
            float value = ((Number) data).floatValue();
            if (value >= 0.0f && value <= 1.0f) {
                return data;
            } else {
                return 0.5f; // default value
            }
        } else {
            throw new RuntimeException("Invalid data type. Expected Number.");
        }
    }

    // Main runner function
    public CompletableFuture<Void> runSentimentiPU(Object chatInput) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Mark status as RUNNING and record start time
                long startTime = System.currentTimeMillis();
                status.set(Status.RUNNING);

                // Write input values into their iMEM variables
                this.chatInput = chatInput;

                // Execute the pipeline following the EXECUTION PLAN
                Map<String, Object> output = Sentiment_Analyzer_iLU(chatInput).get();
                sentiment = (String) output.get("sentiment");
                confidence = (Float) output.get("confidence");

                // Transform via Type_Validator_iSBU_ilu-1775828734058_imem-1775828862904
                sentiment = (String) Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(sentiment);

                // Mark status as OK and record duration
                status.set(Status.OK);
                duration = System.currentTimeMillis() - startTime;
            } catch (Exception e) {
                // Mark status as ERR and record error message and duration
                status.set(Status.ERR);
                errorMessage = e.getMessage();
                duration = System.currentTimeMillis() - System.currentTimeMillis();
            }
        });
    }

    // Write input trigger function
    /**
     * Write input values into their iMEM variables and fire the runner asynchronously.
     * To write data and trigger the execution, call this function with a Map containing the input values.
     * For example: writeInput(Map.of("chatInput", "Hello, world!"));
     */
    public CompletableFuture<Void> writeInput(Map<String, Object> input) {
        // Write input values into their iMEM variables
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            if ("chatInput".equals(entry.getKey())) {
                this.chatInput = entry.getValue();
            }
        }

        // Fire the runner asynchronously
        return runSentimentiPU(chatInput);
    }

    // Read output function
    /**
     * Get the output values from the iMEM variables.
     * To get the data, call this function and retrieve the values from the returned Map.
     * For example: Map<String, Object> output = readOutput();
     */
    public Map<String, Object> readOutput() {
        Map<String, Object> output = new HashMap<>();
        output.put("sentiment", sentiment);
        output.put("confidence", confidence);
        return output;
    }
}