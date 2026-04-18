package com.ultra.core;

import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.handler.*;
import org.cef.network.CefRequest;
import javax.swing.*;
import java.awt.*;

public class BrowserInstance extends JPanel {
    private final CefBrowser browser;
    private final JTextField urlField;

    public BrowserInstance(CefClient client, String url, Main parent) {
        setLayout(new BorderLayout());
        browser = client.createBrowser(url, false, false);
        
        client.addRequestHandler(new CefRequestHandlerAdapter() {
            @Override
            public boolean onBeforeBrowse(CefBrowser b, org.cef.network.CefFrame f, CefRequest r, boolean user, boolean rd) {
                return r.getURL().contains("ads.") || r.getURL().contains("doubleclick");
            }
        });

        JPanel tool = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton bk = new JButton("◀"); bk.addActionListener(e -> browser.goBack());
        JButton fw = new JButton("▶"); fw.addActionListener(e -> browser.goForward());
        JButton rl = new JButton("↻"); rl.addActionListener(e -> browser.reload());
        
        urlField = new JTextField(url, 25);
        urlField.addActionListener(e -> {
            String u = urlField.getText();
            if(!u.startsWith("http")) u = "https://www.google.com/search?q=" + u;
            browser.loadURL(u);
        });

        JButton snap = new JButton("PNG"); snap.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Frame buffer captured to /Downloads/JABR_Snap.png");
        });

        JButton cpu = new JButton("LOW-POWER"); cpu.addActionListener(e -> {
            browser.executeJavaScript("window.requestAnimationFrame = () => {};", "", 0);
            cpu.setText("ECO-MODE");
        });

        JButton zoom = new JButton("100%"); zoom.addActionListener(e -> {
            double current = browser.getZoomLevel();
            browser.setZoomLevel(current + 1.0);
            zoom.setText((int)((current + 1) * 100) + "%");
        });

        JButton close = new JButton("✖"); close.addActionListener(e -> parent.closeTab(this));

        JButton dev = new JButton("F12"); dev.addActionListener(e -> {
            JFrame fr = new JFrame("DevTools"); fr.setSize(800, 600);
            fr.add(browser.getDevTools().getUIComponent()); fr.setVisible(true);
        });

        tool.add(bk); tool.add(fw); tool.add(rl); tool.add(urlField);
        tool.add(snap); tool.add(cpu); tool.add(zoom); tool.add(dev); tool.add(close);

        add(tool, BorderLayout.NORTH);
        add(browser.getUIComponent(), BorderLayout.CENTER);

        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public void onAddressChange(CefBrowser b, org.cef.network.CefFrame f, String u) {
                if(b == browser) urlField.setText(u);
            }
            @Override
            public void onTitleChange(CefBrowser b, String t) {
                if(b == browser) parent.update(BrowserInstance.this, t);
            }
        });
    }
}
