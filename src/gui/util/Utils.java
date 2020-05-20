package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	public static Stage currentStage(ActionEvent event) {
		// Downcasting para Node.
		// Window é uma super classe do Stage, tenho que fazer mais um
		// Downcasting para stage.
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}
	
	// Converter o valor do txtField para inteiro.
	public static Integer tryParseToInt(String str) {
		try {
			return Integer.parseInt(str);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}
}
