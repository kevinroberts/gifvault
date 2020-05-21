package com.vinberts.gifvault.views;

import com.trievosoftware.giphy4j.entity.giphy.GiphyData;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 *
 */
public class GiphyCellWebView extends VBox {
    private WebView webView;

    public GiphyCellWebView(final GiphyData gifSource) {
        super();
        this.webView = new WebView();
        WebEngine engine = this.webView.getEngine();
        engine.load(gifSource.getImages().getFixedHeight().getUrl());

        this.getChildren().addAll(this.webView);
    }
}
