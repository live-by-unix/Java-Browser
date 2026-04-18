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
    private JLabel statusLabel;

    public Main() throws Exception {
        FlatDarkLaf.setup();
        CefAppBuilder builder = new CefAppBuilder();
        builder.setInstallDir(new java.io.File("jcef-bundle"));
        app = builder.build();
        client = app.createClient();

        setTitle("JABR");
        setSize(1500, 950);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        statusLabel = new JLabel(" Ready");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());

        JPanel topContainer = new JPanel(new BorderLayout());
        JButton addBtn = new JButton(" + ");
        addBtn.addActionListener(e -> addTab("https://www.google.com"));
        topContainer.add(tabs, BorderLayout.CENTER);
        topContainer.add(addBtn, BorderLayout.EAST);

        add(topContainer, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        addTab("https://www.youtube.com");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CefApp.getInstance().dispose();
                System.exit(0);
            }
        });
    }

    public void addTab(String url) {
        BrowserInstance instance = new BrowserInstance(client, url, this);
        tabs.addTab("New Tab", instance);
        tabs.setSelectedComponent(instance);
    }

    public void setStatus(String text) { statusLabel.setText(" " + text); }

    public void updateTitle(BrowserInstance instance, String title) {
        int i = tabs.indexOfComponent(instance);
        if (i != -1) tabs.setTitleAt(i, title.length() > 18 ? title.substring(0, 15) + "..." : title);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { new Main().setVisible(true); } 
            catch (Exception e) { e.printStackTrace(); }
        });
    }
}
