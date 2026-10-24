package com.canteen.ui;

import com.canteen.User;
import com.canteen.dao.UserDAO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * LoginView — modern, centred login card.
 * After a successful login the user is passed to DashboardView
 * which opens as a full single-screen dashboard (1100 x 700).
 */
public class LoginView {

    public VBox createView() {

        // ── Outer page — dark green gradient background ──────────────────
        VBox page = new VBox();
        page.setAlignment(Pos.CENTER);
        page.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #1b4332, #40916c);"
        );

        // ── Login card ───────────────────────────────────────────────────
        VBox card = new VBox(18);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(40, 50, 40, 50));
        card.setMaxWidth(420);
        card.setStyle(
            "-fx-background-color: #ffffff;"
            + "-fx-background-radius: 16;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 24, 0, 0, 8);"
        );

        // ── Header ───────────────────────────────────────────────────────
        Label icon = new Label("🍽");
        icon.setFont(Font.font("System", 48));
        icon.setAlignment(Pos.CENTER);
        icon.setMaxWidth(Double.MAX_VALUE);
        icon.setTextAlignment(TextAlignment.CENTER);

        Label appTitle = new Label("Smart Canteen");
        appTitle.setFont(Font.font("System", FontWeight.BOLD, 26));
        appTitle.setTextFill(Color.web("#1b4332"));
        appTitle.setMaxWidth(Double.MAX_VALUE);
        appTitle.setAlignment(Pos.CENTER);

        Label subtitle = new Label("Token System");
        subtitle.setFont(Font.font("System", 14));
        subtitle.setTextFill(Color.web("#6c757d"));
        subtitle.setMaxWidth(Double.MAX_VALUE);
        subtitle.setAlignment(Pos.CENTER);

        Separator sep = new Separator();
        sep.setPadding(new Insets(4, 0, 4, 0));

        // ── Username ─────────────────────────────────────────────────────
        Label userLabel = new Label("Username");
        userLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        userLabel.setTextFill(Color.web("#495057"));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setPrefHeight(40);
        usernameField.setStyle(fieldStyle());

        // ── Password ─────────────────────────────────────────────────────
        Label passLabel = new Label("Password");
        passLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        passLabel.setTextFill(Color.web("#495057"));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(40);
        passwordField.setStyle(fieldStyle());

        // ── Error message ────────────────────────────────────────────────
        Label message = new Label();
        message.setTextFill(Color.web("#dc3545"));
        message.setFont(Font.font("System", 12));
        message.setWrapText(true);

        // ── Login button ─────────────────────────────────────────────────
        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(44);
        loginButton.setFont(Font.font("System", FontWeight.BOLD, 15));
        loginButton.setStyle(
            "-fx-background-color: #2d6a4f;"
            + "-fx-text-fill: white;"
            + "-fx-background-radius: 8;"
            + "-fx-cursor: hand;"
        );

        // Hover effect
        loginButton.setOnMouseEntered(e ->
            loginButton.setStyle(
                "-fx-background-color: #1b4332;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
            ));
        loginButton.setOnMouseExited(e ->
            loginButton.setStyle(
                "-fx-background-color: #2d6a4f;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
            ));

        // ── Login action ─────────────────────────────────────────────────
        loginButton.setOnAction(event -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                message.setText("Please enter both username and password.");
                return;
            }

            UserDAO userDAO = new UserDAO();
            User user = userDAO.login(username, password);

            if (user != null) {
                // Open single-screen dashboard
                DashboardView dashboardView = new DashboardView(user);
                BorderPane dashboard = dashboardView.createView();

                Scene scene = new Scene(dashboard, 1100, 700);

                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Smart Canteen — Dashboard");
                stage.centerOnScreen();
            } else {
                message.setText("Invalid username or password. Please try again.");
                passwordField.clear();
            }
        });

        // Allow Enter key to trigger login
        passwordField.setOnAction(e -> loginButton.fire());
        usernameField.setOnAction(e -> passwordField.requestFocus());

        // ── Register link ─────────────────────────────────────────────────
        Button registerLink = new Button("New here? Create an Account →");
        registerLink.setMaxWidth(Double.MAX_VALUE);
        registerLink.setFont(Font.font("System", 13));
        registerLink.setStyle(
            "-fx-background-color: transparent;"
            + "-fx-text-fill: #1565c0;"
            + "-fx-cursor: hand;"
            + "-fx-underline: true;"
        );
        registerLink.setOnMouseEntered(e -> registerLink.setStyle(
            "-fx-background-color: transparent;"
            + "-fx-text-fill: #0d47a1;"
            + "-fx-cursor: hand;"
            + "-fx-underline: true;"
        ));
        registerLink.setOnMouseExited(e -> registerLink.setStyle(
            "-fx-background-color: transparent;"
            + "-fx-text-fill: #1565c0;"
            + "-fx-cursor: hand;"
            + "-fx-underline: true;"
        ));
        registerLink.setOnAction(e -> {
            RegisterView registerView = new RegisterView();
            Scene scene = new Scene(registerView.createView(), 900, 680);
            Stage stage = (Stage) registerLink.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Smart Canteen — Create Account");
            stage.centerOnScreen();
        });

        // ── Assemble card ────────────────────────────────────────────────
        card.getChildren().addAll(
            icon, appTitle, subtitle, sep,
            userLabel, usernameField,
            passLabel, passwordField,
            message,
            loginButton,
            registerLink
        );

        page.getChildren().add(card);
        VBox.setVgrow(card, Priority.NEVER);

        return page;
    }

    /** Shared text-field style */
    private String fieldStyle() {
        return "-fx-border-color: #ced4da;"
            + "-fx-border-radius: 6;"
            + "-fx-background-radius: 6;"
            + "-fx-padding: 8 12 8 12;"
            + "-fx-font-size: 13;";
    }
}
