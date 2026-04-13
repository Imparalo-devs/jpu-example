import java.util.List;
import java.util.concurrent.Future;

import database.ChatDatabase;
import vcpu.SentimentIPU;

public class SentimentProcessor {
    private final ChatDatabase chatDatabase;
    private final SentimentIPU sentimentIPU;

    public SentimentProcessor(ChatDatabase chatDatabase, SentimentIPU sentimentIPU) {
        this.chatDatabase = chatDatabase;
        this.sentimentIPU = sentimentIPU;
    }

    public void startAnalysis() {
        while (true) {
            String currentChat = chatDatabase.getNextChat();
            if (currentChat == null) {
                break;
            }

            Future<Void> future = sentimentIPU.writeInput("chatInput", currentChat);
            try {
                future.get();
            } catch (Exception e) {
                System.err.println("Error processing chat: " + e.getMessage());
            }

            String resultSentiment = sentimentIPU.readOutput("sentiment");
            String resultConfidence = sentimentIPU.readOutput("confidence");

            System.out.println("Chat: " + currentChat);
            System.out.println("Sentiment: " + resultSentiment);
            System.out.println("Confidence: " + resultConfidence);
        }
    }
}