import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class SentimentIPU {
    // iMEM variables
    private static Map<String, String> results = new HashMap<>();
    private static Object chatInput = null;
    private static Object sanitizedInput = null;
    private static String __status = "idle";
    private static String __error = null;
    private static long __duration_ms = 0;

    // iLU functions
    public static Map<String, Object> Sentiment_Analyzer_iLU(Object input) throws Exception {
        String text = (String) input;
        String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");
        String baseUrl = "https://api.example.com";
        String model = "sentiment-analysis";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/models/" + model + "/predict"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"text\": \"" + text + "\"}"))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            Map<String, Object> output = new HashMap<>();
            output.put("sentiment", response.body().split("\"sentiment\": \"")[1].split("\",")[0]);
            output.put("confidence", response.body().split("\"confidence\": ")[1].split(",")[0]);
            return output;
        } else {
            throw new Exception("Failed to analyze sentiment");
        }
    }

    public static Object Input_Security_Filter_Sanitization__iLU(Object variableToCheck) {
        PolicyFactory policy = new HtmlPolicyBuilder().toFactory();

        if (variableToCheck instanceof String) {
            String input = (String) variableToCheck;
            if (input.length() > 1000) {
                input = input.substring(0, 1000);
            }
            input = input.normalize(java.text.Normalizer.NFKC);
            input = input.replaceAll("[\\x00-\\x1F\\x7F]", "");
            input = policy.sanitize(input);
            if (Pattern.matches(".*(?:UNION|SELECT|OR|AND|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|TRUNCATE|EXECUTE|EXEC|DECLARE|CREATE|ALTER|TRUNCATE).*", input, Pattern.CASE_INSENSITIVE)) {
                return "";
            } else if (Pattern.matches(".*(?:<script|eval|function|\\(|\\)).*", input, Pattern.CASE_INSENSITIVE)) {
                return "";
            } else {
                return input;
            }
        } else {
            return variableToCheck;
        }
    }

    // iSBU functions
    public static Object Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(Object value) {
        if (value instanceof String) {
            return value;
        } else {
            throw new RuntimeException("Invalid type");
        }
    }

    // runner function
    public static String runSentimentiPU(Object chatInput) {
        __status = "running";
        long startTime = System.currentTimeMillis();

        try {
            SentimentIPU.chatInput = chatInput;
            sanitizedInput = Input_Security_Filter_Sanitization__iLU(chatInput);
            Map<String, Object> resultsMap = Sentiment_Analyzer_iLU(sanitizedInput);
            resultsMap = (Map<String, Object>) Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(resultsMap);
            results.put("sentiment", (String) resultsMap.get("sentiment"));
            results.put("confidence", String.valueOf(resultsMap.get("confidence")));
            __status = "done";
            __duration_ms = System.currentTimeMillis() - startTime;
            return results.toString();
        } catch (Exception e) {
            __status = "error";
            __error = e.getMessage();
            __duration_ms = System.currentTimeMillis() - startTime;
            return "Error: " + e.getMessage();
        }
    }

    // writeInput trigger function
    // To write data and trigger the execution, call this function with a Map containing the input data.
    // For example: writeInput(Map.of("chatInput", "Hello, world!"));
    public static void writeInput(Map<String, Object> input) {
        chatInput = input.get("chatInput");
        Thread thread = new Thread(() -> runSentimentiPU(chatInput));
        thread.start();
    }

    // readOutput function
    // To get the output data, call this function.
    // It returns a Map containing the output data.
    public static Map<String, Object> readOutput() {
        Map<String, Object> output = new HashMap<>();
        output.put("results", results);
        output.put("__status", __status);
        output.put("__error", __error);
        output.put("__duration_ms", __duration_ms);
        return output;
    }

    public static void main(String[] args) {
        Map<String, Object> input = new HashMap<>();
        input.put("chatInput", "Hello, world!");
        writeInput(input);
    }
}