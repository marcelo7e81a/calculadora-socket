package sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.UUID;

public class AuxiliarEspecial {

	private String host;
	private int port;

	public static void main(String[] args) throws UnknownHostException, IOException {
		new AuxiliarEspecial("127.0.0.1", 12345).run();
	}

	public AuxiliarEspecial(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void run() throws UnknownHostException, IOException {

		String id = UUID.randomUUID().toString();
		System.out.println("id: " + id);

		// conecta ao servidor
		@SuppressWarnings("resource")
		Socket client = new Socket(host, port);

		System.out.println("# Auxiliar Especial conectado ao servidor!");

		new Thread(new AuxiliarEspecialHandler(id, client.getInputStream(), client.getOutputStream())).start();
		PrintStream output = new PrintStream(client.getOutputStream());
		output.println(Tipo.AUXILIAR_ESPECIAL + ":" + id);
		output.flush();
	}
}

class AuxiliarEspecialHandler implements Runnable {

	private String id;
	private InputStream server;
	private OutputStream output;

	public AuxiliarEspecialHandler(String id, InputStream server, OutputStream output) {
		this.server = server;
		this.output = output;
		this.id = id;
	}

	@Override
	public void run() {

		Scanner s = new Scanner(this.server);
		PrintStream printStream = new PrintStream(output);

		while (s.hasNextLine()) {

			System.out.println("-> Recebendo dados...");
			String msg = s.nextLine();

			if (!msg.isEmpty()) {

				String[] parametros = msg.split(":");
				System.out.println("# Dados recebidos.");

				/*
				 * primeiro parametro e o tipo de operacao (calculo = 123 |
				 * resposta = 456) segundo parametro e o ID do servidor auxiliar
				 * terceiro parametro ID do cliente que fez a requisicao quarto
				 * parametro e a resposta
				 */
				printStream.println("456:" + id + ":" + parametros[0] + ":" + calcular(parametros));
				printStream.flush();
				
				System.out.println("Resposta envianda.");
			}
		}

		printStream.close();
		s.close();
	}

	private String calcular(String[] parametros) {

		System.out.println("-> Calculando...");

		String resultado = "";

		StringTokenizer stringTokenizer = new StringTokenizer(parametros[1]);
		String s = stringTokenizer.nextToken();
		if (s.equals("sqrt")) {
			System.out.println("# Enviando resposta.");
			return String.valueOf(Math.sqrt(Double.valueOf(stringTokenizer.nextToken())));
		}

		Double opr1 = Double.valueOf(s);
		String operacao = stringTokenizer.nextToken();
		Double opr2 = Double.valueOf(stringTokenizer.nextToken());

		switch (operacao) {
		case "^":
			resultado = String.valueOf(Math.pow(opr1, opr2));
			break;
		case "%":
			resultado = String.valueOf((opr1 * opr2) / 100);
			break;
		default:
			System.out.println("@ Erro!");
			break;
		}

		System.out.println("# Enviando resposta.");

		return resultado;
	}
}
