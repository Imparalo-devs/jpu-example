import java.util.ArrayList;
import java.util.List;

public class SentimentAnalyzer {
    private ChatDatabase chatDatabase;
    private SentimentIPU sentimentIPU;

    public SentimentAnalyzer(ChatDatabase chatDatabase, SentimentIPU sentimentIPU) {
        this.chatDatabase = chatDatabase;
        this.sentimentIPU = sentimentIPU;
    }

    public void start() {
        String currentChat;
        while ((currentChat = chatDatabase.getChat()) != null) {
            String currentSentiment = sentimentIPU.processSentiment(currentChat);
            double resultConfidence = sentimentIPU.getResultConfidence();
            System.out.println("Result Sentiment: " + currentSentiment);
            System.out.println("Result Confidence: " + resultConfidence);
        }
    }

    public static void main(String[] args) {
        ChatDatabase chatDatabase = new ChatDatabase();
        SentimentIPU sentimentIPU = new SentimentIPU();
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(chatDatabase, sentimentIPU);
        sentimentAnalyzer.start();
    }
}