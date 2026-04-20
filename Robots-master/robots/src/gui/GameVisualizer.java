package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;


public class GameVisualizer extends JPanel
{
    private final Timer m_timer = initTimer();
    private final gui.RobotModel m_model;
    
    private static Timer initTimer() 
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    
    public GameVisualizer(gui.RobotModel model)
    {
        m_model = model;
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                m_model.update();
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                m_model.addPointInPath(e.getPoint().x, e.getPoint().y);
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            Point lastPoint = null;
            @Override
            public void mouseDragged(MouseEvent e){
                Point currentPoint = e.getPoint();
                if (lastPoint == null || currentPoint.distance(lastPoint) > 10.0){
                    m_model.addPointInPath(e.getPoint().x, e.getPoint().y);
                    lastPoint = currentPoint;
                    repaint();
                }
            }
        });
        setDoubleBuffered(true);
    }

//    protected void setTargetPosition(Point p)
//    {
//        m_model.setTargetPosition(p.x, p.y);
//    }
    
    protected void onRedrawEvent()
    {
        EventQueue.invokeLater(this::repaint);
    }


    private static int round(double value)
    {
        return (int)(value + 0.5);
    }
    
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g; 
        drawPath(g2d);
        drawRobot(g2d, round(m_model.getM_robotPositionX()), round(m_model.getM_robotPositionY()), m_model.getM_robotDirection());

    }
    
    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    
    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    
    private void drawRobot(Graphics2D g, int x, int y, double direction)
    {
        int robotCenterX = round(m_model.getM_robotPositionX());
        int robotCenterY = round(m_model.getM_robotPositionY());
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY); 
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
    }

    private void drawPath(Graphics2D g)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0,0,0);
        g.setTransform(t);
        g.setColor(Color.CYAN);

        for (Point p : m_model.getPath()){
            fillOval(g,p.x, p.y, 4,4);
        }
    }
}
