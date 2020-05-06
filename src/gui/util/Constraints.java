package gui.util;

import javafx.scene.control.TextField;

public class Constraints {

	// Expressao lambida (obs, oldValue, newValue) com 3 argumentos
	// obs -  observador referencia p/ o controle
	// oldValue o valor que o controle tinha antes de mexer
	// o newValue depois que mexeu
	// o valor e testado , se e diferente de null e se e um numero inteiro newValue.matches("\\d*")
	// expressão Regular ? estudar
	
	public static void setTextFieldInteger(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> { 
			if (newValue != null && !newValue.matches("\\d*")) {	 
				txt.setText(oldValue);								
			}														 
		});
	}

	public static void setTextFieldMaxLength(TextField txt, int max) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.length() > max) {
				txt.setText(oldValue);
			}
		});
	}

	public static void setTextFieldDouble(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*([\\.]\\d*)?")) {
				txt.setText(oldValue);
			}
		});
	}

}
