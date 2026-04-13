import java.util.ArrayList;
import java.util.List;

public class ChatDatabase {
    private final List<String> chatPhrases = new ArrayList<>();

    public ChatDatabase() {
        chatPhrases.add("I love this product!");
        chatPhrases.add("I'm so happy with the service!");
        chatPhrases.add("This food is disgusting!");
        chatPhrases.add("I'm feeling very sad today.");
        chatPhrases.add("I'm so excited for the weekend!");
        chatPhrases.add("This product is terrible!");
        chatPhrases.add("I'm feeling hungry.");
        chatPhrases.add("I love my new phone!");
        chatPhrases.add("I'm so angry with the customer service!");
        chatPhrases.add("I'm feeling very tired.");
    }

    public String getNextChat() {
        if (chatPhrases.isEmpty()) {
            return null;
        }
        String chat = chatPhrases.remove(0);
        return chat;
    }
}