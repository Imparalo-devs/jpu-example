import java.util.ArrayList;
import java.util.List;

public class ChatDatabase {
    private List<String> chats;

    public ChatDatabase() {
        this.chats = new ArrayList<>();
        this.chats.add("I'm so happy today!");
        this.chats.add("I'm feeling sad and blue.");
        this.chats.add("I'm hungry, where's the food?");
        this.chats.add("I love this beautiful day!");
        this.chats.add("I hate this rainy weather.");
        this.chats.add("I'm excited for the weekend!");
        this.chats.add("I'm bored, what can I do?");
        this.chats.add("I'm grateful for my friends.");
        this.chats.add("I'm angry at the world.");
    }

    public String getChat() {
        if (chats.isEmpty()) {
            return null;
        }
        return chats.remove(0);
    }
}