import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sentiment iPU: Analyze sentiment from a chat.
 * 
 * Input iMEM: chatInput (any)
 * Output iMEM: results (map)
 */
public class SentimentIPU {

    // iMEM variables
    private static Map<String, Object> chatInput = new HashMap<>();
    private static Map<String, String> results = new HashMap<>();
    private static Object sanitizedInput = null;
    private static String __status = "idle";
    private static String __error = null;
    private static long __duration_ms = 0;

    // iLU: Sentiment_Analyzer_iLU
    public static CompletableFuture<Map<String, String>> Sentiment_Analyzer_iLU(String text) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Set API endpoint and model
                String baseUrl = "https://api.example.com";
                String model = "sentiment-analysis";

                // Set API key
                String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");

                // Create HTTP request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/" + model))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"text\": \"" + text + "\"}"))
                        .build();

                // Send HTTP request
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

                // Parse JSON response
                Map<String, String> output = new HashMap<>();
                output.put("sentiment", response.body().split("\"sentiment\":\"")[1].split("\",\"confidence\"")[0]);
                output.put("confidence", response.body().split("\"confidence\":\"")[1].split("\"}")[0]);

                return output;
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // iLU: Input_Security_Filter_Sanitization__iLU
    public static Object Input_Security_Filter_Sanitization__iLU(Object variableToCheck, int maxSize) {
        if (variableToCheck instanceof String) {
            String input = (String) variableToCheck;

            // Step 1: Size enforcement
            if (input.length() > maxSize) {
                input = input.substring(0, maxSize);
            }

            // Step 2: Normalization
            input = input.normalize(java.text.Normalizer.NFKC);

            // Step 3: Threat detection
            Pattern pattern = Pattern.compile(".*(UNION|SELECT|OR|\\;|&&|`|\\$|system|eval|function).*", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                return "";
            }

            // Step 4: Policy enforcement
            return input;
        } else if (variableToCheck instanceof Object[]) {
            Object[] array = (Object[]) variableToCheck;
            for (int i = 0; i < array.length; i++) {
                array[i] = Input_Security_Filter_Sanitization__iLU(array[i], maxSize);
            }
            return array;
        } else {
            return variableToCheck;
        }
    }

    // iSBU: Type_Validator_iSBU_ilu-1775828734058_imem-1775828862904
    public static Object Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(Object value) {
        if (value instanceof String) {
            if (!((String) value).contains("string")) {
                throw new RuntimeException("Invalid type");
            }
        }
        return value;
    }

    // Main runner function
    public static String runSentimentiPU(Object chatInputValue) {
        __status = "running";
        long startTime = System.currentTimeMillis();

        try {
            chatInput.clear();
            chatInput.put("chatInput", chatInputValue);

            // Stage 1: Parallel execution
            CompletableFuture<Map<String, String>> sentimentFuture = Sentiment_Analyzer_iLU((String) sanitizedInput);
            CompletableFuture<Object> sanitizationFuture = CompletableFuture.supplyAsync(() -> Input_Security_Filter_Sanitization__iLU(chatInputValue, 1000));

            CompletableFuture<Void> stage1Future = CompletableFuture.allOf(sentimentFuture, sanitizationFuture);
            stage1Future.get(10, TimeUnit.SECONDS);

            // Get results
            Map<String, String> sentimentOutput = sentimentFuture.get();
            sanitizedInput = sanitizationFuture.get();

            // Validate sentiment output
            sentimentOutput = (Map<String, String>) Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(sentimentOutput);

            // Update results
            results.put("sentiment", sentimentOutput.get("sentiment"));
            results.put("confidence", sentimentOutput.get("confidence"));

            __status = "done";
            __duration_ms = System.currentTimeMillis() - startTime;
            return results.toString();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            __status = "error";
            __error = e.getMessage();
            __duration_ms = System.currentTimeMillis() - startTime;
            return "Error: " + e.getMessage();
        }
    }

    // Write input trigger function
    public static void writeInput(Map<String, Object> inputMap) {
        // Write input values to iMEM variables
        chatInput.clear();
        chatInput.putAll(inputMap);

        // Fire runner asynchronously
        CompletableFuture.supplyAsync(SentimentIPU::runSentimentiPU);
    }

    // Read output function
    public static Map<String, Object> readOutput() {
        Map<String, Object> outputMap = new HashMap<>();
        outputMap.put("results", results);
        outputMap.put("__status", __status);
        outputMap.put("__error", __error);
        outputMap.put("__duration_ms", __duration_ms);
        return outputMap;
    }

    public static void main(String[] args) {
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("chatInput", "Hello, world!");
        writeInput(inputMap);
    }
}