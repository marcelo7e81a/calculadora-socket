package model;

import java.io.PrintStream;

import sockets.Tipo;

public class Cliente {

	private String id;
	private PrintStream printStream;
	private Tipo tipo;
	
	public Cliente () {
		
	}
	
	public Cliente (PrintStream printStream, Tipo tipo) {
		this.printStream = printStream;
		this.tipo = tipo;
	}
	
	public Cliente (String id, PrintStream printStream, Tipo tipo) {
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
