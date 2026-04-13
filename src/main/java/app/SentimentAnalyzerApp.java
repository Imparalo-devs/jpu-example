package app;

import database.ChatDatabase;
import model.AnalysisResult;
import service.SentimentService;
import vcpu.SentimentIPU;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SentimentAnalyzerApp {

    public static void main(String[] args) {
        // Start process
        System.out.println("START: Sentiment Analyzer");

        // Simulate a simple database as an ArrayList
        ChatDatabase chatDatabase = new ChatDatabase();
        chatDatabase.addChat("I love this product!");
        chatDatabase.addChat("I'm so hungry!");
        chatDatabase.addChat("This is amazing!");
        chatDatabase.addChat("I'm feeling sad today.");
        chatDatabase.addChat("I'm so excited for the weekend!");
        chatDatabase.addChat("I'm not sure about this.");
        chatDatabase.addChat("This is terrible!");
        chatDatabase.addChat("I'm feeling great today!");
        chatDatabase.addChat("I'm so bored!");
        chatDatabase.addChat("I'm loving this!");

        // Process sentiments
        while (true) {
            // Get a chat from the database
            String currentChat = chatDatabase.getNextChat();
            if (currentChat == null) {
                break;
            }

            // Instantiate the SentimentIPU class/module
            SentimentIPU sentimentIPU = new SentimentIPU();

            // Call writeInput for each input
            sentimentIPU.writeInput("chatInput", currentChat);

            // Wait asynchronously for completion
            Future<AnalysisResult> future = sentimentIPU.getFuture();
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                System.out.println("Error processing sentiment: " + e.getMessage());
                continue;
            }

            // Read outputs after async completion
            AnalysisResult analysisResult = sentimentIPU.readOutput("sentimentQueue");
            String resultSentiment = analysisResult.getSentiment();
            double resultConfidence = analysisResult.getConfidence();

            // Print results
            System.out.println("Result Sentiment: " + resultSentiment);
            System.out.println("Result Confidence: " + resultConfidence);
        }

        // End process
        System.out.println("END: Sentiment Analyzer");
    }
}