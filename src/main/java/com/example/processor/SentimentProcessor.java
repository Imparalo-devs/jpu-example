import com.example.vcpu.SentimentIPU;

import java.util.concurrent.Future;

public class SentimentProcessor {
    private SentimentIPU sentimentIPU;

    public SentimentProcessor() {
        sentimentIPU = new SentimentIPU();
    }

    public Future<SentimentResult> process(String chat) {
        return sentimentIPU.writeInput("chatInput", chat)
                .thenApply(result -> {
                    String sentiment = sentimentIPU.readOutput("sentimentQueue");
                    double confidence = sentimentIPU.readOutput("confidenceQueue");
                    return new SentimentResult(sentiment, confidence);
                });
    }
}