package gui;

import java.io.IOException;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
	}

	// A classe DepartmentListController implementa a Interfaca
	// gui.listener.DataChangeListener. Quando houver uma alteração nos dados
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
		Department obj = new Department();
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	TableColumn<Department, Department> tableColumnEDIT;

	@FXML
	TableColumn<Department, Department> tableColumnREMOVE;

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		// Para a tableview acompanhar o tamanho da janela
		// o getWindow pega uma refencia para a janela
		// a Window é uma super classe do stage.
		// entao para poder atribuir para o stage , temos que fazer um
		// downcasting
		Stage stage = (Stage) Main.getMainScene().getWindow();
		// entao fazemos um bind do tableViewDep.. com o stage..
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}

	// Criar uma dependencia entre o controller service
	// injetar uma dependencia sem ACOPLAMENTO FORTE com o DepartmentService
	// private DepartmentService service = new DepartmentService();
	// definir o metodo setDepartmentService(DepartmentService service)

	private DepartmentService service; // DepListService
	// Para carrgar o tableview
	private ObservableList<Department> obsList; // DepListService

	// Inversao de controle. DepListService
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	// este metodo tera que ser chamado no mainview controller
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	// Quando criamos uma janela de dialogo .
	// precisamos infomar quem e o stage que crio a janela de dialogo
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			// pegar uma referencia para o controlador

			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			// Injetar o DepartmentService - Injecao de dependencia manual
			controller.setDepartmentService(new DepartmentService());
			// Inscrever o DepartamentListController
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			// um palco na frente do outro, temos que instanciar
			// outro stage
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department Data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading Dep Dialog View", e.getMessage(), AlertType.ERROR);
		}
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Department obj, boolean empty) {
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
	
	private void removeEntity(Department obj) {
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
