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

        JInternalFrame coordsWindow = new JInternalFrame("Координаты", true, true, true, true);
        JTextArea coordsText = new JTextArea();
        coordsText.setEditable(false);
        coordsText.setFont(new Font("Arial", Font.BOLD, 16));
        coordsWindow.add(coordsText);
        coordsWindow.setSize(250, 150);
        coordsWindow.setLocation(830, 10);
        coordsWindow.setVisible(true);
        desktopPane.add(coordsWindow);

        Timer timer = new Timer("CoordsUpdater", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SlitherModel model = visualizer.getModel();
                if (model != null) {
                    String text = String.format(" X: %d\n Y: %d\n Угол: %.2f\n Счет: %d",
                            (int)model.getX(), (int)model.getY(), model.getDirection(), model.getScore());
                    coordsText.setText(text);
                }
            }
        }, 0, 50);
    }
}