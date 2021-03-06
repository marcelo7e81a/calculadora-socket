package model;

import java.io.PrintStream;

import sockets.Tipo;

public class Auxiliar {

	private String id;
	private PrintStream printStream;
	private Tipo tipo;
	
	public Auxiliar () {
		
	}
	
	public Auxiliar (PrintStream printStream, Tipo tipo) {
		this.printStream = printStream;
		this.tipo = tipo;
	}
	
	public Auxiliar (String id, PrintStream printStream, Tipo tipo) {
		this.printStream = printStream;
		this.tipo = tipo;
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PrintStream getPrintStream() {
		return printStream;
	}
	public void setPrintStream(PrintStream printStream) {
		this.printStream = printStream;
	}
	public Tipo getTipo() {
		return tipo;
	}
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}
}
