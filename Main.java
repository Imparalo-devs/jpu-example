public class Main {
    public static void main(String[] args) {
        ChatDatabase chatDatabase = new ChatDatabase();
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(chatDatabase);
        sentimentAnalyzer.analyzeSentiment();
    }
}