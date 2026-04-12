**File: `src/main/java/com/example/sentiment/ChatDatabase.java`**
```java
package com.example.sentiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simulates a simple chat database using an {@link ArrayList} of preset phrases.
 * The {@code getNextChat()} method returns the next chat line and removes it
 * from the internal list. When the list is empty {@code null} is returned.
 *
 * This class is thread‑safe – the method is synchronized because the
 * {@link SentimentProcessor} may be called from multiple threads in the
 * future.
 */
public class ChatDatabase {

    /** Pre‑populated list of chat phrases with varying sentiment. */
    private final List<String> chats = Collections.synchronizedList(new ArrayList<>());

    /** Initialise the database with ten example phrases. */
    public ChatDatabase() {
        chats.add("I just got a promotion! I'm ecstatic!");
        chats.add("The weather is terrible today, I'm feeling down.");
        chats.add("That movie was absolutely fantastic, loved every minute.");
        chats.add("I'm so hungry, I could eat a horse.");
        chats.add("My cat just knocked over my coffee, what a disaster.");
        chats.add("I finally finished my project, relief is overwhelming.");
        chats.add("Why does my internet keep dropping? So frustrating!");
        chats.add("Just tasted the best pizza ever, pure joy!");
        chats.add("I'm nervous about the interview tomorrow.");
        chats.add("Everything is going perfectly, couldn't be happier.");
    }

    /**
     * Retrieves and removes the next chat line.
     *
     * @return the next chat string, or {@code null} if the database is empty
     */
    public synchronized String getNextChat() {
        if (chats.isEmpty()) {
            return null;
        }
        // Remove from the front of the list to preserve order
        return chats.remove(0);
    }
}
```

---

**File: `src/main/java/com/example/sentiment/SentimentResult.java`**
```java
package com.example.sentiment;

/**
 * Simple data holder for the result of a sentiment analysis.
 */
public final class SentimentResult {

    private final String sentiment;
    private final String confidence;

    public SentimentResult(String sentiment, String confidence) {
        this.sentiment = sentiment;
        this.confidence = confidence;
    }

    public String getSentiment() {
        return sentiment;
    }

    public String getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return String.format("Sentiment: %s, Confidence: %s", sentiment, confidence);
    }
}
```

---

**File: `src/main/java/com/example/sentiment/SentimentProcessor.java`**
```java
package com.example.sentiment;

import vcpu.SentimentIPU;                     // <-- the provided IPU component
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Wrapper around the external {@link SentimentIPU}. It hides the asynchronous
 * plumbing and exposes a simple {@code analyze(String chat)} method that
 * returns a {@link SentimentResult}.
 */
public class SentimentProcessor {

    private final SentimentIPU ipu;

    public SentimentProcessor() {
        this.ipu = new SentimentIPU();
    }

    /**
     * Sends a chat line to the IPU, waits for the asynchronous processing to
     * finish, then reads the sentiment and confidence outputs.
     *
     * @param chat the chat line to analyse
     * @return a {@link SentimentResult} containing the sentiment and confidence
     * @throws RuntimeException if the underlying async task fails or is
     *                          interrupted
     */
    public SentimentResult analyze(String chat) {
        // 1️⃣ Write the input – this automatically triggers the async runner
        ipu.writeInput("chatInput", chat);

        // 2️⃣ Obtain the Future that represents the async execution.
        //    The IPU contract guarantees a Future is available after writeInput.
        Future<?> future = ipu.getFuture();

        // 3️⃣ Wait for completion without busy‑waiting.
        try {
            future.get(); // blocks only until the IPU finishes
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sentiment analysis was interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Sentiment analysis failed", e.getCause());
        }

        // 4️⃣ Read the outputs.
        String sentiment = ipu.readOutput("sentiment");
        String confidence = ipu.readOutput("confidence");

        return new SentimentResult(sentiment, confidence);
    }
}
```

---

**File: `src/main/java/com/example/sentiment/Main.java`**
```java
package com.example.sentiment;

/**
 * Entry point for the basic sentiment analyzer demo.
 *
 * The program repeatedly fetches a chat line from {@link ChatDatabase},
 * sends it to {@link SentimentProcessor}, and prints the resulting sentiment
 * and confidence to the console. The loop terminates when the database is
 * exhausted (i.e., {@code getNextChat()} returns {@code null}).
 */
public class Main {

    public static void main(String[] args) {
        ChatDatabase database = new ChatDatabase();
        SentimentProcessor processor = new SentimentProcessor();

        while (true) {
            String chat = database.getNextChat();
            if (chat == null) {
                System.out.println("No more chats to process. Exiting.");
                break;
            }

            System.out.println("Processing chat: \"" + chat + "\"");

            SentimentResult result = processor.analyze(chat);
            System.out.println("Result -> " + result);
            System.out.println("-----------------------------------");
        }
    }
}
```

---

### How the implementation follows the required flow

1. **START** – `Main.main` begins execution.  
2. **DATA** – `ChatDatabase` holds ten preset phrases and returns/removes one per call.  
3. **VCPU (process sentiments)** – `SentimentProcessor.analyze` creates a `SentimentIPU`, writes the input (`chatInput`), obtains the `Future` via `ipu.getFuture()`, blocks on `future.get()`, then reads `sentiment` and `confidence`.  
4. **LOOP** – The `while` loop in `Main` continues until `ChatDatabase.getNextChat()` returns `null`.  
5. **PROCESS** – The sentiment and confidence are printed to the console.  
6. **END** – When the database is empty, a final message is printed and the program exits.

All mandatory constraints are satisfied:

* The provided `SentimentIPU` is imported from `vcpu/sentiment-ipu.java` (package `vcpu`).  
* No recreation of the IPU class occurs.  
* Asynchronous handling uses the `Future` returned by the IPU; no busy‑waiting loops are used.  
* Inputs/outputs use the exact keys specified (`chatInput`, `sentiment`, `confidence`).