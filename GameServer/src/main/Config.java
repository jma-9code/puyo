package main;

import java.io.FileInputStream;
import java.util.Properties;

public class Config {
	public static int SOCKET_CONNECTION_TIMEOUT = 60000;
	public static int TCP_PORT = 5776;
	public static int DB_PORT = 3306;
	public static boolean DB_USE = false;
	public static String DB_HOST = "localhost";
	public static String DB_BASE = "puyo";
	public static String DB_USER = "puyo";
	public static String DB_PASSWORD = "obelix";
	public static int BAN_MAX_ERREUR = 3;
	
	/**
	 * Initialisation static de la classe. Permet de charger les attribus classe
	 * e partir du fichier Config. En cas d'erreur on laisse les parametres par
	 * defaut.
	 */
	static {
		FileInputStream configFile = null;
		try {
			configFile = new FileInputStream("Config.xml");
			Properties props = new Properties();
			props.load(configFile);
			configFile.close();
			TCP_PORT = props.getProperty("TCP_PORT") != null ? Integer.parseInt(props.getProperty("TCP_PORT")) : TCP_PORT;
			DB_PORT = props.getProperty("DB_PORT") != null ? Integer.parseInt(props.getProperty("DB_PORT")) : DB_PORT;
			DB_HOST = props.getProperty("DB_HOST") != null ? props.getProperty("DB_HOST") : DB_HOST;
			DB_BASE = props.getProperty("DB_BASE") != null ? props.getProperty("DB_BASE") : DB_BASE;
			DB_USER = props.getProperty("DB_USER") != null ? props.getProperty("DB_USER") : DB_USER;
			DB_PASSWORD = props.getProperty("DB_PASSWORD") != null ? props.getProperty("DB_PASSWORD") : DB_PASSWORD;
		}
		catch (Exception e) {
			System.out.println("Lancement de la configuration par defaut");
		}
	}
}
