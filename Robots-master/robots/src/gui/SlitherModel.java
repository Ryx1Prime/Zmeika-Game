package gui;

import java.util.ArrayList;
import java.util.Random;

public class SlitherModel {
    private double x;
    private double y;
    private double direction;

    private int fieldWidth = 800;
    private int fieldHeight = 600;

    private int appleX;
    private int appleY;
    private Random random = new Random();

    private final double normalSpeed = 0.15;
    private final double sprintSpeed = 0.3;
    private final double turnSpeed = 0.04;

    private boolean isTurningLeft = false;
    private boolean isTurningRight = false;
    private boolean isSprinting = false;

    private ArrayList<Double> historyX = new ArrayList<>();
    private ArrayList<Double> historyY = new ArrayList<>();

    private int tailLength = 15;
    private boolean isGameOver = false;

    public SlitherModel(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.direction = 0;
        spawnApple();
    }

    public void setFieldSize(int width, int height) {
        if (width > 0 && height > 0) {
            this.fieldWidth = width;
            this.fieldHeight = height;
        }
    }

    private void spawnApple() {
        appleX = 30 + random.nextInt(Math.max(1, fieldWidth - 60));
        appleY = 30 + random.nextInt(Math.max(1, fieldHeight - 60));
    }

    public void update() {
        if (isGameOver) return;

        if (isTurningLeft) direction -= turnSpeed;
        if (isTurningRight) direction += turnSpeed;

        double currentSpeed = isSprinting ? sprintSpeed : normalSpeed;

        x += currentSpeed * Math.cos(direction) * 10;
        y += currentSpeed * Math.sin(direction) * 10;

        if (x < 30) x = 30;
        if (x > fieldWidth - 30) x = fieldWidth - 30;
        if (y < 30) y = 30;
        if (y > fieldHeight - 30) y = fieldHeight - 30;

        boolean shouldAddHistory = false;
        if (historyX.isEmpty()) {
            shouldAddHistory = true;
        } else {
            double lastX = historyX.get(historyX.size() - 1);
            double lastY = historyY.get(historyY.size() - 1);
            double dX = x - lastX;
            double dY = y - lastY;
            if (Math.sqrt(dX * dX + dY * dY) >= 2.0) {
                shouldAddHistory = true;
            }
        }

        if (shouldAddHistory) {
            historyX.add(x);
            historyY.add(y);
            if (historyX.size() > tailLength) {
                historyX.remove(0);
                historyY.remove(0);
            }
        }

        double distX = x - appleX;
        double distY = y - appleY;
        if (Math.sqrt(distX * distX + distY * distY) < 20) {
            spawnApple();
            tailLength += 10;
        }

        for (int i = 0; i < historyX.size() - 15; i++) {
            double hx = historyX.get(i);
            double hy = historyY.get(i);
            double dX = x - hx;
            double dY = y - hy;
            if (Math.sqrt(dX * dX + dY * dY) < 12) {
                isGameOver = true;
                break;
            }
        }
    }

    public void setTurningLeft(boolean turningLeft) { this.isTurningLeft = turningLeft; }
    public void setTurningRight(boolean turningRight) { this.isTurningRight = turningRight; }
    public void setSprinting(boolean sprinting) { this.isSprinting = sprinting; }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getDirection() { return direction; }
    public int getAppleX() { return appleX; }
    public int getAppleY() { return appleY; }
    public ArrayList<Double> getHistoryX() { return historyX; }
    public ArrayList<Double> getHistoryY() { return historyY; }
    public boolean isGameOver() { return isGameOver; }
    public int getScore() { return (tailLength - 15) / 10; }
}