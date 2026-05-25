package gui;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class SlitherModel {
    private double x;
    private double y;
    private double direction;

    private int fieldWidth = 800;
    private int fieldHeight = 600;

    private int appleX;
    private int appleY;
    private int appleType;

    private int shrinkAppleX = -100;
    private int shrinkAppleY = -100;
    private boolean isShrinkAppleActive = false;

    private Random random = new Random();

    private final double normalSpeed = 0.15;
    private final double sprintSpeed = 0.3;
    private final double turnSpeed = 0.04;

    private double speedMultiplier = 1.0;
    private int magnetRadius = 20;
    private Color snakeColor = new Color(34, 139, 34);

    private int speedLevel = 1;
    private int magnetLevel = 0;

    private boolean isTurningLeft = false;
    private boolean isTurningRight = false;
    private boolean isSprinting = false;

    private ArrayList<Double> historyX = new ArrayList<>();
    private ArrayList<Double> historyY = new ArrayList<>();

    private int tailLength = 15;
    private boolean isGameOver = false;

    private int[] rocksX = new int[13];
    private int[] rocksY = new int[13];
    private int invertTimer = 0;
    private int rockTimer = 0;
    private boolean isInitialized = false;

    public SlitherModel(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.direction = 0;
    }

    public void setFieldSize(int width, int height) {
        if (width > 0 && height > 0) {
            this.fieldWidth = width;
            this.fieldHeight = height;
            if (!isInitialized) {
                isInitialized = true;
                spawnRocks();
                spawnApple();
                SlitherMainFrame.log("игра запущена поле готово");
            }
        }
    }

    private boolean isValidSpawn(int px, int py, int minHeadDist) {
        double dX = px - x;
        double dY = py - y;
        if ((dX * dX + dY * dY) < minHeadDist * minHeadDist) return false;

        for (int i = 0; i < rocksX.length; i++) {
            double rDx = px - rocksX[i];
            double rDy = py - rocksY[i];
            if ((rDx * rDx + rDy * rDy) < 6400) return false;
        }
        return true;
    }

    private void spawnRocks() {
        int halfW = Math.max(100, fieldWidth / 2);
        int halfH = Math.max(100, fieldHeight / 2);
        int[][] quadrants = {
                {0, 0}, {halfW, 0}, {0, halfH}, {halfW, halfH}
        };

        for (int i = 0; i < rocksX.length; i++) {
            boolean safe = false;
            int attempts = 0;
            while (!safe && attempts < 50) {
                int offsetX = quadrants[i % 4][0];
                int offsetY = quadrants[i % 4][1];

                rocksX[i] = offsetX + 40 + random.nextInt(Math.max(1, halfW - 80));
                rocksY[i] = offsetY + 40 + random.nextInt(Math.max(1, halfH - 80));

                double dX = rocksX[i] - x;
                double dY = rocksY[i] - y;
                if ((dX * dX + dY * dY) > 16900) {
                    safe = true;
                }
                attempts++;
            }
        }
        rockTimer = 1000;
        SlitherMainFrame.log("появилось 13 камней");
    }

    private void spawnApple() {
        boolean safe = false;
        int attempts = 0;
        while (!safe && attempts < 50) {
            appleX = 40 + random.nextInt(Math.max(1, fieldWidth - 80));
            appleY = 40 + random.nextInt(Math.max(1, fieldHeight - 80));
            safe = isValidSpawn(appleX, appleY, 120);
            attempts++;
        }

        double r = random.nextDouble();
        if (r < 0.7) {
            appleType = 0;
            SlitherMainFrame.log("создано обычное яблоко");
        } else if (r < 0.85) {
            appleType = 1;
            SlitherMainFrame.log("создано золотое яблоко");
        } else {
            appleType = 2;
            SlitherMainFrame.log("появился ядовитый гриб");
        }
    }

    private void spawnShrinkApple() {
        boolean safe = false;
        int attempts = 0;
        while (!safe && attempts < 50) {
            shrinkAppleX = 40 + random.nextInt(Math.max(1, fieldWidth - 80));
            shrinkAppleY = 40 + random.nextInt(Math.max(1, fieldHeight - 80));
            safe = isValidSpawn(shrinkAppleX, shrinkAppleY, 120);
            attempts++;
        }
        isShrinkAppleActive = true;
        SlitherMainFrame.log("появилось синее яблоко");
    }

    public void update() {
        if (!isInitialized || isGameOver) return;

        if (invertTimer > 0) {
            invertTimer--;
            if (isTurningLeft) direction += turnSpeed;
            if (isTurningRight) direction -= turnSpeed;
        } else {
            if (isTurningLeft) direction -= turnSpeed;
            if (isTurningRight) direction += turnSpeed;
        }

        double currentSpeed = isSprinting ? sprintSpeed : normalSpeed;

        x += currentSpeed * speedMultiplier * Math.cos(direction) * 10;
        y += currentSpeed * speedMultiplier * Math.sin(direction) * 10;

        if (x < 0) x = fieldWidth;
        if (x > fieldWidth) x = 0;
        if (y < 0) y = fieldHeight;
        if (y > fieldHeight) y = 0;

        rockTimer--;
        if (rockTimer <= 0) {
            SlitherMainFrame.log("время камней вышло");
            spawnRocks();
        }

        for (int i = 0; i < rocksX.length; i++) {
            double dX = x - rocksX[i];
            double dY = y - rocksY[i];
            if ((dX * dX + dY * dY) < 2401) {
                isGameOver = true;
                SlitherMainFrame.log("врезался в камень игра окончена");
                return;
            }
        }

        boolean shouldAddHistory = false;
        if (historyX.isEmpty()) {
            shouldAddHistory = true;
        } else {
            double lastX = historyX.get(historyX.size() - 1);
            double lastY = historyY.get(historyY.size() - 1);
            double dX = x - lastX;
            double dY = y - lastY;
            if ((dX * dX + dY * dY) >= 4.0) {
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
        if ((distX * distX + distY * distY) < magnetRadius * magnetRadius) {
            if (appleType == 0) {
                tailLength += 10;
                SlitherMainFrame.log("съел яблоко хвост вырос");
            } else if (appleType == 1) {
                tailLength += 30;
                SlitherMainFrame.log("съел золотое яблоко хороший буст");
            } else if (appleType == 2) {
                tailLength = Math.max(15, tailLength - 10);
                invertTimer = 300;
                SlitherMainFrame.log("съел гриб управление перепутано");
            }
            spawnApple();
        }

        if (getScore() > 5 && !isShrinkAppleActive) {
            if (random.nextInt(300) == 0) {
                spawnShrinkApple();
            }
        }

        if (isShrinkAppleActive) {
            double sDistX = x - shrinkAppleX;
            double sDistY = y - shrinkAppleY;
            if ((sDistX * sDistX + sDistY * sDistY) < magnetRadius * magnetRadius) {
                tailLength = Math.max(15, tailLength - 20);
                isShrinkAppleActive = false;
                shrinkAppleX = -100;
                shrinkAppleY = -100;
                SlitherMainFrame.log("съел синее яблоко уменьшился");
            }
        }

        for (int i = 0; i < historyX.size() - 15; i++) {
            double hx = historyX.get(i);
            double hy = historyY.get(i);
            double dX = x - hx;
            double dY = y - hy;
            if ((dX * dX + dY * dY) < 256) {
                isGameOver = true;
                SlitherMainFrame.log("укусил себя за хвост конец игры");
                break;
            }
        }
    }

    public void setTurningLeft(boolean turningLeft) { this.isTurningLeft = turningLeft; }
    public void setTurningRight(boolean turningRight) { this.isTurningRight = turningRight; }
    public void setSprinting(boolean sprinting) { this.isSprinting = sprinting; }

    public void reset(){
        this.x = 200;
        this.y = 200;
        this.direction = 0;
        this.tailLength = 15;
        this.isGameOver = false;
        this.historyX.clear();
        this.historyY.clear();
        this.isTurningLeft = false;
        this.isTurningRight = false;
        this.isSprinting = false;
        this.speedMultiplier = 1.0;
        this.magnetRadius = 20;
        this.speedLevel = 1;
        this.magnetLevel = 0;
        this.invertTimer = 0;
        this.isShrinkAppleActive = false;
        this.snakeColor = new Color(34, 139, 34);
        this.isInitialized = true;
        SlitherMainFrame.log("рестарт игры");
        spawnRocks();
        spawnApple();
    }

    public void upgradeSpeed() {
        int cost = getSpeedCost();
        if (getScore() >= cost) {
            tailLength -= cost * 10;
            speedMultiplier += 0.2;
            speedLevel++;
            SlitherMainFrame.log("купил скорость уровень " + speedLevel);
        }
    }

    public void upgradeMagnet() {
        int cost = getMagnetCost();
        if (getScore() >= cost) {
            tailLength -= cost * 10;
            magnetRadius += 12;
            magnetLevel++;
            SlitherMainFrame.log("купил магнит уровень " + magnetLevel);
        }
    }

    public void changeColor() {
        this.snakeColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        SlitherMainFrame.log("поменял цвет змейки");
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getDirection() { return direction; }
    public int getAppleX() { return appleX; }
    public int getAppleY() { return appleY; }
    public int getAppleType() { return appleType; }

    public int getShrinkAppleX() { return shrinkAppleX; }
    public int getShrinkAppleY() { return shrinkAppleY; }
    public boolean isShrinkAppleActive() { return isShrinkAppleActive; }

    public int[] getRocksX() { return rocksX; }
    public int[] getRocksY() { return rocksY; }
    public ArrayList<Double> getHistoryX() { return historyX; }
    public ArrayList<Double> getHistoryY() { return historyY; }
    public boolean isGameOver() { return isGameOver; }
    public int getScore() { return (tailLength - 15) / 10; }
    public int getSpeedCost() { return speedLevel * 5; }
    public int getMagnetCost() { return (magnetLevel + 1) * 5; }
    public int getSpeedLevel() { return speedLevel; }
    public int getMagnetLevel() { return magnetLevel; }
    public int getMagnetRadius() { return magnetRadius; }
    public Color getSnakeColor() { return snakeColor; }
}