package model;

public class StrokeIA {

	int place_puyo2 = 1;
	int colonne = 0;
	int quality = 0;

	@Override
	public String toString() {
		return "Stroke [place_puyo2=" + place_puyo2 + ", colonne=" + colonne + ", quality=" + quality + "]";
	}

	public StrokeIA () {

	}

	public StrokeIA ( int place_puyo2, int colonne, int quality ) {
		this.place_puyo2 = place_puyo2;
		this.colonne = colonne;
		this.quality = quality;
	}

	public int getPlace_puyo2() {
		return place_puyo2;
	}

	public void setPlace_puyo2(int place_puyo2) {
		this.place_puyo2 = place_puyo2;
	}

	public int getColonne() {
		return colonne;
	}

	public void setColonne(int colonne) {
		this.colonne = colonne;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}
}
