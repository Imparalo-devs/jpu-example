package vcpu;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.Normalizer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Sentiment iPU
 *
 * Purpose: Analyze sentiment from a chat.
 *
 * Input iMEM:
 *   - chatInput (any) : raw chat data supplied by the trigger.
 *
 * Output iMEM:
 *   - results (Map<String,Object>) : contains "sentiment" (String) and "confidence" (Number).
 *
 * Internal iMEM:
 *   - sanitizedInput (any) : cleaned version of chatInput.
 *   - __status (String) : "idle" | "running" | "done" | "error".
 *   - __error (String) : error message if any.
 *   - __duration_ms (long) : execution time in milliseconds.
 */
public class SentimentIPU {

    /* ==================== iMEM VARIABLES ==================== */
    // INPUT
    public static Object chatInput = null;

    // INTERNAL
    public static Object sanitizedInput = null;
    public static String __status = "idle";
    public static String __error = null;
    public static long __duration_ms = 0;

    // OUTPUT
    public static Map<String, Object> results = new HashMap<>();

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /* ==================== iLU: Input_Security_Filter_Sanitization__iLU ==================== */
    public static void Input_Security_Filter_Sanitization__iLU() {
        final int MAX_SIZE = 2000; // maximum allowed characters
        Object variableToCheck = chatInput;

        if (variableToCheck instanceof String) {
            String value = (String) variableToCheck;
            // STEP 1 – SIZE ENFORCEMENT
            if (value.length() > MAX_SIZE) {
                value = value.substring(0, MAX_SIZE);
            }

            // STEP 2 – NORMALIZATION
            String normalized = Normalizer.normalize(value, Normalizer.Form.NFKC);
            normalized = normalized.replaceAll("\\x00", ""); // remove null bytes
            normalized = normalized.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]+", ""); // remove other control chars

            // STEP 3 – THREAT DETECTION
            boolean malicious = containsThreat(normalized);

            // STEP 4 – POLICY ENFORCEMENT
            sanitizedInput = malicious ? "" : normalized;
        } else if (variableToCheck instanceof List) {
            List<?> list = (List<?>) variableToCheck;
            List<Object> sanitizedList = new ArrayList<>();
            for (Object elem : list) {
                if (elem instanceof String) {
                    String str = (String) elem;
                    if (str.length() > MAX_SIZE) {
                        str = str.substring(0, MAX_SIZE);
                    }
                    String normalized = Normalizer.normalize(str, Normalizer.Form.NFKC);
                    normalized = normalized.replaceAll("\\x00", "");
                    normalized = normalized.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]+", "");
                    boolean malicious = containsThreat(normalized);
                    sanitizedList.add(malicious ? "" : normalized);
                } else {
                    // Non‑string elements are kept as‑is
                    sanitizedList.add(elem);
                }
            }
            sanitizedInput = sanitizedList;
        } else {
            // Non‑string, non‑list inputs are passed through unchanged
            sanitizedInput = variableToCheck;
        }
    }

    private static boolean containsThreat(String input) {
        // Simple regex patterns for demonstration; real‑world use should rely on a vetted library.
        String[] patterns = {
                "(?i)union\\s+select",               // SQL UNION SELECT
                "(?i)or\\s+1=1",                     // SQL tautology
                "(?i)--|#|/\\*",                     // SQL comment tokens
                "(?i)<script\\b[^>]*>(.*?)</script>", // script tags
                "(?i)eval\\s*\\(",                   // eval()
                "(?i)javascript:",                   // javascript: URI
                "(?i)\\b(?:cmd|bash|sh)\\b",         // command shells
                "(?i);\\s*\\b(?:shutdown|reboot)\\b", // dangerous commands
                "(?i)base64\\s*decode",              // base64 decode hint
                "(?i)you\\s+are\\s+now",             // prompt injection phrasing
                "(?i)ignore\\s+instructions",        // prompt injection phrasing
                "(?i)system\\s*:",                   // system directive
        };
        for (String pat : patterns) {
            if (Pattern.compile(pat).matcher(input).find()) {
                return true;
            }
        }
        return false;
    }

    /* ==================== iLU: Sentiment_Analyzer_iLU ==================== */
    public static CompletableFuture<Map<String, Object>> Sentiment_Analyzer_iLU() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (sanitizedInput == null) {
                    throw new IllegalStateException("sanitizedInput is null");
                }
                String text = sanitizedInput.toString();

                // Build request payload
                Map<String, Object> message = new HashMap<>();
                message.put("role", "user");
                message.put("content", "Analyze the following text and return a JSON object with fields \"sentiment\" (positive, negative, neutral) and \"confidence\" (0.0‑1.0):\n\n" + text);

                Map<String, Object> payload = new HashMap<>();
                payload.put("model", "gpt-3.5-turbo");
                payload.put("messages", List.of(message));
                payload.put("temperature", 0);

                String requestBody = objectMapper.writeValueAsString(payload);

                String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");
                if (apiKey == null || apiKey.isBlank()) {
                    throw new IllegalStateException("Environment variable VCPU_UNKNOWN_API_KEY is not set");
                }

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + apiKey)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException("AI provider returned status " + response.statusCode() + ": " + response.body());
                }

                // Parse OpenAI response
                Map<String, Object> respMap = objectMapper.readValue(response.body(),
                        new TypeReference<Map<String, Object>>() {
                        });
                List<Map<String, Object>> choices = (List<Map<String, Object>>) respMap.get("choices");
                if (choices == null || choices.isEmpty()) {
                    throw new RuntimeException("No choices returned from AI provider");
                }
                String content = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");

                // The model is instructed to output pure JSON; attempt to parse it.
                Map<String, Object> sentimentResult = objectMapper.readValue(content,
                        new TypeReference<Map<String, Object>>() {
                        });

                // Validate expected fields exist
                if (!sentimentResult.containsKey("sentiment") || !sentimentResult.containsKey("confidence")) {
                    throw new RuntimeException("AI response missing required fields");
                }

                return sentimentResult;
            } catch (Exception e) {
                throw new RuntimeException("Sentiment analysis failed: " + e.getMessage(), e);
            }
        });
    }

    /* ==================== iSBU: Type_Validator_iSBU_Sentiment_Analyzer_to_results ==================== */
    public static Object Type_Validator_iSBU_Sentiment_Analyzer_to_results(Object value) {
        if (!(value instanceof Map)) {
            throw new IllegalArgumentException("Result must be a Map");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) value;
        if (!map.containsKey("sentiment") || !map.containsKey("confidence")) {
            throw new IllegalArgumentException("Result map missing required keys");
        }
        Object sentiment = map.get("sentiment");
        Object confidence = map.get("confidence");
        if (!(sentiment instanceof String)) {
            throw new IllegalArgumentException("sentiment must be a String");
        }
        if (!(confidence instanceof Number)) {
            throw new IllegalArgumentException("confidence must be a Number");
        }
        return value; // valid
    }

    /* ==================== Main Runner ==================== */
    public static String runSentimentiPU(Object chatInputParam) {
        long start = Instant.now().toEpochMilli();
        __status = "running";
        __error = null;
        __duration_ms = 0;

        try {
            // Write input iMEM
            chatInput = chatInputParam;

            // Stage 1
            Input_Security_Filter_Sanitization__iLU();

            // Stage 2
            Map<String, Object> analysis = Sentiment_Analyzer_iLU().get(); // block for result
            // Validate via iSBU
            Object validated = Type_Validator_iSBU_Sentiment_Analyzer_to_results(analysis);
            // Write to output iMEM
            results = (Map<String, Object>) validated;

            __status = "done";
        } catch (Exception e) {
            __status = "error";
            __error = e.getMessage();
        } finally {
            long end = Instant.now().toEpochMilli();
            __duration_ms = end - start;
        }

        try {
            return objectMapper.writeValueAsString(results);
        } catch (Exception e) {
            // Fallback to empty JSON if serialization fails
            return "{}";
        }
    }

    /**
     * Trigger function.
     * Pass a map containing the key "chatInput" with the raw chat data.
     * The function writes the value into the iMEM variable and fires the iPU runner asynchronously.
     */
    public static void writeInput(Map<String, Object> inputMap) {
        if (inputMap.containsKey("chatInput")) {
            Object value = inputMap.get("chatInput");
            CompletableFuture.runAsync(() -> runSentimentiPU(value));
        }
    }

    /**
     * Read the current state of all iMEM variables.
     * Returns a map where keys are variable names and values are their current contents.
     */
    public static Map<String, Object> readOutput() {
        Map<String, Object> out = new HashMap<>();
        out.put("chatInput", chatInput);
        out.put("sanitizedInput", sanitizedInput);
        out.put("results", results);
        out.put("__status", __status);
        out.put("__error", __error);
        out.put("__duration_ms", __duration_ms);
        return out;
    }
}