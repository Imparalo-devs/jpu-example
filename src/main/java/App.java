import java.util.List;
import java.util.concurrent.Future;

import database.ChatDatabase;
import processor.SentimentProcessor;
import vcpu.SentimentIPU;

public class App {
    public static void main(String[] args) {
        ChatDatabase chatDatabase = new ChatDatabase();
        SentimentProcessor sentimentProcessor = new SentimentProcessor(chatDatabase, new SentimentIPU());
        sentimentProcessor.startAnalysis();
    }
}