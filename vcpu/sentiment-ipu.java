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

public class SentimentIPU {
    // iMEM variables
    private static Object chatInput = null;
    private static Map<String, String> results = new HashMap<>();
    private static String __status = "idle";
    private static String __error = null;
    private static long __duration_ms = 0;

    // iLU functions
    public static Map<String, String> Sentiment_Analyzer_iLU(String text) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.example.com/sentiment"))
                    .header("Authorization", "Bearer " + System.getenv("VCPU_UNKNOWN_API_KEY"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"text\": \"" + text + "\"}"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Map<String, String> result = new HashMap<>();
                result.put("sentiment", response.body().split("\"sentiment\":\"")[1].split("\",")[0]);
                result.put("confidence", response.body().split("\"confidence\":")[1].split("}")[0]);
                return result;
            } else {
                throw new Exception("Failed to analyze sentiment");
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static String Input_Security_Filter_Sanitization__iLU(String variableToCheck, int maxSize) {
        // SIZE ENFORCEMENT
        if (variableToCheck.length() > maxSize) {
            variableToCheck = variableToCheck.substring(0, maxSize);
        }

        // NORMALIZATION
        variableToCheck = variableToCheck.normalize(Normalizer.NFKC);

        // THREAT DETECTION (multi-signal rules)
        Pattern sqlInjectionPattern = Pattern.compile("UNION\\s+SELECT|OR\\s+1\\s*=\\s*1|\\/\\*|\\*\\/|\\;|\\|\\s*\\&\\&|`|\\$\\(|\\)\\s*system\\s*\\(");
        Pattern commandInjectionPattern = Pattern.compile(";|\\|\\s*\\&\\&|`|\\$\\(|\\)\\s*system\\s*\\(");
        Pattern scriptInjectionPattern = Pattern.compile("<script>|eval|function\\s+constructor");
        Pattern codeExecutionPattern = Pattern.compile("eval|function\\s+constructor");
        Pattern promptInjectionPattern = Pattern.compile("you\\s+are\\s+now|system:|developer:|role\\s*:\\s*");

        if (sqlInjectionPattern.matcher(variableToCheck).find() ||
                commandInjectionPattern.matcher(variableToCheck).find() ||
                scriptInjectionPattern.matcher(variableToCheck).find() ||
                codeExecutionPattern.matcher(variableToCheck).find() ||
                promptInjectionPattern.matcher(variableToCheck).find()) {
            return "";
        }

        // POLICY ENFORCEMENT
        return variableToCheck;
    }

    // iSBU functions
    public static Object Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(Object value) {
        if (value instanceof String) {
            if (((String) value).contains("string")) {
                return value;
            } else {
                throw new RuntimeException("Invalid value");
            }
        } else {
            throw new RuntimeException("Invalid value");
        }
    }

    // Main runner function
    public static void runSentimentiPU(Object chatInput) {
        __status = "running";
        long startTime = System.currentTimeMillis();

        try {
            SentimentIPU.chatInput = chatInput;

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            futures.add(CompletableFuture.runAsync(() -> {
                Map<String, String> sentimentResult = Sentiment_Analyzer_iLU((String) SentimentIPU.chatInput);
                results.put("sentiment", sentimentResult.get("sentiment"));
                results.put("confidence", sentimentResult.get("confidence"));
            }));
            futures.add(CompletableFuture.runAsync(() -> {
                String sanitizedVariable = Input_Security_Filter_Sanitization__iLU((String) SentimentIPU.chatInput, 1000);
                SentimentIPU.chatInput = sanitizedVariable;
            }));

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

            __status = "done";
            __duration_ms = System.currentTimeMillis() - startTime;
        } catch (Exception e) {
            __status = "error";
            __error = e.getMessage();
            __duration_ms = System.currentTimeMillis() - startTime;
        }
    }

    // Write input trigger function
    public static void writeInput(Map<String, Object> input) {
        // Write input values to iMEM variables
        SentimentIPU.chatInput = input.get("chatInput");

        // Fire the runner asynchronously
        CompletableFuture.runAsync(() -> runSentimentiPU(SentimentIPU.chatInput));
    }

    // Read output function
    public static Map<String, Object> readOutput() {
        // Return output iMEM values
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