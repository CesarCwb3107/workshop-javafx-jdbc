package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;

public class DepartmentListController implements Initializable {
	
	//Criar referencias para os Componentes/controles no FXML Scene Buider.
	
	@FXML
	private Button btNew;
	
	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
	}
	
	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
		
	}

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

}
