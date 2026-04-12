import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

public class SentimentIPU {

    // iMEM variables
    private static String chatInput = null;
    private static String sentiment = null;
    private static Float confidence = null;

    // iLU functions
    public static CompletableFuture<JSONObject> Sentiment_Analyzer_iLU(String text) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.example.com/sentiment"))
                        .header("Authorization", "Bearer " + System.getenv("VCPU_UNKNOWN_API_KEY"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"text\":\"" + text + "\"}"))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject jsonObject = new JSONObject(response.body());
                return jsonObject;
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static String Input_Security_Filter_Sanitization__iLU(String variableToCheck, int maxSize) {
        if (variableToCheck.length() > maxSize) {
            variableToCheck = variableToCheck.substring(0, maxSize);
        }

        variableToCheck = variableToCheck.normalize(java.text.Normalizer.NFKC);
        variableToCheck = variableToCheck.replaceAll("[\\x00-\\x1F\\x7F]", "");

        if (variableToCheck.matches(".*(?:UNION|SELECT|OR|AND|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|TRUNCATE|EXECUTE|EXEC|DECLARE|CREATE|ALTER|TRUNCATE|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|TRUNCATE).*")) {
            return "";
        }

        if (variableToCheck.matches(".*(?:<script>|eval|function|constructor|prototype|__proto__|Object|Array|String|Number|Boolean|Date|RegExp|Error|EvalError|RangeError|ReferenceError|SyntaxError|TypeError|URIError|ArrayBuffer|DataView|Float32Array|Float64Array|Int8Array|Int16Array|Int32Array|Uint8Array|Uint16Array|Uint32Array|Uint8ClampedArray).*")) {
            return "";
        }

        if (variableToCheck.matches(".*(?:base64|hex|decodeURIComponent|decodeURI|encodeURIComponent|encodeURI).*")) {
            return "";
        }

        if (variableToCheck.matches(".*(?:you are now|system:|developer:|role).*")) {
            return "";
        }

        return variableToCheck;
    }

    // iSBU functions
    public static boolean Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(String value) {
        return value.contains("string");
    }

    public static Float Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(Float value) {
        if (value >= 0.0 && value <= 1.0) {
            return value;
        } else {
            return 0.5f;
        }
    }

    public static String Passthrough_iSBU_imem_1775828537403_ilu_1775993981180(String value) {
        return Input_Security_Filter_Sanitization__iLU(value, 1000);
    }

    public static Object Passthrough_iSBU_ilu_1775993981180_ilu_1775828734058(Object value) {
        return value;
    }

    // Main runner function
    public static void runSentimentiPU(Object chatInput) {
        long startTime = System.currentTimeMillis();
        try {
            SentimentIPU.chatInput = (String) chatInput;

            CompletableFuture<JSONObject> sentimentFuture = Sentiment_Analyzer_iLU(Passthrough_iSBU_imem_1775828537403_ilu_1775993981180((String) chatInput));
            CompletableFuture<String> sanitizedVariableFuture = CompletableFuture.supplyAsync(() -> Input_Security_Filter_Sanitization__iLU((String) chatInput, 1000));

            CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(sentimentFuture, sanitizedVariableFuture);
            combinedFuture.get(10, TimeUnit.SECONDS);

            JSONObject sentimentJson = sentimentFuture.get();
            String sanitizedVariable = sanitizedVariableFuture.get();

            sentiment = sentimentJson.getString("sentiment");
            confidence = sentimentJson.getFloat("confidence");

            System.out.println("OK - " + (System.currentTimeMillis() - startTime) + "ms");
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.out.println("ERR - " + e.getMessage() + " - " + (System.currentTimeMillis() - startTime) + "ms");
        }
    }

    // Write input trigger function
    public static void writeInput(java.util.Map<String, Object> inputMap) {
        // To write data and trigger the execution, simply call this function with a map containing the input values.
        // For example: writeInput(Collections.singletonMap("chatInput", "Hello World"));
        chatInput = (String) inputMap.get("chatInput");
        runSentimentiPU(chatInput);
    }

    // Read output function
    public static java.util.Map<String, Object> readOutput() {
        // To get/read the data, simply call this function and it will return a map containing the output values.
        // For example: Map<String, Object> output = readOutput();
        java.util.Map<String, Object> outputMap = new java.util.HashMap<>();
        outputMap.put("sentiment", sentiment);
        outputMap.put("confidence", confidence);
        return outputMap;
    }

    public static void main(String[] args) {
        writeInput(java.util.Collections.singletonMap("chatInput", "Hello World"));
    }
}