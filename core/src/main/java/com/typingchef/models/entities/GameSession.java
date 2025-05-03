package com.typingchef.models.entities;

import com.badlogic.gdx.graphics.Texture;
import com.typingchef.models.systems.ActionStation;
import com.typingchef.models.systems.PathManager;
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

    private Character chef;           // Nhân vật đầu bếp
    private PathManager pathManager;   // Quản lý quỹ đạo
    private float initialX, initialY;

    public GameSession(int level, Texture chefTexture) {
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

        // Khởi tạo vị trí ban đầu của đầu bếp (giữa màn hình)
        this.initialX = 400;
        this.initialY = 300;

        // Khởi tạo nhân vật
        this.chef = new Character(chefTexture, initialX, initialY);

        // Khởi tạo quản lý quỹ đạo
        this.pathManager = new PathManager(initialX, initialY);

        // Khởi tạo các trạm từ
        initializeWordStations();

        // Đảm bảo mỗi trạm có một từ
        for (ActionStation station : wordStations) {
            if (!station.hasWord()) {
                generateWordForStation(station);
            }
        }

        this.chef.setMovementCompletedCallback(new Character.MovementCompletedCallback() {
            @Override
            public void onMovementCompleted(Path completedPath) {
                // Thực hiện hành động dựa trên loại quỹ đạo
                if (completedPath.getActionType() == ActionType.SERVE_CUSTOMER) {
                    performServeAction(completedPath.getCustomerId());
                } else {
                    performAction(completedPath.getActionType());
                }
            }
        });
    }

    private void performAction(ActionType actionType) {
        switch (actionType) {
            case PREPARE_BREAD:
                hasBread = true;
                break;
            case PREPARE_COFFEE:
                hasCoffee = true;
                break;
        }
    }

    /**
     * Thực hiện hành động phục vụ khách hàng
     * @param customerId ID của khách hàng
     */
    private void performServeAction(int customerId) {
        // Tìm khách hàng theo ID
        for (Customer customer : customers) {
            if (customer.getId() == customerId) {
                if (customer.serve(hasBread, hasCoffee)) {
                    // Nếu phục vụ thành công, reset đồ đang cầm
                    hasBread = false;
                    hasCoffee = false;
                    score += 10;
                }
                break;
            }
        }
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

        // Cập nhật thời gian
        gameTime += delta;
        timeSinceLastCustomer += delta;

        // Kiểm tra hết thời gian level
        if (gameTime >= levelTime) {
            isGameOver = true;
            return;
        }

        // Cập nhật nhân vật
        chef.update(delta);

        // Cập nhật khách hàng
        updateCustomers(delta);

        // Tạo khách mới nếu cần
        if (customers.size() < maxCustomers && timeSinceLastCustomer >= customerSpawnTime) {
            spawnCustomer();
            timeSinceLastCustomer = 0;
        }

        // Đảm bảo mỗi trạm có từ
        for (ActionStation station : wordStations) {
            if (!station.hasWord()) {
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

                // Tìm trạm từ của khách và xóa
                for (ActionStation station : wordStations) {
                    if (station.getActionType() == ActionType.SERVE_CUSTOMER &&
                        station.getCustomerId() == customer.getId()) {
                        stationsToRemove.add(station);
                        // Xóa quỹ đạo đến khách này
                        pathManager.removeCustomerPath(customer.getId());
                        break;
                    }
                }

                // Cộng điểm nếu khách hài lòng
                if (customer.isHappy()) {
                    score += 10;
                }
            }
        }

        // Xóa khách và trạm
        customers.removeAll(customersToRemove);
        wordStations.removeAll(stationsToRemove);
    }

    private void spawnCustomer() {
        float patience = Math.max(20, 60 - level * 3);
        Customer newCustomer = new Customer(++customerCount, patience);
        customers.add(newCustomer);

        // Tạo trạm phục vụ cho khách
        int stationX = 300 + (customers.size() - 1) * 150;
        int stationY = 400;
        ActionStation customerStation = new ActionStation(stationX, stationY, 100, 100, newCustomer.getId());
        wordStations.add(customerStation);

        // Tạo quỹ đạo đến khách này
        pathManager.createCustomerPath(newCustomer.getId(), stationX + 50, stationY + 50);

        // Sinh từ cho trạm khách hàng
        generateWordForStation(customerStation);
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
        if (input == null || input.isEmpty()) {
            return false;
        }

        // Kiểm tra từng trạm
        for (ActionStation station : wordStations) {
            Word word = station.getCurrentWord();
            if (word != null && word.matches(input)) {
                // Nếu nhân vật đang di chuyển, không cho thực hiện hành động mới
                if (chef.isMoving()) {
                    return false;
                }

                // Lấy quỹ đạo đến trạm này
                Path path = pathManager.getPathForStation(station);

                // Bắt đầu di chuyển nhân vật theo quỹ đạo
                chef.startMovingOnPath(path);

                // Xóa từ khỏi trạm
                station.clearWord();

                // Thực hiện hành động (sẽ được gọi khi nhân vật đến nơi)
                // Thay vì thực hiện ngay, ta sẽ đợi đến khi nhân vật di chuyển xong
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

    public Character getChef() {
        return chef;
    }
}
