import java.util.ArrayList;
import java.util.List;

public class ChatDatabase {
    private List<String> chats;

    public ChatDatabase() {
        chats = new ArrayList<>();
        chats.add("I'm so happy today!");
        chats.add("I'm feeling sad.");
        chats.add("I'm hungry.");
        chats.add("I love this restaurant.");
        chats.add("I hate this movie.");
        chats.add("I'm excited for the weekend.");
        chats.add("I'm bored.");
        chats.add("I love my job.");
        chats.add("I'm tired.");
        chats.add("I'm angry.");
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