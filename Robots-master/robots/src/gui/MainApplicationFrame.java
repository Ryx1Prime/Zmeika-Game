package gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import javax.swing.*;

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();

    private final String pathToConfig = System.getProperty("user.home") + File.separator + "robots.properties";
    // использование File.separator позволяет более правильно сделать для различных систем запуска нашего приложения
    // т.е.если у пользователя Linux => /, Windows => \
    
    public MainApplicationFrame() {
        //TODO
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        UIManager.put("OptionPane.cancelButtonText", "Отмена");
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);

        
        gui.LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        gui.GameWindow gameWindow = new gui.GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        loadWindowState();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter(){
            @Override
            public void windowClosing(java.awt.event.WindowEvent e){
                exitApplication();
            }
        });
        setJMenuBar(generateMenuBar());

    }
    
    protected gui.LogWindow createLogWindow()
    {
        gui.LogWindow logWindow = new gui.LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
// 
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
// 
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        return menuBar;
//    }
    
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        //DONE
        JMenu fileMenu = new JMenu("Файл");
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");
        
        {
            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");
        
        {
            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                Logger.debug("Новая строка");
            });
            testMenu.add(addLogMessageItem);
        }

        {
            //DONE
            JMenuItem exitItem = new JMenuItem("Выход");
            exitItem.addActionListener(e -> exitApplication());
            fileMenu.add(exitItem);
            menuBar.add(fileMenu);

        }
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        return menuBar;
    }
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
    //DONE
    private void exitApplication(){
        int res = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.YES_OPTION){
            saveWindowPos();
            dispose();
            System.exit(0);

        }
    }
    // NEW
    // TODO: FOR SAVING POSITION WINDOW
    private void saveWindowPos(){
        try{
            Properties prop = new Properties();
            for (JInternalFrame f : desktopPane.getAllFrames()){
                Rectangle bounds = f.getBounds();
                String name = f.getTitle();
                prop.setProperty(name + ".x", Integer.toString(bounds.x));
                prop.setProperty(name + ".y", Integer.toString(bounds.y));
                prop.setProperty(name + ".w", Integer.toString(bounds.width));
                prop.setProperty(name + ".h", Integer.toString(bounds.height));

            }
            FileOutputStream out = new FileOutputStream(pathToConfig);
            prop.store(out, "Windows settings for each");
            out.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    private void loadWindowState() {
        try{
            Properties prop = new Properties();
            FileInputStream in = new FileInputStream(pathToConfig);
            prop.load(in);
            in.close();
            for (JInternalFrame frame : desktopPane.getAllFrames()) {
                String name = frame.getTitle();
                String xStr = prop.getProperty(name + ".x");
                String yStr = prop.getProperty(name + ".y");
                String wStr = prop.getProperty(name + ".w");
                String hStr = prop.getProperty(name + ".h");

                if (xStr != null && yStr != null && wStr != null && hStr != null) {
                    int x = Integer.parseInt(xStr);
                    int y = Integer.parseInt(yStr);
                    int w = Integer.parseInt(wStr);
                    int h = Integer.parseInt(hStr);
                    frame.setBounds(x, y, w, h);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
