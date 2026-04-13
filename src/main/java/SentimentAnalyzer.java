import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

import src.main.java.vcpu.SentimentIPU;
import src.main.java.ChatDatabase;

public class SentimentAnalyzer {
    public void analyzeSentiment() throws InterruptedException, ExecutionException {
        ChatDatabase chatDatabase = new ChatDatabase();
        String currentChat = chatDatabase.getChat();
        while (currentChat != null) {
            SentimentIPU sentimentIPU = new SentimentIPU();
            sentimentIPU.writeInput("chatInput", currentChat);
            Future<String> sentimentFuture = sentimentIPU.readOutput("sentiment");
            Future<String> confidenceFuture = sentimentIPU.readOutput("confidence");
            String resultSentiment = sentimentFuture.get();
            String resultConfidence = confidenceFuture.get();
            System.out.println("Result Sentiment: " + resultSentiment);
            System.out.println("Result Confidence: " + resultConfidence);
            currentChat = chatDatabase.getChat();
        }
    }
}