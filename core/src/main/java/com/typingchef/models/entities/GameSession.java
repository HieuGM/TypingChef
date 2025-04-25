package com.typingchef.models.entities;

import com.typingchef.models.systems.ActionStation;
import com.typingchef.models.systems.WordGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameSession {
    private int level;
    private int score;
    private float gameTime;
    private float levelTime;
    private boolean isGameOver;

    private List<Customer> customers;
    private List<ActionStation> wordStations;
    private WordGenerator wordGenerator;

    private float wordSpawnTime;
    private float timeSinceLastWord;

    private boolean hasBread;
    private boolean hasCoffee;

    // Cài đặt level
    private float customerSpawnTime;
    private float timeSinceLastCustomer;
    private int maxCustomers;
    private int customerCount;

    public GameSession(int level) {
        this.level = Math.max(1, level);
        this.score = 0;
        this.gameTime = 0;
        this.levelTime = 120;
        this.isGameOver = false;

        this.customers = new ArrayList<>();
        this.wordStations = new ArrayList<>();
        this.wordGenerator = new WordGenerator();
        this.wordGenerator.setLevel(level);

        this.hasBread = false;
        this.hasCoffee = false;

        this.wordSpawnTime = 5.0f - (level * 0.3f);
        this.timeSinceLastWord = 0;

        this.customerSpawnTime = 10.0f - (level * 0.5f);
        this.timeSinceLastCustomer = 0;
        this.maxCustomers = 3 + level;
        this.customerCount = 0;

        initializeWordStations();
    }

    private void initializeWordStations() {
        wordStations.add(new ActionStation(ActionType.PREPARE_BREAD, 100, 100, 100, 100));
        wordStations.add(new ActionStation(ActionType.PREPARE_COFFEE, 600, 100, 100, 100));
        for (ActionStation station : wordStations) {
            if (station.needsWord()) {
                generateWordForStation(station);
            }
        }
    }

    public void update(float delta) {
        if (isGameOver) {
            return;
        }

        gameTime += delta;
        timeSinceLastWord += delta;
        timeSinceLastCustomer += delta;

        if (gameTime >= levelTime) {
            isGameOver = true;
            return;
        }

        for (ActionStation station : wordStations) {
            station.update(delta);
        }

        updateCustomers(delta);

        if (customers.size() < maxCustomers && timeSinceLastCustomer >= customerSpawnTime) {
            spawnCustomer();
            timeSinceLastCustomer = 0;
        }

//        if (timeSinceLastWord >= wordSpawnTime) {
//            spawnRandomWord();
//            timeSinceLastWord = 0;
//        }
        for (ActionStation station : wordStations) {
            if (station.needsWord()) {
                generateWordForStation(station);
            }
        }
    }
    private void generateWordForStation(ActionStation station) {
        Word newWord;

        if (station.getActionType() == ActionType.SERVE_CUSTOMER) {
            newWord = wordGenerator.generateWordForCustomer(station.getCustomerId());
        } else {
            newWord = wordGenerator.generateWord(station.getActionType());
        }

        if (newWord != null) {
            station.setWord(newWord);
        }
    }

    private void updateCustomers(float delta) {
        List<Customer> customersToRemove = new ArrayList<>();
        List<ActionStation> stationsToRemove = new ArrayList<>();

        for (Customer customer : customers) {
            boolean staying = customer.update(delta);

            if (!staying) {
                customersToRemove.add(customer);

                for (ActionStation station : wordStations) {
                    if (station.getActionType() == ActionType.SERVE_CUSTOMER &&
                        station.getCustomerId() == customer.getId()) {
                        stationsToRemove.add(station);
                        break;
                    }
                }

                if (customer.isHappy()) {
                    score += 10;
                }
            }
        }

        customers.removeAll(customersToRemove);
        wordStations.removeAll(stationsToRemove);
    }

    private void spawnCustomer() {
        float patience = Math.max(20, 60 - level * 3);
        Customer newCustomer = new Customer(++customerCount, patience);
        customers.add(newCustomer);

        int stationX = 300 + (customers.size() - 1) * 150;
        int stationY = 400;
        wordStations.add(new ActionStation(stationX, stationY, 100, 100, newCustomer.getId()));
    }

    private void spawnRandomWord() {
        List<ActionStation> availableStations = new ArrayList<>();
        for (ActionStation station : wordStations) {
            if (!station.hasWord() && station.isActive()) {
                availableStations.add(station);
            }
        }

        if (availableStations.isEmpty()) {
            return;
        }
        ActionStation randomStation = availableStations.get((int)(Math.random() * availableStations.size()));
        Word newWord;
        if (randomStation.getActionType() == ActionType.SERVE_CUSTOMER) {
            newWord = wordGenerator.generateWordForCustomer(randomStation.getCustomerId());
        } else {
            newWord = wordGenerator.generateWord(randomStation.getActionType());
        }

        if (newWord != null) {
            randomStation.setWord(newWord);
        }
    }

    public boolean checkWord(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        for (ActionStation station : wordStations) {
            Word word = station.getCurrentWord();
            if (word != null && word.matches(input)) {
                performAction(station);
                station.clearWord();
                return true;
            }
        }

        return false;
    }

    private void performAction(ActionStation station) {
        switch (station.getActionType()) {
            case PREPARE_BREAD:
                hasBread = true;
                break;

            case PREPARE_COFFEE:
                hasCoffee = true;
                break;

            case SERVE_CUSTOMER:
                for (Customer customer : customers) {
                    if (customer.getId() == station.getCustomerId()) {
                        if (customer.serve(hasBread, hasCoffee)) {
                            hasBread = false;
                            hasCoffee = false;
                            score += 10;
                        }
                        break;
                    }
                }
                break;

            default:
                break;
        }
    }


    public int getLevel() {
        return level;
    }

    public int getScore() {
        return score;
    }

    public float getGameTime() {
        return gameTime;
    }

    public float getRemainingTime() {
        return Math.max(0, levelTime - gameTime);
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<ActionStation> getWordStations() {
        return wordStations;
    }

    public boolean hasBread() {
        return hasBread;
    }

    public boolean hasCoffee() {
        return hasCoffee;
    }
}
