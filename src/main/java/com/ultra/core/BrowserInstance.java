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
            public boolean onBeforeBrowse(CefBrowser b, Object f, CefRequest r, boolean u, boolean rd) {
                return r.getURL().contains("ads.") || r.getURL().contains("telemetry");
            }
        });

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton back = new JButton("◀"); back.addActionListener(e -> browser.goBack());
        JButton fwd = new JButton("▶"); fwd.addActionListener(e -> browser.goForward());
        JButton reload = new JButton("↻"); reload.addActionListener(e -> browser.reload());
        
        urlField = new JTextField(url, 25);
        urlField.addActionListener(e -> {
            String val = urlField.getText();
            if(!val.contains(".")) val = "https://www.google.com/search?q=" + val.replace(" ", "+");
            else if(!val.startsWith("http")) val = "https://" + val;
            browser.loadURL(val);
        });

        JButton titan = new JButton("SCRATCH"); titan.addActionListener(e -> 
            browser.executeJavaScript("document.querySelectorAll('*').forEach(el => el.style.fontFamily = 'Comic Sans MS');", "", 0));

        JButton dark = new JButton("DARK"); dark.addActionListener(e -> 
            browser.executeJavaScript("document.body.style.filter='invert(1) hue-rotate(180deg)';" , "", 0));

        JButton speed = new JButton("2X"); speed.addActionListener(e -> 
            browser.executeJavaScript("document.querySelectorAll('video').forEach(v => v.playbackRate = 2.0);", "", 0));

        JButton f12 = new JButton("F12"); f12.addActionListener(e -> {
            JFrame dev = new JFrame("Titan Inspector"); dev.setSize(900, 700);
            dev.add(browser.getDevTools().getUIComponent()); dev.setVisible(true);
        });

        JButton kill = new JButton("✖"); kill.addActionListener(e -> parent.closeTab(this));

        bar.add(back); bar.add(fwd); bar.add(reload); bar.add(urlField);
        bar.add(titan); bar.add(dark); bar.add(speed); bar.add(f12); bar.add(kill);

        add(bar, BorderLayout.NORTH);
        add(browser.getUIComponent(), BorderLayout.CENTER);

        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            public void onAddressChange(CefBrowser b, Object f, String u) {
                if(b == browser) urlField.setText(u);
            }
            public void onTitleChange(CefBrowser b, String t) {
                if(b == browser) parent.updateTab(BrowserInstance.this, t);
            }
        });
    }
}
