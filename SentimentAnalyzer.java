import java.util.ArrayList;
import java.util.List;

public class SentimentAnalyzer {
    private ChatDatabase chatDatabase;
    private SentimentProcessor sentimentProcessor;

    public SentimentAnalyzer(ChatDatabase chatDatabase, SentimentProcessor sentimentProcessor) {
        this.chatDatabase = chatDatabase;
        this.sentimentProcessor = sentimentProcessor;
    }

    public void start() {
        String currentChat = chatDatabase.getChat();
        while (currentChat != null) {
            SentimentResult result = sentimentProcessor.process(currentChat);
            System.out.println("Result Sentiment: " + result.getSentiment());
            System.out.println("Result Confidence: " + result.getConfidence());
            currentChat = chatDatabase.getChat();
        }
    }
    public static void main(String[] args) {
        ChatDatabase chatDatabase = new ChatDatabase();
        SentimentProcessor sentimentProcessor = new SentimentProcessor();
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(chatDatabase, sentimentProcessor);
        sentimentAnalyzer.start();
    }
}