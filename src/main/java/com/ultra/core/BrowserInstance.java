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
                return u.contains("ads.") || u.contains("/ads/");
            }
        });

        JPanel tool = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton bk = new JButton("◀"); bk.addActionListener(e -> browser.goBack());
        JButton fw = new JButton("▶"); fw.addActionListener(e -> browser.goForward());
        JButton rl = new JButton("↻"); rl.addActionListener(e -> browser.reload());
        
        urlField = new JTextField(url, 30);
        urlField.addActionListener(e -> {
            String txt = urlField.getText();
            if(!txt.contains(".")) txt = "https://www.google.com/search?q=" + txt.replace(" ", "+");
            else if(!txt.startsWith("http")) txt = "https://" + txt;
            browser.loadURL(txt);
        });

        JButton zI = new JButton("+"); zI.addActionListener(e -> browser.setZoomLevel(browser.getZoomLevel() + 0.5));
        JButton zO = new JButton("-"); zO.addActionListener(e -> browser.setZoomLevel(browser.getZoomLevel() - 0.5));
        
        JButton sepia = new JButton("SEPIA"); sepia.addActionListener(e -> 
            browser.executeJavaScript("document.body.style.filter='sepia(0.8)';" , "", 0));

        JButton js = new JButton("JS"); js.addActionListener(e -> {
            String cmd = JOptionPane.showInputDialog("Enter JS:");
            if(cmd != null) browser.executeJavaScript(cmd, "", 0);
        });

        JButton clone = new JButton("CLONE"); clone.addActionListener(e -> parent.addTab(urlField.getText()));

        JButton dev = new JButton("F12"); dev.addActionListener(e -> {
            JFrame f = new JFrame("DevTools"); f.setSize(800, 600);
            f.add(browser.getDevTools().getUIComponent()); f.setVisible(true);
        });

        JButton dl = new JButton("DL"); dl.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Active Downloads: 0\nEngine: JCEF-Native"));

        tool.add(bk); tool.add(fw); tool.add(rl); tool.add(urlField);
        tool.add(zI); tool.add(zO); tool.add(sepia); tool.add(js); tool.add(clone); tool.add(dl); tool.add(dev);

        add(tool, BorderLayout.NORTH);
        add(browser.getUIComponent(), BorderLayout.CENTER);

        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public void onAddressChange(CefBrowser b, org.cef.network.CefFrame f, String u) {
                if(b == browser) urlField.setText(u);
            }
            @Override
            public void onTitleChange(CefBrowser b, String t) {
                if(b == browser) parent.updateTab(BrowserInstance.this, t);
            }
        });
    }
}
