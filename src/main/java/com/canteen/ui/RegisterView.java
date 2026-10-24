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
 * RegisterView — styled registration card.
 *
 * Flow:
 *   Fill name / username / password / confirm-password → Register
 *     → saves to DB via UserDAO.registerUser()
 *     → auto-logs in → opens DashboardView (1100 × 700)
 *
 *   "Back to Login" link → returns to LoginView
 */
public class RegisterView {

    public VBox createView() {

        // ── Outer page — same gradient as login ──────────────────────────
        VBox page = new VBox();
        page.setAlignment(Pos.CENTER);
        page.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #1b4332, #40916c);"
        );

        // ── Card ─────────────────────────────────────────────────────────
        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(36, 50, 36, 50));
        card.setMaxWidth(440);
        card.setStyle(
            "-fx-background-color: #ffffff;"
            + "-fx-background-radius: 16;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 24, 0, 0, 8);"
        );

        // ── Header ───────────────────────────────────────────────────────
        Label icon = new Label("🍽");
        icon.setFont(Font.font("System", 44));
        icon.setMaxWidth(Double.MAX_VALUE);
        icon.setAlignment(Pos.CENTER);
        icon.setTextAlignment(TextAlignment.CENTER);

        Label appTitle = new Label("Smart Canteen");
        appTitle.setFont(Font.font("System", FontWeight.BOLD, 24));
        appTitle.setTextFill(Color.web("#1b4332"));
        appTitle.setMaxWidth(Double.MAX_VALUE);
        appTitle.setAlignment(Pos.CENTER);

        Label regTitle = new Label("Create Account");
        regTitle.setFont(Font.font("System", FontWeight.BOLD, 15));
        regTitle.setTextFill(Color.web("#1565c0"));
        regTitle.setMaxWidth(Double.MAX_VALUE);
        regTitle.setAlignment(Pos.CENTER);

        Separator sep = new Separator();

        // ── Full Name ─────────────────────────────────────────────────────
        Label nameLbl = fieldLabel("Full Name");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        nameField.setPrefHeight(40);
        nameField.setStyle(fieldStyle());

        // ── Username ──────────────────────────────────────────────────────
        Label userLbl = fieldLabel("Username");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a username");
        usernameField.setPrefHeight(40);
        usernameField.setStyle(fieldStyle());

        // ── Password ──────────────────────────────────────────────────────
        Label passLbl = fieldLabel("Password");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Choose a password");
        passField.setPrefHeight(40);
        passField.setStyle(fieldStyle());

        // ── Confirm Password ──────────────────────────────────────────────
        Label confirmLbl = fieldLabel("Confirm Password");
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Re-enter your password");
        confirmField.setPrefHeight(40);
        confirmField.setStyle(fieldStyle());

        // ── Message label ─────────────────────────────────────────────────
        Label message = new Label();
        message.setWrapText(true);
        message.setFont(Font.font("System", FontWeight.BOLD, 12));

        // ── Register button ───────────────────────────────────────────────
        Button registerBtn = new Button("Create Account");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setPrefHeight(44);
        registerBtn.setFont(Font.font("System", FontWeight.BOLD, 15));
        registerBtn.setStyle(btnStyle("#1565c0"));
        registerBtn.setOnMouseEntered(e -> registerBtn.setStyle(btnStyle("#0d47a1")));
        registerBtn.setOnMouseExited(e  -> registerBtn.setStyle(btnStyle("#1565c0")));

        // ── Back to Login link ────────────────────────────────────────────
        Button backBtn = new Button("← Already have an account? Login");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setFont(Font.font("System", 13));
        backBtn.setStyle(
            "-fx-background-color: transparent;"
            + "-fx-text-fill: #1565c0;"
            + "-fx-cursor: hand;"
            + "-fx-underline: true;"
        );
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(
            "-fx-background-color: transparent;"
            + "-fx-text-fill: #0d47a1;"
            + "-fx-cursor: hand;"
            + "-fx-underline: true;"
        ));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(
            "-fx-background-color: transparent;"
            + "-fx-text-fill: #1565c0;"
            + "-fx-cursor: hand;"
            + "-fx-underline: true;"
        ));

        // ── Register action ───────────────────────────────────────────────
        registerBtn.setOnAction(e -> {
            String name     = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passField.getText();
            String confirm  = confirmField.getText();

            // Validation
            if (name.isEmpty() || username.isEmpty()
                    || password.isEmpty() || confirm.isEmpty()) {
                showMsg(message, "All fields are required.", false);
                return;
            }
            if (username.length() < 3) {
                showMsg(message, "Username must be at least 3 characters.", false);
                return;
            }
            if (password.length() < 4) {
                showMsg(message, "Password must be at least 4 characters.", false);
                return;
            }
            if (!password.equals(confirm)) {
                showMsg(message, "Passwords do not match.", false);
                confirmField.clear();
                return;
            }

            // Save to database
            UserDAO userDAO = new UserDAO();
            User newUser = userDAO.registerUser(name, username, password);

            if (newUser == null) {
                showMsg(message,
                    "Username \"" + username + "\" is already taken. Choose another.", false);
                usernameField.requestFocus();
                return;
            }

            // Auto-login: open dashboard immediately
            DashboardView dashboard = new DashboardView(newUser);
            Scene scene = new Scene(dashboard.createView(), 1100, 700);
            Stage stage = (Stage) registerBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Smart Canteen — Dashboard");
            stage.centerOnScreen();
        });

        // Enter key on confirmField triggers register
        confirmField.setOnAction(e -> registerBtn.fire());

        // ── Back to login action ──────────────────────────────────────────
        backBtn.setOnAction(e -> {
            LoginView loginView = new LoginView();
            Scene scene = new Scene(loginView.createView(), 900, 600);
            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Smart Canteen Token System");
            stage.centerOnScreen();
        });

        // ── Assemble ──────────────────────────────────────────────────────
        card.getChildren().addAll(
            icon, appTitle, regTitle, sep,
            nameLbl,    nameField,
            userLbl,    usernameField,
            passLbl,    passField,
            confirmLbl, confirmField,
            message,
            registerBtn,
            backBtn
        );

        page.getChildren().add(card);
        return page;
    }

    private Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 13));
        l.setTextFill(Color.web("#212121"));
        return l;
    }

    private String fieldStyle() {
        return "-fx-border-color: #ced4da;"
            + "-fx-border-radius: 6;"
            + "-fx-background-radius: 6;"
            + "-fx-padding: 8 12 8 12;"
            + "-fx-font-size: 13;"
            + "-fx-text-fill: #000000;"
            + "-fx-background-color: #ffffff;";
    }

    private String btnStyle(String bg) {
        return "-fx-background-color: " + bg + ";"
            + "-fx-text-fill: white;"
            + "-fx-background-radius: 8;"
            + "-fx-cursor: hand;";
    }

    private void showMsg(Label lbl, String text, boolean success) {
        lbl.setText(text);
        lbl.setTextFill(success ? Color.web("#1b5e20") : Color.web("#b71c1c"));
    }
}
