package com.typingchef.models.systems;

import com.typingchef.models.entities.ActionType;
import com.typingchef.models.entities.Word;

import java.util.*;

public class WordGenerator {
    private Map<ActionType, List<String>> wordLists;
    private Random random;
    private int level;

    public WordGenerator() {
        this.random = new Random();
        this.level = 1;
        this.wordLists = new HashMap<>();
        initializeWordLists();
    }

    private void initializeWordLists() {
        List<String> breadWords = new ArrayList<>();
        breadWords.add("bread");
        breadWords.add("bake");
        breadWords.add("dough");
        breadWords.add("pastry");
        breadWords.add("toast");
        breadWords.add("wheat");
        breadWords.add("flour");
        wordLists.put(ActionType.PREPARE_BREAD, breadWords);

        List<String> coffeeWords = new ArrayList<>();
        coffeeWords.add("coffee");
        coffeeWords.add("brew");
        coffeeWords.add("pour");
        coffeeWords.add("mocha");
        coffeeWords.add("latte");
        coffeeWords.add("espresso");
        coffeeWords.add("drink");
        wordLists.put(ActionType.PREPARE_COFFEE, coffeeWords);

        List<String> serveWords = new ArrayList<>();
        serveWords.add("serve");
        serveWords.add("give");
        serveWords.add("deliver");
        serveWords.add("here");
        serveWords.add("please");
        serveWords.add("enjoy");
        serveWords.add("ready");
        wordLists.put(ActionType.SERVE_CUSTOMER, serveWords);
    }

    public Word generateWord(ActionType actionType) {
        List<String> words = wordLists.get(actionType);
        if (words == null || words.isEmpty()) {
            return null;
        }

        String randomWord = words.get(random.nextInt(words.size()));

        float baseTime = 3.0f + randomWord.length() * 0.4f;
        float timeLimit = Math.max(2.0f, baseTime - (level * 0.2f));

        return new Word(randomWord, actionType, timeLimit);
    }

    public Word generateWordForCustomer(int customerId) {
        List<String> words = wordLists.get(ActionType.SERVE_CUSTOMER);
        if (words == null || words.isEmpty()) {
            return null;
        }

        String randomWord = words.get(random.nextInt(words.size()));

        float baseTime = 3.0f + randomWord.length() * 0.4f;
        float timeLimit = Math.max(2.0f, baseTime - (level * 0.2f));

        return new Word(randomWord, customerId, timeLimit);
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public void addWord(String word, ActionType actionType) {
        List<String> words = wordLists.get(actionType);
        if (words == null) {
            words = new ArrayList<>();
            wordLists.put(actionType, words);
        }

        if (!words.contains(word.toLowerCase())) {
            words.add(word.toLowerCase());
        }
    }
}
