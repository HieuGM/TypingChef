package com.typingchef.models.systems;


import com.typingchef.models.entities.ActionType;
import com.typingchef.models.entities.Path;

import java.util.HashMap;
import java.util.Map;

/**
 * Quản lý các quỹ đạo di chuyển trong game
 */
public class PathManager {
    // Map lưu trữ quỹ đạo cho mỗi loại hành động
    private Map<ActionType, Path> actionPaths;

    // Map lưu trữ quỹ đạo đến khách hàng
    private Map<Integer, Path> customerPaths;

    // Vị trí khởi đầu của nhân vật
    private float startX, startY;

    /**
     * Khởi tạo PathManager
     */
    public PathManager(float startX, float startY) {
        this.startX = startX;
        this.startY = startY;
        this.actionPaths = new HashMap<>();
        this.customerPaths = new HashMap<>();
        initializeDefaultPaths();
    }

    /**
     * Khởi tạo các quỹ đạo mặc định
     */
    private void initializeDefaultPaths() {
        // Tạo quỹ đạo đến trạm làm bánh
        Path breadPath = new Path(ActionType.PREPARE_BREAD);
        breadPath.addPoint(startX, startY); // Điểm bắt đầu
        breadPath.addPoint(100, 200);       // Điểm trung gian
        breadPath.addPoint(150, 100);       // Điểm đến trạm bánh
        actionPaths.put(ActionType.PREPARE_BREAD, breadPath);

        // Tạo quỹ đạo đến trạm cà phê
        Path coffeePath = new Path(ActionType.PREPARE_COFFEE);
        coffeePath.addPoint(startX, startY); // Điểm bắt đầu
        coffeePath.addPoint(400, 300);       // Điểm trung gian
        coffeePath.addPoint(600, 100);       // Điểm đến trạm cà phê
        actionPaths.put(ActionType.PREPARE_COFFEE, coffeePath);

    }

    /**
     * Tạo quỹ đạo đến khách hàng
     */
    public void createCustomerPath(int customerId, float x, float y) {
        Path customerPath = new Path(customerId);
        customerPath.addPoint(startX, startY); // Điểm bắt đầu

        // Thêm một điểm trung gian để tạo đường cong
        float midX = (startX + x) / 2;
        float midY = Math.min(startY, y) - 50; // Thấp hơn một chút để tạo đường cong
        customerPath.addPoint(midX, midY);

        customerPath.addPoint(x, y); // Điểm đến khách hàng
        customerPaths.put(customerId, customerPath);
    }

    /**
     * Lấy quỹ đạo cho một trạm từ
     */
    public Path getPathForStation(ActionStation station) {
        if (station.getActionType() == ActionType.SERVE_CUSTOMER) {
            return customerPaths.get(station.getCustomerId());
        } else {
            return actionPaths.get(station.getActionType());
        }
    }
    public void addPath(ActionType actionType, Path path) {
        actionPaths.put(actionType, path);
    }
    /**
     * Xóa quỹ đạo đến khách hàng khi khách đã rời đi
     */
    public void removeCustomerPath(int customerId) {
        customerPaths.remove(customerId);
    }

    public Path getPath(ActionType actionType) {
        return actionPaths.get(actionType);
    }

    /**
      Lấy quỹ đạo đến khách hàng
     */
    public Path getCustomerPath(int customerId) {
        return customerPaths.get(customerId);
    }
}
