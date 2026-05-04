package gui;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SlitherVisualizer extends JPanel {
    private final SlitherModel m_model;
    private final Timer m_timer;

    public SlitherVisualizer() {
        m_model = new SlitherModel(200, 200);
        m_timer = new Timer("events generator", true);

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() { repaint(); }
        }, 0, 50);

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() { m_model.update(); }
        }, 0, 10);

        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_A) { m_model.setTurningLeft(true); }
                if (key == KeyEvent.VK_D) { m_model.setTurningRight(true); }
                if (key == KeyEvent.VK_W) { m_model.setSprinting(true); }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_A) { m_model.setTurningLeft(false); }
                if (key == KeyEvent.VK_D) { m_model.setTurningRight(false); }
                if (key == KeyEvent.VK_W) { m_model.setSprinting(false); }
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

        if (!m_model.isGameOver()) {
            g2d.setColor(Color.RED);
            fillOval(g2d, m_model.getAppleX(), m_model.getAppleY(), 20, 20);
        }

        drawSnake(g2d);

        if (m_model.isGameOver()) {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 60));
            String text = "GAME OVER";
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(text)) / 2;
            g2d.drawString(text, textX, getHeight() / 2);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            String scoreText = "Счет: " + m_model.getScore();
            FontMetrics fm2 = g2d.getFontMetrics();
            int scoreX = (getWidth() - fm2.stringWidth(scoreText)) / 2;
            g2d.drawString(scoreText, scoreX, getHeight() / 2 + 50);
        }
    }

    private void drawSnake(Graphics2D g) {
        ArrayList<Double> hX = m_model.getHistoryX();
        ArrayList<Double> hY = m_model.getHistoryY();
        int cX = (int) Math.round(m_model.getX());
        int cY = (int) Math.round(m_model.getY());

        if (hX.size() > 0) {
            g.setColor(new Color(34, 139, 34));
            g.setStroke(new BasicStroke(24, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            for (int i = 0; i < hX.size() - 1; i++) {
                int x1 = (int) Math.round(hX.get(i));
                int y1 = (int) Math.round(hY.get(i));
                int x2 = (int) Math.round(hX.get(i + 1));
                int y2 = (int) Math.round(hY.get(i + 1));
                g.drawLine(x1, y1, x2, y2);
            }

            int lastIdx = hX.size() - 1;
            g.drawLine((int) Math.round(hX.get(lastIdx)), (int) Math.round(hY.get(lastIdx)), cX, cY);
            g.setStroke(new BasicStroke(1));
        }

        double direction = m_model.getDirection();

        AffineTransform oldT = g.getTransform();
        AffineTransform t = AffineTransform.getRotateInstance(direction, cX, cY);
        g.setTransform(t);

        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(2));
        g.drawLine(cX + 14, cY, cX + 22, cY);
        g.drawLine(cX + 22, cY, cX + 26, cY - 4);
        g.drawLine(cX + 22, cY, cX + 26, cY + 4);
        g.setStroke(new BasicStroke(1));

        g.setColor(new Color(34, 139, 34));
        fillOval(g, cX, cY, 28, 28);

        g.setColor(Color.WHITE);
        fillOval(g, cX + 4, cY - 8, 10, 10);
        fillOval(g, cX + 4, cY + 8, 10, 10);

        g.setColor(Color.BLACK);
        fillOval(g, cX + 6, cY - 8, 6, 6);
        fillOval(g, cX + 6, cY + 8, 6, 6);

        g.setTransform(oldT);
    }

    private void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
}