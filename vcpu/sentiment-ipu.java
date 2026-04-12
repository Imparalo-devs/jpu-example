import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Sentiment iPU: Analyze sentiment from a chat.
 * 
 * Input iMEM: 
 *   - chatInput (variable): any
 * 
 * Output iMEM: 
 *   - sentiment (variable): String
 *   - confidence (variable): float
 */
public class SentimentIPU {

    // iMEM variables
    private static String chatInput = null;
    private static String sentiment = null;
    private static Float confidence = null;

    // Status variables
    private static String status = "IDLE";
    private static long startTime = 0;

    // AI provider endpoint
    private static final String AI_PROVIDER_URL = "https://api.example.com/sentiment";
    private static final String AI_PROVIDER_MODEL = "sentiment-analysis";
    private static final String API_KEY = System.getenv("VCPU_UNKNOWN_API_KEY");

    // OWASP library for input sanitization
    private static final PolicyFactory policyFactory = new HtmlPolicyBuilder().toFactory();

    // iLU function: Sentiment_Analyzer_iLU
    public static CompletableFuture<Map<String, Object>> Sentiment_Analyzer_iLU(String text) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Sanitize input text
                String sanitizedText = policyFactory.sanitize(text);

                // Create JSON payload
                JSONObject payload = new JSONObject();
                payload.put("text", sanitizedText);

                // Set API key in header
                Request request = new Request.Builder()
                        .url(AI_PROVIDER_URL)
                        .post(RequestBody.create(MediaType.get("application/json"), payload.toString()))
                        .header("Authorization", "Bearer " + API_KEY)
                        .build();

                // Send HTTP POST request
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.body().string());
                String sentiment = jsonResponse.getString("sentiment");
                Float confidence = Float.parseFloat(jsonResponse.getString("confidence"));

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

    // iSBU function: Custom_iSBU_imem-1775828537403_ilu-1775828734058
    public static String Custom_iSBU_imem_1775828537403_ilu_1775828734058(String data) {
        // Sanitize input data
        String sanitizedData = policyFactory.sanitize(data);
        return sanitizedData;
    }

    // iSBU function: Type_Validator_iSBU_ilu-1775828734058_imem-1775828862904
    public static boolean Type_Validator_iSBU_ilu_1775828734058_imem_1775828862904(String data) {
        // Check if data is a string
        return data instanceof String;
    }

    // iSBU function: Type_Validator_iSBU_ilu-1775828734058_imem-1775829044340
    public static Float Type_Validator_iSBU_ilu_1775828734058_imem_1775829044340(Float data) {
        // Check if data is a number between 0.0 and 1.0
        if (data >= 0.0 && data <= 1.0) {
            return data;
        } else {
            return 0.5f; // Default to 0.5 if invalid
        }
    }

    // Main runner function
    public static void runSentimentiPU(String chatInput) {
        try {
            // Mark status as RUNNING and record start time
            status = "RUNNING";
            startTime = System.currentTimeMillis();

            // Write input values into their iMEM variables
            SentimentIPU.chatInput = chatInput;

            // Execute the pipeline following the EXECUTION PLAN
            Map<String, Object> output = Sentiment_Analyzer_iLU(chatInput).get();
            sentiment = (String) output.get("sentiment");
            confidence = (Float) output.get("confidence");

            // Mark status as OK and record duration
            status = "OK";
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Sentiment iPU completed successfully in " + duration + "ms");
        } catch (InterruptedException | ExecutionException e) {
            // Mark status as ERR and record duration
            status = "ERR";
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Error occurred in Sentiment iPU: " + e.getMessage() + " (duration: " + duration + "ms)");
        }
    }

    // Write input trigger function
    /**
     * Write input values into their iMEM variables and fire the runner asynchronously.
     * 
     * To write data and trigger the execution, call this function with a Map containing the input values.
     * For example:
     *   Map<String, Object> input = new HashMap<>();
     *   input.put("chatInput", "Hello, how are you?");
     *   writeInput(input);
     */
    public static void writeInput(Map<String, Object> input) {
        // Write input values into their iMEM variables
        chatInput = (String) input.get("chatInput");

        // Fire the runner asynchronously
        CompletableFuture.runAsync(() -> runSentimentiPU(chatInput));
    }

    // Read output function
    /**
     * Get the output values from their iMEM variables.
     * 
     * To get the output values, call this function and retrieve the values from the returned Map.
     * For example:
     *   Map<String, Object> output = readOutput();
     *   String sentiment = (String) output.get("sentiment");
     *   Float confidence = (Float) output.get("confidence");
     */
    public static Map<String, Object> readOutput() {
        // Return output iMEM values
        Map<String, Object> output = new HashMap<>();
        output.put("sentiment", sentiment);
        output.put("confidence", confidence);
        return output;
    }

    public static void main(String[] args) {
        // Example usage:
        Map<String, Object> input = new HashMap<>();
        input.put("chatInput", "Hello, how are you?");
        writeInput(input);
    }
}