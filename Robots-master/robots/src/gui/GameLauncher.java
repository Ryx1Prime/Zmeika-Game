package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameLauncher extends JFrame {

    public GameLauncher() {
        setTitle("Выбор режима игры");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 1, 10, 10));

        JButton btn1 = new JButton("Классика (Рисование мышкой)");
        btn1.setFont(new Font("Arial", Font.BOLD, 18));
        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainApplicationFrame mainFrame = new MainApplicationFrame();
                mainFrame.pack();
                mainFrame.setVisible(true);
                mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
                dispose();
            }
        });

        JButton btn2 = new JButton("Slither.io (Управление WASD)");
        btn2.setFont(new Font("Arial", Font.BOLD, 18));
        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame f = new JFrame("Slither.io - Играем!");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(800, 600);
                f.setLocationRelativeTo(null);
                f.add(new SlitherVisualizer());
                f.setVisible(true);
                dispose();
            }
        });

        add(btn1);
        add(btn2);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GameLauncher launcher = new GameLauncher();
                launcher.setVisible(true);
            }
        });
    }
}