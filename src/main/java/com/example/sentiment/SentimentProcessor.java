package com.example.sentiment;

import vcpu.SentimentIPU;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Wraps interaction with the provided {@link SentimentIPU} iPU.
 * It triggers asynchronous processing, waits for completion,
 * then reads the sentiment and confidence outputs.
 */
public class SentimentProcessor {

    /**
     * Sends a chat string to the {@code SentimentIPU} and returns the
     * resulting sentiment and confidence.
     *
     * @param chat the chat text to analyse
     * @return a {@link Result} containing sentiment and confidence
     * @throws RuntimeException if the asynchronous processing fails
     */
    public Result process(String chat) {
        SentimentIPU ipu = new SentimentIPU();

        // 1. Provide the input – this automatically triggers the async runner
        ipu.writeInput("chatInput", chat);

        // 2. Wait for the asynchronous job to finish
        Future<?> future = ipu.getFuture(); // assumed API
        try {
            future.get(); // blocks until completion without busy‑waiting
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sentiment processing was interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Sentiment processing failed", e.getCause());
        }

        // 3. Retrieve outputs
        String sentiment = ipu.readOutput("sentiment");
        String confidence = ipu.readOutput("confidence");

        return new Result(sentiment, confidence);
    }
}