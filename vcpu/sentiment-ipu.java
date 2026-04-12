import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sentiment iPU: Analyze sentiment from a chat.
 * 
 * Input iMEM: chatInput (any)
 * Output iMEM: results (String)
 */
public class SentimentIPU {

    // iMEM variables
    private static Object chatInput = null;
    private static List<String> results = new ArrayList<>();
    private static String __status = "idle";
    private static String __error = null;
    private static long __duration_ms = 0;

    // iLU: Sentiment_Analyzer_iLU
    public static Map<String, Object> Sentiment_Analyzer_iLU(String text) {
        Map<String, Object> output = new HashMap<>();
        try {
            String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");
            String url = "https://api.example.com/sentiment";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"text\": \"" + text + "\"}"))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                // Parse JSON response
                Map<String, Object> json = parseJson(responseBody);
                output.put("sentiment", json.get("sentiment"));
                output.put("confidence", json.get("confidence"));
            } else {
                throw new Exception("Failed to analyze sentiment");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    // iLU: Input_Security_Filter_Sanitization__iLU
    public static String Input_Security_Filter_Sanitization__iLU(String variableToCheck, int maxSize) {
        // SIZE ENFORCEMENT
        if (variableToCheck.length() > maxSize) {
            variableToCheck = variableToCheck.substring(0, maxSize);
        }

        // NORMALIZATION
        variableToCheck = normalizeString(variableToCheck);

        // THREAT DETECTION
        if (isMalicious(variableToCheck)) {
            return "";
        }

        return variableToCheck;
    }

    // iSBU: Type_Validator_iSBU_ilu-1775828734058_imem-1775828862904
    public static Object Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(Object value) {
        if (!(value instanceof String)) {
            throw new RuntimeException("Invalid type: expected String");
        }
        String str = (String) value;
        if (!str.contains("string")) {
            throw new RuntimeException("Invalid value: must contain a string");
        }
        return value;
    }

    // Helper functions
    private static Map<String, Object> parseJson(String json) {
        // Simple JSON parser implementation
        Map<String, Object> map = new HashMap<>();
        String[] pairs = json.substring(1, json.length() - 1).split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            map.put(keyValue[0].trim().replaceAll("\"", ""), keyValue[1].trim().replaceAll("\"", ""));
        }
        return map;
    }

    private static String normalizeString(String str) {
        // Unicode normalization (NFKC)
        str = str.normalize(java.text.Normalizer.NFKC);
        // Removal of null bytes and control characters
        str = str.replaceAll("[\\x00-\\x1F\\x80-\\x9F]", "");
        return str;
    }

    private static boolean isMalicious(String str) {
        // Injection patterns
        Pattern sqlInjection = Pattern.compile("UNION|SELECT|OR 1=1|\\/\\*|\\*\\/|;|--");
        Pattern commandInjection = Pattern.compile(";|\\|&&|`|\\$|\\(|\\)");
        Pattern scriptInjection = Pattern.compile("<script>|eval|function");
        // Code execution patterns
        Pattern dynamicExecution = Pattern.compile("eval|exec|system");
        Pattern encodedPayloads = Pattern.compile("base64|hex");
        // Prompt injection patterns
        Pattern promptInjection = Pattern.compile("you are now|system:|developer:");
        // Check for malicious patterns
        Matcher matcher = sqlInjection.matcher(str);
        if (matcher.find()) {
            return true;
        }
        matcher = commandInjection.matcher(str);
        if (matcher.find()) {
            return true;
        }
        matcher = scriptInjection.matcher(str);
        if (matcher.find()) {
            return true;
        }
        matcher = dynamicExecution.matcher(str);
        if (matcher.find()) {
            return true;
        }
        matcher = encodedPayloads.matcher(str);
        if (matcher.find()) {
            return true;
        }
        matcher = promptInjection.matcher(str);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    // Main runner function
    public static String runSentimentiPU(Object chatInput) {
        __status = "running";
        long startTime = System.currentTimeMillis();
        try {
            SentimentIPU.chatInput = chatInput;
            // Execute pipeline
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            futures.add(CompletableFuture.runAsync(() -> {
                Map<String, Object> output = Sentiment_Analyzer_iLU((String) SentimentIPU.chatInput);
                results.add((String) output.get("sentiment"));
                results.add(String.valueOf(output.get("confidence")));
            }));
            futures.add(CompletableFuture.runAsync(() -> {
                String sanitizedVariable = Input_Security_Filter_Sanitization__iLU((String) SentimentIPU.chatInput, 1000);
                SentimentIPU.chatInput = sanitizedVariable;
            }));
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
            __status = "done";
        } catch (Exception e) {
            __error = e.getMessage();
            __status = "error";
        } finally {
            __duration_ms = System.currentTimeMillis() - startTime;
        }
        return results.toString();
    }

    // Write input trigger function
    /**
     * Write input values into their iMEM variables and trigger the runner asynchronously.
     * 
     * @param inputMap Map of input values (key: variable name, value: variable value)
     */
    public static void writeInput(Map<String, Object> inputMap) {
        // Write input values into their iMEM variables
        for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
            if (entry.getKey().equals("chatInput")) {
                chatInput = entry.getValue();
            }
        }
        // Fire the runner asynchronously
        CompletableFuture.runAsync(() -> runSentimentiPU(chatInput));
    }

    // Read output function
    /**
     * Get the output iMEM values.
     * 
     * @return Map of output values (key: variable name, value: variable value)
     */
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
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(readOutput());
    }
}