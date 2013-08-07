package game.chess;

import game.AbsParty;
import game.AbsPartyManager;

import java.util.Observable;

import client.Client;

public class Chess_PartyManager extends AbsPartyManager {

	@Override
	public boolean addParty(AbsParty p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean joinParty(int id, Client c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void leftParty(Client c) {
		// TODO Auto-generated method stub

	}

	@Override
	public AbsParty isInParty(Client cli) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateObservers(Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

}
