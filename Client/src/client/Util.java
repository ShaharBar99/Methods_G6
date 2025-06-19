package client;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.function.Supplier;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class Util {
	public static void exportToCSV(TableView<?> table, File file) throws Exception {
		try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
			// Header
			for (int i = 0; i < table.getColumns().size(); i++) {
				TableColumn<?, ?> col = table.getColumns().get(i);
				writer.print(col.getText());
				if (i < table.getColumns().size() - 1)
					writer.print(",");
			}
			writer.println();

			// Data rows
			for (Object item : table.getItems()) {
				for (int i = 0; i < table.getColumns().size(); i++) {
					TableColumn col = table.getColumns().get(i);
					Object cell = col.getCellData(item);
					String cellText = (cell != null ? cell.toString() : "").replace("\"", "\"\"");
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
	
	public static boolean waitForServerResponse(long timeoutMillis, Supplier<Boolean> responseCondition) throws Exception {
        long startTime = System.currentTimeMillis();
        while (!responseCondition.get()) {
            Thread.sleep(10);
            if (System.currentTimeMillis() - startTime > timeoutMillis) {
                throw new Exception("Server response timed out after " + timeoutMillis + " milliseconds");
            }
        }
        return true;
    }
}
