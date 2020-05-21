package com.vinberts.gifvault.views;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;

/**
 *
 */
@Getter
public class LabelCell extends VBox {
    private Label gifTitle;

    public LabelCell(final Label gifTitle) {
        super();
        this.gifTitle = gifTitle;
        this.getChildren().addAll(this.gifTitle);
    }
}

