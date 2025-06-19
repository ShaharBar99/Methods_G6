package client;

import java.io.File;
import java.time.Year;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

public class ReportReservationController extends ViewReservationController {

	@FXML
	private LineChart<Number, Number> reservationsLineChart;

	@FXML
	private Button exportCsvButton;

	@FXML
	public void initialize() {
		super.initialize(); // Load base logic
		if (exportCsvButton != null) {
			exportCsvButton.setOnAction(e -> {
				try {
					FileChooser fileChooser = new FileChooser();
					fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
					File file = fileChooser.showSaveDialog(exportCsvButton.getScene().getWindow());
					Util.exportToCSV(reservationTable, file);
					showAlert("Exported table to reservations.csv!");
				} catch (Exception ex) {
					showAlert("Failed to export CSV: " + ex.getMessage());
				}
			});
		}
	}

	@Override
	protected void filterReservations() {
		super.filterReservations(); // Call base logic
		updateLineChart(); // Add chart functionality
	}

	private void updateLineChart() {
		reservationsLineChart.getData().clear();

		String selectedMonth = monthComboBox.getValue();
		Integer selectedYear = yearComboBox.getValue();

		if (selectedMonth == null || "All".equals(selectedMonth) || selectedYear == null || selectedYear == 0) {
			reservationsLineChart.setTitle("Select specific month and year to see daily reservation count");
			return;
		}

		int monthIndex = months.indexOf(selectedMonth); // January=1
		int daysInMonth = java.time.Month.of(monthIndex).length(Year.isLeap(selectedYear));

		int[] counts = new int[daysInMonth + 1]; // 1-based indexing
		filteredReservations.stream()
				.filter(res -> res.getDate().getMonthValue() == monthIndex && res.getDate().getYear() == selectedYear)
				.forEach(res -> counts[res.getDate().getDayOfMonth()]++);

		XYChart.Series<Number, Number> series = new XYChart.Series<>();
		series.setName("Reservations per Day");

		for (int day = 1; day <= daysInMonth; day++) {
			series.getData().add(new XYChart.Data<>(day, counts[day]));
		}
		reservationsLineChart.getData().add(series);
		reservationsLineChart.setTitle("Reservations in " + selectedMonth + " " + selectedYear);
	}

	private void showAlert(String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
		alert.showAndWait();
	}
}
