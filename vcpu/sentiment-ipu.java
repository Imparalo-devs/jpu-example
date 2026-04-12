import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

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
                // Set API endpoint and model
                String baseUrl = "https://api.example.com/sentiment";
                String model = "sentiment-analysis";

                // Set API key
                String apiKey = System.getenv("VCPU_UNKNOWN_API_KEY");

                // Create request body
                MediaType mediaType = MediaType.get("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"text\": \"" + text + "\"}");

                // Create request
                Request request = new Request.Builder()
                        .url(baseUrl)
                        .post(body)
                        .header("Authorization", "Bearer " + apiKey)
                        .build();

                // Send request and get response
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();

                // Parse response
                JSONObject jsonObject = new JSONObject(response.body().string());
                String sentiment = jsonObject.getString("sentiment");
                Float confidence = Float.parseFloat(jsonObject.getString("confidence"));

                // Return output slots
                Map<String, Object> output = new HashMap<>();
                output.put("sentiment", sentiment);
                output.put("confidence", confidence);
                return output;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static String Input_Security_Filter_Sanitization__iLU(String variableToCheck, int maxSize) {
        // Step 1: Size enforcement
        if (variableToCheck.length() > maxSize) {
            variableToCheck = variableToCheck.substring(0, maxSize);
        }

        // Step 2: Normalization
        variableToCheck = variableToCheck.normalize(java.text.Normalizer.NFKC);
        variableToCheck = variableToCheck.replaceAll("[\\x00-\\x1F]", "");

        // Step 3: Threat detection
        if (variableToCheck.matches(".*(?:UNION|SELECT|OR|1=1|;|\\|&&|`|\\$|\\(|\\)).*")) {
            return "";
        }

        // Step 4: Policy enforcement
        if (variableToCheck.matches(".*(?:<script>|eval|function|base64|hex).*")) {
            return "";
        }

        // Step 5: Safe output
        return variableToCheck;
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
        return Input_Security_Filter_Sanitization__iLU(input, 1000);
    }

    public static String Passthrough_iSBU_ilu_1775993981180_ilu_1775828734058(String input) {
        return input;
    }

    // Main runner function
    public static void runSentimentiPU(Object chatInput) {
        try {
            // Mark status as RUNNING
            System.out.println("Status: RUNNING");

            // Record start time
            long startTime = System.currentTimeMillis();

            // Write input values into their iMEM variables
            SentimentIPU.chatInput = (String) chatInput;

            // Execute the pipeline following the EXECUTION PLAN
            CompletableFuture<Map<String, Object>> sentimentFuture = Sentiment_Analyzer_iLU(SentimentIPU.chatInput);
            String sanitizedVariable = Input_Security_Filter_Sanitization__iLU(SentimentIPU.chatInput, 1000);

            // Wait for the futures to complete
            Map<String, Object> sentimentOutput = sentimentFuture.get(10, TimeUnit.SECONDS);

            // Transform via Type_Validator_iSBU_ilu-1775828734058_imem-1775828862904
            boolean isValid = Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904((String) sentimentOutput.get("sentiment"));

            // Transform via Type_Validator_iSBU_ilu-1775828734058_imem-1775829044340
            Float confidence = Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340((Float) sentimentOutput.get("confidence"));

            // Transform via Passthrough_iSBU_imem-1775828537403_ilu-1775993981180
            String sanitizedChatInput = Passthrough_iSBU_imem_1775828537403_ilu_1775993981180(SentimentIPU.chatInput);

            // Transform via Passthrough_iSBU_ilu-1775993981180_ilu-1775828734058
            String finalSanitizedVariable = Passthrough_iSBU_ilu_1775993981180_ilu_1775828734058(sanitizedVariable);

            // Update iMEM variables
            SentimentIPU.sentiment = (String) sentimentOutput.get("sentiment");
            SentimentIPU.confidence = confidence;

            // Mark status as OK
            System.out.println("Status: OK");

            // Record duration
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Duration: " + duration + "ms");

            // Return output iMEM values
            System.out.println("Sentiment: " + SentimentIPU.sentiment);
            System.out.println("Confidence: " + SentimentIPU.confidence);
        } catch (Exception e) {
            // Mark status as ERR
            System.out.println("Status: ERR");

            // Record duration
            long duration = System.currentTimeMillis() - System.currentTimeMillis();
            System.out.println("Duration: " + duration + "ms");

            // Print error message
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Write input trigger function
    public static void writeInput(Map<String, Object> input) {
        // Write input values into their iMEM variables
        SentimentIPU.chatInput = (String) input.get("chatInput");

        // Fire the runner asynchronously (fire-and-forget)
        // To write data and trigger the execution, simply call this function with a map containing the input values.
        // For example: writeInput(Collections.singletonMap("chatInput", "Hello World"));
        CompletableFuture.runAsync(() -> runSentimentiPU(SentimentIPU.chatInput));
    }

    // Read output function
    public static Map<String, Object> readOutput() {
        // To get/read the data, simply call this function.
        // It will return a map containing the output iMEM values.
        Map<String, Object> output = new HashMap<>();
        output.put("sentiment", SentimentIPU.sentiment);
        output.put("confidence", SentimentIPU.confidence);
        return output;
    }

    public static void main(String[] args) {
        // Test the writeInput function
        Map<String, Object> input = new HashMap<>();
        input.put("chatInput", "Hello World");
        writeInput(input);

        // Test the readOutput function
        Map<String, Object> output = readOutput();
        System.out.println(output);
    }
}