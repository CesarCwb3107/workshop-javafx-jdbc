package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	// Exceção para validar um formulário.
	// uma mensagem especifica para cada campo
	// Usamos o Map (Pares de Chave , Valor)
	
	// Coleção de erros.
	private Map<String, String> errors = new HashMap<>();
	
	public ValidationException (String msg) {
		super(msg);
	}
	
	public Map<String, String> getErrors() {
		return errors;
	}
	
	public void addError(String fieldName, String errorMessage) {
		errors.put(fieldName, errorMessage);
	}
	
}
