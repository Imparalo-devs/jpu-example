import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Sentiment iPU: Analyze sentiment from a chat.
 * 
 * @author [Your Name]
 */
public class SentimentIPU {

    // iMEM variables
    private static Map<String, String> results = new HashMap<>();
    private static Object chatInput = null;
    private static Object sanitizedInput = null;
    private static String __status = "idle";
    private static String __error = null;
    private static long __duration_ms = 0;

    /**
     * Sentiment Analyzer iLU: Analyze sentiment and confidence from a chat string.
     * 
     * @param text Chat string to analyze
     * @return Map with sentiment and confidence
     * @throws IOException If API request fails
     * @throws InterruptedException If API request is interrupted
     */
    private static Map<String, Object> Sentiment_Analyzer_iLU(String text) throws IOException, InterruptedException {
        // Set API endpoint and model
        String baseUrl = "https://api.example.com";
        String model = "sentiment-analyzer";

        // Set API key from environment variable
        String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");

        // Create API request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + model))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"text\": \"" + text + "\"}"))
                .build();

        // Send API request and get response
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        // Parse JSON response
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("sentiment", response.body().split("\"sentiment\":\"")[1].split("\",")[0]);
        resultMap.put("confidence", response.body().split("\"confidence\":")[1].split("}")[0]);

        return resultMap;
    }

    /**
     * Input Security Filter Sanitization iLU: Sanitize input variable.
     * 
     * @param variableToCheck Input variable to sanitize
     * @param maxSize Maximum size of input variable
     * @return Sanitized input variable
     */
    private static Object Input_Security_Filter_Sanitization__iLU(Object variableToCheck, int maxSize) {
        // Check if input is a string
        if (variableToCheck instanceof String) {
            String inputString = (String) variableToCheck;

            // Truncate input string if it exceeds maximum size
            if (inputString.length() > maxSize) {
                inputString = inputString.substring(0, maxSize);
            }

            // Apply unicode normalization and remove null bytes and control characters
            inputString = inputString.normalize(java.text.Normalizer.NFKC);
            inputString = inputString.replaceAll("[\\x00-\\x1F\\x7F]", "");

            // Detect malicious patterns
            Pattern pattern = Pattern.compile(".*(UNION|SELECT|OR|1=1|;|\\|&&|`|$|system|eval|function).*", Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(inputString).matches()) {
                return "";
            }

            return inputString;
        } else {
            // If input is not a string, return it as is
            return variableToCheck;
        }
    }

    /**
     * Type Validator iSBU: Validate input value matches expected shape for any.
     * 
     * @param value Input value to validate
     * @return Validated input value
     * @throws Exception If input value is invalid
     */
    private static Object Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(Object value) throws Exception {
        // Check if input value contains a string
        if (value instanceof String) {
            if (!((String) value).contains("string")) {
                throw new Exception("Input value does not match expected shape for any");
            }
        }

        return value;
    }

    /**
     * Run Sentiment iPU.
     * 
     * @param chatInput Chat input to analyze
     * @return Results of sentiment analysis
     */
    public static String runSentimentiPU(Object chatInput) {
        // Mark status as running and record start time
        __status = "running";
        long startTime = System.currentTimeMillis();

        try {
            // Write input values into their iMEM variables
            SentimentIPU.chatInput = chatInput;

            // Execute pipeline
            sanitizedInput = Input_Security_Filter_Sanitization__iLU(chatInput, 1000);
            Map<String, Object> sentimentResults = Sentiment_Analyzer_iLU((String) sanitizedInput);
            sentimentResults = (Map<String, Object>) Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(sentimentResults);
            results.put("sentiment", (String) sentimentResults.get("sentiment"));
            results.put("confidence", (String) sentimentResults.get("confidence"));

            // Mark status as done and record duration
            __status = "done";
            __duration_ms = System.currentTimeMillis() - startTime;

            return results.toString();
        } catch (Exception e) {
            // Mark status as error and record error message and duration
            __status = "error";
            __error = e.getMessage();
            __duration_ms = System.currentTimeMillis() - startTime;

            return "Error: " + e.getMessage();
        }
    }

    /**
     * Write input trigger function.
     * 
     * @param inputMap Map of input values
     */
    public static void writeInput(Map<String, Object> inputMap) {
        // Write input values into their iMEM variables and fire runner asynchronously
        // To write data and trigger the execution, call this function with a map of input values, e.g.:
        // writeInput(Collections.singletonMap("chatInput", "Hello, world!"));
        chatInput = inputMap.get("chatInput");
        Thread runnerThread = new Thread(() -> runSentimentiPU(chatInput));
        runnerThread.start();
    }

    /**
     * Read output function.
     * 
     * @return Map of output values
     */
    public static Map<String, Object> readOutput() {
        // To get/read the data, call this function and access the returned map, e.g.:
        // Map<String, Object> outputMap = readOutput();
        // String sentiment = (String) outputMap.get("sentiment");
        Map<String, Object> outputMap = new HashMap<>();
        outputMap.put("results", results);
        outputMap.put("__status", __status);
        outputMap.put("__error", __error);
        outputMap.put("__duration_ms", __duration_ms);
        return outputMap;
    }

    public static void main(String[] args) {
        // Example usage:
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("chatInput", "Hello, world!");
        writeInput(inputMap);
    }
}