package sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import model.Auxiliar;
import model.Cliente;

public class Servidor {

	private int porta;
	private ServerSocket server;

	private Map<String, Auxiliar> auxiliaresMap;
	private Map<String, Cliente> usuariosMap;
	private List<Cliente> usuarios;

	// usado para escalonamento
	private CircularFifoQueue<Auxiliar> auxiliaresFila;
	private CircularFifoQueue<Auxiliar> auxiliaresEspeciaisFila;

	public static void main(String[] args) throws IOException {
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Digite a porta:");
		
		Integer i = sc.nextInt();
		
		new Servidor(i).run();
	}

	public Servidor(int port) {
		this.porta = port;
		this.usuarios = new ArrayList<>();
		this.usuariosMap = new HashMap<>();
		this.auxiliaresMap = new HashMap<>();
		this.auxiliaresFila = new CircularFifoQueue<>();
		this.auxiliaresEspeciaisFila = new CircularFifoQueue<>();
	}

	@SuppressWarnings("static-access")
	public void run() throws IOException {

		server = new ServerSocket(porta) {
			protected void finalize() throws IOException {
				this.close();
			}
		};

		System.out.println("# Servidor rodando IP " + InetAddress.getLocalHost().getHostAddress() + " Porta " + this.porta);
		System.out.println("# Aguardando conexoes.");

		while (true) {

			Socket client = server.accept();
			System.err.print("# Novo cliente conectado: " + client.getInetAddress().getLocalHost().getHostAddress());

			String tipo = new BufferedReader(new InputStreamReader(client.getInputStream())).readLine();
			String[] s = tipo.split(":");
			switch (s[0]) {
			case "USUARIO":
				System.err.print(", tipo: USUARIO");
				Cliente cliente = new Cliente(s[1], new PrintStream(client.getOutputStream()), Tipo.USUARIO);
				this.usuarios.add(cliente);
				this.usuariosMap.put(cliente.getId(), cliente);
				break;
			case "AUXILIAR":
				System.err.print(", tipo: AUXILIAR");
				Auxiliar auxiliar = new Auxiliar(s[1], new PrintStream(client.getOutputStream()), Tipo.AUXILIAR);
				// add na fila
				this.auxiliaresFila.add(auxiliar);
				// add referencia
				this.auxiliaresMap.put(auxiliar.getId(), auxiliar);
				break;
			case "AUXILIAR_ESPECIAL":
				System.err.print(", tipo: AUXILIAR_ESPECIAL");
				Auxiliar auxiliarEsp = new Auxiliar(s[1], new PrintStream(client.getOutputStream()), Tipo.AUXILIAR_ESPECIAL);
				// add na fila
				this.auxiliaresEspeciaisFila.add(auxiliarEsp);
				// add referencia
				this.auxiliaresMap.put(auxiliarEsp.getId(), auxiliarEsp);
				break;
			}

			System.out.println();

			// gera uma nova thread
			new Thread(new ClientHandler(this, client.getInputStream())).start();
		}
	}

	void enviarDados(String msg) {

		String[] parametros = msg.split(":");

		switch (Integer.parseInt(parametros[0])) {
		case 123: // calcular

			Auxiliar auxiliar = operacao(parametros[2]);

			if (auxiliar == null) {
				Cliente cliente = usuariosMap.get(parametros[1]);
				cliente.getPrintStream().println("Nehum servidor disponivel no momento!");
				break;
			}

			System.out.println("# Enviando parametros...");
			PrintStream ps = auxiliar.getPrintStream();
			ps.println(parametros[1] + ":" + parametros[2]);
			ps.flush();

			System.out.println("# Parametros enviados.");

			break;
		case 456: // resposta

			System.out.println("# Parametros de retorno recebidos.");
			Auxiliar aux = auxiliaresMap.get(parametros[1]);

			// adiciona servidor no final da fila
			// para uma nova requisicao
			addServidorNaFila(aux);

			// devolve os dados para o usuario
			System.out.println("# Enviando parametros de retorno para o cliente.");
			Cliente cliente = usuariosMap.get(parametros[2]);
			cliente.getPrintStream().println(parametros[3]);

			break;
		default:
			System.err.println("@ Erro!");
			break;
		}
	}

	private Auxiliar operacao(String tipo) {

		Auxiliar auxiliar = null;

		StringTokenizer stringTokenizer = new StringTokenizer(tipo);
		String raiz = stringTokenizer.nextToken();
		String operacao = stringTokenizer.nextToken();

		if (raiz.equals("sqrt")) {
			return auxiliaresEspeciaisFila.poll();
		}

		switch (operacao) {
		case "+":
		case "-":
		case "*":
		case "/":
			auxiliar = auxiliaresFila.poll();
			break;
		case "%":
		case "^":
			auxiliar = auxiliaresEspeciaisFila.poll();
			break;
		default:
			break;
		}

		return auxiliar;
	}
	
	private void addServidorNaFila(Auxiliar aux) {
		
		if (aux.getTipo() == Tipo.AUXILIAR) {
			auxiliaresFila.add(aux);
		} else {
			auxiliaresEspeciaisFila.add(aux);
		}
		
	}
}

class ClientHandler implements Runnable {

	private Servidor server;
	private InputStream client;

	public ClientHandler(Servidor server, InputStream client) {
		this.server = server;
		this.client = client;
	}

	@Override
	public void run() {

		String message;

		// recebe uma nova mensagem e a encaminha
		Scanner sc = new Scanner(this.client);

		while (sc.hasNextLine()) {
			message = sc.nextLine();
			server.enviarDados(message);
			System.out.println("# Aguardando conexoes.");
		}

		sc.close();
	}
}
