package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

public class SlitherMainFrame extends JFrame {

    public SlitherMainFrame() {
        setTitle("Slither.io - Игровой процесс");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                UIManager.put("OptionPane.yesButtonText", "Да");
                UIManager.put("OptionPane.noButtonText", "Нет");
                int result = JOptionPane.showConfirmDialog(
                        SlitherMainFrame.this,
                        "Вы уверены, что хотите выйти?",
                        "Подтверждение",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        JDesktopPane desktopPane = new JDesktopPane();
        setContentPane(desktopPane);

        JInternalFrame gameWindow = new JInternalFrame("Игровое поле", true, true, true, true);
        SlitherVisualizer visualizer = new SlitherVisualizer();
        gameWindow.add(visualizer);
        gameWindow.setSize(800, 600);
        gameWindow.setLocation(10, 10);
        gameWindow.setVisible(true);
        desktopPane.add(gameWindow);

        JInternalFrame coordsWindow = new JInternalFrame("Магазин и Статы", true, true, true, true);
        JPanel shopPanel = new JPanel();
        shopPanel.setLayout(new GridLayout(5, 1, 5, 5));

        JLabel statsLabel = new JLabel();
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton buySpeedBtn = new JButton("Скорость");
        JButton buyMagnetBtn = new JButton("Магнит");
        JButton changeColorBtn = new JButton("Сменить цвет (Бесплатно)");

        shopPanel.add(statsLabel);
        shopPanel.add(buySpeedBtn);
        shopPanel.add(buyMagnetBtn);
        shopPanel.add(changeColorBtn);

        coordsWindow.add(shopPanel);
        coordsWindow.setSize(350, 350);
        coordsWindow.setLocation(830, 100);
        coordsWindow.setVisible(true);
        desktopPane.add(coordsWindow);

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

        Timer timer = new Timer("CoordsUpdater", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (model != null) {
                    String text = String.format(
                            "<html><b>Текущая статистика:</b><br>" +
                                    "Счет: <font color='red'><b>%d</b></font> очков<hr>" +
                                    "<b>Скорость:</b> Уровень %d (Нужно: %d очков)<br>" +
                                    "<b>Магнит:</b> Уровень %d (Нужно: %d очков)</html>",
                            model.getScore(),
                            model.getSpeedLevel(), model.getSpeedCost(),
                            model.getMagnetLevel(), model.getMagnetCost()
                    );
                    statsLabel.setText(text);

                    buySpeedBtn.setEnabled(model.getScore() >= model.getSpeedCost() && !model.isGameOver());
                    buyMagnetBtn.setEnabled(model.getScore() >= model.getMagnetCost() && !model.isGameOver());
                    changeColorBtn.setEnabled(!model.isGameOver());
                }
            }
        }, 0, 50);
    }
}