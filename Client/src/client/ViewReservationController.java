package client;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import logic.Reservation;
import logic.SendObject;

public class ViewReservationController extends Controller {
	// Controller for the Report screen

	@FXML
	private TableView<Reservation> reservationTable;

	@FXML
	private TableColumn<Reservation, Integer> colSubscriberId;

	@FXML
	private TableColumn<Reservation, Integer> colSpotId;

	@FXML
	private TableColumn<Reservation, LocalDate> colDate;

	@FXML
	private TableColumn<Reservation, String> colStartTime;

	@FXML
	private TableColumn<Reservation, String> colEndTime;

	@FXML
	private Button sortByDateButton;

	@FXML
	private Button sortBySubscriberIdButton;

	@FXML
	private ComboBox<String> monthComboBox;

	@FXML
	private ComboBox<Integer> yearComboBox;

	// Store all and filtered reservations
	private List<Reservation> allReservations = new ArrayList<>();
	private List<Reservation> filteredReservations = new ArrayList<>();

	private final List<String> months = Arrays.asList("All", "January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December");
	private final List<Integer> years = IntStream.rangeClosed(2020, 2030).boxed()
			.collect(Collectors.toCollection(ArrayList::new));

	private Runnable backHandler;

	@FXML
	public void initialize() {
		// Add "All" (0) as first value in years
		years.add(0, 0);

		// Setup columns
		colSubscriberId.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().getSubscriberId()).asObject());
		colSpotId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSpot()).asObject());
		colDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()));
		colStartTime.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStartTime()));
		colEndTime.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEndTime()));

		// Setup ComboBoxes
		monthComboBox.getItems().setAll(months);
		monthComboBox.getSelectionModel().selectFirst(); // "All"
		yearComboBox.getItems().setAll(years);
		yearComboBox.setCellFactory(cb -> new ListCell<Integer>() {
			@Override
			protected void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				setText(item == null ? "" : (item == 0 ? "All" : item.toString()));
			}
		});
		yearComboBox.setButtonCell(new ListCell<Integer>() {
			@Override
			protected void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				setText(item == null ? "" : (item == 0 ? "All" : item.toString()));
			}
		});
		yearComboBox.getSelectionModel().selectFirst(); // 0 ("All")

		// Filter on ComboBox selection
		monthComboBox.setOnAction(e -> filterReservations());
		yearComboBox.setOnAction(e -> filterReservations());
		if (sortByDateButton != null) {
			sortByDateButton.setOnAction(e -> sortByDate());
		}
		if (sortBySubscriberIdButton != null) {
			sortBySubscriberIdButton.setOnAction(e -> sortBySubscriberId());
		}
	}

	/** Called with all reservations from server */
	public void setReservations(List<Reservation> reservations) {
		this.allReservations = reservations;
		filterReservations(); // Always refresh filter!
	}

	// Called when the server sends a new list of reservations
	// This method updates the table view with the new data.
	// It also applies the current filters(month and year) to the new data.
	private void filterReservations() {
		String selectedMonth = monthComboBox.getValue();
		Integer selectedYear = yearComboBox.getValue();

		filteredReservations = allReservations.stream().filter(res -> {
			boolean monthOk = true, yearOk = true;
			// Filter by month
			if (selectedMonth != null && !"All".equals(selectedMonth)) {
				int monthIndex = months.indexOf(selectedMonth); // January=1
				monthOk = res.getDate().getMonthValue() == monthIndex;
			}
			// Filter by year
			if (selectedYear != null && selectedYear != 0) {
				yearOk = res.getDate().getYear() == selectedYear;
			}
			return monthOk && yearOk;
		}).collect(Collectors.toList());
		reservationTable.getItems().setAll(filteredReservations);
		updateLineChart(); // Update line chart with filtered data
	}

	private void sortByDate() {
		colDate.setSortType(TableColumn.SortType.ASCENDING);
		reservationTable.getSortOrder().setAll(colDate);
		reservationTable.sort();
	}

	private void sortBySubscriberId() {
		colSubscriberId.setSortType(TableColumn.SortType.ASCENDING);
		reservationTable.getSortOrder().setAll(colSubscriberId);
		reservationTable.sort();
	}

	public void handleServerMessage(Object msg) {
		if (msg instanceof SendObject<?>) {
			if (((SendObject) msg).getObj() instanceof List<?>) {
				List<?> updated = (List<?>) ((SendObject) msg).getObj();
				// Only accept lists of Reservation
				if (!updated.isEmpty() && updated.get(0) instanceof Reservation) {
					Platform.runLater(() -> setReservations((List<Reservation>) updated));
				}
			}
		}
	}

	// Update the line chart based on selected month and year
	private void updateLineChart() {
		// Only show when a specific month and year are selected
		String selectedMonth = monthComboBox.getValue();
		Integer selectedYear = yearComboBox.getValue();

		int monthIndex = months.indexOf(selectedMonth); // January=1
		int daysInMonth = java.time.Month.of(monthIndex).length(java.time.Year.isLeap(selectedYear));

		// Prepare daily counts
		int[] counts = new int[daysInMonth + 1]; // 1-based indexing

		filteredReservations.stream()
				.filter(res -> res.getDate().getMonthValue() == monthIndex && res.getDate().getYear() == selectedYear)
				.forEach(res -> {
					int day = res.getDate().getDayOfMonth();
					counts[day]++;
				});

		XYChart.Series<Number, Number> series = new XYChart.Series<>();
		series.setName("Reservations per Day");

		for (int day = 1; day <= daysInMonth; day++) {
			series.getData().add(new XYChart.Data<>(day, counts[day]));
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
