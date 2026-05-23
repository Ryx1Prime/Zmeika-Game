package gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class RobotsProgram extends JFrame {

    public RobotsProgram() {
        setTitle("Выбор режима игры");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);

        getContentPane().setBackground(new Color(13, 15, 23));
        setLayout(new GridLayout(2, 1, 15, 15));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(25, 25, 25, 25));

        JButton btn1 = new JButton("Классика (Рисование мышкой)");
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

        JButton btn2 = new JButton("Slither.io (Управление WASD)");
        styleLauncherButton(btn2, new Color(0, 230, 118));
        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SlitherMainFrame slitherFrame = new SlitherMainFrame();
                slitherFrame.setVisible(true);
                dispose();
            }
        });

        add(btn1);
        add(btn2);
    }

    private void styleLauncherButton(JButton btn, Color accentColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(new Color(22, 25, 37));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
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
        Locale.setDefault(new Locale("ru", "RU"));
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                RobotsProgram launcher = new RobotsProgram();
                launcher.setVisible(true);
            }
        });
    }
}