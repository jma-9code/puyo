package server;

import java.net.InetAddress;
import java.util.Hashtable;

import main.Config;
import client.Client;

public class BanByIP {
	/**
	 * Liste des IP ayant �t� rejet� apr�s une erreur
	 */
	private static Hashtable<InetAddress, Integer> listeIp = new Hashtable<InetAddress, Integer>();;

	/**
	 * Incr�ment le degr� d'avertissement de l'ip du client
	 * 
	 * @param c Client incrimin�
	 */
	public static void addAvert(Client c) {
		int value = 0;
		if ( listeIp.containsKey(c.getInetAddress()) ) {
			value = listeIp.get(c.getInetAddress());
		}
		listeIp.put(c.getInetAddress(), value + 1);
	}

	/**
	 * Permet de savoir si une ip est bannie depuis un client
	 * 
	 * @param c Client
	 * @return Vrai/Faux
	 */
	public static boolean isBanned(Client c) {
		return isBanned(c.getInetAddress());
	}

	/**
	 * Permet de savoir si une ip est bannie
	 * 
	 * @param c IP
	 * @return Vrai/Faux
	 */
	public static boolean isBanned(InetAddress c) {
		return listeIp.containsKey(c) && listeIp.get(c) >= Config.BAN_MAX_ERREUR;
	}
}
