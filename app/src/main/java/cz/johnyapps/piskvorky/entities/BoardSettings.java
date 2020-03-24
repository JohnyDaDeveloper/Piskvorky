package cz.johnyapps.piskvorky.entities;

import java.util.HashMap;
import java.util.Map;

public class BoardSettings {
    private int widthFieldCount = 13;
    private int lineWidth = 6;

    private int shapeWidth = 10;
    private int shapePadding = 15;
    private int validStrike = 5;

    public BoardSettings() {

    }

    public Map<String, Object> toMap() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("widthFieldCount", widthFieldCount);
        settings.put("lineWidth", lineWidth);
        settings.put("shapeWidth", shapeWidth);
        settings.put("shapePadding", shapePadding);
        settings.put("validStrike", validStrike);

        return settings;
    }

    public int getWidthFieldCount() {
        return widthFieldCount;
    }

    public void setWidthFieldCount(int widthFieldCount) {
        this.widthFieldCount = widthFieldCount;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public int getShapeWidth() {
        return shapeWidth;
    }

    public void setShapeWidth(int shapeWidth) {
        this.shapeWidth = shapeWidth;
    }

    public int getShapePadding() {
        return shapePadding;
    }

    public void setShapePadding(int shapePadding) {
        this.shapePadding = shapePadding;
    }

    public int getValidStrike() {
        return validStrike;
    }

    public void setValidStrike(int validStrike) {
        this.validStrike = validStrike;
    }
}
