package model;

public class Puyo {
	public enum PuyoColor {
		RED, GREEN, BLUE, YELLOW, VIOLET, BLACK
	}

	private PuyoColor color;

	public Puyo ( PuyoColor _color ) {
		color = _color;
	}

	@Override
	public Puyo clone() {
		return new Puyo(color);
	}

	public PuyoColor getColor() {
		return color;
	}

	public void setColor(PuyoColor color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "" + color.ordinal();
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) { return true; }
		if ( obj == null ) { return false; }
		if ( getClass() != obj.getClass() ) { return false; }
		Puyo other = (Puyo) obj;
		if ( color != other.color ) { return false; }
		return true;
	}

}
