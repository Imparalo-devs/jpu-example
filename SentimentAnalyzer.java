import java.util.ArrayList;
import java.util.List;

public class SentimentAnalyzer {
    private ChatDatabase chatDatabase;
    private SentimentIPU sentimentIPU;

    public SentimentAnalyzer(ChatDatabase chatDatabase) {
        this.chatDatabase = chatDatabase;
        this.sentimentIPU = new SentimentIPU();
    }

    public void analyzeSentiments() {
        String currentChat;
        while ((currentChat = this.chatDatabase.getChat()) != null) {
            SentimentIPU.SentimentResult sentimentResult = this.sentimentIPU.processSentiment(currentChat);
            System.out.println("Sentiment: " + sentimentResult.getSentiment() + ", Confidence: " + sentimentResult.getConfidence());
        }
    }
}