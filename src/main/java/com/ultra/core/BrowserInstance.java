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
        
        // Error-Proof Request Handler (No explicit Frame casting)
        client.addRequestHandler(new CRequestHandler());

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton bk = new JButton("◀"); bk.addActionListener(e -> browser.goBack());
        JButton fw = new JButton("▶"); fw.addActionListener(e -> browser.goForward());
        JButton rl = new JButton("↻"); rl.addActionListener(e -> browser.reload());
        
        urlField = new JTextField(url, 30);
        urlField.addActionListener(e -> {
            String u = urlField.getText();
            if(!u.startsWith("http")) u = "https://www.google.com/search?q=" + u;
            browser.loadURL(u);
        });

        JButton night = new JButton("NIGHT"); night.addActionListener(e -> 
            browser.executeJavaScript("document.body.style.filter='invert(1) hue-rotate(180deg)';" , "", 0));

        JButton ram = new JButton("MEM"); ram.addActionListener(e -> {
            long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
            JOptionPane.showMessageDialog(this, "Process Memory: " + used + "MB");
        });

        JButton dev = new JButton("F12"); dev.addActionListener(e -> {
            JFrame f = new JFrame("Inspector"); f.setSize(900, 700);
            f.add(browser.getDevTools().getUIComponent()); f.setVisible(true);
        });

        bar.add(bk); bar.add(fw); bar.add(rl); bar.add(urlField);
        bar.add(night); bar.add(ram); bar.add(dev);

        add(bar, BorderLayout.NORTH);
        add(browser.getUIComponent(), BorderLayout.CENTER);

        // Error-Proof Display Handler
        client.addDisplayHandler(new CDisplayHandler(this, parent));
    }

    // Static Inner Classes to isolate the CefFrame symbol from the main compiler pass
    private class CRequestHandler extends CefRequestHandlerAdapter {
        @Override
        public boolean onBeforeBrowse(CefBrowser b, org.cef.network.CefFrame f, CefRequest r, boolean u, boolean rd) {
            return r.getURL().contains("ads.") || r.getURL().contains("analytics");
        }
    }

    private class CDisplayHandler extends CefDisplayHandlerAdapter {
        private BrowserInstance bi;
        private Main p;
        public CDisplayHandler(BrowserInstance bi, Main p) { this.bi = bi; this.p = p; }
        @Override
        public void onAddressChange(CefBrowser b, org.cef.network.CefFrame f, String u) {
            if(b == bi.browser) bi.urlField.setText(u);
        }
        @Override
        public void onTitleChange(CefBrowser b, String t) {
            if(b == bi.browser) p.update(bi, t);
        }
    }
}
