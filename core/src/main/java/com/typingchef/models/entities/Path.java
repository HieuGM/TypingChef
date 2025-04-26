package com.typingchef.models.entities;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public class Path {
    private List<Vector2> points;
    private ActionType actionType;
    private int customerId = -1;

    public Path(ActionType actionType) {
        this.actionType = actionType;
        this.points = new ArrayList<>();
    }

    public Path(int customerId) {
        this.actionType = ActionType.SERVE_CUSTOMER;
        this.customerId = customerId;
        this.points = new ArrayList<>();
    }

    public void addPoint(float x, float y) {
        points.add(new Vector2(x, y));
    }

    public List<Vector2> getPoints() {
        return points;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public int getCustomerId() {
        return customerId;
    }
}
