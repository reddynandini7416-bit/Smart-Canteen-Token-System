package com.canteen.ui;

import javafx.scene.layout.VBox;

/**
 * TokenView — kept for compile compatibility.
 *
 * All token-viewing functionality has been moved into
 * DashboardView (the single-screen dashboard).
 * This class is no longer used at runtime but must compile cleanly.
 */
public class TokenView {

    /** @deprecated Use DashboardView instead. */
    @Deprecated
    public VBox createView() {
        return new VBox();   // empty stub — never called
    }
}
