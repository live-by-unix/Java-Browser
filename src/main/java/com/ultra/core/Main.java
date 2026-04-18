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
    private CefClient client;

    public Main() throws Exception {
        FlatDarkLaf.setup();
        CefAppBuilder builder = new CefAppBuilder();
        builder.setInstallDir(new java.io.File("jcef-bundle"));
        CefApp app = builder.build();
        client = app.createClient();

        setTitle("JABR Stable");
        setSize(1500, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        JPanel container = new JPanel(new BorderLayout());
        JButton addBtn = new JButton(" + ");
        addBtn.addActionListener(e -> addTab("https://www.google.com"));
        
        container.add(tabs, BorderLayout.CENTER);
        container.add(addBtn, BorderLayout.EAST);
        add(container);

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
        BrowserInstance bi = new BrowserInstance(client, url, this);
        tabs.addTab("New Tab", bi);
        tabs.setSelectedComponent(bi);
    }

    public void updateTab(BrowserInstance bi, String title) {
        int i = tabs.indexOfComponent(bi);
        if (i != -1) tabs.setTitleAt(i, title.length() > 15 ? title.substring(0, 12) + "..." : title);
    }

    public void closeTab(BrowserInstance bi) {
        if (tabs.getTabCount() > 1) tabs.remove(bi);
        else System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { new Main().setVisible(true); } 
            catch (Exception e) { e.printStackTrace(); }
        });
    }
}
