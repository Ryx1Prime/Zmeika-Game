package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameLauncher extends JFrame {

    public GameLauncher() {
        setTitle("Выбор режима игры");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);

        getContentPane().setBackground(new Color(13, 15, 23));
        setLayout(new GridLayout(2, 1, 15, 15));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(25, 25, 25, 25));

        JButton btn1 = new JButton("Классика (Мышь)");
        styleLauncherButton(btn1, new Color(41, 121, 255));
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

        JButton btn2 = new JButton("Slither.io (WASD)");
        styleLauncherButton(btn2, new Color(0, 230, 118));
        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SlitherMainFrame();
                dispose();
            }
        });

        add(btn1);
        add(btn2);
    }

    private void styleLauncherButton(JButton btn, Color accentColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(new Color(22, 25, 37));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(accentColor, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(accentColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(22, 25, 37));
            }
        });
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