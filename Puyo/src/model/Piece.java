package model;

import java.util.Random;

public class Piece {
	private static int nextid = 0;

	private Puyo puyo1;
	private Puyo puyo2;
	private int id;

	private static Random r = new Random();

	public Piece () {
		setId(nextid++);
		puyo1 = new Puyo(Puyo.PuyoColor.values()[r.nextInt(Puyo.PuyoColor.values().length - 1)]);
		puyo2 = new Puyo(Puyo.PuyoColor.values()[r.nextInt(Puyo.PuyoColor.values().length - 1)]);
	}

	public Piece ( int _id, Puyo p1, Puyo p2 ) {
		id = _id;
		puyo1 = p1;
		puyo2 = p2;
	}

	@Override
	public String toString() {
		return "Piece [puyo1=" + puyo1 + ", puyo2=" + puyo2 + "]";
	}

	public static void injectGraine(long graine) {
		r = new Random(graine);
	}

	@Override
	public Piece clone() {
		if ( puyo1 == null || puyo2 == null ) { return null; }
		return new Piece(id, puyo1.clone(), puyo2.clone());
	}

	public Puyo getPuyo1() {
		return puyo1;
	}

	public void setPuyo1(Puyo puyo1) {
		this.puyo1 = puyo1;
	}

	public Puyo getPuyo2() {
		return puyo2;
	}

	public void setPuyo2(Puyo puyo2) {
		this.puyo2 = puyo2;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static int getNextid() {
		return nextid;
	}

	public static void setNextid(int nextid) {
		Piece.nextid = nextid;
	}

}
