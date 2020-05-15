package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;

public class MainViewController implements Initializable {

	// Cria as variavel que serao linkadas ao menu item no scene builder
	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction()");
	}

	@FXML // metodos event handler para cada menu
	public void onMenuItemDepartmentAction() {
		// metodo criado temporariamente.
		// usar uma expressão lambida como parametro.
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}

	// a view about nao tem nada para executar 
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
	}

	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
		// Cria um objeto do tipo Vbox
			VBox newVBox = loader.load();
		// como mostrar a view na janela principal
			Scene mainScene = Main.getMainScene();
		// pega o primeiro elemetno da minha view principal o scrollPane. getRoot
		// temos que fazer um casting do getroot para scrollPane
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			T controller = loader.getController();
			initializingAction.accept(controller);
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error Loading View", e.getMessage(), AlertType.ERROR);
		}
	}
}
