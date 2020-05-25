package gui;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
	}

	// A classe SellerListController implementa a Interfaca
	// gui.listener.DataChangeListener. Quando houver uma altera��o nos dados
	// usamos o metodo onDataChanged()
	@Override
	public void onDataChanged() {
		updateTableView();
	}

	@FXML
	private Button btNew;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}

	@FXML
	private TableView<Seller> tableViewSeller;

	@FXML
	private TableColumn<Seller, Integer> tableColumnId;

	@FXML
	private TableColumn<Seller, String> tableColumnName;

	@FXML
	TableColumn<Seller, Seller> tableColumnEDIT;

	@FXML
	TableColumn<Seller, Seller> tableColumnREMOVE;

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		// Para a tableview acompanhar o tamanho da janela
		// o getWindow pega uma refencia para a janela
		// a Window � uma super classe do stage.
		// entao para poder atribuir para o stage , temos que fazer um
		// downcasting
		Stage stage = (Stage) Main.getMainScene().getWindow();
		// entao fazemos um bind do tableViewDep.. com o stage..
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
	}

	// Criar uma dependencia entre o controller service
	// injetar uma dependencia sem ACOPLAMENTO FORTE com o SellerService
	// private SellerService service = new SellerService();
	// definir o metodo setSellerService(SellerService service)

	private SellerService service; // DepListService
	// Para carrgar o tableview
	private ObservableList<Seller> obsList; // DepListService

	// Inversao de controle. DepListService
	public void setSellerService(SellerService service) {
		this.service = service;
	}

	// este metodo tera que ser chamado no mainview controller
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
	//	try {
	//		FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
	//		Pane pane = loader.load();
	//		SellerFormController controller = loader.getController();
	//		controller.setSeller(obj);
	//		controller.setSellerService(new SellerService());
	//		controller.subscribeDataChangeListener(this);
	//		controller.updateFormData();
	//		Stage dialogStage = new Stage();
	//		dialogStage.setTitle("Enter Seller Data");
	//		dialogStage.setScene(new Scene(pane));
	//		dialogStage.setResizable(false);
	//		dialogStage.initOwner(parentStage);
	//		dialogStage.initModality(Modality.WINDOW_MODAL);
	//		dialogStage.showAndWait();
	//	} catch (IOException e) {
	//		Alerts.showAlert("IO Exception", "Error loading Dep Dialog View", e.getMessage(), AlertType.ERROR);
	//	}
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}
	
	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		if (result.get() == ButtonType.OK ) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Error Removing Object", "Removing Object", e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
