package sockets;

import java.util.StringTokenizer;

public class Util {
	
	public static boolean validarEntrada(String entrada) {
		
		StringTokenizer token = new StringTokenizer(entrada);
		String operador = token.nextToken();
		String valor = token.nextToken();		
		
		if (ehExpresao(entrada)) {
			return true;
		} else if (operador.equals("sqrt") && ehExpresao2(valor)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean ehExpresao(String s){
		return s.matches("(\\d+([.]\\d+)?\\s{1}[\\+*/\\-\\^%]\\s{1}\\d+([.]\\d+)?)");
	}
	
	public static boolean ehExpresao2(String s) {
		return s.matches("(\\d+([.]\\d+)?)");
	}
	
	public static boolean ehNumero(String s) {
		return s.matches("\\d");
	}
	
	public static String dividir(Double opr1, Double opr2) {
		
		if (opr2 == 0.0f || opr2 == 0f) {
			return "@ Impossivel dividir por ZERO!";
		}
		
		return String.valueOf(opr1 / opr2);
	}
}
