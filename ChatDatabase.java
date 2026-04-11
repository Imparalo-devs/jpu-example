import java.util.ArrayList;
import java.util.List;

public class ChatDatabase {
    private List<String> chats = new ArrayList<>();

    public ChatDatabase() {
        chats.add("I'm feeling very happy today!");
        chats.add("I'm so hungry, I could eat a whole pizza!");
        chats.add("I'm feeling sad and depressed.");
        chats.add("I'm excited for my upcoming vacation!");
        chats.add("I'm feeling anxious about my upcoming exam.");
        chats.add("I'm feeling grateful for my wonderful friends.");
        chats.add("I'm feeling angry about the current situation.");
        chats.add("I'm feeling surprised by the unexpected news.");
        chats.add("I'm feeling bored and need something to do.");
        chats.add("I'm feeling tired and need to sleep.");
    }

    public String getChat() {
        if (chats.isEmpty()) {
            return null;
        }
        String chat = chats.get(0);
        chats.remove(0);
        return chat;
    }
}