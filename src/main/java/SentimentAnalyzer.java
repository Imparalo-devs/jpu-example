import java.util.ArrayList;
import java.util.List;

public class SentimentAnalyzer {
    private ChatDatabase chatDatabase;
    private SentimentIPU sentimentIPU;

    public SentimentAnalyzer() {
        this.chatDatabase = new ChatDatabase();
        this.sentimentIPU = new SentimentIPU();
    }

    public void startProcess() {
        String currentChat = chatDatabase.getChat();
        while (currentChat != null) {
            String currentSentiment = sentimentIPU.processSentiment(currentChat);
            System.out.println("Result Sentiment: " + currentSentiment);
            System.out.println("Result Confidence: " + sentimentIPU.getConfidence());
            currentChat = chatDatabase.getChat();
        }
    }

    public static void main(String[] args) {
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
        sentimentAnalyzer.startProcess();
    }
}