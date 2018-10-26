package sockets;

import java.util.StringTokenizer;

public class Util {
	
	public static boolean entradaValida(String entrada) {
		
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
		return s.matches("(\\d+([.]\\d+)?\\s{1}[+*/-\\^%]\\s{1}\\d+([.]\\d+)?)");
	}
	
	public static boolean ehExpresao2(String s) {
		return s.matches("(\\d+([.]\\d+)?)");
	}
	
	public static boolean ehNumero(String s) {
		return s.matches("\\d");
	}
}
