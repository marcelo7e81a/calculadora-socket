package sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.UUID;

public class Auxiliar {

	private String host;
	private int port;

	public static void main(String[] args) {

		Auxiliar auxiliar = new Auxiliar("127.0.0.1", 12345);

		try {
			auxiliar.run();
		} catch (ConnectException e) {
			System.err.println("Servidor principal nao encontrado!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Auxiliar(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void run() throws UnknownHostException, IOException {

		// conecta ao servidor
		@SuppressWarnings("resource")
		Socket client = new Socket(host, port);

		String id = UUID.randomUUID().toString();
		System.out.println("id: " + id);

		System.out.println("Auxiliar conectado ao servidor!");

		new Thread(new AuxiliarHandler(id, client.getInputStream(), client.getOutputStream())).start();
		PrintStream output = new PrintStream(client.getOutputStream());
		output.println(Tipo.AUXILIAR + ":" + id);
		output.flush();
	}
}

class AuxiliarHandler implements Runnable {

	private String id;
	private InputStream server;
	private OutputStream output;

	public AuxiliarHandler(String id, InputStream server, OutputStream output) {
		this.server = server;
		this.output = output;
		this.id = id;
	}

	@Override
	public void run() {

		Scanner s = new Scanner(this.server);
		PrintStream printStream = new PrintStream(output);

		while (s.hasNextLine()) {

			System.out.println("Recebendo dados...");
			String msg = s.nextLine();

			if (!msg.isEmpty()) {

				String[] parametros = msg.split(":");
				System.out.println("Dados recebidos.");

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

		System.out.println("Calculando...");

		String resultado = "";

		StringTokenizer stringTokenizer = new StringTokenizer(parametros[1]);
		Double opr1 = Double.valueOf(stringTokenizer.nextToken());
		String operacao = stringTokenizer.nextToken();
		Double opr2 = Double.valueOf(stringTokenizer.nextToken());

		switch (operacao) {
		case "+":
			resultado = String.valueOf(opr1 + opr2);
			break;
		case "-":
			resultado = String.valueOf(opr1 - opr2);
			break;
		case "*":
			resultado = String.valueOf(opr1 * opr2);
			break;
		case "/":
			
			if (opr2 == 0.0f) {
				resultado = "Impossivel dividir por ZERO!";
				break;
			}
			
			resultado = String.valueOf(opr1 / opr2);
			break;
		default:
			System.out.println("@ Erro!");
			break;
		}

		System.out.println("Enviando resposta.");

		return resultado;
	}
}
