package sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;

public class Cliente {

	private String host;
	private int port;

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Digite o ip:");
		String ip = sc.nextLine();
		
		System.out.println("Digite a porta:");	
		Integer por = sc.nextInt();
		
		Cliente cliente = new Cliente(ip, por);
		
		try {
			cliente.run();
		} catch (ConnectException e) {
			System.err.println("@ Servidor principal nao encontrado!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Cliente(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void run() throws UnknownHostException, NoSuchElementException, IOException {
		
		// conecta com o servidor
		Socket client = new Socket(host, port);

		String id = UUID.randomUUID().toString();
		PrintStream output = new PrintStream(client.getOutputStream());
		System.out.println("# Meu ID: " + id);
		System.out.println("# Conectado ao servidor principal.");
		System.err.println("");
		System.err.println("################################################################################");
		System.err.println("#     A operacao deve ser da seguinte forma '2 + 2' SEPARANDO COM ESPACO       #");
		System.err.println("# Para raiz quadrada, SQRT seguido do valor SEPARANDO COM ESPACO ex: 'sqrt 25' #");
		System.err.println("################################################################################");
		System.err.println("");
		
		// cria uma nova thread
		new Thread(new ClienteHandler(client.getInputStream())).start();
		output.println(Tipo.USUARIO + ":" + id);
		
		String entrada = "";
		Scanner sc = new Scanner(System.in);
		System.out.println("# Digite a operacao: ");
		
		while (true) {
						
			entrada = sc.nextLine();
			
			if (entrada.toUpperCase().equals("SAIR")) {
				break;
			} else if (Util.validarEntrada(entrada)) {
				output.println("123:" + id + ":" + entrada);				
			} else {
				System.err.println("# Entrada invalida!");
				System.out.println("# Digite a operacao: ");
			}
		}
		
		output.close();
		sc.close();
		client.close();
		
		System.out.println("# Sessao encerrada.");
	}
}

class ClienteHandler implements Runnable {

	private InputStream server;

	public ClienteHandler(InputStream server) {
		this.server = server;
	}

	@Override
	public void run() {
		
		// recebe os dados do servidor e imprime na tela
		Scanner s = new Scanner(server);
		
		while (s.hasNextLine()) {			
			String retorno = s.nextLine();			
			System.out.println("# Resposta: " + retorno);
			System.out.println("# Digite a operacao: ");
		}
		
		s.close();
	}
}
