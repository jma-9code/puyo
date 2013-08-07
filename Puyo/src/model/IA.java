package model;

import java.util.ArrayList;

import main.Config;

public class IA {

	private static void dropAllPuyo(Case[][] tab) {
		if ( !Plateau.checkDropAllPuyo(tab) ) {
			for ( int i = 0; i < Config.NB_LINE; i++ ) {
				for ( int j = 0; j < Config.NB_COL; j++ ) {
					if ( tab[i][j].getPuyo() != null ) {
						if ( Movement.try_down(tab, tab[i][j]) ) {
							Movement.replacePuyo(tab[i][j], tab[i + 1][j]);
							;
						}
					}
				}
			}
			dropAllPuyo(tab);
		}
	}

	/**
	 * Permet de placer le puyotmp en haut du tableau a la position voulu
	 * 
	 * @param _plateau
	 * @param _piece
	 * @param pos
	 * @param col
	 * @return
	 */
	public static boolean injectPiece(Case[][] _plateau, Piece _piece, int pos, int col) {
		// Possible d'aller jusqu'a la colonne ?
		int posStart = _plateau[0].length / 2 - 1;
		if ( posStart != col ) {
			int translate = posStart - col;
			if ( translate > 0 ) {
				for ( int i = posStart - 1; i >= 0; i-- ) {
					if ( countPuyoByCol(_plateau, i) >= _plateau.length - 2 ) { return false; }
				}
			} else {
				for ( int i = posStart + 1; i < _plateau[0].length; i++ ) {
					if ( countPuyoByCol(_plateau, i) >= _plateau.length - 2 ) { return false; }
				}
			}
		}
		switch ( pos ) {
			case 1:
				if ( _plateau[1][col].getPuyo() == null && _plateau[0][col].getPuyo() == null ) {
					_plateau[1][col].setPuyo(_piece.getPuyo1());
					_plateau[0][col].setPuyo(_piece.getPuyo2());
					return true;
				}
				return false;
			case 3:
				if ( _plateau[1][col].getPuyo() == null && _plateau[0][col].getPuyo() == null ) {
					_plateau[1][col].setPuyo(_piece.getPuyo2());
					_plateau[0][col].setPuyo(_piece.getPuyo1());
					return true;
				}
				return false;
			case 2:
				if ( col + 1 < _plateau[0].length && _plateau[0][col].getPuyo() == null && _plateau[0][col + 1].getPuyo() == null ) {
					_plateau[0][col].setPuyo(_piece.getPuyo1());
					_plateau[0][col + 1].setPuyo(_piece.getPuyo2());
					return true;
				}
				return false;
			case 4:
				if ( col - 1 >= 0 && _plateau[0][col].getPuyo() == null && _plateau[0][col - 1].getPuyo() == null ) {
					_plateau[0][col].setPuyo(_piece.getPuyo1());
					_plateau[0][col - 1].setPuyo(_piece.getPuyo2());
					return true;
				}
				return false;
			default:
				return false;
		}
	}

	public static StrokeIA getIAStroke(Case[][] plateau, ArrayList<Piece> pieces, Case cpuyo1, Case cpuyo2) {
		Case[][] p = clonePlateau(plateau);
		ArrayList<Piece> piecesTMP = clonePieces(pieces);
		if ( cpuyo1 != null ) {
			p[cpuyo1.getX()][cpuyo1.getY()].setPuyo(null);
		}
		if ( cpuyo2 != null ) {
			p[cpuyo2.getX()][cpuyo2.getY()].setPuyo(null);
		}
		return getIAStroke(p, piecesTMP);
	}

	public static ArrayList<Piece> clonePieces(ArrayList<Piece> pieces) {
		ArrayList<Piece> pieces_ret = new ArrayList<Piece>();
		for ( Piece p : pieces ) {
			pieces_ret.add(p);
		}
		return pieces_ret;
	}

