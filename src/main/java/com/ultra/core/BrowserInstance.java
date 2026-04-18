package com.ultra.core;

import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.handler.*;
import org.cef.network.*;
import javax.swing.*;
import java.awt.*;

public class BrowserInstance extends JPanel {
    private final CefBrowser browser;
    private final JTextField urlField;
    private Timer autoRefresh;

    public BrowserInstance(CefClient client, String url, Main parent) {
        setLayout(new BorderLayout());
        browser = client.createBrowser(url, false, false);
        
        client.addRequestHandler(new CefRequestHandlerAdapter() {
            @Override
            public boolean onBeforeBrowse(CefBrowser b, org.cef.network.CefFrame f, CefRequest r, boolean user, boolean rd) {
                return r.getURL().contains("google-analytics.com") || r.getURL().contains("ads.");
            }
        });

        JPanel tool = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton b = new JButton("◀"); b.addActionListener(e -> browser.goBack());
        JButton f = new JButton("▶"); f.addActionListener(e -> browser.goForward());
        JButton r = new JButton("↻"); r.addActionListener(e -> browser.reload());
        
        urlField = new JTextField(url, 25);
        urlField.addActionListener(e -> {
            String u = urlField.getText();
            if(!u.startsWith("http")) u = "https://www.google.com/search?q=" + u;
            browser.loadURL(u);
        });

        JButton night = new JButton("NIGHT"); night.addActionListener(e -> 
            browser.executeJavaScript("document.body.style.filter='invert(1) hue-rotate(180deg) brightness(0.8)';" , "", 0));

        JButton refresh = new JButton("AUTO"); refresh.addActionListener(e -> {
            if(autoRefresh == null) {
                autoRefresh = new Timer(30000, ev -> browser.reload());
                autoRefresh.start();
                refresh.setText("AUTO: ON");
            } else {
                autoRefresh.stop();
                autoRefresh = null;
                refresh.setText("AUTO");
            }
        });

        JButton wipe = new JButton("WIPE"); wipe.addActionListener(e -> {
            browser.executeJavaScript("document.cookie.split(';').forEach(c => document.cookie = c.replace(/^ +/, '').replace(/=.*/, '=;expires=' + new Date().toUTCString() + ';path=/')); location.reload();", "", 0);
        });

        JButton dev = new JButton("F12"); dev.addActionListener(e -> {
            JFrame fr = new JFrame("DevTools"); fr.setSize(800, 600);
            fr.add(browser.getDevTools().getUIComponent()); fr.setVisible(true);
        });

        tool.add(b); tool.add(f); tool.add(r); tool.add(urlField);
        tool.add(night); tool.add(refresh); tool.add(wipe); tool.add(dev);

        add(tool, BorderLayout.NORTH);
        add(browser.getUIComponent(), BorderLayout.CENTER);

        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public void onAddressChange(CefBrowser b, org.cef.network.CefFrame frame, String u) {
                if(b == browser) urlField.setText(u);
            }
            @Override
            public void onTitleChange(CefBrowser b, String t) {
                if(b == browser) parent.update(BrowserInstance.this, t);
            }
        });
    }
}
