package com.canteen;

import com.canteen.ui.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Application entry point.
 *
 * Flow:
 *   start()  →  showLogin()  →  LoginView (900 × 600)
 *                              │
 *                              └─ on success → DashboardView (1100 × 700)
 *                                             (scene swap handled inside LoginView)
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        showLogin(stage);
        stage.setTitle("Smart Canteen Token System");
        stage.setResizable(true);
        stage.show();
    }

    private void showLogin(Stage stage) {
        LoginView loginView = new LoginView();
        VBox root = loginView.createView();

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