	/**
	 * @param plateau
	 * @param pieces
	 * @return
	 */
	private static StrokeIA getIAStroke(Case[][] plateau, ArrayList<Piece> pieces) {
		int max_quality = Integer.MIN_VALUE;
		StrokeIA stroke = new StrokeIA();
		if ( plateau == null || pieces == null || pieces.isEmpty() ) { return null; }

		Piece piece2inject = pieces.remove(0);
		int relationQualityBeforeChange = relationQuality(plateau);
		ArrayList<Integer> colWithMax = getColoneWithMaxPuyo(plateau);
		ArrayList<Integer> colWithMin = getColoneWithMinPuyo(plateau);
		for ( int col = 0; col < plateau[0].length; col++ ) {
			for ( int pos = 1; pos < 5; pos++ ) {
				Case[][] tab_tmp = clonePlateau(plateau);
				int quality = 0;
				if ( injectPiece(tab_tmp, piece2inject, pos, col) ) {
					dropAllPuyo(tab_tmp);
					int nb_puyo_explo = 0;
					/* Explosion */
					do {
						for ( int i = 0; i < Config.NB_LINE; i++ ) {
							for ( int j = 0; j < Config.NB_COL; j++ ) {
								ArrayList<Case> cases = Plateau.countRelation(tab_tmp, tab_tmp[i][j], new ArrayList<Case>());
								if ( cases.size() >= 4 ) {
									for ( Case c : cases ) {
										c.setPuyo(null);
										// destruction des pierres
										if ( c.getX() + 1 < Config.NB_LINE && plateau[c.getX() + 1][c.getY()].getPuyo() != null && plateau[c.getX() + 1][c.getY()].getPuyo().getColor() == Puyo.PuyoColor.BLACK ) {
											plateau[c.getX() + 1][c.getY()].setPuyo(null);
										}

										if ( c.getX() - 1 >= 0 && plateau[c.getX() - 1][c.getY()].getPuyo() != null && plateau[c.getX() - 1][c.getY()].getPuyo().getColor() == Puyo.PuyoColor.BLACK ) {
											plateau[c.getX() - 1][c.getY()].setPuyo(null);
										}

										if ( c.getY() + 1 < Config.NB_COL && plateau[c.getX()][c.getY() + 1].getPuyo() != null && plateau[c.getX()][c.getY() + 1].getPuyo().getColor() == Puyo.PuyoColor.BLACK ) {
											plateau[c.getX()][c.getY() + 1].setPuyo(null);
										}

										if ( c.getY() - 1 >= 0 && plateau[c.getX()][c.getY() - 1].getPuyo() != null && plateau[c.getX()][c.getY() - 1].getPuyo().getColor() == Puyo.PuyoColor.BLACK ) {
											plateau[c.getX()][c.getY() - 1].setPuyo(null);
										}
									}
									nb_puyo_explo += cases.size();
								}
							}
						}
						dropAllPuyo(tab_tmp);
					} while ( Plateau.checkPuyoRelations(tab_tmp) );
					if ( Plateau.isLosing(tab_tmp) ) {
						quality = Integer.MIN_VALUE + 1;
					} else {
						int relationQuality = relationQuality(tab_tmp) - relationQualityBeforeChange;
						if ( colWithMax.contains(col) || (pos == 2 && colWithMax.contains(col + 1)) || (pos == 3 && colWithMax.contains(col - 1)) ) {
							quality -= 1;
						}
						if ( colWithMin.contains(col) || (pos == 2 && colWithMin.contains(col + 1)) || (pos == 3 && colWithMin.contains(col - 1)) ) {
							quality += 1;
						}
						quality += (relationQuality < 0) ? 0 : relationQuality;

						if ((double) countPuyo(tab_tmp)/(double) (tab_tmp.length * tab_tmp[0].length) > 0.5 ) {
							quality += nb_puyo_explo * 100;
						} else {
							quality += (nb_puyo_explo > 4) ? nb_puyo_explo * 100 : 0;
						}

						StrokeIA stroketmp = getIAStroke(clonePlateau(tab_tmp), clonePieces(pieces));
						if ( stroketmp != null ) {
							quality += stroketmp.quality;
						}
					}
				} else {
					quality = -5000;
				}

				if ( quality > max_quality ) {
					max_quality = quality;
					stroke = new StrokeIA(pos, col, quality);
				}
			}
		}
		return stroke;
	}

