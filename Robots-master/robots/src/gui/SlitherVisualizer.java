package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SlitherVisualizer extends JPanel {
    private final SlitherModel m_model;
    private final Timer m_physicsTimer;
    private final javax.swing.Timer m_renderTimer;
    private JButton retryButton;

    private final Color COLOR_GAME_BG = new Color(40, 44, 62);
    private final Color COLOR_GRID = new Color(52, 57, 80);
    private final Color COLOR_ROCK = new Color(68, 74, 102);
    private final Color COLOR_ROCK_BORDER = new Color(255, 61, 0);

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean sprintPressed = false;

    private javax.swing.Timer leftTimer;
    private javax.swing.Timer rightTimer;
    private javax.swing.Timer sprintTimer;

    public SlitherVisualizer() {
        m_model = new SlitherModel(200, 200);
        setLayout(null);
        setBackground(COLOR_GAME_BG);

        retryButton = new JButton("RETRY");
        retryButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        retryButton.setBackground(new Color(255, 61, 0));
        retryButton.setForeground(Color.WHITE);
        retryButton.setFocusPainted(false);
        retryButton.setBorder(BorderFactory.createEmptyBorder());
        retryButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        retryButton.setVisible(false);
        retryButton.addActionListener(e -> {
            m_model.reset();
            retryButton.setVisible(false);
            requestFocusInWindow();
        });
        add(retryButton);

        m_physicsTimer = new Timer("physics generator", true);
        m_physicsTimer.schedule(new TimerTask() {
            @Override
            public void run() { m_model.update(); }
        }, 0, 10);

        m_renderTimer = new javax.swing.Timer(16, e -> repaint());
        m_renderTimer.start();

        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                if (key == KeyEvent.VK_A) {
                    if (leftTimer != null && leftTimer.isRunning()) {
                        leftTimer.stop();
                    } else if (!leftPressed) {
                        leftPressed = true;
                        m_model.setTurningLeft(true);
                        SlitherMainFrame.log("нажал а поворот налево");
                    }
                }
                if (key == KeyEvent.VK_D) {
                    if (rightTimer != null && rightTimer.isRunning()) {
                        rightTimer.stop();
                    } else if (!rightPressed) {
                        rightPressed = true;
                        m_model.setTurningRight(true);
                        SlitherMainFrame.log("нажал d поворот направо");
                    }
                }
                if (key == KeyEvent.VK_W) {
                    if (sprintTimer != null && sprintTimer.isRunning()) {
                        sprintTimer.stop();
                    } else if (!sprintPressed) {
                        sprintPressed = true;
                        m_model.setSprinting(true);
                        SlitherMainFrame.log("нажал w ускорение");
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();

                if (key == KeyEvent.VK_A) {
                    if (leftTimer != null) leftTimer.stop();
                    leftTimer = new javax.swing.Timer(20, el -> {
                        leftPressed = false;
                        m_model.setTurningLeft(false);
                        SlitherMainFrame.log("отпустил а");
                    });
                    leftTimer.setRepeats(false);
                    leftTimer.start();
                }
                if (key == KeyEvent.VK_D) {
                    if (rightTimer != null) rightTimer.stop();
                    rightTimer = new javax.swing.Timer(20, el -> {
                        rightPressed = false;
                        m_model.setTurningRight(false);
                        SlitherMainFrame.log("отпустил d");
                    });
                    rightTimer.setRepeats(false);
                    rightTimer.start();
                }
                if (key == KeyEvent.VK_W) {
                    if (sprintTimer != null) sprintTimer.stop();
                    sprintTimer = new javax.swing.Timer(20, el -> {
                        sprintPressed = false;
                        m_model.setSprinting(false);
                        SlitherMainFrame.log("отпустил w");
                    });
                    sprintTimer.setRepeats(false);
                    sprintTimer.start();
                }
            }
        });
    }

    public SlitherModel getModel() {
        return m_model;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        m_model.setFieldSize(getWidth(), getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(COLOR_GRID);
        for (int i = 0; i < getWidth(); i += 60) {
            g2d.drawLine(i, 0, i, getHeight());
        }
        for (int j = 0; j < getHeight(); j += 60) {
            g2d.drawLine(0, j, getWidth(), j);
        }

        int[] rx = m_model.getRocksX();
        int[] ry = m_model.getRocksY();
        for (int i = 0; i < rx.length; i++) {
            g2d.setColor(COLOR_ROCK);
            g2d.fillRect(rx[i] - 30, ry[i] - 30, 60, 60);
            g2d.setColor(COLOR_ROCK_BORDER);
            g2d.drawRect(rx[i] - 30, ry[i] - 30, 60, 60);
        }

        if (!m_model.isGameOver()) {
            int type = m_model.getAppleType();
            if (type == 0) {
                g2d.setColor(new Color(255, 17, 96));
            } else if (type == 1) {
                g2d.setColor(new Color(255, 234, 0));
            } else {
                g2d.setColor(new Color(170, 0, 255));
            }
            fillOval(g2d, m_model.getAppleX(), m_model.getAppleY(), 36, 36);
        }

        if (m_model.isShrinkAppleActive()) {
            g2d.setColor(new Color(0, 229, 255));
            fillOval(g2d, m_model.getShrinkAppleX(), m_model.getShrinkAppleY(), 36, 36);
        }

        drawSnake(g2d);

        if (m_model.isGameOver()) {
            g2d.setColor(new Color(13, 15, 23, 200));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(new Color(255, 17, 96));
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 64));
            String text = "GAME OVER";
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(text)) / 2;
            g2d.drawString(text, textX, getHeight() / 2 - 30);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 28));
            String scoreText = "Итоговый результат: " + m_model.getScore() + " pts";
            FontMetrics fm2 = g2d.getFontMetrics();
            int scoreX = (getWidth() - fm2.stringWidth(scoreText)) / 2;
            g2d.drawString(scoreText, scoreX, getHeight() / 2 + 25);

            if (!retryButton.isVisible()){
                int btnW = 200;
                int btnH = 55;
                retryButton.setBounds((getWidth() - btnW) / 2, getHeight()/ 2 + 80, btnW, btnH);
                retryButton.setVisible(true);
            }
        }
    }

    private void drawSnake(Graphics2D g) {
        ArrayList<Double> hX = m_model.getHistoryX();
        ArrayList<Double> hY = m_model.getHistoryY();
        int cX = (int) Math.round(m_model.getX());
        int cY = (int) Math.round(m_model.getY());

        if (hX.size() > 0) {
            g.setColor(m_model.getSnakeColor());
            g.setStroke(new BasicStroke(32, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            for (int i = 0; i < hX.size() - 1; i++) {
                int x1 = (int) Math.round(hX.get(i));
                int y1 = (int) Math.round(hY.get(i));
                int x2 = (int) Math.round(hX.get(i + 1));
                int y2 = (int) Math.round(hY.get(i + 1));
                if (Math.abs(x1 - x2) < 100 && Math.abs(y1 - y2) < 100){
                    g.drawLine(x1, y1, x2, y2);
                }
            }

            int lastIdx = hX.size() - 1;
            int lX = (int) Math.round(hX.get(lastIdx));
            int lY = (int) Math.round(hY.get(lastIdx));
            if (Math.abs(lX - cX) < 100 && Math.abs(lY - cY) < 100) {
                g.drawLine(lX, lY, cX, cY);
            }
            g.setStroke(new BasicStroke(1));
        }

        if (m_model.getMagnetLevel() > 0) {
            g.setColor(new Color(0, 229, 255, 40));
            fillOval(g, cX, cY, m_model.getMagnetRadius() * 2, m_model.getMagnetRadius() * 2);
        }

        double direction = m_model.getDirection();

        AffineTransform oldT = g.getTransform();
        AffineTransform t = AffineTransform.getRotateInstance(direction, cX, cY);
        g.setTransform(t);

        g.setColor(new Color(255, 17, 96));
        g.setStroke(new BasicStroke(3));
        g.drawLine(cX + 18, cY, cX + 28, cY);
        g.drawLine(cX + 28, cY, cX + 33, cY - 5);
        g.drawLine(cX + 28, cY, cX + 33, cY + 5);
        g.setStroke(new BasicStroke(1));

        g.setColor(m_model.getSnakeColor());
        fillOval(g, cX, cY, 38, 38);

        g.setColor(Color.WHITE);
        fillOval(g, cX + 6, cY - 10, 13, 13);
        fillOval(g, cX + 6, cY + 10, 13, 13);

        g.setColor(Color.BLACK);
        fillOval(g, cX + 9, cY - 10, 7, 7);
        fillOval(g, cX + 9, cY + 10, 7, 7);

        g.setTransform(oldT);
    }

    private void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
}