package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SlitherMainFrame extends JFrame {

    private final Color COLOR_BG = new Color(13, 15, 23);
    private final Color COLOR_PANEL = new Color(22, 25, 37);
    private final Color COLOR_ACCENT = new Color(0, 230, 118);
    private final Color COLOR_TEXT_MAIN = new Color(240, 244, 248);
    private final Color COLOR_TEXT_MUTED = new Color(144, 164, 174);
    private final Color COLOR_SCORE = new Color(255, 61, 0);

    private static JTextArea logArea;
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

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

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(16, 18, 26));
        logArea.setForeground(new Color(155, 170, 190));
        logArea.setFont(new Font("Consolas", Font.PLAIN, 18));
        logArea.setMargin(new Insets(8, 12, 8, 12));

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createEmptyBorder());

        JInternalFrame logWindow = new JInternalFrame("", false, false, false, false);
        ((javax.swing.plaf.basic.BasicInternalFrameUI) logWindow.getUI()).setNorthPane(null);
        logWindow.setBorder(new LineBorder(COLOR_PANEL, 2));
        logWindow.setPreferredSize(new Dimension(0, 180));
        logWindow.add(logScroll);

        JInternalFrame gameWindow = new JInternalFrame("", false, false, false, false);
        ((javax.swing.plaf.basic.BasicInternalFrameUI) gameWindow.getUI()).setNorthPane(null);
        gameWindow.setBorder(new LineBorder(COLOR_PANEL, 2));
        SlitherVisualizer visualizer = new SlitherVisualizer();
        gameWindow.add(visualizer);

        JInternalFrame coordsWindow = new JInternalFrame("", false, false, false, false);
        ((javax.swing.plaf.basic.BasicInternalFrameUI) coordsWindow.getUI()).setNorthPane(null);
        coordsWindow.setBorder(new LineBorder(COLOR_PANEL, 2));
        coordsWindow.setPreferredSize(new Dimension(420, 0));

        JPanel shopPanel = new JPanel();
        shopPanel.setBackground(COLOR_PANEL);
        shopPanel.setLayout(new GridLayout(7, 1, 0, 20));
        shopPanel.setBorder(new EmptyBorder(40, 30, 40, 30));

        JLabel titleLabel = new JLabel("МОНИТОРИНГ СИСТЕМЫ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(COLOR_TEXT_MAIN);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel scoreLabel = new JLabel("Счет: 0 pts");
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        scoreLabel.setForeground(COLOR_SCORE);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel speedLabel = new JLabel("Скорость: Уровень 1");
        speedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        speedLabel.setForeground(COLOR_TEXT_MUTED);
        speedLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel magnetLabel = new JLabel("Магнит: Уровень 0");
        magnetLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        magnetLabel.setForeground(COLOR_TEXT_MUTED);
        magnetLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton buySpeedBtn = new JButton("Улучшить Скорость");
        styleGameButton(buySpeedBtn, new Color(34, 40, 58));

        JButton buyMagnetBtn = new JButton("Улучшить Магнит");
        styleGameButton(buyMagnetBtn, new Color(34, 40, 58));

        JButton changeColorBtn = new JButton("Сменить скин");
        styleGameButton(changeColorBtn, new Color(41, 45, 62));

        shopPanel.add(titleLabel);
        shopPanel.add(scoreLabel);
        shopPanel.add(speedLabel);
        shopPanel.add(magnetLabel);
        shopPanel.add(buySpeedBtn);
        shopPanel.add(buyMagnetBtn);
        shopPanel.add(changeColorBtn);

        coordsWindow.add(shopPanel);

        desktopPane.add(gameWindow, BorderLayout.CENTER);
        desktopPane.add(coordsWindow, BorderLayout.EAST);
        desktopPane.add(logWindow, BorderLayout.SOUTH);

        gameWindow.setVisible(true);
        coordsWindow.setVisible(true);
        logWindow.setVisible(true);

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

    public static void log(String message) {
        if (logArea != null) {
            SwingUtilities.invokeLater(() -> {
                String time = LocalTime.now().format(timeFormatter);
                logArea.append(time + " " + message + "\n");

                if (logArea.getLineCount() > 100) {
                    try {
                        int end = logArea.getLineStartOffset(logArea.getLineCount() - 100);
                        logArea.replaceRange("", 0, end);
                    } catch (Exception ex) {}
                }

                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        }
    }
}