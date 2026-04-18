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
            public boolean onBeforeBrowse(CefBrowser b, Object f, CefRequest r, boolean user, boolean rd) {
                String u = r.getURL();
                return u.contains("ads.") || u.contains("analytics") || u.contains("doubleclick");
            }
        });

        JPanel tool = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton bk = new JButton("◀"); bk.addActionListener(e -> browser.goBack());
        JButton fw = new JButton("▶"); fw.addActionListener(e -> browser.goForward());
        JButton rl = new JButton("↻"); rl.addActionListener(e -> browser.reload());
        
        urlField = new JTextField(url, 25);
        urlField.addActionListener(e -> {
            String u = urlField.getText();
            if(!u.startsWith("http")) u = "https://www.google.com/search?q=" + u.replace(" ", "+");
            browser.loadURL(u);
        });

        
        JButton dark = new JButton("DARK"); dark.addActionListener(e -> 
            browser.executeJavaScript("document.body.style.filter='invert(1) hue-rotate(180deg)';" , "", 0));

        
        JButton sniff = new JButton("SNIFF"); sniff.addActionListener(e -> 
            browser.getSource(s -> System.out.println("PAGE SOURCE LENGTH: " + s.length())));

        
        JButton clean = new JButton("CLEAN"); clean.addActionListener(e -> {
            System.gc();
            JOptionPane.showMessageDialog(this, "Garbage Collector Invoked");
        });


        JButton dev = new JButton("F12"); dev.addActionListener(e -> {
            JFrame fr = new JFrame("DevTools"); fr.setSize(800, 600);
            fr.add(browser.getDevTools().getUIComponent()); fr.setVisible(true);
        });

        JButton close = new JButton("✖"); close.addActionListener(e -> parent.closeTab(this));

        tool.add(bk); tool.add(fw); tool.add(rl); tool.add(urlField);
        tool.add(dark); tool.add(sniff); tool.add(clean); tool.add(dev); tool.add(close);

        add(tool, BorderLayout.NORTH);
        add(browser.getUIComponent(), BorderLayout.CENTER);

        
        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            public void onAddressChange(CefBrowser b, Object f, String u) {
                if(b == browser) urlField.setText(u);
            }
            public void onTitleChange(CefBrowser b, String t) {
                if(b == browser) parent.update(BrowserInstance.this, t);
            }
        });
    }
}
