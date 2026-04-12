import java.util.ArrayList;
import java.util.List;

public class ChatDatabase {
    private List<String> chats = new ArrayList<>();

    public ChatDatabase() {
        chats.add("I'm so happy today!");
        chats.add("I'm feeling sad.");
        chats.add("This is a great day!");
        chats.add("I'm feeling hungry.");
        chats.add("I love this weather!");
        chats.add("I'm feeling tired.");
        chats.add("This is a terrible day!");
        chats.add("I'm feeling excited!");
        chats.add("I'm feeling bored.");
        chats.add("I love this food!");
    }

    public String getNextChat() {
        if (chats.isEmpty()) {
            return null;
        }
        String chat = chats.remove(0);
        return chat;
    }
}