package model;

public class Case {
	private Puyo puyo;
	private int x;
	private int y;

	public Case ( Puyo puyo, int x, int y ) {
		this.puyo = puyo;
		this.x = x;
		this.y = y;
	}

	public Case clone() {
		if ( puyo == null ) { return new Case(null, x, y); }
		return new Case(puyo.clone(), x, y);
	}

	public Puyo getPuyo() {
		return puyo;
	}

	public void setPuyo(Puyo puyo) {
		this.puyo = puyo;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (puyo == null ? 0 : puyo.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) { return true; }
		if ( obj == null ) { return false; }
		if ( getClass() != obj.getClass() ) { return false; }
		Case other = (Case) obj;
		if ( puyo == null ) {
			if ( other.puyo != null ) { return false; }
		} else if ( !puyo.equals(other.puyo) ) { return false; }
		if ( x != other.x ) { return false; }
		if ( y != other.y ) { return false; }
		return true;
	}

}
