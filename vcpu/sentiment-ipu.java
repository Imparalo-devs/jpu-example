/**
 * Sentiment iPU
 *
 * Purpose: Analyze sentiment from a chat input.
 *
 * Input iMEM:
 *   - chatInput (any) : holds the raw chat conversation.
 *
 * Output iMEM:
 *   - results (Map<String,Object>) : contains "sentiment" (String) and "confidence" (Number).
 *
 * Internal iMEM:
 *   - sanitizedInput (any) : holds the sanitized version of chatInput.
 *   - __status (String) : "idle", "running", "done", or "error".
 *   - __error (String) : error message if any.
 *   - __duration_ms (long) : execution duration in milliseconds.
 */
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.Normalizer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SentimentIPU {

    // iMEM variables
    private static Object chatInput = null;                     // INPUT
    private static Object sanitizedInput = null;                // INTERNAL
    private static Map<String, Object> results = new HashMap<>(); // OUTPUT

    // Internal status tracking
    private static String __status = "idle";
    private static String __error = null;
    private static long __duration_ms = 0;

    // Constants
    private static final int MAX_SIZE = 1000;
    private static final Pattern THREAT_PATTERN = Pattern.compile(
            "(?i)(union\\s+select|or\\s+1=1|--|;|\\|\\||`|\\$\\(|<script>|eval\\(|function\\s+\\w+\\s*\\()"
    );

    /**
     * Input_Security_Filter_Sanitization__iLU
     *
     * Sanitizes the value stored in {@code chatInput} and writes the result to {@code sanitizedInput}.
     */
    private static void Input_Security_Filter_Sanitization__iLU() {
        Object variableToCheck = chatInput;
        if (variableToCheck == null) {
            sanitizedInput = null;
            return;
        }

        if (variableToCheck instanceof String) {
            sanitizedInput = sanitizeString((String) variableToCheck);
        } else if (variableToCheck instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) variableToCheck;
            List<Object> sanitizedList = new ArrayList<>(list.size());
            for (Object element : list) {
                if (element instanceof String) {
                    sanitizedList.add(sanitizeString((String) element));
                } else {
                    sanitizedList.add(element);
                }
            }
            sanitizedInput = sanitizedList;
        } else {
            // For any other type, pass through unchanged
            sanitizedInput = variableToCheck;
        }
    }

    private static String sanitizeString(String input) {
        // Step 1 – Size enforcement
        String truncated = input.length() > MAX_SIZE ? input.substring(0, MAX_SIZE) : input;

        // Step 2 – Normalization
        String normalized = Normalizer.normalize(truncated, Normalizer.Form.NFKC);
        normalized = normalized.replaceAll("[\\x00-\\x1F\\x7F]", ""); // remove control chars

        // Step 3 – Threat detection
        boolean threatDetected = THREAT_PATTERN.matcher(normalized).find();

        // Step 4 – Policy enforcement
        if (threatDetected) {
            return "";
        } else {
            return normalized;
        }
    }

    /**
     * Sentiment_Analyzer_iLU
     *
     * Calls the OpenAI Chat Completion API to obtain sentiment and confidence.
     * Reads from {@code sanitizedInput} and writes the parsed result to {@code results}.
     */
    private static void Sentiment_Analyzer_iLU() throws Exception {
        if (sanitizedInput == null) {
            results.clear();
            return;
        }

        String textToAnalyze = sanitizedInput.toString();

        String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Environment variable VCPU_UNKNOWN_API_KEY is not set.");
        }

        ObjectMapper mapper = new ObjectMapper();

        // Build request payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content",
                "You are a sentiment analysis assistant. Respond with a JSON object containing fields \"sentiment\" (positive, negative, or neutral) and \"confidence\" (a number between 0.0 and 1.0). Do not include any additional text.");
        messages.add(systemMsg);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", textToAnalyze);
        messages.add(userMsg);

        payload.put("messages", messages);

        String requestBody = mapper.writeValueAsString(payload);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("OpenAI API returned status code " + response.statusCode() + ": " + response.body());
        }

        JsonNode root = mapper.readTree(response.body());
        JsonNode contentNode = root.path("choices").get(0).path("message").path("content");
        if (contentNode.isMissingNode()) {
            throw new RuntimeException("Unexpected OpenAI response format: missing content.");
        }

        String content = contentNode.asText();

        // The model should return a JSON object; parse it
        Map<String, Object> sentimentMap = mapper.readValue(content, new TypeReference<Map<String, Object>>() {
        });

        // Validate expected fields
        if (!sentimentMap.containsKey("sentiment") || !sentimentMap.containsKey("confidence")) {
            throw new RuntimeException("Sentiment response missing required fields.");
        }

        results.clear();
        results.putAll(sentimentMap);
    }

    /**
     * Type_Validator_iSBU_ilu-1775828734058_imem-1775828862904
     *
     * Validates that the provided value is not null and returns it unchanged.
     *
     * @param value any value to validate
     * @return the same value if valid
     * @throws IllegalArgumentException if the value is null
     */
    private static Object Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Validation failed: value cannot be null.");
        }
        return value;
    }

    /**
     * Main runner for the Sentiment iPU.
     *
     * @param chatInputParam the raw chat input
     * @return a JSON string representing the sentiment analysis result
     */
    public static String runSentimentiPU(Object chatInputParam) {
        long startTime = System.currentTimeMillis();
        __status = "running";
        __error = null;
        __duration_ms = 0;

        try {
            // Write input to iMEM
            chatInput = chatInputParam;

            // Stage 1
            Input_Security_Filter_Sanitization__iLU();

            // Stage 2
            Sentiment_Analyzer_iLU();

            // iSBU validation
            Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(results);

            // Success handling
            __status = "done";
            return new ObjectMapper().writeValueAsString(results);
        } catch (Exception e) {
            __status = "error";
            __error = e.getMessage();
            return "";
        } finally {
            __duration_ms = System.currentTimeMillis() - startTime;
        }
    }

    /**
     * writeInput – trigger function.
     *
     * Pass a map containing the key "chatInput" to set the input value and automatically
     * start the iPU processing in a fire‑and‑forget asynchronous task.
     *
     * Example:
     *   Map<String,Object> data = new HashMap<>();
     *   data.put("chatInput", "I love this product!");
     *   SentimentIPU.writeInput(data);
     *
     * @param inputMap map of iMEM variable names to their values
     */
    public static void writeInput(Map<String, Object> inputMap) {
        if (inputMap.containsKey("chatInput")) {
            chatInput = inputMap.get("chatInput");
        }
        // Fire-and-forget asynchronous execution
        CompletableFuture.runAsync(() -> runSentimentiPU(chatInput));
    }

    /**
     * readOutput – returns the current state of all iMEM variables.
     *
     * Example:
     *   Map<String,Object> state = SentimentIPU.readOutput();
     *   System.out.println(state.get("results"));
     *
     * @return map containing iMEM variable names and their current values
     */
    public static Map<String, Object> readOutput() {
        Map<String, Object> output = new HashMap<>();
        output.put("chatInput", chatInput);
        output.put("sanitizedInput", sanitizedInput);
        output.put("results", results);
        output.put("__status", __status);
        output.put("__error", __error);
        output.put("__duration_ms", __duration_ms);
        return output;
    }
}