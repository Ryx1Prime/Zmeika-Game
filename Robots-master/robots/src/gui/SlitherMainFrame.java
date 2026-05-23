package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SlitherMainFrame extends JFrame {

    private final Color COLOR_BG = new Color(13, 15, 23);
    private final Color COLOR_PANEL = new Color(22, 25, 37);
    private final Color COLOR_ACCENT = new Color(0, 230, 118);
    private final Color COLOR_TEXT_MAIN = new Color(240, 244, 248);
    private final Color COLOR_TEXT_MUTED = new Color(144, 164, 174);
    private final Color COLOR_SCORE = new Color(255, 61, 0);

    public SlitherMainFrame() {
        setTitle("Slither.io - Игровой процесс");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                UIManager.put("OptionPane.background", COLOR_PANEL);
                UIManager.put("Panel.background", COLOR_PANEL);
                UIManager.put("OptionPane.messageForeground", COLOR_TEXT_MAIN);

                int result = JOptionPane.showConfirmDialog(
                        SlitherMainFrame.this,
                        "Вы уверены, что хотите выйти из игры?",
                        "Выход",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        JDesktopPane desktopPane = new JDesktopPane();
        desktopPane.setBackground(COLOR_BG);
        desktopPane.setLayout(new BorderLayout(15, 15));
        desktopPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(desktopPane);

        JInternalFrame gameWindow = new JInternalFrame("", false, false, false, false);
        ((javax.swing.plaf.basic.BasicInternalFrameUI) gameWindow.getUI()).setNorthPane(null);
        gameWindow.setBorder(new LineBorder(COLOR_PANEL, 2));
        SlitherVisualizer visualizer = new SlitherVisualizer();
        gameWindow.add(visualizer);
        desktopPane.add(gameWindow, BorderLayout.CENTER);
        gameWindow.setVisible(true);

        JInternalFrame coordsWindow = new JInternalFrame("", false, false, false, false);
        ((javax.swing.plaf.basic.BasicInternalFrameUI) coordsWindow.getUI()).setNorthPane(null);
        coordsWindow.setBorder(new LineBorder(COLOR_PANEL, 2));
        coordsWindow.setPreferredSize(new Dimension(420, 0));

        JPanel shopPanel = new JPanel();
        shopPanel.setBackground(COLOR_PANEL);
        shopPanel.setLayout(new GridBagLayout());
        shopPanel.setBorder(new EmptyBorder(40, 30, 40, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("МОНИТОРИНГ СИСТЕМЫ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(COLOR_TEXT_MAIN);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.weighty = 0.1;
        gbc.insets = new Insets(0, 0, 10, 0);
        shopPanel.add(titleLabel, gbc);

        JLabel scoreLabel = new JLabel("Счет: 0 pts");
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        scoreLabel.setForeground(COLOR_SCORE);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.weighty = 0.15;
        shopPanel.add(scoreLabel, gbc);

        JLabel speedLabel = new JLabel("Скорость: Уровень 1");
        speedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        speedLabel.setForeground(COLOR_TEXT_MUTED);
        speedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        gbc.weighty = 0.1;
        shopPanel.add(speedLabel, gbc);

        JLabel magnetLabel = new JLabel("Магнит: Уровень 0");
        magnetLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        magnetLabel.setForeground(COLOR_TEXT_MUTED);
        magnetLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 3;
        gbc.weighty = 0.1;
        shopPanel.add(magnetLabel, gbc);

        JButton buySpeedBtn = new JButton("Улучшить Скорость");
        styleGameButton(buySpeedBtn, new Color(34, 40, 58));
        gbc.gridy = 4;
        gbc.weighty = 0.15;
        gbc.insets = new Insets(10, 0, 10, 0);
        shopPanel.add(buySpeedBtn, gbc);

        JButton buyMagnetBtn = new JButton("Улучшить Магнит");
        styleGameButton(buyMagnetBtn, new Color(34, 40, 58));
        gbc.gridy = 5;
        gbc.weighty = 0.15;
        shopPanel.add(buyMagnetBtn, gbc);

        JButton changeColorBtn = new JButton("Сменить скин");
        styleGameButton(changeColorBtn, new Color(41, 45, 62));
        gbc.gridy = 6;
        gbc.weighty = 0.15;
        shopPanel.add(changeColorBtn, gbc);

        coordsWindow.add(shopPanel);
        desktopPane.add(coordsWindow, BorderLayout.EAST);
        coordsWindow.setVisible(true);

        SlitherModel model = visualizer.getModel();

        buySpeedBtn.addActionListener(e -> {
            if (model != null) {
                model.upgradeSpeed();
                visualizer.requestFocusInWindow();
            }
        });

        buyMagnetBtn.addActionListener(e -> {
            if (model != null) {
                model.upgradeMagnet();
                visualizer.requestFocusInWindow();
            }
        });

        changeColorBtn.addActionListener(e -> {
            if (model != null) {
                model.changeColor();
                visualizer.requestFocusInWindow();
            }
        });

        javax.swing.Timer uiTimer = new javax.swing.Timer(50, e -> {
            if (model != null) {
                scoreLabel.setText("Счёт: " + model.getScore() + " pts");
                speedLabel.setText("Скорость: Lvl " + model.getSpeedLevel() + " (Цена: " + model.getSpeedCost() + ")");
                magnetLabel.setText("Магнит: Lvl " + model.getMagnetLevel() + " (Цена: " + model.getMagnetCost() + ")");

                boolean canBuySpeed = model.getScore() >= model.getSpeedCost() && !model.isGameOver();
                boolean canBuyMagnet = model.getScore() >= model.getMagnetCost() && !model.isGameOver();

                buySpeedBtn.setEnabled(canBuySpeed);
                buyMagnetBtn.setEnabled(canBuyMagnet);
                changeColorBtn.setEnabled(!model.isGameOver());

                buySpeedBtn.setBackground(canBuySpeed ? COLOR_ACCENT : new Color(28, 31, 46));
                buyMagnetBtn.setBackground(canBuyMagnet ? COLOR_ACCENT : new Color(28, 31, 46));

                buySpeedBtn.setForeground(canBuySpeed ? COLOR_BG : COLOR_TEXT_MUTED);
                buyMagnetBtn.setForeground(canBuyMagnet ? COLOR_BG : COLOR_TEXT_MUTED);
            }
        });
        uiTimer.start();

        setVisible(true);
    }

    private void styleGameButton(JButton button, Color bg) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBackground(bg);
        button.setForeground(COLOR_TEXT_MAIN);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(45, 52, 74), 1));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}