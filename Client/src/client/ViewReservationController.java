package client;

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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import logic.Reservation;
import logic.SendObject;

public class ViewReservationController extends Controller {

	@FXML
	protected TableView<Reservation> reservationTable;

	@FXML
	protected TableColumn<Reservation, Integer> colSubscriberId;

	@FXML
	protected TableColumn<Reservation, Integer> colSpotId;

	@FXML
	protected TableColumn<Reservation, LocalDate> colDate;

	@FXML
	protected TableColumn<Reservation, String> colStartTime;

	@FXML
	protected TableColumn<Reservation, String> colEndTime;

	@FXML
	protected Button sortByDateButton;

	@FXML
	protected Button sortBySubscriberIdButton;

	@FXML
	protected ComboBox<String> monthComboBox;

	@FXML
	protected ComboBox<Integer> yearComboBox;

	protected List<Reservation> allReservations = new ArrayList<>();
	protected List<Reservation> filteredReservations = new ArrayList<>();

	protected final List<String> months = Arrays.asList("All", "January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December");
	protected final List<Integer> years = IntStream.rangeClosed(2020, 2030).boxed()
			.collect(Collectors.toCollection(ArrayList::new));

	private Runnable backHandler;

	@FXML
	public void initialize() {
		years.add(0, 0); // Add "All" as first year

		// Setup table columns
		colSubscriberId.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().getSubscriberId()).asObject());
		colSpotId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSpot()).asObject());
		colDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()));
		colStartTime.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStartTime()));
		colEndTime.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEndTime()));

		// Setup ComboBoxes
		monthComboBox.getItems().setAll(months);
		monthComboBox.getSelectionModel().selectFirst(); // All
		yearComboBox.getItems().setAll(years);
		setupYearComboBox();

		monthComboBox.setOnAction(e -> filterReservations());
		yearComboBox.setOnAction(e -> filterReservations());

		if (sortByDateButton != null)
			sortByDateButton.setOnAction(e -> sortByDate());
		if (sortBySubscriberIdButton != null)
			sortBySubscriberIdButton.setOnAction(e -> sortBySubscriberId());
	}

	protected void setupYearComboBox() {
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
		yearComboBox.getSelectionModel().selectFirst(); // 0 = All
	}

	public void setReservations(List<Reservation> reservations) {
		this.allReservations = reservations;
		filterReservations();
	}

	protected void filterReservations() {
		String selectedMonth = monthComboBox.getValue();
		Integer selectedYear = yearComboBox.getValue();

		filteredReservations = allReservations.stream().filter(res -> {
			boolean monthOk = true, yearOk = true;
			if (selectedMonth != null && !"All".equals(selectedMonth)) {
				int monthIndex = months.indexOf(selectedMonth);
				monthOk = res.getDate().getMonthValue() == monthIndex;
			}
			if (selectedYear != null && selectedYear != 0) {
				yearOk = res.getDate().getYear() == selectedYear;
			}
			return monthOk && yearOk;
		}).collect(Collectors.toList());

		reservationTable.getItems().setAll(filteredReservations);
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
			if (((SendObject<?>) msg).getObj() instanceof List<?>) {
				List<?> updated = (List<?>) ((SendObject<?>) msg).getObj();
				if (!updated.isEmpty() && updated.get(0) instanceof Reservation) {
					Platform.runLater(() -> setReservations((List<Reservation>) updated));
				}
			}
		}
	}
}