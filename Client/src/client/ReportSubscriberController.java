package client;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

public class ReportSubscriberController extends ViewSubscriberController {
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

}
