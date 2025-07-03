package client;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.Parkingsession;
import logic.Role;
import logic.SendObject;
import logic.subscriber;

public class ViewSubscriberController extends Controller {

	@FXML
	protected TableView<subscriber> subscriberTable;

	@FXML
	protected TableColumn<subscriber, Integer> colSubscriberId;
	@FXML
	protected TableColumn<subscriber, String> colName;
	@FXML
	protected TableColumn<subscriber, String> colEmail;
	@FXML
	protected TableColumn<subscriber, String> colPhone;
	@FXML
	protected TableColumn<subscriber, String> colTag;
	@FXML
	protected TableColumn<subscriber, Role> colRole;

	@FXML
	protected ComboBox<String> roleComboBox;

	@FXML
	protected Button sortBySubscriberIdButton;
	@FXML
	protected Button backButton;

	protected List<subscriber> allSubscribers = new ArrayList<>();
	protected List<subscriber> filteredSubscribers = new ArrayList<>();
	protected final List<String> roles = Arrays.asList("All", "MANAGER", "SUBSCRIBER", "ATTENDANT");

	protected Runnable backHandler;
	
	@FXML
    private TextField subscriberIdField;

    @FXML
    private Button viewHistoryButton;

    @FXML
    private TableView<Parkingsession> historyTable;

    @FXML
    private TableColumn<Parkingsession, Integer> colSessionId;
    @FXML
    private TableColumn<Parkingsession, Integer> colSpotId;
    @FXML
    private TableColumn<Parkingsession, String> colInTime;
    @FXML
    private TableColumn<Parkingsession, String> colOutTime;
    @FXML
    private TableColumn<Parkingsession, String> colLate;

    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    
    private boolean responseReceived = false;
    private List<Parkingsession> historySessions;

    private final HistoryController controller = new HistoryController();

	@FXML
	public void initialize() {
		// Column setup
		colSubscriberId
				.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
		colName.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getName()));
		colEmail.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEmail()));
		colPhone.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPhone()));
		colTag.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTag()));
		colRole.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRole()));

		// Combo box
		roleComboBox.getItems().setAll(roles);
		roleComboBox.getSelectionModel().selectFirst();
		roleComboBox.setOnAction(e -> filterSubscribers());

		if (sortBySubscriberIdButton != null) {
			sortBySubscriberIdButton.setOnAction(e -> sortBySubscriberId());
		}
		
		// History table setup
		colSessionId.setCellValueFactory(new PropertyValueFactory<>("sessionId"));
        colSpotId.setCellValueFactory(new PropertyValueFactory<>("spotId"));
        colInTime.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() ->
            new SimpleDateFormat("dd/MM/yyyy HH:mm").format(cellData.getValue().getInTime())));
        colOutTime.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() ->
            new SimpleDateFormat("dd/MM/yyyy HH:mm").format(cellData.getValue().getOutTime())));
        colLate.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() ->
            cellData.getValue().isLate() ? "Yes" : "No"));
        viewHistoryButton.setOnAction(e -> fetchHistory());
	}
	
	private void fetchHistory() {
        String idText = subscriberIdField.getText().trim();
        if (idText.isEmpty()) {
            showAlert("Please enter a Subscriber ID");
            return;
        }

        try {
            int subscriberId = Integer.parseInt(idText);
            responseReceived = false;
            client.sendToServerSafely(new SendObject<Integer>("Get history", subscriberId));

            new Thread(() -> {
                try {
                    Util.waitForServerResponse(10000, () -> responseReceived);
                    List<Parkingsession> result = historySessions;
                    if (result != null) {
                        Platform.runLater(() -> {
                            populateHistoryTable(result);
                            populateBarChart(result);
                        });
                    } else {
                        Platform.runLater(() -> showAlert("No session data received."));
                    }
                } catch (Exception ex) {
                    Platform.runLater(() -> showAlert("Error: " + ex.getMessage()));
                }
            }).start();
        } catch (NumberFormatException e) {
            showAlert("Subscriber ID must be a number.");
        }
    }

    private void populateHistoryTable(List<Parkingsession> sessions) {
        ObservableList<Parkingsession> data = FXCollections.observableArrayList(sessions);
        historyTable.setItems(data);
    }

    private void populateBarChart(List<Parkingsession> sessions) {
        int[] monthly = new int[12];
        int currentYear = LocalDate.now().getYear();

        for (Parkingsession s : sessions) {
            LocalDate date = s.getInTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (date.getYear() == currentYear) {
                monthly[date.getMonthValue() - 1]++;
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < 12; i++) {
            series.getData().add(new XYChart.Data<>(Month.of(i + 1).name(), monthly[i]));
        }
        barChart.getData().clear();
        barChart.getData().add(series);
    }

	/** Can be reused in subclass */
	protected void filterSubscribers() {
		String selectedRole = roleComboBox.getValue();

		filteredSubscribers = allSubscribers.stream().filter(sub -> {
			if (selectedRole == null || selectedRole.equals("All"))
				return true;
			return sub.getRole().name().equals(selectedRole);
		}).collect(Collectors.toList());

		subscriberTable.getItems().setAll(filteredSubscribers);
	}

	protected void sortBySubscriberId() {
		colSubscriberId.setSortType(TableColumn.SortType.ASCENDING);
		subscriberTable.getSortOrder().setAll(colSubscriberId);
		subscriberTable.sort();
	}

	public void setSubscribers(List<subscriber> subscribers) {
		this.allSubscribers = subscribers;
		filterSubscribers();
	}

	@Override
	public void handleServerMessage(Object msg) {
	    if (msg instanceof SendObject<?>) {
	        SendObject<?> so = (SendObject<?>) msg;

	        // Handle subscriber list updates
	        if (so.getObj() instanceof List<?>) {
	            List<?> updated = (List<?>) so.getObj();
	            if (!updated.isEmpty() && updated.get(0) instanceof subscriber) {
	                Platform.runLater(() -> setSubscribers((List<subscriber>) updated));
	            }
	        }

	        // Handle history data per specific subscriber
	        if ("Parkingsession list of subscriber".equals(so.getObjectMessage())) {
	            Object obj = so.getObj();
	            if (obj instanceof List<?>) {
	                List<?> list = (List<?>) obj;
	                if (!list.isEmpty() && list.get(0) instanceof Parkingsession) {
	                    historySessions = (List<Parkingsession>) list;
	                    responseReceived = true;
	                }
	            }
	        }
	    }
	}


	protected void showAlert(String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
		alert.showAndWait();
	}
}
