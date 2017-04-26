package timer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

public class GUI extends JFrame implements Runnable {

    private long current, stop, res;
    StoreTime st = new StoreTime();
    private TimerModel timerModel = new TimerModel();
    BufferedWriter fileWriter;
    //GUI gui;
    private TimerPanel timerPanel;

    authenticate au = new authenticate();

    private void initialization() {
        timerPanel = new TimerPanel(new TimerListener());
        timerPanel.setDisplay(timerModel.getTime());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Timer", timerPanel);

        add(tabbedPane, BorderLayout.CENTER);

        setTitle("Timer");
        //setDefaultCloseOperation(cl());

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (au.getAllow() != true) {
                    au.auth();
                }
                if (au.getAllow() == true) {
                    st.store("Window closed at ",0);
                    System.exit(0);
                }
            }
        });

        setVisible(true);
        pack();
    }
   
    @Override
    public void run() {
        initialization();
    }

    class StoreTime {

        void store(String s, long result) {
            try {
                fileWriter = new BufferedWriter(new FileWriter("D:\\time.txt", true));
                fileWriter.write(s + new SimpleDateFormat("dd-MM-YYYY_hh:mm:ss").format(Calendar.getInstance().getTime()) + "  " + timerModel.getCount() + "s " + result + " ms");
                System.out.println(s + new SimpleDateFormat("dd-MM-YYYY_hh:mm:ss").format(Calendar.getInstance().getTime()));
                fileWriter.newLine();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class TimerListener implements ActionListener {

        SecondaryLoop loop;
        private Timer swingTimerTicker;

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getActionCommand() == "Start") {
                Toolkit tk = Toolkit.getDefaultToolkit();
                EventQueue eq = tk.getSystemEventQueue();
                loop = eq.createSecondaryLoop();

                swingTimerTicker = new Timer(1000, this);
                swingTimerTicker.start();

                current = System.currentTimeMillis();
                st.store("Start Time at ",0);
                timerPanel.setDisplay(timerModel.getTime());
                timerPanel.setCommandText("Stop");
                au.setAllow(false);
                loop.exit();

            }
            if (e.getActionCommand() == "Stop") {
                try {

                    timerPanel.setCommandText("Start");
                    swingTimerTicker.stop();
                    stop=System.currentTimeMillis();
                    res=stop-current;
                    st.store("Stop time at ",res);
                    au.setAllow(false);
                    final String path = System.getenv("windir") + File.separator + "System32" + File.separator + "rundll32.exe";
                    Process pr = Runtime.getRuntime().exec(path + " user32.dll,LockWorkStation");
                    JOptionPane.showConfirmDialog(rootPane, "You took " + timerModel.getCount() + " seconds OR "+ res +"ms");
                    timerModel.setCount();
                    loop.exit();

                } catch (IOException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (e.getActionCommand() == "Reset") {
                if (au.getAllow() != true) {
                    au.auth();
                }
                if (au.getAllow() == true) {
                    timerModel.restartTimer();
                    timerPanel.setDisplay(timerModel.getTime());
                    timerModel.setCount();
                    au.setAllow(false);
                }
            }
            if (e.getActionCommand() == "Set") {
                if (au.getAllow() != true) {
                    au.auth();
                }
                if (au.getAllow() == true) {
                    String s = (String) JOptionPane.showInputDialog("Input timer time", timerModel.getTime());
                    timerModel.setTime(s);
                    timerPanel.setDisplay(timerModel.getTime());
                    au.setAllow(false);
                }
            }
            if (e.getSource() == swingTimerTicker) {
                if (timerModel.isTimeUp()) // time is up
                {
                    swingTimerTicker.stop();
                    timerModel.restartTimer();
                    timerPanel.setDisplay(timerModel.getTime());
                    timerPanel.setCommandText("Start");

                    // Launching new thread to Lock Screen
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                st.store("Time Up at ",0);
                                final String path = System.getenv("windir") + File.separator + "System32" + File.separator + "rundll32.exe";
                                Process pr = Runtime.getRuntime().exec(path + " user32.dll,LockWorkStation");
                                JOptionPane.showConfirmDialog(rootPane, "You took " + timerModel.getCount() + " seconds OR " +res+ "ms");
                                timerModel.setCount();
                            } catch (IOException ex) {
                                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }).start();
                } else // still counting
                {
                    timerModel.timeTick();
                    timerPanel.setDisplay(timerModel.getTime());
                }
            }
        } // method actionPerformed		
    } // class TimerListener

    class authenticate {

        boolean allow = false;

        void auth() {
            String password = null;
            JPasswordField pf = new JPasswordField(10);
            String ValidPassword = "itb@vyro.1";
            int okCxl = JOptionPane.showConfirmDialog(null, pf, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (okCxl == JOptionPane.OK_OPTION) {
                password = new String(pf.getPassword());
            }
            while (allow == false) {
                if (ValidPassword.equals(password)) {
                    setAllow(true);
                } else {
                    auth();
                }
            }
        }

        void setAllow(boolean a) {
            this.allow = a;
        }

        boolean getAllow() {
            return allow;
        }

    }
}
