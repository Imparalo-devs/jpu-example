import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

public class SentimentIPU {

    // iMEM variables
    private static Object chatInput = null;
    private static Map<String, String> results = Map.of();
    private static Object sanitizedInput = null;
    private static String __status = "idle";
    private static String __error = null;
    private static long __duration_ms = 0;

    // iLU: Sentiment_Analyzer_iLU
    public static CompletableFuture<Map<String, String>> Sentiment_Analyzer_iLU() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String text = (String) sanitizedInput;
                String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");
                String url = "https://api.example.com/sentiment";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"text\":\"" + text + "\"}"))
                        .build();

                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject jsonObject = new JSONObject(response.body());
                String sentiment = jsonObject.getString("sentiment");
                double confidence = jsonObject.getDouble("confidence");

                results.put("sentiment", sentiment);
                results.put("confidence", String.valueOf(confidence));

                return results;
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // iLU: Input_Security_Filter_Sanitization__iLU
    public static Object Input_Security_Filter_Sanitization__iLU(Object variableToCheck, int maxSize) {
        if (variableToCheck instanceof String) {
            String str = (String) variableToCheck;
            if (str.length() > maxSize) {
                str = str.substring(0, maxSize);
            }
            str = str.normalize(java.text.Normalizer.NFKC);
            str = str.replaceAll("[\\x00-\\x1F\\x80-\\x9F]", "");
            if (str.matches(".*(?:UNION|SELECT|OR|\\;|&&|`|\\$|\\(|\\)).*")) {
                return "";
            }
            if (str.matches(".*(?:<script>|eval|function).*")) {
                return "";
            }
            if (str.matches(".*(?:you are now|system:|developer:).*")) {
                return "";
            }
            return str;
        } else if (variableToCheck instanceof Object[]) {
            Object[] array = (Object[]) variableToCheck;
            for (int i = 0; i < array.length; i++) {
                if (array[i] instanceof String) {
                    String str = (String) array[i];
                    if (str.length() > maxSize) {
                        str = str.substring(0, maxSize);
                    }
                    str = str.normalize(java.text.Normalizer.NFKC);
                    str = str.replaceAll("[\\x00-\\x1F\\x80-\\x9F]", "");
                    if (str.matches(".*(?:UNION|SELECT|OR|\\;|&&|`|\\$|\\(|\\)).*")) {
                        array[i] = "";
                    } else if (str.matches(".*(?:<script>|eval|function).*")) {
                        array[i] = "";
                    } else if (str.matches(".*(?:you are now|system:|developer:).*")) {
                        array[i] = "";
                    } else {
                        array[i] = str;
                    }
                }
            }
            return array;
        } else {
            return variableToCheck;
        }
    }

    // iSBU: Type_Validator_iSBU_ilu-1775828734058_imem-1775828862904
    public static Object Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(Object value) {
        if (!(value instanceof String)) {
            throw new RuntimeException("Invalid type. Expected a string.");
        }
        return value;
    }

    // Main runner function
    public static String runSentimentiPU(Object chatInput) {
        __status = "running";
        long startTime = System.currentTimeMillis();

        try {
            SentimentIPU.chatInput = chatInput;
            sanitizedInput = Input_Security_Filter_Sanitization__iLU(chatInput, 1000);

            CompletableFuture<Map<String, String>> sentimentFuture = Sentiment_Analyzer_iLU();
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(sentimentFuture);

            allFutures.get();

            __status = "done";
            __duration_ms = System.currentTimeMillis() - startTime;
            return results.toString();
        } catch (InterruptedException | ExecutionException e) {
            __status = "error";
            __error = e.getMessage();
            __duration_ms = System.currentTimeMillis() - startTime;
            return "Error: " + e.getMessage();
        }
    }

    // Write input trigger function
    // To write data and trigger the execution, call this function with a map containing the input values.
    // For example: writeInput(Map.of("chatInput", "Hello, world!"));
    public static void writeInput(Map<String, Object> inputMap) {
        chatInput = inputMap.get("chatInput");
        Thread thread = new Thread(() -> runSentimentiPU(chatInput));
        thread.start();
    }

    // Read output function
    // To get the output values, call this function after the runner has completed.
    // For example: Map<String, Object> outputMap = readOutput();
    public static Map<String, Object> readOutput() {
        return Map.of("results", results, "__status", __status, "__error", __error, "__duration_ms", __duration_ms);
    }

    public static void main(String[] args) {
        writeInput(Map.of("chatInput", "Hello, world!"));
        while (__status.equals("running")) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println(readOutput());
    }
}