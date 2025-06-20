package client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

	public void handleServerMessage(Object msg) {
		if (msg instanceof SendObject<?>) {
			if (((SendObject<?>) msg).getObj() instanceof List<?>) {
				List<?> updated = (List<?>) ((SendObject<?>) msg).getObj();
				if (!updated.isEmpty() && updated.get(0) instanceof subscriber) {
					Platform.runLater(() -> setSubscribers((List<subscriber>) updated));
				}
			}
		}
	}

	public void setBackHandler(Runnable backHandler) {
		this.backHandler = backHandler;
	}

	@FXML
	protected void handleBackButton() {
		if (backHandler != null) {
			backHandler.run();
		}
	}

	protected void showAlert(String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
		alert.showAndWait();
	}
}
