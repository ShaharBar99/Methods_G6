package client;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

public class ReportActiveSessionsController extends ViewActiveSessionsController {

	@FXML
	private Button exportCsvButton;

	@FXML
	@Override
	public void initialize() {
		super.initialize();

		if (exportCsvButton != null) {
			exportCsvButton.setOnAction(e -> {
				try {
					FileChooser fileChooser = new FileChooser();
					fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
					File file = fileChooser.showSaveDialog(exportCsvButton.getScene().getWindow());
					if (file != null) {
						Util.exportToCSV(sessionTable, file);
						showAlert("Exported table to " + file.getName());
					}
				} catch (Exception ex) {
					showAlert("Failed to export CSV: " + ex.getMessage());
				}
			});
		}
	}

}
