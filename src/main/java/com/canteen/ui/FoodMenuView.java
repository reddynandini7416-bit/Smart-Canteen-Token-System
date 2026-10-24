package com.canteen.ui;

import com.canteen.User;
import javafx.scene.layout.VBox;

/**
 * FoodMenuView — kept for compile compatibility.
 *
 * All food-menu and order functionality has been moved into
 * DashboardView (the single-screen dashboard).
 * This class is no longer used at runtime but must compile cleanly.
 */
public class FoodMenuView {

    @SuppressWarnings("unused")
    private final User user;

    public FoodMenuView(User user) {
        this.user = user;
    }

    /** @deprecated Use DashboardView instead. */
    @Deprecated
    public VBox createView() {
        return new VBox();   // empty stub — never called
    }
}
