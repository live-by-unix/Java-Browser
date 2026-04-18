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
            public boolean onBeforeBrowse(CefBrowser b, org.cef.network.CefFrame f, CefRequest r, boolean user, boolean rd) {
                return r.getURL().contains("ads.") || r.getURL().contains("tracker");
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

        JButton pdf = new JButton("PDF"); pdf.addActionListener(e -> browser.print());

        JButton theme = new JButton("THEME"); theme.addActionListener(e -> {
            String color = JOptionPane.showInputDialog("Enter Background Color (e.g. #333):");
            browser.executeJavaScript("document.body.style.backgroundColor='" + color + "';", "", 0);
        });

        JButton flush = new JButton("FLUSH"); flush.addActionListener(e -> {
            System.gc();
            JOptionPane.showMessageDialog(this, "Memory Purged");
        });

        JButton incog = new JButton("INCOGNITO"); incog.addActionListener(e -> {
            browser.executeJavaScript("localStorage.clear(); sessionStorage.clear();", "", 0);
            parent.addTab("https://duckduckgo.com");
        });

        JButton dev = new JButton("F12"); dev.addActionListener(e -> {
            JFrame fr = new JFrame("DevTools"); fr.setSize(800, 600);
            fr.add(browser.getDevTools().getUIComponent()); fr.setVisible(true);
        });

        tool.add(bk); tool.add(fw); tool.add(rl); tool.add(urlField);
        tool.add(pdf); tool.add(theme); tool.add(flush); tool.add(incog); tool.add(dev);

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
