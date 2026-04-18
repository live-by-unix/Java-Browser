package com.ultra.core;

import me.friwi.jcefmaven.*;
import org.cef.CefApp;
import org.cef.CefClient;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends JFrame {
    private JTabbedPane tabs;
    private CefApp app;
    private CefClient client;

    public Main() throws Exception {
        FlatDarkLaf.setup();
        CefAppBuilder builder = new CefAppBuilder();
        builder.setInstallDir(new java.io.File("jcef-bundle"));
        app = builder.build();
        client = app.createClient();

        setTitle("JABR OMEGA");
        setSize(1600, 950);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        JPanel wrap = new JPanel(new BorderLayout());
        JButton add = new JButton(" + ");
        add.addActionListener(e -> addTab("https://www.google.com"));
        
        wrap.add(tabs, BorderLayout.CENTER);
        wrap.add(add, BorderLayout.EAST);
        add(wrap);

        addTab("https://www.youtube.com");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                CefApp.getInstance().dispose();
                System.exit(0);
            }
        });
    }

    public void addTab(String url) {
        BrowserInstance bi = new BrowserInstance(client, url, this);
        tabs.addTab("Loading...", bi);
        tabs.setSelectedComponent(bi);
    }

    public void closeTab(BrowserInstance bi) {
        if (tabs.getTabCount() > 1) tabs.remove(bi);
        else System.exit(0);
    }

    public void update(BrowserInstance bi, String t) {
        int i = tabs.indexOfComponent(bi);
        if (i != -1) tabs.setTitleAt(i, t.length() > 18 ? t.substring(0, 15) + "..." : t);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { new Main().setVisible(true); } 
            catch (Exception e) { e.printStackTrace(); }
        });
    }
}
