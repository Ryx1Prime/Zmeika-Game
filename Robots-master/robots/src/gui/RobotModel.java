package gui;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class RobotModel {
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.01;

    public double getM_robotPositionX() {return m_robotPositionX;};
    public double getM_robotPositionY() {return m_robotPositionY;};
    public double getM_robotDirection() {return m_robotDirection;};

    public int getM_targetPositionX(){return m_targetPositionX;};
    public int getM_targetPositionY(){return m_targetPositionY;};

    private final List<gui.Observer> observers = new ArrayList<>();
    public void addObserver(gui.Observer obs){
        observers.add(obs);
    }
    private void notifyObservers(){
        for (gui.Observer obs: observers){
            obs.updateState();
        }
    }

    public void setTargetPosition(int x, int y){
        m_targetPositionX = x;
        m_targetPositionY = y;
    }

    public void update(){
        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance < 0.5)
        {
            return;
        }
        double velocity = maxVelocity;
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        //TODO: кратчайший путь между двумя любыми углами на окружности никогда не превышает половину круга => 180 градусов

        double angularVelocity = 0;

        double aDifference = angleToTarget - m_robotDirection;
        while (aDifference < -Math.PI){
            aDifference += 2 * Math.PI;
        }
        while (aDifference > Math.PI){
            aDifference -= 2 * Math.PI;
        }
        if (aDifference > 0.001){
            angularVelocity = maxAngularVelocity;
        }else if (aDifference < -0.001){
            angularVelocity = -maxAngularVelocity;
        }

        moveRobot(velocity, angularVelocity, 10);

        notifyObservers();
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }
    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private void moveRobot(double velocity, double angularVelocity, double duration)
    {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection  + angularVelocity * duration) -
                        Math.sin(m_robotDirection));
        if (!Double.isFinite(newX))
        {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection  + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY))
        {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }
        m_robotPositionX = newX;
        m_robotPositionY = newY;
        double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
        m_robotDirection = newDirection;
    }

    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
        {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI)
        {
            angle -= 2*Math.PI;
        }
        return angle;
    }
}
