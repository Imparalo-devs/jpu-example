package service;

import vcpu.SentimentIPU;
import model.AnalysisResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SentimentService {

    private SentimentIPU sentimentIPU;

    public SentimentService(SentimentIPU sentimentIPU) {
        this.sentimentIPU = sentimentIPU;
    }

    public AnalysisResult analyzeSentiment(String chat) {
        // Call writeInput for each input
        this.sentimentIPU.writeInput("chatInput", chat);

        // Wait asynchronously for completion
        Future<AnalysisResult> future = this.sentimentIPU.getFuture();
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            System.out.println("Error processing sentiment: " + e.getMessage());
            return null;
        }

        // Read outputs after async completion
        AnalysisResult analysisResult = this.sentimentIPU.readOutput("sentimentQueue");
        return analysisResult;
    }
}