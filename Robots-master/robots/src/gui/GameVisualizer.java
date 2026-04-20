package gui;

import java.awt.*;
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
                if (lastPoint == null || currentPoint.distance(lastPoint) > 3.0){
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
        int cX = round(m_model.getM_robotPositionX());
        int cY = round(m_model.getM_robotPositionY());
        // сглаживание
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform t = AffineTransform.getRotateInstance(direction, cX, cY);
        g.setTransform(t);
        // язык
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(2));
        g.drawLine(cX + 28, cY, cX + 40, cY);
        g.drawLine(cX + 40, cY, cX + 45, cY - 4);
        g.drawLine(cX + 40, cY, cX + 45, cY + 4);
        g.setStroke(new BasicStroke(1));
        // голова
        g.setColor(new Color(34, 139, 34));
        fillOval(g, cX, cY, 60, 24);
        g.setColor(Color.BLACK);
        drawOval(g, cX, cY, 60, 24);
        // узор
        g.setColor(Color.YELLOW);
        fillOval(g, cX - 10, cY, 30, 8);
        // левый глаз
        g.setColor(Color.WHITE);
        fillOval(g, cX + 14, cY - 8, 10, 10);
        g.setColor(Color.BLACK);
        drawOval(g, cX + 14, cY - 8, 10, 10);
        fillOval(g, cX + 17, cY - 8, 4, 4);
        // правый глаз
        g.setColor(Color.WHITE);
        fillOval(g, cX + 14, cY + 8, 10, 10);
        g.setColor(Color.BLACK);
        drawOval(g, cX + 14, cY + 8, 10, 10);
        fillOval(g, cX + 17, cY + 8, 4, 4);
    }

    private void drawPath(Graphics2D g)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0,0,0);
        g.setTransform(t);
        g.setColor(Color.BLACK);

        for (Point p : m_model.getPath()){
            fillOval(g,p.x, p.y, 6,6);
        }
    }
}
