package model;

import model.Puyo.PuyoColor;

public class Puyo2Destroy {

	private int x;
	private int y;
	private PuyoColor color;

	public Puyo2Destroy ( int x, int y, PuyoColor color ) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public PuyoColor getColor() {
		return color;
	}

	public void setColor(PuyoColor color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "Puyo2Destroy [x=" + x + ", y=" + y + ", color=" + color + "]";
	}

}
