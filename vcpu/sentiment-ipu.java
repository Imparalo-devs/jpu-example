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
 * Sentiment iPU
 * 
 * Analyze sentiment from a chat
 * 
 * @input chatInput (any)
 * @output results (String)
 */
public class SentimentIPU {

    // iMEM variables
    private static Map<String, String> results = new HashMap<>();
    private static String chatInput = null;
    private static String __status = "idle";
    private static String __error = null;
    private static long __duration_ms = 0;

    // iLU: Sentiment_Analyzer_iLU
    public static CompletableFuture<Map<String, String>> Sentiment_Analyzer_iLU(String text) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Call AI provider
                String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");
                String baseUrl = "https://api.example.com";
                String model = "sentiment-analysis";
                String url = baseUrl + "/models/" + model + "/predict";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"text\": \"" + text + "\"}"))
                        .build();

                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

                // Parse JSON response
                String sentiment = null;
                double confidence = 0.0;
                try {
                    // Assuming JSON response format: {"sentiment": "positive", "confidence": 0.8}
                    String responseBody = response.body();
                    Pattern sentimentPattern = Pattern.compile("\"sentiment\": \"(.*?)\"");
                    Pattern confidencePattern = Pattern.compile("\"confidence\": (\\d+\\.\\d+)");
                    Matcher sentimentMatcher = sentimentPattern.matcher(responseBody);
                    Matcher confidenceMatcher = confidencePattern.matcher(responseBody);
                    if (sentimentMatcher.find()) {
                        sentiment = sentimentMatcher.group(1);
                    }
                    if (confidenceMatcher.find()) {
                        confidence = Double.parseDouble(confidenceMatcher.group(1));
                    }
                } catch (Exception e) {
                    // Handle JSON parsing error
                }

                // Return results
                Map<String, String> output = new HashMap<>();
                output.put("sentiment", sentiment);
                output.put("confidence", String.valueOf(confidence));
                return output;
            } catch (IOException | InterruptedException e) {
                // Handle API call error
                throw new RuntimeException(e);
            }
        });
    }

    // iLU: Input_Security_Filter_Sanitization__iLU
    public static String Input_Security_Filter_Sanitization__iLU(String variableToCheck, int maxSize) {
        // SIZE ENFORCEMENT
        if (variableToCheck.length() > maxSize) {
            variableToCheck = variableToCheck.substring(0, maxSize);
        }

        // NORMALIZATION
        variableToCheck = variableToCheck.normalize(Normalizer.NFKC);

        // THREAT DETECTION
        Pattern sqlInjectionPattern = Pattern.compile("UNION|SELECT|OR 1=1|\\/\\*|\\*\\/", Pattern.CASE_INSENSITIVE);
        Pattern commandInjectionPattern = Pattern.compile(";|\\|&&|`|\\$|\\(|\\)", Pattern.CASE_INSENSITIVE);
        Pattern scriptInjectionPattern = Pattern.compile("<script>|eval|function", Pattern.CASE_INSENSITIVE);
        Pattern promptInjectionPattern = Pattern.compile("you are now|system:|developer:", Pattern.CASE_INSENSITIVE);

        Matcher sqlInjectionMatcher = sqlInjectionPattern.matcher(variableToCheck);
        Matcher commandInjectionMatcher = commandInjectionPattern.matcher(variableToCheck);
        Matcher scriptInjectionMatcher = scriptInjectionPattern.matcher(variableToCheck);
        Matcher promptInjectionMatcher = promptInjectionPattern.matcher(variableToCheck);

        if (sqlInjectionMatcher.find() || commandInjectionMatcher.find() || scriptInjectionMatcher.find() || promptInjectionMatcher.find()) {
            return "";
        }

        // POLICY ENFORCEMENT
        return variableToCheck;
    }

    // iSBU: Type_Validator_iSBU_ilu-1775828734058_imem-1775828862904
    public static Object Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(Object value) {
        if (!(value instanceof String)) {
            throw new RuntimeException("Value must be a string");
        }
        return value;
    }

    // Main runner function
    public static void runSentimentiPU(String chatInput) {
        __status = "running";
        long startTime = System.currentTimeMillis();

        try {
            // Write input values into their iMEM variables
            SentimentIPU.chatInput = chatInput;

            // Execute the pipeline following the EXECUTION PLAN
            CompletableFuture<Map<String, String>> sentimentFuture = Sentiment_Analyzer_iLU(chatInput);
            String sanitizedVariable = Input_Security_Filter_Sanitization__iLU(chatInput, 1000);

            // Wait for sentiment analysis to complete
            Map<String, String> sentimentResults = sentimentFuture.get(10, TimeUnit.SECONDS);

            // Update results iMEM variable
            results.put("sentiment", sentimentResults.get("sentiment"));
            results.put("confidence", sentimentResults.get("confidence"));

            // Mark OK and record duration
            __status = "done";
            __duration_ms = System.currentTimeMillis() - startTime;
        } catch (Exception e) {
            // Mark ERR and record error message and duration
            __status = "error";
            __error = e.getMessage();
            __duration_ms = System.currentTimeMillis() - startTime;
        }
    }

    // Write input trigger function
    /**
     * Write input values into their iMEM variables and trigger the runner asynchronously.
     * 
     * Example usage:
     * Map<String, String> inputMap = new HashMap<>();
     * inputMap.put("chatInput", "Hello, world!");
     * writeInput(inputMap);
     */
    public static void writeInput(Map<String, String> inputMap) {
        // Write input values into their iMEM variables
        if (inputMap.containsKey("chatInput")) {
            chatInput = inputMap.get("chatInput");
        }

        // Fire the runner asynchronously
        Thread runnerThread = new Thread(() -> runSentimentiPU(chatInput));
        runnerThread.start();
    }

    // Read output function
    /**
     * Read the output iMEM values.
     * 
     * Example usage:
     * Map<String, String> outputMap = readOutput();
     * System.out.println(outputMap.get("results"));
     */
    public static Map<String, String> readOutput() {
        // Return the output iMEM values
        Map<String, String> outputMap = new HashMap<>();
        outputMap.put("results", results.toString());
        return outputMap;
    }

    public static void main(String[] args) {
        // Test the writeInput function
        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("chatInput", "Hello, world!");
        writeInput(inputMap);

        // Test the readOutput function
        try {
            Thread.sleep(1000); // Wait for the runner to complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Map<String, String> outputMap = readOutput();
        System.out.println(outputMap.get("results"));
    }
}