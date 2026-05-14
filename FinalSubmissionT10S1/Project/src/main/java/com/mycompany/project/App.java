package com.mycompany.project;
//=============================== Team 10 Sec 01 =======================
//================================= Authors ============================
//1. Bavly Maged Naguib         2200385
//2. Mokhtar Khalil Rafaat      2200552
//3. Omar Wessam Farouk         2200220
//4. Philopateer Hany Nashaat   2200151
//5. Remon Hanna Nage Ayad      2200393

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class App extends Application {

    // ─── Colour palette ───────────────────────────────────────────────────────
    private static final String C_BG       = "#0d1117";   // page background
    private static final String C_SURFACE  = "#161b22";   // card / panel
    private static final String C_BORDER   = "#30363d";   // subtle borders
    private static final String C_ACCENT   = "#238636";   // green – available / confirm
    private static final String C_RED      = "#da3633";   // red  – rented / danger
    private static final String C_BLUE     = "#1f6feb";   // blue – primary action
    private static final String C_MUTED    = "#8b949e";   // secondary text
    private static final String C_TEXT     = "#e6edf3";   // primary text
    private static final String C_GOLD     = "#d29922";   // highlight / sort

    // ─── Shared state ─────────────────────────────────────────────────────────
    private final RentalManager rentalManager = new RentalManager();
    private final ObservableList<VehicleRow> masterData =
            FXCollections.observableArrayList();

    // ══════════════════════════════════════════════════════════════════════════
    //  JavaFX property wrapper – one row in a TableView
    // ══════════════════════════════════════════════════════════════════════════
    public static class VehicleRow {
        private final SimpleStringProperty id;
        private final SimpleStringProperty type;
        private final SimpleStringProperty brand;
        private final SimpleStringProperty status;
        private final SimpleDoubleProperty dailyRate;
        private final Vehicle vehicle;

        public VehicleRow(Vehicle v) {
            this.vehicle   = v;
            this.id        = new SimpleStringProperty(v.getId());
            this.type      = new SimpleStringProperty(v.getClass().getSimpleName());
            this.brand     = new SimpleStringProperty(v.getBrand());
            this.status    = new SimpleStringProperty(
                    v.isAvailable() ? "Available" : "Rented");
            this.dailyRate = new SimpleDoubleProperty(v.calculateRentalRate(1));
        }

        public void refresh() {
            status.set(vehicle.isAvailable() ? "Available" : "Rented");
        }

        // Getters required by PropertyValueFactory
        public String getId()        { return id.get(); }
        public String getType()      { return type.get(); }
        public String getBrand()     { return brand.get(); }
        public String getStatus()    { return status.get(); }
        public double getDailyRate() { return dailyRate.get(); }
        public Vehicle getVehicle()  { return vehicle; }

        public SimpleStringProperty idProperty()        { return id; }
        public SimpleStringProperty typeProperty()      { return type; }
        public SimpleStringProperty brandProperty()     { return brand; }
        public SimpleStringProperty statusProperty()    { return status; }
        public SimpleDoubleProperty dailyRateProperty() { return dailyRate; }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Lifecycle
    // ══════════════════════════════════════════════════════════════════════════
    @Override
    public void start(Stage stage) {
        // Seed demo fleet
        rentalManager.addVehicle(new Car("C-001", "Toyota",   4));
        rentalManager.addVehicle(new Car("C-002", "BMW",      2));
        rentalManager.addVehicle(new Car("C-003", "Mercedes", 4));
        rentalManager.addVehicle(new Bike("B-001", "Yamaha",  true));
        rentalManager.addVehicle(new Bike("B-002", "Ducati",  false));
        rentalManager.addVehicle(new Van("V-001", "Ford",   1200.0));
        rentalManager.addVehicle(new Van("V-002", "Nissan",  900.0));
        rebuildMasterData();

        stage.setTitle("The Lone Ranger Rental System");
        stage.setMinWidth(860);
        stage.setMinHeight(580);
        showRoleSelection(stage);
        stage.show();
    }

    // Sync masterData with rentalManager inventory
    private void rebuildMasterData() {
        masterData.clear();
        for (Vehicle v : rentalManager.getInventory()) {
            masterData.add(new VehicleRow(v));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SCREEN 1 – Role selection
    // ══════════════════════════════════════════════════════════════════════════
    private void showRoleSelection(Stage stage) {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(0);
        root.setStyle("-fx-background-color:" + C_BG + ";");

        // ── Header ──
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(64, 40, 32, 40));

        Label logo = new Label("The Lone Ranger Rental Agency");
        logo.setStyle("-fx-font-family:'Courier New'; -fx-font-size:52px;"
                + "-fx-font-weight:bold; -fx-text-fill:" + C_BLUE + ";");

        Label title = new Label("Your Trip... Our Wheels");
        title.setStyle("-fx-font-size:22px; -fx-font-weight:bold;"
                + "-fx-text-fill:" + C_TEXT + ";");

        Label sub = new Label("Select your role to get started");
        sub.setStyle("-fx-font-size:14px; -fx-text-fill:" + C_MUTED + ";");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:" + C_BLUE + ";");
        sep.setPrefWidth(320);
        sep.setMaxWidth(320);

        header.getChildren().addAll(logo, title, sub, sep);

        // ── Role cards ──
        HBox cards = new HBox(28);
        cards.setAlignment(Pos.CENTER);
        cards.setPadding(new Insets(32, 40, 48, 40));

        VBox managerCard = roleCard("⚙", "Manager",
                "Manage fleet inventory\nAdd / sort vehicles\nMonitor rental status",
                C_BLUE,
                () -> showManagerView(stage));

        VBox customerCard = roleCard("🚘", "Customer",
                "Browse available vehicles\nBook & pay instantly\nTrack rental cost",
                C_ACCENT,
                () -> showCustomerView(stage));

        cards.getChildren().addAll(managerCard, customerCard);

        root.getChildren().addAll(header, cards);
        stage.setScene(new Scene(root, 860, 560));
    }

    private VBox roleCard(String icon, String title, String desc,
                          String accent, Runnable action) {
        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36, 44, 36, 44));
        card.setPrefWidth(280);

        String baseStyle =
                "-fx-background-color:" + C_SURFACE + ";"
                + "-fx-background-radius:12;"
                + "-fx-border-color:" + accent + ";"
                + "-fx-border-width:1.5;"
                + "-fx-border-radius:12;"
                + "-fx-cursor:hand;";
        String hoverStyle =
                "-fx-background-color:" + C_BORDER + ";"
                + "-fx-background-radius:12;"
                + "-fx-border-color:" + accent + ";"
                + "-fx-border-width:2;"
                + "-fx-border-radius:12;"
                + "-fx-cursor:hand;"
                + "-fx-effect:dropshadow(gaussian," + accent + ",18,0.25,0,0);";

        card.setStyle(baseStyle);
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e  -> card.setStyle(baseStyle));
        card.setOnMouseClicked(e -> action.run());

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size:42px;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size:20px; -fx-font-weight:bold;"
                + "-fx-text-fill:" + C_TEXT + ";");

        Label descLabel = new Label(desc);
        descLabel.setStyle("-fx-font-size:13px; -fx-text-fill:" + C_MUTED + ";"
                + "-fx-text-alignment:center;");
        descLabel.setTextAlignment(TextAlignment.CENTER);

        Button btn = styledButton("Enter as " + title, accent);
        btn.setOnAction(e -> action.run());

        card.getChildren().addAll(iconLabel, titleLabel, descLabel, btn);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SCREEN 2 – Manager view
    // ══════════════════════════════════════════════════════════════════════════
    private void showManagerView(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + C_BG + ";");
        root.setTop(navBar("Manager Dashboard", "⚙", stage));

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color:" + C_BG + ";");

        Tab fleetTab = new Tab("  📋  Fleet Inventory  ");
        fleetTab.setContent(buildFleetPane());

        Tab addTab = new Tab("  ➕  Add Vehicle  ");
        addTab.setContent(buildAddVehiclePane());

        tabs.getTabs().addAll(fleetTab, addTab);
        root.setCenter(tabs);

        Scene scene = new Scene(root, 960, 680);
        applyTabStyle(scene);
        stage.setScene(scene);
    }

    // ── Fleet inventory pane ──
    private VBox buildFleetPane() {
        VBox pane = new VBox(20);
        pane.setPadding(new Insets(26));
        pane.setStyle("-fx-background-color:" + C_BG + ";");

        HBox statsRow = new HBox(16);

        Label totalVal  = statValue("0", C_BLUE);
        Label availVal  = statValue("0", C_ACCENT);
        Label rentedVal = statValue("0", C_RED);

        statsRow.getChildren().addAll(
                statCard("Total Vehicles", totalVal,  C_BLUE),
                statCard("Available",      availVal,  C_ACCENT),
                statCard("Rented Out",     rentedVal, C_RED));

        TableView<VehicleRow> table = buildInventoryTable(masterData, true);
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox actions = new HBox(12);

        Button sortBtn = styledButton("Sort by Brand", C_GOLD);
        sortBtn.setOnAction(e -> {
            rentalManager.sortInventory();
            rebuildMasterData();
            refreshStats(totalVal, availVal, rentedVal);
        });

        Button refreshBtn = styledButton("Refresh", C_BLUE);
        refreshBtn.setOnAction(e -> {
            rebuildMasterData();
            refreshStats(totalVal, availVal, rentedVal);
        });

        actions.getChildren().addAll(sortBtn, refreshBtn);

        refreshStats(totalVal, availVal, rentedVal);

        pane.getChildren().addAll(statsRow, actions, table);
        return pane;
    }

    private void refreshStats(Label total, Label avail, Label rented) {
        int t  = rentalManager.getInventory().size();
        long a = rentalManager.getInventory().stream()
                .filter(Vehicle::isAvailable).count();
        total.setText(String.valueOf(t));
        avail.setText(String.valueOf(a));
        rented.setText(String.valueOf(t - a));
    }

    // ── Add vehicle pane ──
    private ScrollPane buildAddVehiclePane() {
        VBox pane = new VBox(22);
        pane.setPadding(new Insets(30, 36, 30, 36));
        pane.setStyle("-fx-background-color:" + C_BG + ";");
        pane.setMaxWidth(620);

        Label heading = sectionHeading("Add New Vehicle to Fleet");

        ToggleGroup typeGroup = new ToggleGroup();
        HBox typeRow = new HBox(12);
        RadioButton rbCar  = styledRadio("🚗  Car",  typeGroup);
        RadioButton rbBike = styledRadio("🏍  Bike", typeGroup);
        RadioButton rbVan  = styledRadio("🚐  Van",  typeGroup);
        rbCar.setSelected(true);
        typeRow.getChildren().addAll(rbCar, rbBike, rbVan);

        TextField idField    = styledField("e.g. C-004");
        TextField brandField = styledField("e.g. Honda");

        // Car
        TextField doorsField = styledField("e.g. 4");
        VBox carBox = labelledField("Number of Doors", doorsField);

        // Bike
        CheckBox helmetBox = new CheckBox("Helmet included");
        helmetBox.setStyle("-fx-text-fill:" + C_TEXT + "; -fx-font-size:14px;");
        VBox bikeBox = new VBox(8, formLabel("Helmet Included?"), helmetBox);

        // Van
        TextField capacityField = styledField("e.g. 1500.0");
        VBox vanBox = labelledField("Capacity (Kg.)", capacityField);

        VBox dynamicBox = new VBox(carBox);
        typeGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            dynamicBox.getChildren().clear();
            if      (n == rbCar)  dynamicBox.getChildren().add(carBox);
            else if (n == rbBike) dynamicBox.getChildren().add(bikeBox);
            else                  dynamicBox.getChildren().add(vanBox);
        });

        Label status = new Label("");
        status.setWrapText(true);
        status.setStyle("-fx-font-size:13px;");

        Button addBtn = styledButton("Add Vehicle to Fleet", C_ACCENT);
        addBtn.setPrefWidth(240);
        addBtn.setOnAction(e -> {
            String vid   = idField.getText().trim();
            String brand = brandField.getText().trim();

            if (vid.isEmpty() || brand.isEmpty()) {
                setStatus(status, "⚠  Vehicle ID and Brand are required.", C_GOLD);
                return;
            }
            for (Vehicle v : rentalManager.getInventory()) {
                if (v.getId().equalsIgnoreCase(vid)) {
                    setStatus(status,
                            "⚠  Vehicle ID \"" + vid + "\" already exists.", C_GOLD);
                    return;
                }
            }
            Vehicle newV;
            try {
                if (rbCar.isSelected()) {
                    int doors = Integer.parseInt(doorsField.getText().trim());
                    newV = new Car(vid, brand, doors);
                } else if (rbBike.isSelected()) {
                    newV = new Bike(vid, brand, helmetBox.isSelected());
                } else {
                    double cap = Double.parseDouble(capacityField.getText().trim());
                    newV = new Van(vid, brand, cap);
                }
            } catch (NumberFormatException ex) {
                setStatus(status, "⚠  Please enter valid numeric values.", C_GOLD);
                return;
            }

            rentalManager.addVehicle(newV);
            rebuildMasterData();

            setStatus(status, "✓  " + newV.getClass().getSimpleName()
                    + " \"" + brand + "\" added to fleet.", C_ACCENT);

            idField.clear(); brandField.clear();
            doorsField.clear(); capacityField.clear();
            helmetBox.setSelected(false);
        });

        pane.getChildren().addAll(
                heading,
                formLabel("Vehicle Type"), typeRow,
                labelledField("Vehicle ID",  idField),
                labelledField("Brand",       brandField),
                dynamicBox,
                addBtn, status);

        ScrollPane scroll = new ScrollPane(pane);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:" + C_BG
                + "; -fx-background:" + C_BG + ";");
        return scroll;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SCREEN 3 – Customer view
    // ══════════════════════════════════════════════════════════════════════════
    private void showCustomerView(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + C_BG + ";");
        root.setTop(navBar("Customer Portal", "🚘", stage));

        ScrollPane scroll = new ScrollPane(buildCustomerContent());
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:" + C_BG
                + "; -fx-background:" + C_BG + ";");

        root.setCenter(scroll);
        stage.setScene(new Scene(root, 960, 720));
    }

    // ── MODIFIED: all vehicles shown; rented vehicles stay and show status ──
    private VBox buildCustomerContent() {
        VBox pane = new VBox(24);
        pane.setPadding(new Insets(26));
        pane.setStyle("-fx-background-color:" + C_BG + ";");

        // ── User details card ──
        VBox userCard = surfaceCard();
        Label userHeading = sectionHeading("Your Details");

        HBox userRow = new HBox(20);
        TextField nameField    = styledField("Full name");
        TextField licenseField = styledField("License Code");
        HBox.setHgrow(nameField,    Priority.ALWAYS);
        HBox.setHgrow(licenseField, Priority.ALWAYS);
        userRow.getChildren().addAll(
                labelledField("Full Name",       nameField),
                labelledField("License Code",  licenseField));

        userCard.getChildren().addAll(userHeading, userRow);

        // ── Full fleet table (status column visible so user sees what's rented) ──
        Label fleetLabel = sectionHeading("All Vehicles");

        // Use masterData directly — rented vehicles remain after booking
        TableView<VehicleRow> vehicleTable = buildInventoryTable(masterData, true);
        vehicleTable.setPrefHeight(210);
        vehicleTable.setMaxHeight(210);

        // ── Booking card ──
        VBox bookingCard = surfaceCard();
        Label bookingHeading = sectionHeading("Confirm Your Booking");

        HBox bookingRow = new HBox(20);
        TextField daysField = styledField("Number of days (e.g. 3)");
        HBox.setHgrow(daysField, Priority.ALWAYS);
        bookingRow.getChildren().add(labelledField("Rental Duration", daysField));

        Label costLabel = new Label(
                "Select a vehicle and enter rental days to see cost estimate");
        costLabel.setStyle("-fx-font-size:13px; -fx-text-fill:" + C_MUTED + ";");
        costLabel.setWrapText(true);

        Label rentalStatus = new Label("");
        rentalStatus.setWrapText(true);
        rentalStatus.setStyle("-fx-font-size:13px;");

        // Live cost preview — warns immediately if a rented vehicle is selected
        Runnable calcCost = () -> {
            VehicleRow sel = vehicleTable.getSelectionModel().getSelectedItem();
            if (sel == null) return;

            // If the vehicle is already rented, show a warning instead of a price
            if (!sel.getVehicle().isAvailable()) {
                costLabel.setText("✗  This vehicle is currently rented out "
                        + "— please select an available one.");
                costLabel.setStyle("-fx-font-size:13px; -fx-font-weight:bold;"
                        + "-fx-text-fill:" + C_RED + ";");
                return;
            }

            try {
                int d = Integer.parseInt(daysField.getText().trim());
                if (d > 0) {
                    double cost = sel.getVehicle().calculateRentalRate(d);
                    costLabel.setText("💰  Estimated Total: $"
                            + String.format("%.2f", cost)
                            + "  (" + sel.getType() + " · " + sel.getBrand()
                            + " · " + d + " day" + (d > 1 ? "s" : "") + ")");
                    costLabel.setStyle("-fx-font-size:15px; -fx-font-weight:bold;"
                            + "-fx-text-fill:" + C_ACCENT + ";");
                }
            } catch (NumberFormatException ignored) {
                costLabel.setText(
                        "Select a vehicle and enter rental days to see cost estimate");
                costLabel.setStyle(
                        "-fx-font-size:13px; -fx-text-fill:" + C_MUTED + ";");
            }
        };

        vehicleTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, o, n) -> calcCost.run());
        daysField.textProperty()
                .addListener((obs, o, n) -> calcCost.run());

        // Confirm button
        Button confirmBtn = styledButton("Confirm Booking & Process Payment", C_ACCENT);
        confirmBtn.setPrefWidth(300);
        confirmBtn.setOnAction(e -> {
            String    name    = nameField.getText().trim();
            String    license = licenseField.getText().trim();
            VehicleRow sel    = vehicleTable.getSelectionModel().getSelectedItem();
            String    daysText= daysField.getText().trim();

            if (name.isEmpty() || license.isEmpty()) {
                setStatus(rentalStatus,
                        "⚠  Please enter your name and license code.", C_GOLD);
                return;
            }
            if (sel == null) {
                setStatus(rentalStatus,
                        "⚠  Please select a vehicle from the table.", C_GOLD);
                return;
            }
            try {
                int days = Integer.parseInt(daysText);
                if (days <= 0) throw new NumberFormatException();

                User    user    = new User(name, license);
                Vehicle vehicle = sel.getVehicle();
                double  cost    = vehicle.calculateRentalRate(days);

                Booking booking = new Booking(user, vehicle, days);

                // rentVehicle() throws VehicleUnavailableException
                // if vehicle.isAvailable() == false
                rentalManager.rentVehicle(vehicle, days);
                booking.processPayment(cost);

                // Vehicle stays in the table — just refresh its status to "Rented"
                sel.refresh();
                rebuildMasterData();

                setStatus(rentalStatus,
                        "✓  Booking confirmed! "
                        + vehicle.getClass().getSimpleName()
                        + " \"" + vehicle.getBrand() + "\" rented for "
                        + days + " day" + (days > 1 ? "s" : "")
                        + ".  Total charged: $" + String.format("%.2f", cost),
                        C_ACCENT);

                daysField.clear();
                costLabel.setText(
                        "Select a vehicle and enter rental days to see cost estimate");
                costLabel.setStyle(
                        "-fx-font-size:13px; -fx-text-fill:" + C_MUTED + ";");

            } catch (NumberFormatException ex) {
                setStatus(rentalStatus,
                        "⚠  Please enter a valid positive number of days.", C_GOLD);
            } catch (VehicleUnavailableException ex) {
                // Fires when the user selects a rented vehicle and clicks Confirm
                setStatus(rentalStatus, "✗  " + ex.getMessage(), C_RED);
            }
        });

        bookingCard.getChildren().addAll(
                bookingHeading, bookingRow, costLabel, confirmBtn, rentalStatus);

        pane.getChildren().addAll(userCard, fleetLabel, vehicleTable, bookingCard);
        return pane;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Shared: inventory TableView builder
    // ══════════════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    private TableView<VehicleRow> buildInventoryTable(
            ObservableList<VehicleRow> data, boolean showStatus) {

        TableView<VehicleRow> table = new TableView<>(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle(
                "-fx-background-color:" + C_SURFACE + ";"
                + "-fx-border-color:" + C_BORDER + ";"
                + "-fx-border-radius:8;"
                + "-fx-background-radius:8;"
                + "-fx-table-cell-border-color:transparent;");
        table.setPlaceholder(new Label("No vehicles to display"));

        TableColumn<VehicleRow, String> idCol   = col("Vehicle ID", "id",    110);
        TableColumn<VehicleRow, String> typeCol  = col("Type",       "type",   90);
        TableColumn<VehicleRow, String> brandCol = col("Brand",      "brand", 130);

        TableColumn<VehicleRow, Double> rateCol = new TableColumn<>("Daily Rate ($)");
        rateCol.setCellValueFactory(new PropertyValueFactory<>("dailyRate"));
        rateCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null
                        ? null : "$" + String.format("%.2f", item));
                setStyle("-fx-text-fill:" +"#ff9600"+ ";");
            }
        });
        rateCol.setPrefWidth(130);

        if (showStatus) {
            TableColumn<VehicleRow, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
            statusCol.setCellFactory(c -> new TableCell<>() {
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) { setText(null); setStyle(""); }
                    else {
                        setText(item);
                        setStyle("-fx-font-weight:bold; -fx-text-fill:"
                                + (item.equals("Available") ? C_ACCENT : C_RED) + ";");
                    }
                }
            });
            statusCol.setPrefWidth(100);
            table.getColumns().addAll(idCol, typeCol, brandCol, rateCol, statusCol);
        } else {
            table.getColumns().addAll(idCol, typeCol, brandCol, rateCol);
        }
        return table;
    }

    private static TableColumn<VehicleRow, String> col(
            String title, String prop, int prefWidth) {
        TableColumn<VehicleRow, String> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(prefWidth);
        c.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-text-fill:" + "#ff9600" + ";");
            }
        });
        return c;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Navigation bar
    // ══════════════════════════════════════════════════════════════════════════
    private HBox navBar(String title, String icon, Stage stage) {
        HBox nav = new HBox();
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setPadding(new Insets(14, 22, 14, 22));
        nav.setStyle(
                "-fx-background-color:" + C_SURFACE + ";"
                + "-fx-border-color:" + C_BORDER + ";"
                + "-fx-border-width:0 0 1 0;");

        Label lbl = new Label(icon + "  " + title);
        lbl.setStyle("-fx-font-size:17px; -fx-font-weight:bold;"
                + "-fx-text-fill:" + "#ff9600" + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button back = styledButton("← Back", C_MUTED);
        back.setStyle(
                "-fx-background-color:transparent;"
                + "-fx-text-fill:" + C_MUTED + ";"
                + "-fx-font-size:13px;"
                + "-fx-border-color:" + C_BORDER + ";"
                + "-fx-border-radius:6;"
                + "-fx-padding:6 16;"
                + "-fx-cursor:hand;");
        back.setOnMouseEntered(e -> back.setStyle(
                "-fx-background-color:" + C_BORDER + ";"
                + "-fx-text-fill:" + C_TEXT + ";"
                + "-fx-font-size:13px;"
                + "-fx-border-color:" + C_BORDER + ";"
                + "-fx-border-radius:6;"
                + "-fx-padding:6 16;"
                + "-fx-cursor:hand;"));
        back.setOnMouseExited(e -> back.setStyle(
                "-fx-background-color:transparent;"
                + "-fx-text-fill:" + C_MUTED + ";"
                + "-fx-font-size:13px;"
                + "-fx-border-color:" + C_BORDER + ";"
                + "-fx-border-radius:6;"
                + "-fx-padding:6 16;"
                + "-fx-cursor:hand;"));
        back.setOnAction(e -> showRoleSelection(stage));

        nav.getChildren().addAll(lbl, spacer, back);
        return nav;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UI helpers
    // ══════════════════════════════════════════════════════════════════════════
    private VBox surfaceCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20, 24, 20, 24));
        card.setStyle(
                "-fx-background-color:" + C_SURFACE + ";"
                + "-fx-background-radius:10;"
                + "-fx-border-color:" + C_BORDER + ";"
                + "-fx-border-radius:10;"
                + "-fx-border-width:1;");
        return card;
    }

    private HBox statCard(String label, Label valueLabel, String accent) {
        VBox card = new VBox(4);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 22, 14, 22));
        card.setPrefWidth(160);
        card.setStyle(
                "-fx-background-color:" + C_SURFACE + ";"
                + "-fx-background-radius:8;"
                + "-fx-border-color:" + accent + " transparent transparent transparent;"
                + "-fx-border-width:3 0 0 0;"
                + "-fx-border-radius:8;");

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size:12px; -fx-text-fill:" + C_MUTED + ";");
        card.getChildren().addAll(valueLabel, lbl);

        return new HBox(card);
    }

    private Label statValue(String val, String color) {
        Label l = new Label(val);
        l.setStyle("-fx-font-size:30px; -fx-font-weight:bold;"
                + "-fx-text-fill:" + color + ";");
        return l;
    }

    private Label sectionHeading(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:18px; -fx-font-weight:bold;"
                + "-fx-text-fill:" + "#ff9600" + ";");
        return l;
    }

    private Label formLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:12px; -fx-font-weight:bold;"
                + "-fx-text-fill:" + C_MUTED + "; -fx-padding:0 0 2 0;");
        return l;
    }

    private VBox labelledField(String label, javafx.scene.Node field) {
        VBox box = new VBox(6, formLabel(label), field);
        VBox.setVgrow(field, Priority.NEVER);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private TextField styledField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setMaxWidth(Double.MAX_VALUE);
        f.setStyle(
                "-fx-background-color:" + C_BG + ";"
                + "-fx-text-fill:" + C_TEXT + ";"
                + "-fx-prompt-text-fill:" + C_MUTED + ";"
                + "-fx-border-color:" + C_BORDER + ";"
                + "-fx-border-radius:6;"
                + "-fx-background-radius:6;"
                + "-fx-padding:9 13;"
                + "-fx-font-size:14px;");
        f.focusedProperty().addListener((obs, o, focused) -> f.setStyle(
                "-fx-background-color:" + C_BG + ";"
                + "-fx-text-fill:" + C_TEXT + ";"
                + "-fx-prompt-text-fill:" + C_MUTED + ";"
                + "-fx-border-color:" + (focused ? C_BLUE : C_BORDER) + ";"
                + "-fx-border-radius:6;"
                + "-fx-background-radius:6;"
                + "-fx-padding:9 13;"
                + "-fx-font-size:14px;"));
        return f;
    }

    private RadioButton styledRadio(String text, ToggleGroup group) {
        RadioButton rb = new RadioButton(text);
        rb.setToggleGroup(group);
        rb.setStyle("-fx-text-fill:" + C_TEXT + "; -fx-font-size:14px;");
        return rb;
    }

    private Button styledButton(String text, String accent) {
        Button btn = new Button(text);
        String base =
                "-fx-background-color:" + accent + ";"
                + "-fx-text-fill:white;"
                + "-fx-font-size:13px;"
                + "-fx-font-weight:bold;"
                + "-fx-padding:9 22;"
                + "-fx-background-radius:7;"
                + "-fx-cursor:hand;";
        String hover =
                "-fx-background-color:" + accent + ";"
                + "-fx-text-fill:white;"
                + "-fx-font-size:13px;"
                + "-fx-font-weight:bold;"
                + "-fx-padding:9 22;"
                + "-fx-background-radius:7;"
                + "-fx-cursor:hand;"
                + "-fx-effect:dropshadow(gaussian," + accent + ",12,0.3,0,2);";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e  -> btn.setStyle(base));
        return btn;
    }

    private void setStatus(Label lbl, String msg, String color) {
        lbl.setText(msg);
        lbl.setStyle("-fx-text-fill:" + color + "; -fx-font-size:13px;");
    }

    /** Inject minimal CSS so TabPane header looks dark-themed. */
    private void applyTabStyle(Scene scene) {
        scene.getStylesheets().add("data:text/css,"
                + ".tab-pane .tab-header-area .tab-header-background{"
                + "  -fx-background-color:" + C_SURFACE + ";}"
                + ".tab-pane .tab{"
                + "  -fx-background-color:" + C_BG + ";"
                + "  -fx-background-radius:0;}"
                + ".tab-pane .tab:selected{"
                + "  -fx-background-color:" + C_SURFACE + ";}"
                + ".tab-pane .tab .tab-label{"
                + "  -fx-text-fill:" + C_MUTED + ";"
                + "  -fx-font-size:13px;}"
                + ".tab-pane .tab:selected .tab-label{"
                + "  -fx-text-fill:" + C_TEXT + ";"
                + "  -fx-font-weight:bold;}"
                + ".table-view .column-header,"
                + ".table-view .column-header-background{"
                + "  -fx-background-color:" + C_BG + ";}"
                + ".table-view .column-header .label{"
                + "  -fx-text-fill:" + C_MUTED + ";"
                + "  -fx-font-size:12px;}"
                + ".table-row-cell{"
                + "  -fx-background-color:" + C_SURFACE + ";}"
                + ".table-row-cell:odd{"
                + "  -fx-background-color:derive(" + C_SURFACE + ",5%);}"
                + ".table-row-cell:selected{"
                + "  -fx-background-color:" + C_BORDER + ";}"
                + ".table-view .placeholder .label{"
                + "  -fx-text-fill:" + C_MUTED + ";}"
                + ".scroll-bar:vertical .thumb,"
                + ".scroll-bar:horizontal .thumb{"
                + "  -fx-background-color:" + C_BORDER + ";"
                + "  -fx-background-radius:4;}"
                + ".scroll-bar{"
                + "  -fx-background-color:" + C_BG + ";}"
                + ".radio-button .radio{"
                + "  -fx-border-color:" + C_BORDER + ";"
                + "  -fx-background-color:" + C_BG + ";}"
                + ".check-box .box{"
                + "  -fx-border-color:" + C_BORDER + ";"
                + "  -fx-background-color:" + C_BG + ";}");
    }

    // ══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) { launch(); }
}