import java.util.ArrayList;
import java.util.List;

public class ChatDatabase {
    private List<String> chats;

    public ChatDatabase() {
        chats = new ArrayList<>();
        chats.add("I'm so happy today!");
        chats.add("I'm feeling sad and lonely.");
        chats.add("I'm hungry, where's the food?");
        chats.add("I love this beautiful day!");
        chats.add("I'm so angry with this situation.");
        chats.add("I'm feeling grateful for my friends.");
        chats.add("I'm bored, what can I do?");
        chats.add("I'm excited for the weekend!");
        chats.add("I'm feeling anxious about the future.");
        chats.add("I'm happy to be alive!");
    }

    public String getChat() {
        if (chats.isEmpty()) {
            return null;
        }
        return chats.remove(0);
    }
}