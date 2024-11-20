package main.code;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;

public class Main extends Application {

    private DormManager dormManager = new DormManager();
    private static final String DATA_FILE = "dorm_data.ser";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Dorm Chore Tracker");

        // Login Screen
        VBox loginPane = new VBox(10);
        loginPane.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        Label welcomeLabel = new Label("Welcome to Dorm Chore Tracker");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button adminLogin = new Button("Admin");
        Button userLogin = new Button("User");

        loginPane.getChildren().addAll(welcomeLabel, adminLogin, userLogin);

        Scene loginScene = new Scene(loginPane, 400, 300);

        // Admin Login
        adminLogin.setOnAction(e -> {
            showAdminUI(primaryStage, loginScene);
        });

        // User Login
        userLogin.setOnAction(e -> {
            showUserUI(primaryStage, loginScene);
        });

        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private void showAdminUI(Stage stage, Scene loginScene) {
        BorderPane adminRoot = new BorderPane();

        // Top Section - Title
        Label title = new Label("Admin - Dorm Chore Tracker");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        adminRoot.setTop(title);
        BorderPane.setAlignment(title, javafx.geometry.Pos.CENTER);

        // Center Section - Building List and Dorm List
        VBox centerPane = new VBox(10);
        ListView<String> buildingListView = new ListView<>();
        ListView<String> dormListView = new ListView<>();
        centerPane.getChildren().addAll(new Label("Buildings:"), buildingListView, new Label("Dorms:"), dormListView);
        adminRoot.setCenter(centerPane);

        // Right Section - Admin Actions
        VBox actions = new VBox(10);
        actions.setStyle("-fx-padding: 10px;");
        Button addBuilding = new Button("Add Building");
        Button addDorm = new Button("Add Dorm");
        Button addTenant = new Button("Add Tenant");
        Button addChore = new Button("Add Chore");
        Button removeBuilding = new Button("Remove Building");
        Button removeDorm = new Button("Remove Dorm");
        Button removeTenant = new Button("Remove Tenant");
        Button removeChore = new Button("Remove Chore");
        Button viewDetails = new Button("View Details");
        Button saveData = new Button("Save Data");
        Button loadData = new Button("Load Data");
        Button switchUser = new Button("Switch User");

        actions.getChildren().addAll(addBuilding, addDorm, addTenant, addChore, removeBuilding, removeDorm, removeTenant, removeChore, viewDetails, saveData, loadData, switchUser);
        adminRoot.setRight(actions);

        // Update Dorm ListView when a Building is selected
        buildingListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            dormListView.getItems().clear();
            if (newVal != null) {
                Building building = dormManager.getBuildings().stream()
                        .filter(b -> b.toString().equals(newVal))
                        .findFirst()
                        .orElse(null);
                if (building != null) {
                    building.getDorms().forEach(dorm -> dormListView.getItems().add(dorm.toString()));
                }
            }
        });

        viewDetails.setOnAction(e -> {
            String selectedBuilding = buildingListView.getSelectionModel().getSelectedItem();
            String selectedDorm = dormListView.getSelectionModel().getSelectedItem();

            if (selectedBuilding != null) {
                Building building = dormManager.getBuildings().stream()
                        .filter(b -> b.toString().equals(selectedBuilding))
                        .findFirst()
                        .orElse(null);

                if (building != null) {
                    StringBuilder details = new StringBuilder("Building: " + building.getName() + "\n\n");

                    if (selectedDorm != null) {
                        Dorm dorm = building.getDorms().get(dormListView.getSelectionModel().getSelectedIndex());
                        if (dorm != null) {
                            details.append("Dorm Details:\n");
                            details.append("Tenants:\n");
                            for (Tenant tenant : dorm.getTenants()) {
                                details.append("- ").append(tenant).append("\n");
                            }
                            details.append("Chores:\n");
                            for (Chore chore : dorm.getChores()) {
                                details.append("- ").append(chore).append("\n"); // Includes completion status
                            }
                        } else {
                            details.append("Dorm not found.\n");
                        }
                    } else {
                        details.append("Dorms:\n");
                        for (Dorm dorm : building.getDorms()) {
                            details.append("- ").append(dorm).append("\n");
                        }
                    }

                    // Display the details in an alert
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, details.toString(), ButtonType.OK);
                    alert.setTitle("Building Details");
                    alert.setHeaderText("Details for: " + building.getName());
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Selected building not found.", ButtonType.OK);
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a building to view details.", ButtonType.OK);
                alert.showAndWait();
            }
        });


        // Add Building
        addBuilding.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("New Building");
            dialog.setTitle("Add Building");
            dialog.setHeaderText("Enter the building name:");
            dialog.setContentText("Name:");
            dialog.showAndWait().ifPresent(name -> {
                Building newBuilding = new Building(name);
                dormManager.add(newBuilding);
                buildingListView.getItems().add(newBuilding.toString());
            });
        });

        // Add Dorm
        addDorm.setOnAction(e -> {
            String selectedBuilding = buildingListView.getSelectionModel().getSelectedItem();
            if (selectedBuilding != null) {
                Building building = dormManager.getBuildings().stream()
                        .filter(b -> b.toString().equals(selectedBuilding))
                        .findFirst()
                        .orElse(null);
                if (building != null) {
                    TextInputDialog dialog = new TextInputDialog("New Dorm");
                    dialog.setTitle("Add Dorm");
                    dialog.setHeaderText("Enter the dorm name:");
                    dialog.setContentText("Name:");
                    dialog.showAndWait().ifPresent(name -> {
                        Dorm newDorm = new Dorm(5, name); // Example: maxTenants = 5
                        building.addDorm(newDorm);
                        dormListView.getItems().add(newDorm.toString());
                    });
                }
            }
        });

        // Add Tenant
        addTenant.setOnAction(e -> {
            String selectedBuilding = buildingListView.getSelectionModel().getSelectedItem();
            String selectedDorm = dormListView.getSelectionModel().getSelectedItem();
            if (selectedBuilding != null && selectedDorm != null) {
                Building building = dormManager.getBuildings().stream()
                        .filter(b -> b.toString().equals(selectedBuilding))
                        .findFirst()
                        .orElse(null);
                if (building != null) {
                    Dorm dorm = building.getDorms().get(dormListView.getSelectionModel().getSelectedIndex());
                    if (dorm != null) {
                        Dialog<Tenant> dialog = new Dialog<>();
                        dialog.setTitle("Add Tenant");
                        dialog.setHeaderText("Enter Tenant Details");

                        GridPane grid = new GridPane();
                        grid.setHgap(10);
                        grid.setVgap(10);

                        TextField nameField = new TextField();
                        nameField.setPromptText("Name");
                        TextField genderField = new TextField();
                        genderField.setPromptText("Gender");
                        TextField ageField = new TextField();
                        ageField.setPromptText("Age");

                        grid.add(new Label("Name:"), 0, 0);
                        grid.add(nameField, 1, 0);
                        grid.add(new Label("Gender:"), 0, 1);
                        grid.add(genderField, 1, 1);
                        grid.add(new Label("Age:"), 0, 2);
                        grid.add(ageField, 1, 2);

                        dialog.getDialogPane().setContent(grid);

                        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

                        dialog.setResultConverter(dialogButton -> {
                            if (dialogButton == okButtonType) {
                                String name = nameField.getText();
                                String gender = genderField.getText();
                                int age = Integer.parseInt(ageField.getText());
                                return new Tenant(name, gender, age);
                            }
                            return null;
                        });

                        dialog.showAndWait().ifPresent(tenant -> {
                            dorm.addTenant(tenant);
                            dormListView.getItems().set(dormListView.getSelectionModel().getSelectedIndex(), dorm.toString());
                        });
                    }
                }
            }
        });

        // Add Chore
        addChore.setOnAction(e -> {
            String selectedBuilding = buildingListView.getSelectionModel().getSelectedItem();
            String selectedDorm = dormListView.getSelectionModel().getSelectedItem();
            if (selectedBuilding != null && selectedDorm != null) {
                Building building = dormManager.getBuildings().stream()
                        .filter(b -> b.toString().equals(selectedBuilding))
                        .findFirst()
                        .orElse(null);
                if (building != null) {
                    Dorm dorm = building.getDorms().get(dormListView.getSelectionModel().getSelectedIndex());
                    if (dorm != null) {
                        Dialog<Chore> dialog = new Dialog<>();
                        dialog.setTitle("Add Chore");
                        dialog.setHeaderText("Enter Chore Details");

                        GridPane grid = new GridPane();
                        grid.setHgap(10);
                        grid.setVgap(10);

                        TextField descriptionField = new TextField();
                        descriptionField.setPromptText("Description");
                        TextField difficultyField = new TextField();
                        difficultyField.setPromptText("Difficulty (1-5)");

                        grid.add(new Label("Description:"), 0, 0);
                        grid.add(descriptionField, 1, 0);
                        grid.add(new Label("Difficulty:"), 0, 1);
                        grid.add(difficultyField, 1, 1);

                        dialog.getDialogPane().setContent(grid);

                        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

                        dialog.setResultConverter(dialogButton -> {
                            if (dialogButton == okButtonType) {
                                String description = descriptionField.getText();
                                int difficulty = Integer.parseInt(difficultyField.getText());
                                return new Chore(description, difficulty);
                            }
                            return null;
                        });

                        dialog.showAndWait().ifPresent(chore -> {
                            dorm.addChore(chore);
                        });
                    }
                }
            }
        });

        // Remove Building
        removeBuilding.setOnAction(e -> {
            String selectedBuilding = buildingListView.getSelectionModel().getSelectedItem();
            if (selectedBuilding != null) {
                Building building = dormManager.getBuildings().stream()
                        .filter(b -> b.toString().equals(selectedBuilding))
                        .findFirst()
                        .orElse(null);
                if (building != null) {
                    dormManager.getBuildings().remove(building);
                    buildingListView.getItems().remove(selectedBuilding);
                    dormListView.getItems().clear();
                }
            }
        });

        // Remove Dorm
        removeDorm.setOnAction(e -> {
            String selectedBuilding = buildingListView.getSelectionModel().getSelectedItem();
            String selectedDorm = dormListView.getSelectionModel().getSelectedItem();
            if (selectedBuilding != null && selectedDorm != null) {
                Building building = dormManager.getBuildings().stream()
                        .filter(b -> b.toString().equals(selectedBuilding))
                        .findFirst()
                        .orElse(null);
                if (building != null) {
                    Dorm dorm = building.getDorms().get(dormListView.getSelectionModel().getSelectedIndex());
                    if (dorm != null) {
                        building.getDorms().remove(dorm);
                        dormListView.getItems().remove(selectedDorm);
                    }
                }
            }
        });

        // Remove Tenant
        removeTenant.setOnAction(e -> {
            String selectedBuilding = buildingListView.getSelectionModel().getSelectedItem();
            String selectedDorm = dormListView.getSelectionModel().getSelectedItem();
            if (selectedBuilding != null && selectedDorm != null) {
                Building building = dormManager.getBuildings().stream()
                        .filter(b -> b.toString().equals(selectedBuilding))
                        .findFirst()
                        .orElse(null);
                if (building != null) {
                    Dorm dorm = building.getDorms().get(dormListView.getSelectionModel().getSelectedIndex());
                    if (dorm != null) {
                        List<Tenant> tenants = dorm.getTenants();
                        if (!tenants.isEmpty()) {
                            ChoiceDialog<Tenant> dialog = new ChoiceDialog<>(tenants.get(0), tenants);
                            dialog.setTitle("Remove Tenant");
                            dialog.setHeaderText("Select a tenant to remove:");
                            dialog.setContentText("Tenant:");
                            dialog.showAndWait().ifPresent(tenants::remove);
                            dormListView.getItems().set(dormListView.getSelectionModel().getSelectedIndex(), dorm.toString());
                        }
                    }
                }
            }
        });

        // Remove Chore
        removeChore.setOnAction(e -> {
            String selectedBuilding = buildingListView.getSelectionModel().getSelectedItem();
            String selectedDorm = dormListView.getSelectionModel().getSelectedItem();
            if (selectedBuilding != null && selectedDorm != null) {
                Building building = dormManager.getBuildings().stream()
                        .filter(b -> b.toString().equals(selectedBuilding))
                        .findFirst()
                        .orElse(null);
                if (building != null) {
                    Dorm dorm = building.getDorms().get(dormListView.getSelectionModel().getSelectedIndex());
                    if (dorm != null) {
                        List<Chore> chores = dorm.getChores();
                        if (!chores.isEmpty()) {
                            ChoiceDialog<Chore> dialog = new ChoiceDialog<>(chores.get(0), chores);
                            dialog.setTitle("Remove Chore");
                            dialog.setHeaderText("Select a chore to remove:");
                            dialog.setContentText("Chore:");
                            dialog.showAndWait().ifPresent(chores::remove);
                        }
                    }
                }
            }
        });

        // Save and Load Data
        saveData.setOnAction(e -> saveDormData());
        loadData.setOnAction(e -> {
            loadDormData();
            buildingListView.getItems().clear();
            dormManager.getBuildings().forEach(building -> buildingListView.getItems().add(building.toString()));
        });

        // Switch User
        switchUser.setOnAction(e -> {
            loadDormData(); // Reload updated data
            buildingListView.getItems().clear();
            dormManager.getBuildings().forEach(building -> buildingListView.getItems().add(building.toString()));
            stage.setScene(loginScene);
        });


        Scene adminScene = new Scene(adminRoot, 800, 600);
        stage.setScene(adminScene);
    }


    private void showUserUI(Stage stage, Scene loginScene) {
        BorderPane userRoot = new BorderPane();

        // Top Section - Title
        Label title = new Label("User - Dorm Chore Tracker");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        userRoot.setTop(title);
        BorderPane.setAlignment(title, javafx.geometry.Pos.CENTER);

        // Center Section - Building and Dorm List
        VBox centerPane = new VBox(10);
        ListView<String> buildingListView = new ListView<>();
        ListView<String> dormListView = new ListView<>();
        centerPane.getChildren().addAll(new Label("Buildings:"), buildingListView, new Label("Dorms:"), dormListView);
        userRoot.setCenter(centerPane);

        // Right Section - Actions
        VBox actions = new VBox(10);
        actions.setStyle("-fx-padding: 10px;");
        Button loadData = new Button("Load Data");
        Button viewDetails = new Button("View Details");
        Button switchUser = new Button("Switch User");
        Button markComplete = new Button("Mark Chore Complete");
        actions.getChildren().add(markComplete);

        actions.getChildren().addAll(loadData, viewDetails, switchUser);
        userRoot.setRight(actions);

        // Event Handlers
        loadData.setOnAction(e -> {
            loadDormData();
            buildingListView.getItems().clear();
            dormListView.getItems().clear();
            dormManager.getBuildings().forEach(building -> buildingListView.getItems().add(building.toString()));
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data loaded successfully.", ButtonType.OK);
            alert.showAndWait();
        });

        markComplete.setOnAction(e -> {
            String selectedBuilding = buildingListView.getSelectionModel().getSelectedItem();
            String selectedDorm = dormListView.getSelectionModel().getSelectedItem();
            if (selectedBuilding != null && selectedDorm != null) {
                Building building = dormManager.getBuildings().stream()
                        .filter(b -> b.toString().equals(selectedBuilding))
                        .findFirst()
                        .orElse(null);
                if (building != null) {
                    Dorm dorm = building.getDorms().get(dormListView.getSelectionModel().getSelectedIndex());
                    if (dorm != null) {
                        List<Chore> chores = dorm.getChores();
                        if (!chores.isEmpty()) {
                            ChoiceDialog<Chore> dialog = new ChoiceDialog<>(chores.get(0), chores);
                            dialog.setTitle("Mark Chore Complete");
                            dialog.setHeaderText("Select a chore to mark as complete:");
                            dialog.setContentText("Chore:");
                            dialog.showAndWait().ifPresent(chore -> {
                                chore.markComplete(); // Update the chore's status
                                saveDormData();       // Persist the changes
                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Chore marked as complete.", ButtonType.OK);
                                alert.showAndWait();
                            });
                        } else {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "No chores available to mark as complete.", ButtonType.OK);
                            alert.showAndWait();
                        }
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a dorm to mark a chore as complete.", ButtonType.OK);
                alert.showAndWait();
            }
        });


        buildingListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            dormListView.getItems().clear();
            if (newVal != null) {
                Building building = dormManager.getBuildings().stream()
                        .filter(b -> b.toString().equals(newVal))
                        .findFirst()
                        .orElse(null);
                if (building != null) {
                    building.getDorms().forEach(dorm -> dormListView.getItems().add("Dorm with " + dorm.getTenants().size() + " tenants"));
                }
            }
        });

        viewDetails.setOnAction(e -> {
            String selectedBuilding = buildingListView.getSelectionModel().getSelectedItem();
            String selectedDorm = dormListView.getSelectionModel().getSelectedItem();

            if (selectedBuilding != null) {
                Building building = dormManager.getBuildings().stream()
                        .filter(b -> b.toString().equals(selectedBuilding))
                        .findFirst()
                        .orElse(null);
                if (building != null) {
                    StringBuilder details = new StringBuilder("Building: " + building.getName() + "\n\n");

                    if (selectedDorm != null) {
                        Dorm dorm = building.getDorms().get(dormListView.getSelectionModel().getSelectedIndex());
                        if (dorm != null) {
                            details.append("Dorm Details:\n");
                            details.append("Tenants:\n");
                            for (Tenant tenant : dorm.getTenants()) {
                                details.append("- ").append(tenant).append("\n");
                            }
                            details.append("Chores:\n");
                            for (Chore chore : dorm.getChores()) {
                                details.append("- ").append(chore).append("\n");
                            }
                        }
                    } else {
                        details.append("Dorms:\n");
                        for (Dorm dorm : building.getDorms()) {
                            details.append("- ").append(dorm).append("\n");
                        }
                    }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, details.toString(), ButtonType.OK);
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a building to view details.", ButtonType.OK);
                alert.showAndWait();
            }
        });

        switchUser.setOnAction(e -> stage.setScene(loginScene));

        Scene userScene = new Scene(userRoot, 800, 600);
        stage.setScene(userScene);
    }



    private void saveDormData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(dormManager);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data saved successfully.", ButtonType.OK);
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save data.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void loadDormData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            dormManager = (DormManager) ois.readObject();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data loaded successfully.", ButtonType.OK);
            alert.showAndWait();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load data.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
