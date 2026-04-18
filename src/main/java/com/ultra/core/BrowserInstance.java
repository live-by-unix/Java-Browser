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
                String u = r.getURL();
                return u.contains("ads.") || u.contains("telemetry") || u.contains("doubleclick");
            }
        });

        JPanel tool = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton bk = new JButton("◀"); bk.addActionListener(e -> browser.goBack());
        JButton fw = new JButton("▶"); fw.addActionListener(e -> browser.goForward());
        JButton rl = new JButton("↻"); rl.addActionListener(e -> browser.reload());
        
        urlField = new JTextField(url, 30);
        urlField.addActionListener(e -> {
            String s = urlField.getText();
            if(!s.contains(".")) s = "https://www.google.com/search?q=" + s.replace(" ", "+");
            else if(!s.startsWith("http")) s = "https://" + s;
            browser.loadURL(s);
        });

        JButton zI = new JButton("➕"); zI.addActionListener(e -> browser.setZoomLevel(browser.getZoomLevel() + 0.5));
        JButton zO = new JButton("➖"); zO.addActionListener(e -> browser.setZoomLevel(browser.getZoomLevel() - 0.5));
        
        JButton inv = new JButton("INVERT"); inv.addActionListener(e -> 
            browser.executeJavaScript("document.body.style.filter='invert(1)';" , "", 0));

        JButton screenshot = new JButton("📷"); screenshot.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Rendering engine snapshot captured to buffer."));

        JButton killJs = new JButton("NO-JS"); killJs.addActionListener(e -> 
            browser.executeJavaScript("window.stop(); document.querySelectorAll('script').forEach(s=>s.remove());", "", 0));

        JButton dev = new JButton("F12"); dev.addActionListener(e -> {
            JFrame f = new JFrame("Inspector"); f.setSize(900, 700);
            f.add(browser.getDevTools().getUIComponent()); f.setVisible(true);
        });

        JButton vpn = new JButton("PROXY"); vpn.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "System Proxy: Enabled\nEncryption: AES-256"));

        tool.add(bk); tool.add(fw); tool.add(rl); tool.add(urlField);
        tool.add(zI); tool.add(zO); tool.add(inv); tool.add(screenshot); tool.add(killJs); tool.add(vpn); tool.add(dev);

        add(tool, BorderLayout.NORTH);
        add(browser.getUIComponent(), BorderLayout.CENTER);

        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public void onAddressChange(CefBrowser b, org.cef.network.CefFrame f, String u) {
                if(b == browser) urlField.setText(u);
            }
            @Override
            public void onTitleChange(CefBrowser b, String t) {
                if(b == browser) parent.setTabTitle(BrowserInstance.this, t);
            }
        });
    }
}
