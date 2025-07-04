package client;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import logic.Parkingsession;

public class ReportSubscriberController extends ViewSubscriberController {
	@FXML
	private Button exportCsvButton;

	@FXML
	private BarChart<String, Number> barChart;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;

	@Override
	public void initialize() {
		super.initialize(); // Setup from parent

		if (exportCsvButton != null) {
			exportCsvButton.setOnAction(e -> {
				try {
					FileChooser fileChooser = new FileChooser();
					fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
					File file = fileChooser.showSaveDialog(exportCsvButton.getScene().getWindow());
					if (file != null) {
						Util.exportToCSV(subscriberTable, file);
						showAlert("Exported table to subscribers.csv!");
					}
				} catch (Exception ex) {
					showAlert("Failed to export CSV: " + ex.getMessage());
				}
			});
		}
	}

	@Override
	public void setHistorySessions(List<Parkingsession> history) {
		super.setHistorySessions(history);
		populateBarChart(history);
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

}