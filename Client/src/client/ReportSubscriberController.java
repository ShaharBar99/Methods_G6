package client;

import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import logic.Role;

public class ReportSubscriberController extends ViewSubscriberController {

	@FXML
	private PieChart rolePieChart;
	@FXML
	private Button exportCsvButton;

	@Override
	public void initialize() {
		super.initialize(); // Setup from parent

		if (exportCsvButton != null) {
			exportCsvButton.setOnAction(e -> {
				try {
					FileChooser fileChooser = new FileChooser();
					fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
					File file = fileChooser.showSaveDialog(exportCsvButton.getScene().getWindow());
					Util.exportToCSV(subscriberTable, file);
					showAlert("Exported table to subscribers.csv!");
				} catch (Exception ex) {
					showAlert("Failed to export CSV: " + ex.getMessage());
				}
			});
		}
	}

	@Override
	protected void filterSubscribers() {
		super.filterSubscribers(); // run the default filter logic
		updatePieChart(); // then update the pie chart
	}

	private void updatePieChart() {
		long subscriberCount = filteredSubscribers.stream().filter(sub -> sub.getRole() == Role.SUBSCRIBER).count();
		long attendantCount = filteredSubscribers.stream().filter(sub -> sub.getRole() == Role.ATTENDANT).count();
		long managerCount = filteredSubscribers.stream().filter(sub -> sub.getRole() == Role.MANAGER).count();

		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
				new PieChart.Data("Subscriber", subscriberCount), new PieChart.Data("Attendant", attendantCount),
				new PieChart.Data("Manager", managerCount));
		rolePieChart.setData(pieChartData);
		rolePieChart.setTitle("Subscribers by Role");
	}

}
