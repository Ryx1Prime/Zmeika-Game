package gui;

import javax.swing.*;
import java.awt.*;

public class CoordinatesWindow extends JInternalFrame implements gui.Observer {
    private final gui.RobotModel m_model;
    private final JLabel coordinatesLabel;

    public CoordinatesWindow(gui.RobotModel model) {
        super("Координаты", true, true, true, true);
        m_model = model;
        m_model.addObserver(this);
        coordinatesLabel = new JLabel("X: 0, Y: 0", SwingConstants.CENTER);
        coordinatesLabel.setFont(new Font("Arial", Font.BOLD, 14));

        add(coordinatesLabel, BorderLayout.CENTER);
        pack();

        updateState();
    }
    @Override
    public void updateState(){
        int x = (int)(m_model.getM_robotPositionX() + 0.5); // hack
        int y = (int)(m_model.getM_robotPositionY() + 0.5);

        coordinatesLabel.setText("X: " + x + " Y: " + y);
    }
}
