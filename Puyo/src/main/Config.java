package main;

import java.util.Random;


public class Config {
	
	/*
	 * Config touches
	 */
	public static int J0_DOWN = 40;
	public static int J0_RIGHT = 39;
	public static int J0_LEFT = 37;
	public static int J0_ROT1 = 38;
	public static int J0_ROT2 = 17;

	/*
	public static int J1_DOWN = 83;
	public static int J1_RIGHT = 68;
	public static int J1_LEFT = 81;
	public static int J1_ROT1 = 90;
	public static int J1_ROT2 = 65;
	*/

	/*
	 * Config de vitesse tableau
	 */
	/**
	 * TEmps de descente lors de trou (par colonne)
	 */
	public static final int TIMETODOWN = 50;
	
	/**
	 * TEmps avant nouvelle piece
	 */
	public static final int TIMEBEFORENEWPIECE = 500;
	
	/**
	 * TEmps entre deux explosions succesives
	 */
	public static final int TIMEBETWEENCOMBO = 500;
	
	/**
	 * TEmps de flotement pour valider la descente d'une piece
	 */
	public static final int TIMEBEFOREVALIDSTROKE = 500;

	/*
	 * Config du tableau
	 */
	
	/**
	 * NB col du tableau
	 */
	public static int NB_COL = 6;

	/**
	 * NB ligne du tableau
	 */
	public static int NB_LINE = 13;

	

	/*
	 * Config clavier 
	 */
	
	public static int KEY_REPEAT_DELAY = 0;

	public static int KEY_TDP = 100;

	/*
	 * Config reseau
	 */
	
	public static String NAME = "DEFAULT" + new Random().nextInt();
	public static String PWD = "afe5fd4ff1a85caa390fd9f36005c6f785b58cb4";
	public static String ADRESS_SERV = /*"sddbserver.dyndns.org"*/ "127.0.0.1";
	public static int SERVER_PORT = 5776;
	
	/*
	 * Config IA
	 */
	public static int IASPEED = 300;
	
	
	/*
	 * Config Graphique
	 */
	
	/**
	 * Taille graphique d'un puyo
	 */
	public static int PUYO_SIZE = 25;

}
