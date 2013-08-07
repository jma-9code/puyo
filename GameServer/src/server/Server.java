package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import main.Config;
import client.Client;
import client.ClientManager;

public class Server {
	private boolean run = false;
	private ServerSocket socketTCP;
	private ExecutorService executor = Executors.newFixedThreadPool(50);
	
	static {
		if (Config.DB_USE){
			DataBase.get();
		}
	}

	public Server() {
		
	}

	public void start() {
		System.out.println("Lancement du serveur");
		run = true;
		try{
			socketTCP = new ServerSocket(Config.TCP_PORT);
			while ( run ) {
				Socket sock = null;
				try {
					sock = socketTCP.accept();
				} catch (IOException e) {
					run = false;
					return;
				}
				if (BanByIP.isBanned(sock.getInetAddress())) {
					System.err.println("IP " + sock.getInetAddress().getHostAddress() + " bannie, socket ignoree.");
				}else{
					Client c = new Client(this, sock);
					executor.execute(c);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void stop() {
		run = false;

		// Fermeture des sockets d'ecoute
		try {
			socketTCP.close();
		} catch (Exception e) {
		}

		ClientManager.get().removeClients();
		executor.shutdownNow();
	}	
}
