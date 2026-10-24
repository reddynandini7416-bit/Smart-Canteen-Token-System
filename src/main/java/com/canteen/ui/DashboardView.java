package com.canteen.ui;

import com.canteen.FoodItem;
import com.canteen.Order;
import com.canteen.OrderItem;
import com.canteen.Token;
import com.canteen.User;
import com.canteen.dao.FoodDAO;
import com.canteen.dao.OrderDAO;
import com.canteen.dao.OrderItemDAO;
import com.canteen.dao.TokenDAO;
import com.canteen.dao.UserDAO;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DashboardView {

    private final User user;
    private final StackPane contentArea = new StackPane();
    private Button activeNavBtn = null;

    public DashboardView(User user) {
        this.user = user;
    }

    public BorderPane createView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ffffff;");
        root.setTop(buildHeader());
        root.setLeft(buildSidebar());
        contentArea.setPadding(new Insets(28));
        contentArea.setAlignment(Pos.TOP_LEFT);
        contentArea.setStyle("-fx-background-color: #ffffff;");
        root.setCenter(contentArea);
        showFoodMenuPanel();
        return root;
    }

    // ─── HEADER ──────────────────────────────────────────────────────────────
    private HBox buildHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(0, 24, 0, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPrefHeight(62);
        header.setStyle("-fx-background-color: #1565c0;");   // strong blue

        Label logo = new Label("🍽  Smart Canteen Token System");
        logo.setFont(Font.font("System", FontWeight.BOLD, 20));
        logo.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String name = (user != null) ? user.getName() : "Guest";
        Label userInfo = new Label("👤  " + name);
        userInfo.setFont(Font.font("System", FontWeight.BOLD, 14));
        userInfo.setTextFill(Color.WHITE);

        header.getChildren().addAll(logo, spacer, userInfo);
        return header;
    }

    // ─── SIDEBAR ─────────────────────────────────────────────────────────────
    private VBox buildSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.setPrefWidth(210);
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        sidebar.setStyle("-fx-background-color: #1565c0;");   // same strong blue

        Label nav = new Label("  MENU");
        nav.setFont(Font.font("System", FontWeight.BOLD, 11));
        nav.setTextFill(Color.web("#bbdefb"));
        nav.setPadding(new Insets(0, 0, 10, 6));

        Button btnFood    = sideBtn("🍛   Food Menu / Order");
        Button btnToken   = sideBtn("🎟   View Token");
        Button btnHistory = sideBtn("📋   Order History");
        Button btnStatus  = sideBtn("🔄   Order Status");
        Button btnQueue   = sideBtn("📊   Order Queue");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = sideBtn("🚪   Logout");
        btnLogout.setStyle(sideBtnStyle(false, true));
        btnLogout.setOnMouseEntered(e -> btnLogout.setStyle(sideBtnStyle(true, true)));
        btnLogout.setOnMouseExited(e  -> btnLogout.setStyle(sideBtnStyle(false, true)));

        btnFood.setOnAction(e    -> { activate(btnFood);    showFoodMenuPanel(); });
        btnToken.setOnAction(e   -> { activate(btnToken);   showTokenPanel(); });
        btnHistory.setOnAction(e -> { activate(btnHistory); showOrderHistoryPanel(); });
        btnStatus.setOnAction(e  -> { activate(btnStatus);  showOrderStatusPanel(); });
        btnQueue.setOnAction(e   -> { activate(btnQueue);   showQueuePanel(); });
        btnLogout.setOnAction(e  -> doLogout(btnLogout));

        activate(btnFood);

        sidebar.getChildren().addAll(nav,
            btnFood, btnToken, btnHistory, btnStatus, btnQueue,
            spacer, btnLogout);
        return sidebar;
    }

    private Button sideBtn(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setStyle(sideBtnStyle(false, false));
        b.setOnMouseEntered(e -> { if (b != activeNavBtn) b.setStyle(sideBtnStyle(true, false)); });
        b.setOnMouseExited(e  -> { if (b != activeNavBtn) b.setStyle(sideBtnStyle(false, false)); });
        return b;
    }

    private String sideBtnStyle(boolean hover, boolean logout) {
        String bg    = hover  ? "rgba(255,255,255,0.18)" : "transparent";
        String color = logout ? "#ff8a80" : "#ffffff";
        return "-fx-background-color: " + bg + ";"
             + "-fx-text-fill: " + color + ";"
             + "-fx-font-size: 13px;"
             + "-fx-font-weight: bold;"
             + "-fx-alignment: CENTER_LEFT;"
             + "-fx-padding: 10 12 10 12;"
             + "-fx-background-radius: 6;"
             + "-fx-cursor: hand;";
    }

    private void activate(Button b) {
        if (activeNavBtn != null) activeNavBtn.setStyle(sideBtnStyle(false, false));
        activeNavBtn = b;
        b.setStyle(
            "-fx-background-color: #ffffff;"
          + "-fx-text-fill: #1565c0;"
          + "-fx-font-size: 13px;"
          + "-fx-font-weight: bold;"
          + "-fx-alignment: CENTER_LEFT;"
          + "-fx-padding: 10 12 10 12;"
          + "-fx-background-radius: 6;"
          + "-fx-cursor: hand;"
        );
    }

    private void show(Node panel) {
        contentArea.getChildren().setAll(panel);
    }

    // ─── PANEL 1 : FOOD MENU / ORDER ─────────────────────────────────────────
    /**
     * CartItem — one row in the cart (food + chosen quantity).
     */
    private static final class CartItem {
        final FoodItem food;
        int qty;
        CartItem(FoodItem food, int qty) { this.food = food; this.qty = qty; }
        double lineTotal() { return food.getPrice() * qty; }
    }

    private void showFoodMenuPanel() {

        // ── Left: food menu table ─────────────────────────────────────────
        Label menuTitle = panelTitle("🍛  Food Menu");

        TableView<FoodItem> foodTable = new TableView<>();
        foodTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        foodTable.setPrefHeight(260);

        TableColumn<FoodItem, String> nameCol = new TableColumn<>("Food Item");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFoodName()));
        nameCol.setStyle("-fx-font-size: 13px;");

        TableColumn<FoodItem, String> priceCol = new TableColumn<>("Price (₹)");
        priceCol.setCellValueFactory(d ->
            new SimpleStringProperty(String.format("%.2f", d.getValue().getPrice())));
        priceCol.setPrefWidth(110);
        priceCol.setStyle("-fx-font-size: 13px;");

        foodTable.getColumns().addAll(nameCol, priceCol);
        List<FoodItem> allFood = new FoodDAO().getAvailableFood();
        foodTable.setItems(FXCollections.observableArrayList(allFood));

        // ── Right: order form ─────────────────────────────────────────────
        Label orderTitle = panelTitle("🛒  Place Order");

        Label selectionHint = bodyLabel("Click a food item on the left, set quantity, then Add to Cart.");

        // Quantity row
        Label qtyLbl = boldLabel("Quantity:");
        TextField qtyField = new TextField("1");
        qtyField.setPrefWidth(80);
        qtyField.setStyle(fieldStyle());

        // Add to Cart button
        Button addToCartBtn = primaryBtn("➕  Add to Cart");
        addToCartBtn.setDisable(true);

        // ── Cart table ────────────────────────────────────────────────────
        Label cartTitle = boldLabel("Your Cart:");

        ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

        TableView<CartItem> cartTable = new TableView<>(cartItems);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        cartTable.setPrefHeight(160);
        cartTable.setPlaceholder(new Label("Cart is empty — add items above."));

        TableColumn<CartItem, String> cName = new TableColumn<>("Item");
        cName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().food.getFoodName()));
        cName.setStyle("-fx-font-size: 13px;");

        TableColumn<CartItem, String> cQty = new TableColumn<>("Qty");
        cQty.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().qty)));
        cQty.setPrefWidth(60);
        cQty.setStyle("-fx-font-size: 13px;");

        TableColumn<CartItem, String> cUnit = new TableColumn<>("Unit ₹");
        cUnit.setCellValueFactory(d ->
            new SimpleStringProperty(String.format("%.2f", d.getValue().food.getPrice())));
        cUnit.setPrefWidth(90);
        cUnit.setStyle("-fx-font-size: 13px;");

        TableColumn<CartItem, String> cTotal = new TableColumn<>("Line ₹");
        cTotal.setCellValueFactory(d ->
            new SimpleStringProperty(String.format("%.2f", d.getValue().lineTotal())));
        cTotal.setPrefWidth(90);
        cTotal.setStyle("-fx-font-size: 13px;");

        cartTable.getColumns().addAll(cName, cQty, cUnit, cTotal);

        // Remove selected row button
        Button removeBtn = new Button("🗑  Remove Selected");
        removeBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
        removeBtn.setStyle(
            "-fx-background-color: #e53935; -fx-text-fill: white;"
          + "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 14 6 14;");
        removeBtn.setOnMouseEntered(e -> removeBtn.setStyle(
            "-fx-background-color: #b71c1c; -fx-text-fill: white;"
          + "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 14 6 14;"));
        removeBtn.setOnMouseExited(e -> removeBtn.setStyle(
            "-fx-background-color: #e53935; -fx-text-fill: white;"
          + "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 14 6 14;"));

        // Cart total label
        Label totalLabel = new Label("Cart Total:  ₹ 0.00");
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        totalLabel.setTextFill(Color.web("#1565c0"));

        // Recalculate cart total helper
        Runnable recalcTotal = () -> {
            double sum = cartItems.stream().mapToDouble(CartItem::lineTotal).sum();
            totalLabel.setText(cartItems.isEmpty()
                ? "Cart Total:  ₹ 0.00"
                : String.format("Cart Total:  ₹ %.2f", sum));
        };

        Label msgLabel = new Label();
        msgLabel.setWrapText(true);
        msgLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        Button placeBtn = primaryBtn("✅  Place Order");
        placeBtn.setDisable(true);

        VBox receiptBox = new VBox();

        // ── Selection listener on food table ─────────────────────────────
        foodTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                selectionHint.setText("Selected:  " + n.getFoodName()
                    + "  (₹ " + String.format("%.2f", n.getPrice()) + ")");
                selectionHint.setTextFill(Color.web("#1b5e20"));
                selectionHint.setFont(Font.font("System", FontWeight.BOLD, 13));
                addToCartBtn.setDisable(false);
            }
        });

        // ── Add to Cart ───────────────────────────────────────────────────
        addToCartBtn.setOnAction(e -> {
            FoodItem food = foodTable.getSelectionModel().getSelectedItem();
            if (food == null) { setMsg(msgLabel, "Select a food item first.", false); return; }

            int qty;
            try {
                qty = Integer.parseInt(qtyField.getText().trim());
                if (qty <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                setMsg(msgLabel, "Enter a valid quantity (1 or more).", false); return;
            }

            // If the same food item is already in cart, increase its quantity
            boolean found = false;
            for (CartItem ci : cartItems) {
                if (ci.food.getFoodId() == food.getFoodId()) {
                    ci.qty += qty;
                    cartTable.refresh();   // update display
                    found = true;
                    break;
                }
            }
            if (!found) {
                cartItems.add(new CartItem(food, qty));
            }

            recalcTotal.run();
            placeBtn.setDisable(false);
            msgLabel.setText("");

            // reset selection
            qtyField.setText("1");
            foodTable.getSelectionModel().clearSelection();
            selectionHint.setText("Click a food item on the left, set quantity, then Add to Cart.");
            selectionHint.setTextFill(Color.web("#212121"));
            selectionHint.setFont(Font.font("System", 13));
            addToCartBtn.setDisable(true);
        });

        // ── Remove selected cart row ──────────────────────────────────────
        removeBtn.setOnAction(e -> {
            CartItem sel = cartTable.getSelectionModel().getSelectedItem();
            if (sel != null) {
                cartItems.remove(sel);
                recalcTotal.run();
                if (cartItems.isEmpty()) placeBtn.setDisable(true);
            }
        });

        // ── Place Order ───────────────────────────────────────────────────
        placeBtn.setOnAction(e -> {
            if (cartItems.isEmpty()) { setMsg(msgLabel, "Cart is empty.", false); return; }

            double orderTotal = cartItems.stream().mapToDouble(CartItem::lineTotal).sum();
            int userId = (user != null) ? user.getUserId() : 0;

            // Create one Order record
            int orderId = new OrderDAO().createOrder(new Order(userId, orderTotal));
            if (orderId == -1) {
                setMsg(msgLabel, "Order creation failed. Check DB connection.", false); return;
            }

            // Save every cart item as an OrderItem
            OrderItemDAO itemDAO = new OrderItemDAO();
            for (CartItem ci : cartItems) {
                boolean saved = itemDAO.addOrderItem(
                    new OrderItem(orderId, ci.food.getFoodId(), ci.qty, ci.food.getPrice()));
                if (!saved) {
                    setMsg(msgLabel, "Failed saving item: " + ci.food.getFoodName(), false);
                    return;
                }
            }

            // Generate one token for the whole order
            int tokenNumber = new TokenDAO().generateToken(orderId);
            if (tokenNumber == -1) {
                setMsg(msgLabel, "Order saved but token generation failed.", false); return;
            }

            setMsg(msgLabel, "✅  Order placed successfully!", true);

            // Build receipt with all cart items
            List<CartItem> snapshot = new java.util.ArrayList<>(cartItems);
            receiptBox.getChildren().setAll(
                buildReceipt(orderId, tokenNumber,
                    (user != null) ? user.getName() : "Guest",
                    snapshot, orderTotal));

            // Clear cart
            cartItems.clear();
            recalcTotal.run();
            placeBtn.setDisable(true);
            qtyField.setText("1");
            foodTable.getSelectionModel().clearSelection();
            selectionHint.setText("Click a food item on the left, set quantity, then Add to Cart.");
            selectionHint.setTextFill(Color.web("#212121"));
            selectionHint.setFont(Font.font("System", 13));
            addToCartBtn.setDisable(true);
        });

        // ── Layout ────────────────────────────────────────────────────────
        HBox qtyRow = new HBox(10, qtyLbl, qtyField, addToCartBtn);
        qtyRow.setAlignment(Pos.CENTER_LEFT);

        HBox cartHeader = new HBox(12, cartTitle, new Region(), removeBtn);
        HBox.setHgrow(cartHeader.getChildren().get(1), Priority.ALWAYS);
        cartHeader.setAlignment(Pos.CENTER_LEFT);

        VBox leftCol = new VBox(10, menuTitle, foodTable);
        leftCol.setMinWidth(320);

        VBox rightCol = new VBox(14,
            orderTitle, selectionHint, qtyRow,
            cartHeader, cartTable,
            totalLabel, placeBtn, msgLabel, receiptBox);
        rightCol.setPadding(new Insets(0, 0, 0, 30));
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        HBox layout = new HBox(0, leftCol, rightCol);
        show(scroll(layout));
    }

    private void calcTotal(Label lbl, double price, String qtyText) {
        try {
            int q = Integer.parseInt(qtyText.trim());
            lbl.setText(q > 0 ? String.format("Total:  ₹ %.2f", price * q) : "Total:  ₹ 0.00");
        } catch (NumberFormatException ignored) {
            lbl.setText("Total:  ₹ 0.00");
        }
    }

    // ─── RECEIPT ─────────────────────────────────────────────────────────────
    private VBox buildReceipt(int orderId, int tokenNo, String customer,
                               List<CartItem> items, double total) {
        String now = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy  HH:mm:ss"));

        VBox card = receiptContent(orderId, tokenNo, customer, items, total, now);
        card.setMaxWidth(480);
        card.setStyle(
            "-fx-background-color: #ffffff;"
          + "-fx-background-radius: 12;"
          + "-fx-border-color: #1565c0;"
          + "-fx-border-radius: 12;"
          + "-fx-border-width: 2;"
          + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.18),14,0,0,5);"
        );

        Button printBtn = new Button("🖨  Print Receipt");
        printBtn.setFont(Font.font("System", FontWeight.BOLD, 13));
        printBtn.setMaxWidth(Double.MAX_VALUE);
        printBtn.setPrefHeight(42);
        printBtn.setStyle(
            "-fx-background-color: #37474f; -fx-text-fill: white;"
          + "-fx-background-radius: 0 0 10 10; -fx-cursor: hand;");
        printBtn.setOnMouseEntered(e -> printBtn.setStyle(
            "-fx-background-color: #212121; -fx-text-fill: white;"
          + "-fx-background-radius: 0 0 10 10; -fx-cursor: hand;"));
        printBtn.setOnMouseExited(e -> printBtn.setStyle(
            "-fx-background-color: #37474f; -fx-text-fill: white;"
          + "-fx-background-radius: 0 0 10 10; -fx-cursor: hand;"));

        printBtn.setOnAction(e -> {
            VBox printable = receiptContent(orderId, tokenNo, customer, items, total, now);
            printable.setMaxWidth(480);
            doPrint(printable, printBtn);
        });

        VBox wrapper = new VBox(0, card, printBtn);
        wrapper.setMaxWidth(480);

        Label heading = new Label("🧾  Your Receipt");
        heading.setFont(Font.font("System", FontWeight.BOLD, 17));
        heading.setTextFill(Color.web("#000000"));

        return new VBox(10, heading, wrapper);
    }

    private VBox receiptContent(int orderId, int tokenNo, String customer,
                                 List<CartItem> items, double total, String dateTime) {
        // ── header
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(18, 20, 14, 20));
        header.setStyle("-fx-background-color: #1565c0; -fx-background-radius: 10 10 0 0;");

        Label canteen = new Label("🍽  SMART CANTEEN");
        canteen.setFont(Font.font("System", FontWeight.BOLD, 22));
        canteen.setTextFill(Color.WHITE);

        Label sub = new Label("ORDER RECEIPT");
        sub.setFont(Font.font("System", FontWeight.BOLD, 13));
        sub.setTextFill(Color.web("#bbdefb"));
        header.getChildren().addAll(canteen, sub);

        // ── token band
        VBox tokenBand = new VBox(3);
        tokenBand.setAlignment(Pos.CENTER);
        tokenBand.setPadding(new Insets(14, 20, 14, 20));
        tokenBand.setStyle("-fx-background-color: #e53935;");

        Label tLbl = new Label("TOKEN NUMBER");
        tLbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        tLbl.setTextFill(Color.WHITE);

        Label tNum = new Label(String.valueOf(tokenNo));
        tNum.setFont(Font.font("System", FontWeight.BOLD, 58));
        tNum.setTextFill(Color.WHITE);

        Label tSub = new Label("🎟  Wait for your token to be called");
        tSub.setFont(Font.font("System", FontWeight.BOLD, 12));
        tSub.setTextFill(Color.web("#ffcdd2"));
        tokenBand.getChildren().addAll(tLbl, tNum, tSub);

        // ── detail rows
        VBox rows = new VBox(1);
        rows.setStyle("-fx-background-color: #cccccc;");
        rows.setPadding(new Insets(1, 0, 1, 0));

        rows.getChildren().addAll(
            rRow("Order ID",    "#" + orderId,  "#e3f2fd", "#0d47a1"),
            rRow("Customer",    customer,        "#fff8e1", "#e65100"),
            rRow("Date & Time", dateTime,        "#f3e5f5", "#4a148c")
        );

        // ── one row per cart item
        rows.getChildren().add(rDivider("ORDER ITEMS  (" + items.size() + " item(s))"));

        String[] itemBg    = {"#e8f5e9", "#f1f8e9", "#e0f2f1", "#fce4ec", "#e8eaf6", "#fff3e0"};
        String[] itemColor = {"#1b5e20", "#33691e", "#004d40", "#880e4f", "#1a237e", "#bf360c"};

        for (int i = 0; i < items.size(); i++) {
            CartItem ci  = items.get(i);
            String   bg  = itemBg[i  % itemBg.length];
            String   col = itemColor[i % itemColor.length];
            rows.getChildren().addAll(
                rRow((i + 1) + ". " + ci.food.getFoodName(),
                     "Qty: " + ci.qty + "   Unit: ₹ " + String.format("%.2f", ci.food.getPrice())
                     + "   → ₹ " + String.format("%.2f", ci.lineTotal()),
                     bg, col)
            );
        }

        rows.getChildren().addAll(
            rDivider("ORDER STATUS"),
            rRow("Status", "Pending", "#fff9c4", "#f57f17")
        );

        // ── total band
        VBox totalBand = new VBox(4);
        totalBand.setAlignment(Pos.CENTER);
        totalBand.setPadding(new Insets(16, 20, 16, 20));
        totalBand.setStyle("-fx-background-color: #2e7d32;");

        Label tTitle = new Label("TOTAL AMOUNT");
        tTitle.setFont(Font.font("System", FontWeight.BOLD, 13));
        tTitle.setTextFill(Color.web("#c8e6c9"));

        Label tAmt = new Label("₹  " + String.format("%.2f", total));
        tAmt.setFont(Font.font("System", FontWeight.BOLD, 42));
        tAmt.setTextFill(Color.WHITE);
        totalBand.getChildren().addAll(tTitle, tAmt);

        // ── footer
        VBox foot = new VBox(6);
        foot.setAlignment(Pos.CENTER);
        foot.setPadding(new Insets(14, 20, 18, 20));
        foot.setStyle("-fx-background-color: #1565c0;");

        Label stars  = new Label("★  ★  ★  ★  ★");
        stars.setFont(Font.font("System", FontWeight.BOLD, 16));
        stars.setTextFill(Color.web("#fff176"));

        Label thanks = new Label("Thank you for your order!");
        thanks.setFont(Font.font("System", FontWeight.BOLD, 15));
        thanks.setTextFill(Color.WHITE);

        Label note = new Label("Collect your order when your token number is called.");
        note.setFont(Font.font("System", 12));
        note.setTextFill(Color.web("#bbdefb"));
        note.setWrapText(true);
        note.setTextAlignment(TextAlignment.CENTER);
        note.setMaxWidth(380);

        foot.getChildren().addAll(stars, thanks, note);

        return new VBox(0, header, tokenBand, rows, totalBand, foot);
    }

    /**
     * One receipt detail row.
     * Each row has a light tinted background with very dark text — easy to read.
     */
    private HBox rRow(String key, String value, String bg, String valueColor) {
        Label k = new Label(key + " :");
        k.setFont(Font.font("System", FontWeight.BOLD, 13));
        k.setTextFill(Color.web("#212121"));     // near-black — always readable
        k.setMinWidth(140);
        k.setPadding(new Insets(10, 10, 10, 18));

        Label v = new Label(value);
        v.setFont(Font.font("System", FontWeight.BOLD, 14));
        v.setTextFill(Color.web(valueColor));    // strong colour per field
        v.setWrapText(true);
        v.setPadding(new Insets(10, 18, 10, 6));

        HBox row = new HBox(k, v);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: " + bg + ";");
        HBox.setHgrow(v, Priority.ALWAYS);
        return row;
    }

    /** Bold section-divider banner inside the receipt. */
    private HBox rDivider(String title) {
        Label l = new Label("  " + title);
        l.setFont(Font.font("System", FontWeight.BOLD, 12));
        l.setTextFill(Color.WHITE);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setPadding(new Insets(7, 18, 7, 18));

        HBox b = new HBox(l);
        b.setStyle("-fx-background-color: #37474f;");
        HBox.setHgrow(l, Priority.ALWAYS);
        return b;
    }

    private void doPrint(VBox printable, Button trigger) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) { alert("No printer found on this system."); return; }
        if (job.showPrintDialog((Stage) trigger.getScene().getWindow())) {
            if (job.printPage(printable)) { job.endJob(); alert("Printed successfully."); }
            else alert("Printing failed. Please try again.");
        }
    }

    // ─── PANEL 2 : VIEW RECEIPT BY ORDER ID OR TOKEN NUMBER ──────────────────
    private void showTokenPanel() {

        Label title = panelTitle("🧾  View Receipt");
        Label instr = bodyLabel(
            "Enter either your Order ID or Token Number to fetch and display your full receipt.");

        // ── Input row ─────────────────────────────────────────────────────
        Label idTypeLbl = boldLabel("Search by:");
        ToggleGroup tg = new ToggleGroup();
        RadioButton byOrder = new RadioButton("Order ID");
        RadioButton byToken = new RadioButton("Token Number");
        byOrder.setFont(Font.font("System", FontWeight.BOLD, 13));
        byToken.setFont(Font.font("System", FontWeight.BOLD, 13));
        byOrder.setTextFill(Color.web("#000000"));
        byToken.setTextFill(Color.web("#000000"));
        byOrder.setToggleGroup(tg);
        byToken.setToggleGroup(tg);
        byOrder.setSelected(true);

        HBox radioRow = new HBox(20, idTypeLbl, byOrder, byToken);
        radioRow.setAlignment(Pos.CENTER_LEFT);

        TextField inputField = new TextField();
        inputField.setPromptText("Enter Order ID (e.g. 5)");
        inputField.setMaxWidth(280);
        inputField.setStyle(fieldStyle());

        // Update prompt text when toggle changes
        byOrder.setOnAction(e -> inputField.setPromptText("Enter Order ID (e.g. 5)"));
        byToken.setOnAction(e -> inputField.setPromptText("Enter Token Number (e.g. 3)"));

        Button searchBtn = primaryBtn("🔍  Get Receipt");
        Label msgLabel   = new Label();
        msgLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        msgLabel.setWrapText(true);

        // Receipt display area
        VBox receiptArea = new VBox();

        // ── Search action ─────────────────────────────────────────────────
        searchBtn.setOnAction(e -> {
            String input = inputField.getText().trim();
            receiptArea.getChildren().clear();
            msgLabel.setText("");

            if (input.isEmpty()) {
                setMsg(msgLabel, "Please enter an Order ID or Token Number.", false);
                return;
            }

            int number;
            try {
                number = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                setMsg(msgLabel, "Please enter a valid number.", false);
                return;
            }

            OrderDAO orderDAO       = new OrderDAO();
            OrderItemDAO itemDAO    = new OrderItemDAO();
            TokenDAO tokenDAO       = new TokenDAO();
            UserDAO  userDAO        = new UserDAO();

            int orderId;
            int tokenNumber;

            if (byOrder.isSelected()) {
                // lookup by order id
                orderId     = number;
                tokenNumber = tokenDAO.getTokenByOrderId(orderId);
            } else {
                // lookup by token number → get order id
                orderId     = orderDAO.getOrderIdByTokenNumber(number);
                tokenNumber = number;
            }

            if (orderId == -1) {
                setMsg(msgLabel, "No order found for Token Number " + number + ".", false);
                return;
            }

            Order order = orderDAO.getOrderById(orderId);
            if (order == null) {
                setMsg(msgLabel, "Order #" + orderId + " not found in database.", false);
                return;
            }

            List<String[]> items = itemDAO.getItemsByOrderId(orderId);
            if (items.isEmpty()) {
                setMsg(msgLabel,
                    "Order #" + orderId + " exists but has no items recorded.", false);
                return;
            }

            String customerName = userDAO.getNameByUserId(order.getUserId());
            String tokenDisplay = (tokenNumber != -1)
                ? String.valueOf(tokenNumber) : "N/A";

            // Build and display full receipt
            VBox receipt = buildLookupReceipt(
                orderId, tokenDisplay, customerName,
                order.getStatus(), order.getTotalAmount(), items);

            receiptArea.getChildren().setAll(receipt);
            msgLabel.setText("");
        });

        inputField.setOnAction(e -> searchBtn.fire());

        HBox inputRow = new HBox(12, inputField, searchBtn);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        show(scroll(new VBox(16,
            title, instr, radioRow, inputRow, msgLabel, receiptArea)));
    }

    /**
     * Builds a full receipt card from database-fetched data.
     * items: each String[] = { food_name, quantity, unit_price, line_total }
     */
    private VBox buildLookupReceipt(int orderId, String tokenDisplay,
                                     String customer, String status,
                                     double total, List<String[]> items) {

        String now = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy  HH:mm:ss"));

        VBox card = lookupReceiptContent(
            orderId, tokenDisplay, customer, status, total, items, now);
        card.setMaxWidth(520);
        card.setStyle(
            "-fx-background-color: #ffffff;"
          + "-fx-background-radius: 12;"
          + "-fx-border-color: #1565c0;"
          + "-fx-border-radius: 12;"
          + "-fx-border-width: 2;"
          + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.18),14,0,0,5);"
        );

        Button printBtn = new Button("🖨  Print Receipt");
        printBtn.setFont(Font.font("System", FontWeight.BOLD, 13));
        printBtn.setMaxWidth(Double.MAX_VALUE);
        printBtn.setPrefHeight(42);
        printBtn.setStyle(
            "-fx-background-color: #37474f; -fx-text-fill: white;"
          + "-fx-background-radius: 0 0 10 10; -fx-cursor: hand;");
        printBtn.setOnMouseEntered(e -> printBtn.setStyle(
            "-fx-background-color: #212121; -fx-text-fill: white;"
          + "-fx-background-radius: 0 0 10 10; -fx-cursor: hand;"));
        printBtn.setOnMouseExited(e -> printBtn.setStyle(
            "-fx-background-color: #37474f; -fx-text-fill: white;"
          + "-fx-background-radius: 0 0 10 10; -fx-cursor: hand;"));

        printBtn.setOnAction(e -> {
            VBox printable = lookupReceiptContent(
                orderId, tokenDisplay, customer, status, total, items, now);
            printable.setMaxWidth(520);
            doPrint(printable, printBtn);
        });

        VBox wrapper = new VBox(0, card, printBtn);
        wrapper.setMaxWidth(520);

        Label heading = new Label("🧾  Receipt for Order #" + orderId);
        heading.setFont(Font.font("System", FontWeight.BOLD, 17));
        heading.setTextFill(Color.web("#000000"));

        return new VBox(10, heading, wrapper);
    }

    private VBox lookupReceiptContent(int orderId, String tokenDisplay,
                                       String customer, String status,
                                       double total, List<String[]> items,
                                       String printedAt) {
        // ── Header ────────────────────────────────────────────────────────
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(18, 20, 14, 20));
        header.setStyle("-fx-background-color: #1565c0; -fx-background-radius: 10 10 0 0;");

        Label canteen = new Label("🍽  SMART CANTEEN");
        canteen.setFont(Font.font("System", FontWeight.BOLD, 22));
        canteen.setTextFill(Color.WHITE);

        Label sub = new Label("ORDER RECEIPT");
        sub.setFont(Font.font("System", FontWeight.BOLD, 13));
        sub.setTextFill(Color.web("#bbdefb"));
        header.getChildren().addAll(canteen, sub);

        // ── Token band ────────────────────────────────────────────────────
        VBox tokenBand = new VBox(3);
        tokenBand.setAlignment(Pos.CENTER);
        tokenBand.setPadding(new Insets(12, 20, 12, 20));
        tokenBand.setStyle("-fx-background-color: #e53935;");

        Label tLbl = new Label("TOKEN NUMBER");
        tLbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        tLbl.setTextFill(Color.WHITE);

        Label tNum = new Label(tokenDisplay);
        tNum.setFont(Font.font("System", FontWeight.BOLD, 52));
        tNum.setTextFill(Color.WHITE);

        Label tSub = new Label("🎟  Collect your order when this number is called");
        tSub.setFont(Font.font("System", FontWeight.BOLD, 11));
        tSub.setTextFill(Color.web("#ffcdd2"));
        tokenBand.getChildren().addAll(tLbl, tNum, tSub);

        // ── Details section ───────────────────────────────────────────────
        VBox rows = new VBox(1);
        rows.setStyle("-fx-background-color: #cccccc;");
        rows.setPadding(new Insets(1, 0, 1, 0));

        rows.getChildren().addAll(
            rRow("Order ID",    "#" + orderId,   "#e3f2fd", "#0d47a1"),
            rRow("Customer",    customer,         "#fff8e1", "#e65100"),
            rRow("Printed At",  printedAt,        "#f3e5f5", "#4a148c")
        );

        // ── Order items ───────────────────────────────────────────────────
        rows.getChildren().add(
            rDivider("ORDER ITEMS  (" + items.size() + " item(s))"));

        String[] itemBg    = {"#e8f5e9","#f1f8e9","#e0f2f1","#fce4ec","#e8eaf6","#fff3e0"};
        String[] itemColor = {"#1b5e20","#33691e","#004d40","#880e4f","#1a237e","#bf360c"};

        for (int i = 0; i < items.size(); i++) {
            String[] it  = items.get(i);
            // it[0]=food_name  it[1]=qty  it[2]=unit_price  it[3]=line_total
            String bg    = itemBg[i % itemBg.length];
            String color = itemColor[i % itemColor.length];

            // Item name row
            rows.getChildren().add(rRow(
                (i + 1) + ".  " + it[0],
                "Qty: " + it[1]
                + "   Unit: ₹ " + it[2]
                + "   →  ₹ " + it[3],
                bg, color));
        }

        // ── Status ────────────────────────────────────────────────────────
        rows.getChildren().addAll(
            rDivider("ORDER STATUS"),
            rRow("Status", status, "#fff9c4", "#f57f17")
        );

        // ── Total band ────────────────────────────────────────────────────
        VBox totalBand = new VBox(4);
        totalBand.setAlignment(Pos.CENTER);
        totalBand.setPadding(new Insets(16, 20, 16, 20));
        totalBand.setStyle("-fx-background-color: #2e7d32;");

        Label tTitle = new Label("TOTAL AMOUNT");
        tTitle.setFont(Font.font("System", FontWeight.BOLD, 13));
        tTitle.setTextFill(Color.web("#c8e6c9"));

        Label tAmt = new Label("₹  " + String.format("%.2f", total));
        tAmt.setFont(Font.font("System", FontWeight.BOLD, 42));
        tAmt.setTextFill(Color.WHITE);
        totalBand.getChildren().addAll(tTitle, tAmt);

        // ── Footer ────────────────────────────────────────────────────────
        VBox foot = new VBox(6);
        foot.setAlignment(Pos.CENTER);
        foot.setPadding(new Insets(14, 20, 18, 20));
        foot.setStyle("-fx-background-color: #1565c0;");

        Label stars  = new Label("★  ★  ★  ★  ★");
        stars.setFont(Font.font("System", FontWeight.BOLD, 16));
        stars.setTextFill(Color.web("#fff176"));

        Label thanks = new Label("Thank you for dining with us!");
        thanks.setFont(Font.font("System", FontWeight.BOLD, 15));
        thanks.setTextFill(Color.WHITE);

        Label note = new Label("Please visit again — Smart Canteen Token System");
        note.setFont(Font.font("System", 12));
        note.setTextFill(Color.web("#bbdefb"));
        note.setWrapText(true);
        note.setTextAlignment(TextAlignment.CENTER);
        note.setMaxWidth(420);

        foot.getChildren().addAll(stars, thanks, note);

        return new VBox(0, header, tokenBand, rows, totalBand, foot);
    }

    // ─── PANEL 3 : ORDER HISTORY ──────────────────────────────────────────────
    private void showOrderHistoryPanel() {
        Label title    = panelTitle("📋  Order History");
        Button refresh = secondaryBtn("↻  Refresh");

        TableView<Order> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(440);

        TableColumn<Order, String> c1 = col("Order ID",   d -> String.valueOf(d.getValue().getOrderId()));
        TableColumn<Order, String> c2 = col("User ID",    d -> String.valueOf(d.getValue().getUserId()));
        TableColumn<Order, String> c3 = col("Amount (₹)", d -> String.format("%.2f", d.getValue().getTotalAmount()));
        TableColumn<Order, String> c4 = col("Status",     d -> d.getValue().getStatus());
        c1.setPrefWidth(90); c2.setPrefWidth(80);
        table.getColumns().addAll(c1, c2, c3, c4);

        // Single reused list — clear + refill on every refresh
        ObservableList<Order> orderList = FXCollections.observableArrayList();
        table.setItems(orderList);

        Runnable load = () -> {
            orderList.clear();
            orderList.addAll(new OrderDAO().getAllOrders());
        };
        load.run();
        refresh.setOnAction(e -> load.run());

        HBox top = new HBox(12, title, new Region(), refresh);
        HBox.setHgrow(top.getChildren().get(1), Priority.ALWAYS);
        top.setAlignment(Pos.CENTER_LEFT);

        show(scroll(new VBox(14, top, table)));
    }

    // ─── PANEL 4 : ORDER STATUS ───────────────────────────────────────────────
    private void showOrderStatusPanel() {
        Label title = panelTitle("🔄  Order Status");
        Label instr = bodyLabel("Enter an Order ID and choose a new status to update it.");

        Label idLbl = boldLabel("Order ID:");
        TextField idField = new TextField();
        idField.setPromptText("e.g. 5");
        idField.setMaxWidth(200);
        idField.setStyle(fieldStyle());

        Label stLbl  = boldLabel("New Status:");
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Pending", "Preparing", "Ready", "Served");
        statusBox.setPromptText("Select status");
        statusBox.setMaxWidth(200);

        Button updateBtn = primaryBtn("✅  Update Status");
        Label msgLabel   = new Label();
        msgLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        msgLabel.setWrapText(true);

        updateBtn.setOnAction(e -> {
            String idTxt = idField.getText().trim();
            String st    = statusBox.getValue();
            if (idTxt.isEmpty()) { setMsg(msgLabel, "Please enter an Order ID.", false); return; }
            if (st == null)      { setMsg(msgLabel, "Please select a status.",   false); return; }
            try {
                int oid = Integer.parseInt(idTxt);
                boolean ok = new OrderDAO().updateOrderStatus(oid, st);
                if (ok) {
                    setMsg(msgLabel, "Order #" + oid + " updated to \"" + st + "\".", true);
                    idField.clear(); statusBox.setValue(null);
                } else {
                    setMsg(msgLabel, "Order #" + oid + " not found.", false);
                }
            } catch (NumberFormatException ex) {
                setMsg(msgLabel, "Order ID must be a number.", false);
            }
        });

        GridPane form = new GridPane();
        form.setVgap(14); form.setHgap(16);
        form.add(idLbl, 0, 0); form.add(idField,    1, 0);
        form.add(stLbl, 0, 1); form.add(statusBox,  1, 1);

        show(scroll(new VBox(16, title, instr, form, updateBtn, msgLabel)));
    }

    // ─── PANEL 5 : ORDER QUEUE ────────────────────────────────────────────────
    private static final class QueueRow {
        final String token, orderId, status;
        QueueRow(String t, String o, String s) { token=t; orderId=o; status=s; }
    }

    private void showQueuePanel() {
        Label title    = panelTitle("📊  Order Queue");
        Label instr    = bodyLabel("Showing all orders with their token numbers and current status.");
        Button refresh = secondaryBtn("↻  Refresh");

        TableView<QueueRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(440);

        TableColumn<QueueRow, String> tc = col("Token #",  d -> d.getValue().token);
        TableColumn<QueueRow, String> oc = col("Order ID", d -> d.getValue().orderId);
        TableColumn<QueueRow, String> sc = col("Status",   d -> d.getValue().status);
        tc.setPrefWidth(100); oc.setPrefWidth(100);
        table.getColumns().addAll(tc, oc, sc);

        // Use a single ObservableList that is reused on every refresh.
        // Calling setItems() with a new list every time can silently fail to trigger
        // a table repaint — clearing and refilling the same list is always reliable.
        ObservableList<QueueRow> rows = FXCollections.observableArrayList();
        table.setItems(rows);

        Runnable load = () -> {
            rows.clear();
            for (String[] r : new OrderDAO().getQueueOrders())
                rows.add(new QueueRow(r[0], r[1], r[2]));
        };

        load.run();                            // populate on first open
        refresh.setOnAction(e -> load.run()); // re-populate on every click

        HBox top = new HBox(12, title, new Region(), refresh);
        HBox.setHgrow(top.getChildren().get(1), Priority.ALWAYS);
        top.setAlignment(Pos.CENTER_LEFT);

        show(scroll(new VBox(14, top, instr, table)));
    }

    // ─── LOGOUT ───────────────────────────────────────────────────────────────
    private void doLogout(Button trigger) {
        LoginView lv = new LoginView();
        Stage stage  = (Stage) trigger.getScene().getWindow();
        stage.setScene(new Scene(lv.createView(), 900, 600));
        stage.setTitle("Smart Canteen Token System — Login");
        stage.centerOnScreen();
    }

    // ─── SHARED HELPERS ───────────────────────────────────────────────────────

    /** Big bold section title — black text, always readable on white. */
    private Label panelTitle(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 22));
        l.setTextFill(Color.web("#000000"));
        l.setPadding(new Insets(0, 0, 6, 0));
        return l;
    }

    /** Standard body instruction text — dark grey, not light grey. */
    private Label bodyLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", 14));
        l.setTextFill(Color.web("#212121"));
        l.setWrapText(true);
        return l;
    }

    /** Bold form field label — black. */
    private Label boldLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 14));
        l.setTextFill(Color.web("#000000"));
        return l;
    }

    /** Blue primary action button. */
    private Button primaryBtn(String text) {
        Button b = new Button(text);
        b.setFont(Font.font("System", FontWeight.BOLD, 14));
        b.setPrefHeight(42);
        b.setStyle(
            "-fx-background-color: #1565c0;"
          + "-fx-text-fill: white;"
          + "-fx-background-radius: 8;"
          + "-fx-cursor: hand;"
          + "-fx-padding: 0 22 0 22;"
        );
        b.setOnMouseEntered(e -> b.setStyle(
            "-fx-background-color: #0d47a1;"
          + "-fx-text-fill: white;"
          + "-fx-background-radius: 8;"
          + "-fx-cursor: hand;"
          + "-fx-padding: 0 22 0 22;"));
        b.setOnMouseExited(e -> b.setStyle(
            "-fx-background-color: #1565c0;"
          + "-fx-text-fill: white;"
          + "-fx-background-radius: 8;"
          + "-fx-cursor: hand;"
          + "-fx-padding: 0 22 0 22;"));
        return b;
    }

    /** Light secondary button — dark text on light background. */
    private Button secondaryBtn(String text) {
        Button b = new Button(text);
        b.setFont(Font.font("System", FontWeight.BOLD, 13));
        b.setStyle(
            "-fx-background-color: #e3f2fd;"
          + "-fx-text-fill: #0d47a1;"
          + "-fx-background-radius: 6;"
          + "-fx-cursor: hand;"
          + "-fx-padding: 7 16 7 16;"
        );
        return b;
    }

    private String fieldStyle() {
        return "-fx-border-color: #1565c0;"
             + "-fx-border-radius: 6;"
             + "-fx-background-radius: 6;"
             + "-fx-padding: 8 10 8 10;"
             + "-fx-font-size: 14px;"
             + "-fx-text-fill: #000000;"
             + "-fx-background-color: #ffffff;";
    }

    @FunctionalInterface
    private interface StrExtractor<T> {
        String get(TableColumn.CellDataFeatures<T, String> d);
    }

    private <T> TableColumn<T, String> col(String title, StrExtractor<T> extractor) {
        TableColumn<T, String> c = new TableColumn<>(title);
        c.setCellValueFactory(d -> new SimpleStringProperty(extractor.get(d)));
        c.setStyle("-fx-font-size: 13px;");
        return c;
    }

    private void setMsg(Label lbl, String msg, boolean ok) {
        lbl.setText(msg);
        lbl.setTextFill(ok ? Color.web("#1b5e20") : Color.web("#b71c1c"));
    }

    private ScrollPane scroll(Node content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #ffffff; -fx-background: #ffffff;");
        return sp;
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle("Smart Canteen");
        a.showAndWait();
    }

    // kept to satisfy any GridPane callers — not used by receipt anymore
    private void addReceiptRow(GridPane grid, int row, String key, String value) {
        Label k = new Label(key + ":"); k.setFont(Font.font("System", FontWeight.BOLD, 13));
        k.setTextFill(Color.web("#000000"));
        Label v = new Label(value); v.setFont(Font.font("System", FontWeight.BOLD, 14));
        v.setTextFill(Color.web("#0d47a1"));
        grid.add(k, 0, row); grid.add(v, 1, row);
    }
}
