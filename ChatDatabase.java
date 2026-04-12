import java.util.ArrayList;
import java.util.List;

public class ChatDatabase {
    private List<String> chats = new ArrayList<>();

    public ChatDatabase() {
        chats.add("I love this product!");
        chats.add("This product is amazing!");
        chats.add("I'm so happy with this product!");
        chats.add("This product is okay, I guess.");
        chats.add("I'm not impressed with this product.");
        chats.add("This product is terrible!");
        chats.add("I'm so hungry!");
        chats.add("I'm so tired!");
        chats.add("I'm so sad!");
        chats.add("I'm so angry!");
    }

    public String getNextChat() {
        if (chats.isEmpty()) {
            return null;
        }
        String chat = chats.remove(0);
        return chat;
    }
}