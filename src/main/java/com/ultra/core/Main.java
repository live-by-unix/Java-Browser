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

        setTitle("JABR SUPERNOVA X");
        setSize(1600, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        JPanel p = new JPanel(new BorderLayout());
        JButton add = new JButton(" + ");
        add.addActionListener(e -> addTab("https://www.google.com"));
        p.add(tabs, BorderLayout.CENTER);
        p.add(add, BorderLayout.EAST);
        add(p);

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
        tabs.addTab("New Tab", bi);
        tabs.setSelectedComponent(bi);
    }

    public void update(BrowserInstance bi, String title) {
        int i = tabs.indexOfComponent(bi);
        if (i != -1) tabs.setTitleAt(i, title.length() > 15 ? title.substring(0, 12) + "..." : title);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { new Main().setVisible(true); } 
            catch (Exception e) { e.printStackTrace(); }
        });
    }
}
