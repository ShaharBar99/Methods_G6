package client;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import logic.Role;
import logic.subscriber;
import logic.user;

public class ReportSubscriberController extends Controller {

    @FXML
    private TableView<subscriber> subscriberTable;

    @FXML
    private TableColumn<subscriber, Integer> colSubscriberId;
    @FXML
    private TableColumn<subscriber, String> colName;
    @FXML
    private TableColumn<subscriber, String> colEmail;
    @FXML
    private TableColumn<subscriber, String> colPhone;
    @FXML
    private TableColumn<subscriber, String> colTag;
    @FXML
    private TableColumn<subscriber, Role> colRole;

    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Button sortBySubscriberIdButton;
    @FXML
    private Button exportCsvButton;
    @FXML
    private Button backButton;

    private List<subscriber> allSubscribers = new ArrayList<>();
    private List<subscriber> filteredSubscribers = new ArrayList<>();
    private final List<String> roles = Arrays.asList("All", "ADMIN", "SUBSCRIBER", "ATTENDANT");

    private Runnable backHandler;

    @FXML
    public void initialize() {
        // Set up table columns
        colSubscriberId.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colName.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getName()));
        colEmail.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getEmail()));
        colPhone.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getPhone()));
        colTag.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getTag()));
        colRole.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getRole()));

        // Setup roleComboBox
        roleComboBox.getItems().setAll(roles);
        roleComboBox.getSelectionModel().selectFirst(); // "All"
        roleComboBox.setOnAction(e -> filterSubscribers());

        if (exportCsvButton != null) {
            exportCsvButton.setOnAction(e -> {
                try {
                    exportToCSV(subscriberTable, "subscribers.csv");
                    showAlert("Exported table to subscribers.csv!");
                } catch (Exception ex) {
                    showAlert("Failed to export CSV: " + ex.getMessage());
                }
            });
        }
        if (sortBySubscriberIdButton != null) {
            sortBySubscriberIdButton.setOnAction(e -> sortBySubscriberId());
        }
    }

    /** Called with all subscribers from server */
    public void setSubscribers(List<subscriber> subscribers) {
        this.allSubscribers = new ArrayList<>(subscribers);
        filterSubscribers(); // Always refresh filter!
    }

    private void filterSubscribers() {
        String selectedRole = roleComboBox.getValue();

        filteredSubscribers = allSubscribers.stream().filter(sub -> {
            if (selectedRole == null || selectedRole.equals("All"))
                return true;
            return sub.getRole().name().equals(selectedRole);
        }).collect(Collectors.toList());
        subscriberTable.getItems().setAll(filteredSubscribers);
    }

    private void sortBySubscriberId() {
        colSubscriberId.setSortType(TableColumn.SortType.ASCENDING);
        subscriberTable.getSortOrder().setAll(colSubscriberId);
        subscriberTable.sort();
    }

    public void handleServerMessage(Object msg) {
        if (msg instanceof List<?>) {
            List<?> updated = (List<?>) msg;
            if (!updated.isEmpty() && updated.get(0) instanceof Subscriber) {
                Platform.runLater(() -> setSubscribers((List<subscriber>) updated));
            }
        }
    }

    private void exportToCSV(TableView<?> table, String filename) throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Header
            for (int i = 0; i < table.getColumns().size(); i++) {
                TableColumn<?, ?> col = table.getColumns().get(i);
                writer.print(col.getText());
                if (i < table.getColumns().size() - 1)
                    writer.print(",");
            }
            writer.println();
            // Rows
            for (Object item : table.getItems()) {
                for (int i = 0; i < table.getColumns().size(); i++) {
                    TableColumn col = table.getColumns().get(i);
                    Object cell = col.getCellData(item);
                    String cellText = (cell != null ? cell.toString() : "");
                    cellText = cellText.replace("\"", "\"\"");
                    if (cellText.contains(",") || cellText.contains("\""))
                        cellText = "\"" + cellText + "\"";
                    writer.print(cellText);
                    if (i < table.getColumns().size() - 1)
                        writer.print(",");
                }
                writer.println();
            }
        }
    }

    public void setBackHandler(Runnable backHandler) {
        this.backHandler = backHandler;
    }

    @FXML
    private void handleBackButton() {
        if (backHandler != null) {
            backHandler.run();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
