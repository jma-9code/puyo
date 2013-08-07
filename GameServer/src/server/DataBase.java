package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import main.Config;

import commun.EGames;

public class DataBase {
	
	public Connection conn = null;
	public String url;
	private final static Object lock = new Object();
	private static DataBase instance = new DataBase(); 
	
	public static DataBase get(){
		return instance;
	}

	private DataBase() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			url = "jdbc:mysql://" + Config.DB_HOST + ":" + Config.DB_PORT + "/" + Config.DB_BASE;
			connect();
		} catch (final ClassNotFoundException e) {
			System.err.println("JDBC :: Librairie absente");
			System.exit(-1);
		} catch (final Exception e) {
			System.err.println("JDBC :: Erreur de connexion JDBC : " + e.getMessage().split("\n")[0]);
			System.exit(-1);
		}
	}
	
	private void connect() {
		if (!Config.DB_USE){
			return;
		}
		try {
			if(conn != null) {
				conn.close();
			}
			conn = DriverManager.getConnection(url, Config.DB_USER, Config.DB_PASSWORD);
			System.out.println("BDD OK");
		} catch (final Exception e) {
			System.err.println("JDBC :: Erreur de connexion JDBC : " + e.getMessage().split("\n")[0]);
			System.exit(-1);
		}
	}

	private void controlConn() {
		try {
			if(!conn.isValid(1)) {
				connect();
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Permet r�cup�rer un compte par son identifiant
	 * 
	 * @param nom Nom du compte
	 * @return R�sultat de la requ�te
	 */
	public ResultSet getUser(final String nom) {
		final String sql = "SELECT * FROM fos_user WHERE LOWER(username_canonical)='"+nom.toLowerCase()+"'";

		PreparedStatement req;
		ResultSet res;
		try {
			controlConn();
			req = conn.prepareStatement(sql);

			res = req.executeQuery();

			return res.next() ? res : null;
		} catch (final SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Connection getConn() {
		return conn;
	}

	public void updatePlayerScore(String name, int newScore, EGames game){
		synchronized (lock) {
			if (!Config.DB_USE){
				return;
			}
			String sql = "UPDATE "+ game +" SET score = " + newScore + " WHERE id_user = (SELECT id FROM fos_user WHERE LOWER(username_canonical)='"+ name.toLowerCase() +"')";
			Statement req;
			try {
				controlConn();
				req = conn.createStatement();
				int nb = req.executeUpdate(sql.toString());
				System.out.println(nb + " Update score : " + name + " -> " + newScore);
			} catch (final SQLException e) {
				System.err.println("Erreur SQL : " + e.getMessage());
			}
		}
	}
	
	public int getPlayerScore(String name, EGames game){
		synchronized (lock) {
			if (!Config.DB_USE){
				return 1000;
			}
			
			String sql = "SELECT score FROM "+ game + " WHERE id_user = (SELECT id FROM fos_user WHERE LOWER(username_canonical)='"+ name.toLowerCase() +"')";
			ResultSet results = null;
			try {
				controlConn();
				Statement stmt = conn.createStatement();
				results = stmt.executeQuery(sql);
				results.next();
				return results.getInt("score");
			} catch (final Exception e) {
				System.err.println("Erreur SQL : " + e.getMessage());
			}
			return -1;
		}
	}
	
	public void updatePlayerWIN(String name, EGames game){
		synchronized (lock) {
			if (!Config.DB_USE){
				return;
			}
			String sql = "UPDATE "+ game +" SET win = 'win+1' WHERE id_user = (SELECT id FROM fos_user WHERE LOWER(username_canonical)='"+ name.toLowerCase() +"')";
			Statement req;
			try {
				controlConn();
				req = conn.createStatement();
				int nb = req.executeUpdate(sql.toString());
			} catch (final SQLException e) {
				System.err.println("Erreur SQL : " + e.getMessage());
			}
		}
	}
	
	public void updatePlayerLOSE(String name, EGames game){
		synchronized (lock) {
			if (!Config.DB_USE){
				return;
			}
			String sql = "UPDATE "+ game +" SET lose = 'lose+1' WHERE id_user = (SELECT id FROM fos_user WHERE LOWER(username_canonical)='"+ name.toLowerCase() +"')";
			Statement req;
			try {
				controlConn();
				req = conn.createStatement();
				int nb = req.executeUpdate(sql.toString());
			} catch (final SQLException e) {
				System.err.println("Erreur SQL : " + e.getMessage());
			}
		}
	}
	
	public int numRows(final ResultSet res) {
		int size = 0, row;

		try {
			// On m�morise le curseur
			if ( res.isBeforeFirst() ) {
				row = 0;
			} else if ( res.isAfterLast() ) {
				row = -1;
			} else {
				row = res.getRow();
			}

			// On va � la fin et on m�morise le rowid
			res.last();
			size = res.getRow();

			// On remet le curseur
			if ( row == 0 ) {
				res.beforeFirst();
			} else if ( row == -1 ) {
				res.afterLast();
			} else {
				res.absolute(row);
			}

		} catch (final SQLException e) {
			System.err.println("ERREUR " + e.getMessage());
		}

		return size;
	}

}
