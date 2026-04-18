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

    public BrowserInstance(CefClient client, String url, Main parent) {
        setLayout(new BorderLayout());
        browser = client.createBrowser(url, false, false);
        
        client.addRequestHandler(new CefRequestHandlerAdapter() {
            @Override
            public boolean onBeforeBrowse(CefBrowser b, org.cef.network.CefFrame f, CefRequest r, boolean user, boolean redirect) {
                String u = r.getURL();
                if(u.contains("ads") || u.contains("analytics")) {
                    parent.setStatus("Blocked Ad: " + u);
                    return true;
                }
                return false;
            }
        });

        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton b = new JButton("◀"); b.addActionListener(e -> browser.goBack());
        JButton f = new JButton("▶"); f.addActionListener(e -> browser.goForward());
        JButton r = new JButton("↻"); r.addActionListener(e -> browser.reload());
        
        urlField = new JTextField(url, 35);
        urlField.addActionListener(e -> {
            String target = urlField.getText();
            if(!target.contains(".")) target = "https://www.google.com/search?q=" + target.replace(" ", "+");
            else if(!target.startsWith("http")) target = "https://" + target;
            browser.loadURL(target);
        });

        JButton zoomI = new JButton("+"); zoomI.addActionListener(e -> browser.setZoomLevel(browser.getZoomLevel() + 0.5));
        JButton zoomO = new JButton("-"); zoomO.addActionListener(e -> browser.setZoomLevel(browser.getZoomLevel() - 0.5));
        
        JButton agent = new JButton("Mobile"); agent.addActionListener(e -> {
            browser.executeJavaScript("navigator.__defineGetter__('userAgent', function(){ return 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1'; });", "", 0);
            browser.reload();
            parent.setStatus("User-Agent: iPhone Emulation");
        });

        JButton mem = new JButton("RAM"); mem.addActionListener(e -> {
            long m = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
            JOptionPane.showMessageDialog(this, "JABR Memory Usage: " + m + "MB");
        });

        JButton dev = new JButton("F12"); dev.addActionListener(e -> {
            JFrame df = new JFrame("Inspector"); df.setSize(900, 700);
            df.add(browser.getDevTools().getUIComponent()); df.setVisible(true);
        });

        toolBar.add(b); toolBar.add(f); toolBar.add(r); toolBar.add(urlField);
        toolBar.add(zoomI); toolBar.add(zoomO); toolBar.add(agent); toolBar.add(mem); toolBar.add(dev);

        add(toolBar, BorderLayout.NORTH);
        add(browser.getUIComponent(), BorderLayout.CENTER);

        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public void onAddressChange(CefBrowser b, org.cef.network.CefFrame f, String u) {
                if(b == browser) urlField.setText(u);
            }
            @Override
            public void onTitleChange(CefBrowser b, String t) {
                if(b == browser) parent.updateTitle(BrowserInstance.this, t);
            }
        });
    }
}
