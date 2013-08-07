package model;

import java.io.Serializable;

/**
 * Cette classe permet d'obtenir les diff�rentes informations (score, stats ...)
 * sur le jeu du joueur.
 * 
 * @author Administrateur
 */
public class Stat implements Serializable {

	private static final long serialVersionUID = 1L;

	private int score;

	/**
	 * Combo maximal (nombre de bloc maximum �clat� en une action)
	 */
	private int combo_max;

	/**
	 * Explosion maximale (nombre de puyo maximum �clat� en une action)
	 */
	private int explo_max;

	/**
	 * Nombre de pierre maximal envoy� � l'adversaire
	 */
	private int pierre_max;

	/**
	 * Dernier combo
	 */
	private int combo_last;

	/**
	 * Derniere explosion
	 */
	private int explo_last;

	/**
	 * Dernier nombre de pierre envoy�es
	 */
	private int pierre_last;

	/**
	 * Combo total sur la partie
	 */
	private int combo_total;

	/**
	 * Explosion totale sur la partie
	 */
	private int explo_total;

	/**
	 * Nombre de pierre total envoy� sur la partie
	 */
	private int pierre_total;

	public Stat () {
		initStat();
	}

	/**
	 * Permet d'initialiser tout � 0.
	 */
	public void initStat() {
		score = 0;
		combo_max = 0;
		explo_max = 0;
		pierre_max = 0;
		combo_total = 0;
		explo_total = 0;
		pierre_total = 0;
		combo_last = 0;
		explo_last = 0;
		pierre_last = 0;
	}

	public int get_combo_last() {
		return combo_last;
	}

	public void set_combo_last(final int _combo_last) {
		this.combo_last = _combo_last;
	}

	public int get_explo_last() {
		return explo_last;
	}

	public void set_explo_last(final int _explo_last) {
		this.explo_last = _explo_last;
	}

	public int get_pierre_last() {
		return pierre_last;
	}

	public void set_pierre_last(final int _pierre_last) {
		this.pierre_last = _pierre_last;
	}

	public void set_score(final int _score) {
		this.score = _score;
	}

	public int get_score() {
		return score;
	}

	public void set_combo_max(final int _combo_max) {
		this.combo_max = _combo_max;
	}

	public int get_combo_max() {
		return combo_max;
	}

	public void set_explo_max(final int _explo_max) {
		this.explo_max = _explo_max;
	}

	public int get_explo_max() {
		return explo_max;
	}

	public void set_pierre_max(final int _pierre_max) {
		this.pierre_max = _pierre_max;
	}

	public int get_pierre_max() {
		return pierre_max;
	}

	public void set_combo_total(final int _combo_total) {
		this.combo_total = _combo_total;
	}

	public int get_combo_total() {
		return combo_total;
	}

	public void set_explo_total(final int _explo_total) {
		this.explo_total = _explo_total;
	}

	public int get_explo_total() {
		return explo_total;
	}

	public void set_pierre_total(final int _pierre_total) {
		this.pierre_total = _pierre_total;
	}

	public int get_pierre_total() {
		return pierre_total;
	}

}
