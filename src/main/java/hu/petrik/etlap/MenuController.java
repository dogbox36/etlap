package hu.petrik.etlap;

import hu.petrik.etlap.database.Meal;
import hu.petrik.etlap.database.MealCategory;
import hu.petrik.etlap.database.MenuDB;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuController extends Controller {

    @FXML
    private TabPane rootView;
    @FXML
    private ListView<MealCategory> categoryListView;
    @FXML
    private GridPane categoryGridPane;
    @FXML
    private Spinner<Integer> priceIncreasePercentSpinner;
    @FXML
    private Spinner<Integer> priceIncreaseMoneySpinner;
    @FXML
    private TableView<Meal> menuTable;
    @FXML
    private TableColumn<Meal, String> nameCol;
    @FXML
    private TableColumn<Meal, String> categoryCol;
    @FXML
    private TableColumn<Meal, Integer> priceCol;
    @FXML
    private TextArea descriptionField;

    private MenuDB menuDB;
    private List<MealCategory> selectedCategories;

    @FXML
    private void initialize() {
        priceIncreasePercentSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                0, 50, 0, 5));
        priceIncreaseMoneySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                0, 3000, 0, 50));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        selectedCategories = new ArrayList<>();

        Platform.runLater(() -> {
            try {
                menuDB = new MenuDB();
                loadData();
            } catch (SQLException e) {
                error("Nem siker??lt kapcsol??dni a szerverre", e.getMessage());
                Platform.exit();
            }
        });
    }

    private void loadCategoryFilters() throws SQLException {
        categoryGridPane.getChildren().clear();
        List<MealCategory> categories = menuDB.getCategories();
        int colCount = 0;
        int rowCount = 0;
        for (int i = 0; i < categories.size(); i++) {
            MealCategory category = categories.get(i);
            CheckBox checkBox = new CheckBox(category.getNev());

            checkBox.setOnAction(actionEvent -> {
                if (checkBox.isSelected()) {
                    selectedCategories.add(category);
                } else {
                    selectedCategories.remove(category);
                }
                try {
                    loadMenuTable();
                } catch (SQLException e) {
                    error("Hiba az adatb??zis bet??lt??se k??zben", e.getMessage());
                }
            });
            categoryGridPane.add(checkBox, colCount, rowCount);
            if (colCount == 4) {
                rowCount++;
                colCount = 0;
            } else {
                colCount++;
            }
        }
    }


    private void loadMenuTable() throws SQLException {
        menuTable.getItems().clear();
        descriptionField.setText("");
        List<Meal> menu = menuDB.getFilteredMenu(selectedCategories);
        for (Meal meal : menu) {
            menuTable.getItems().add(meal);
        }
    }

    private void loadCategoryTable() throws SQLException {
        categoryListView.getItems().clear();
        List<MealCategory> mealCategories = menuDB.getCategories();
        for (MealCategory category : mealCategories) {
            categoryListView.getItems().add(category);
        }
    }

    private void showStage(Stage stage) {
        rootView.setDisable(true);
        stage.show();
        stage.setOnHidden(event -> {
            loadData();
            rootView.setDisable(false);
        });
    }

    private void loadData() {
        try {
            loadMenuTable();
            loadCategoryFilters();
            loadCategoryTable();
        } catch (SQLException e) {
            error("Hiba az adatok bet??lt??se k??zben", e.getMessage());
            Platform.exit();
        }
    }

    @FXML
    public void insertBtnClick(ActionEvent actionEvent) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("meal-insert-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 300, 400);
            Stage stage = new Stage();
            stage.setTitle("??tel hozz??ad??s");
            stage.setScene(scene);
            showStage(stage);
        } catch (IOException e) {
            error("Nem sikert bet??lteni", e.getMessage());
        }

    }

    @FXML
    public void deleteBtnClick(ActionEvent actionEvent) {

        int selectedIndex = menuTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            warning("El??bb v??lassz ki egy elemet!");
            return;
        }

        Meal selected = menuTable.getSelectionModel().getSelectedItem();
        if (confirmation("Biztos t??r??lni akarod a(z) " + selected.getName() + " nev?? ??telt?", "")) {
            try {
                if (!menuDB.deleteMeal(selected.getId())) {
                    error("Sikertelen t??rl??s");
                    return;
                }
                loadData();
            } catch (SQLException e) {
                error("Hiba a t??rl??s k??zben", e.getMessage());
            }
        }
    }

    @FXML
    public void priceIncreasePercentClick(ActionEvent actionEvent) {
        int increment = priceIncreasePercentSpinner.getValueFactory().getValue();
        if (increment <= 0) {
            warning("Az ??r emel??shez adjon meg egy ??rv??nyes ??rt??ket!");
            return;
        }

        int selectedIndex = menuTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            if (!confirmation("Biztos szeretn??d n??velni az ??sszes term??k ??r??t?", "")) {
                return;
            }
            try {
                menuDB.increasePriceWithPercentage(selectedIndex, increment);
            } catch (SQLException e) {
                error("Nem siker??lt n??velni", e.getMessage());
            }
        } else {
            Meal selectedMeal = menuTable.getSelectionModel().getSelectedItem();
            if (!confirmation("Biztos szeretn??d n??velni a(z) " + selectedMeal.getName() + " ??r??t?", "")) {
                return;
            }
            try {
                menuDB.increasePriceWithPercentage(selectedMeal.getId(), increment);
            } catch (SQLException e) {
                error("Nem siker??lt n??velni", e.getMessage());
            }

        }
        loadData();
    }


    @FXML
    public void priceIncreaseMoneyClick(ActionEvent actionEvent) {
        int increment = priceIncreaseMoneySpinner.getValueFactory().getValue();
        if (increment <= 0) {
            warning("Az ??r emel??shez adjon meg egy ??rv??nyes ??rt??ket!");
            return;
        }

        int selectedIndex = menuTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            if (!confirmation("Biztos szeretn??d n??velni az ??sszes term??k ??r??t?", "")) {
                return;
            }
            try {
                menuDB.increasePriceWithMoney(selectedIndex, increment);
            } catch (SQLException e) {
                error("Nem siker??lt n??velni", e.getMessage());
            }
        } else {
            Meal selectedMeal = menuTable.getSelectionModel().getSelectedItem();
            if (!confirmation("Biztos szeretn??d n??velni a(z) " + selectedMeal.getName() + " ??r??t?", "")) {
                return;
            }
            try {
                menuDB.increasePriceWithMoney(selectedMeal.getId(), increment);
            } catch (SQLException e) {
                error("Nem siker??lt n??velni", e.getMessage());
            }
        }
        loadData();
    }


    @FXML
    public void mealSelectClick(MouseEvent mouseEvent) {
        int selected = menuTable.getSelectionModel().getSelectedIndex();
        if (selected == -1) {
            descriptionField.setText("");
        } else {
            Meal selectedMeal = menuTable.getSelectionModel().getSelectedItem();
            descriptionField.setText(selectedMeal.getDescription());
        }
    }

    public void insertCategoryClick(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("category-insert-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Kateg??ria hozz?? ad??s");
            showStage(stage);
        } catch (Exception e) {
            error("Hiba", e.getMessage());
        }
    }

    public void deleteCategoryClick(ActionEvent actionEvent) {
        int selectedIndex = categoryListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            warning("Nincs kiv??lasztva elem", "A t??rl??shez el??bb v??lasszon ki egy elemet");
            return;
        }

        MealCategory category = categoryListView.getSelectionModel().getSelectedItem();

        if (!confirmation("Biztos szeretn??d t??r??lni a(z) " + category.getNev() + " kateg??ri??t?", "")) {
            return;
        }

        try {
            if (menuDB.deleteCategory(category)) {
                information("Sikeres t??rl??s");
            } else {
                warning("Sikertelen t??rl??s");
            }
        } catch (SQLException e) {
            error("Sikertelen t??rl??s", e.getMessage());
        }
        loadData();
    }
}