	public static int relationQuality(Case[][] plateau) {
		int relQuality = 0;
		for ( int i = 0; i < plateau.length; i++ ) {
			for ( int j = 0; j < plateau[0].length; j++ ) {
				if ( plateau[i][j].getPuyo() != null ) {
					relQuality += Plateau.countRelation(plateau, plateau[i][j], new ArrayList<Case>()).size();
				}
			}
		}
		return relQuality;
	}

	public static int countPuyo(Case[][] tab) {
		int nb = 0;
		for ( int i = 0; i < tab.length; i++ ) {
			for ( int j = 0; j < tab[0].length; j++ ) {
				if ( tab[i][j].getPuyo() != null ) {
					nb++;
				}
			}
		}
		return nb;
	}

	public static Case[][] clonePlateau(Case[][] tab) {
		Case[][] ret = new Case[tab.length][tab[0].length];
		for ( int i = 0; i < tab.length; i++ ) {
			for ( int j = 0; j < tab[0].length; j++ ) {
				ret[i][j] = tab[i][j].clone();
			}
		}
		return ret;
	}

	public static ArrayList<Integer> getColoneWithMaxPuyo(Case[][] tab) {
		ArrayList<Integer> listInt = new ArrayList<Integer>();
		int max = 0;
		for ( int j = 0; j < tab[0].length; j++ ) {
			int nbpuyo = countPuyoByCol(tab, j);
			if ( nbpuyo > max ) {
				max = nbpuyo;
			}
		}

		for ( int j = 0; j < tab[0].length; j++ ) {
			int nbpuyo = countPuyoByCol(tab, j);
			if ( nbpuyo == max ) {
				listInt.add(j);
			}
		}

		return listInt;
	}

	public static ArrayList<Integer> getColoneWithMinPuyo(Case[][] tab) {
		int min = tab[0].length;
		ArrayList<Integer> listInt = new ArrayList<Integer>();
		for ( int j = 0; j < tab[0].length; j++ ) {
			int nbpuyo = countPuyoByCol(tab, j);
			if ( nbpuyo < min ) {
				min = nbpuyo;
			}
		}

		for ( int j = 0; j < tab[0].length; j++ ) {
			int nbpuyo = countPuyoByCol(tab, j);
			if ( nbpuyo == min ) {
				listInt.add(j);
			}
		}

		return listInt;
	}

	public static int countPuyoByCol(Case[][] tab, int col) {
		int nbpuyo = 0;
		for ( int i = 0; i < tab.length; i++ ) {
			if ( tab[i][col].getPuyo() != null ) {
				nbpuyo += 1;
			}
		}
		return nbpuyo;
	}

	/**
	 * Interpretation de la classe IA dans le modele, permet de generer le coup
	 */
	public static void playStrokeIA(Plateau plateau, StrokeIA strokeIA) {
		Case cpuyo1 = null;
		try {
			cpuyo1 = plateau.caseWithPuyo1();
		} catch (Exception e) {}
		Case cpuyo2 = null;
		try {
			cpuyo2 = plateau.caseWithPuyo2();
		} catch (Exception e) {}
		if ( cpuyo1 == null || cpuyo2 == null || strokeIA == null || plateau == null ) { return; }

		int dpCol = strokeIA.getColonne() - cpuyo1.getY();

		// Rotation
		int x = 0;
		while ( strokeIA.getPlace_puyo2() != plateau.place_puyo2() && x < 5 ) {
			Movement.move_rot1(plateau);
			x += 1;
		}

		// Translation
		for ( int k = 0; k < Math.abs(dpCol); k++ ) {
			if ( dpCol < 0 ) {
				Movement.move_left(plateau);
			} else if ( dpCol > 0 ) {
				Movement.move_right(plateau);
			}
		}

		int countPuyo = 0;
		for ( int i = 0; i < Config.NB_LINE; i++ ) {
			if ( plateau.getPlateau()[i][strokeIA.getColonne()].getPuyo() != null ) {
				countPuyo++;
			}
		}
		int nbMoveDown = Config.NB_LINE - countPuyo;
		for ( int i = 0; i < nbMoveDown; i++ ) {
			Movement.move_down(plateau, false);
			try {
				Thread.sleep(Config.IASPEED/nbMoveDown);
			} catch (InterruptedException e) {
			}
		}
	}
}
