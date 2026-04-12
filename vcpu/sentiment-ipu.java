import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SentimentIPU {

    // iMEM variables
    private static String chatInput = null;
    private static String sentiment = null;
    private static Float confidence = null;

    // iLU functions
    public static CompletableFuture<Map<String, Object>> Sentiment_Analyzer_iLU(String text) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.get("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, "{\"text\":\"" + text + "\"}");
                Request request = new Request.Builder()
                        .url("https://api.example.com/sentiment")
                        .post(body)
                        .header("Authorization", "Bearer " + System.getenv("VCPU_UNKNOWN_API_KEY"))
                        .build();
                Response response = client.newCall(request).execute();
                JSONObject jsonObject = new JSONObject(response.body().string());
                Map<String, Object> result = new HashMap<>();
                result.put("sentiment", jsonObject.getString("sentiment"));
                result.put("confidence", jsonObject.getDouble("confidence"));
                return result;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Map<String, Object> Input_Security_Filter_Sanitization__iLU(String variableToCheck, int maxSize) {
        // SIZE ENFORCEMENT
        if (variableToCheck.length() > maxSize) {
            variableToCheck = variableToCheck.substring(0, maxSize);
        }

        // NORMALIZATION
        variableToCheck = Jsoup.parse(variableToCheck).text();
        variableToCheck = variableToCheck.replaceAll("[\\x00-\\x1F]", "");

        // THREAT DETECTION
        if (variableToCheck.matches(".*(?:UNION|SELECT|OR|AND|;|\\|&&|`|$|\\(|\\)).*")) {
            variableToCheck = "";
        } else if (variableToCheck.matches(".*(?:<script|eval|function).*")) {
            variableToCheck = "";
        } else if (variableToCheck.matches(".*(?:base64|hex).*")) {
            variableToCheck = "";
        }

        // POLICY ENFORCEMENT
        Map<String, Object> result = new HashMap<>();
        result.put("sanitizedVariable", variableToCheck);
        return result;
    }

    // iSBU functions
    public static boolean Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(String input) {
        return input.contains("string");
    }

    public static Float Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(Float input) {
        if (input >= 0.0 && input <= 1.0) {
            return input;
        } else {
            return 0.5f;
        }
    }

    public static String Passthrough_iSBU_imem_1775828537403_ilu_1775993981180(String input) {
        return Jsoup.parse(input).text();
    }

    public static Object Passthrough_iSBU_ilu_1775993981180_ilu_1775828734058(Object input) {
        return input;
    }

    // main runner function
    public static Map<String, Object> runSentimentiPU(Object chatInput) {
        SentimentIPU.chatInput = chatInput.toString();
        long startTime = System.currentTimeMillis();
        try {
            CompletableFuture<Map<String, Object>> sentimentFuture = Sentiment_Analyzer_iLU(SentimentIPU.chatInput);
            CompletableFuture<Map<String, Object>> securityFuture = CompletableFuture.supplyAsync(() -> Input_Security_Filter_Sanitization__iLU(SentimentIPU.chatInput, 1000));
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(sentimentFuture, securityFuture);
            allFutures.get(10, TimeUnit.SECONDS);
            Map<String, Object> sentimentResult = sentimentFuture.get();
            Map<String, Object> securityResult = securityFuture.get();
            sentiment = (String) sentimentResult.get("sentiment");
            confidence = (Float) sentimentResult.get("confidence");
            System.out.println("OK - " + (System.currentTimeMillis() - startTime) + "ms");
            Map<String, Object> result = new HashMap<>();
            result.put("sentiment", sentiment);
            result.put("confidence", confidence);
            return result;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.out.println("ERR - " + e.getMessage() + " - " + (System.currentTimeMillis() - startTime) + "ms");
            return null;
        }
    }

    // writeInput trigger function
    public static void writeInput(Map<String, Object> input) {
        // To write data and trigger the execution, simply call this function with a map containing the input values.
        // For example: writeInput(Collections.singletonMap("chatInput", "Hello World"));
        SentimentIPU.chatInput = (String) input.get("chatInput");
        runSentimentiPU(SentimentIPU.chatInput);
    }

    // readOutput function
    public static Map<String, Object> readOutput() {
        // To get/read the data, simply call this function and it will return a map containing the output values.
        Map<String, Object> result = new HashMap<>();
        result.put("sentiment", sentiment);
        result.put("confidence", confidence);
        return result;
    }

    public static void main(String[] args) {
        writeInput(new HashMap<String, Object>() {{
            put("chatInput", "Hello World");
        }});
    }
}