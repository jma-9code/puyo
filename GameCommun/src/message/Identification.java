package message;

import commun.EGames;
import commun.KeyEncrypt;
import commun.Player;

public class Identification implements IMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private KeyEncrypt key = null;
	private String encrypt_pwd = null;
	private String encrypt_name = null;
	private String countryCode = null;
	private Player player;
	private EGames game;
	
	public Identification(Player _player){
		setPlayer(_player);
	}
	
	public Identification(KeyEncrypt _key){
		setKey(_key);
	}
	
	public Identification(KeyEncrypt _key, String name, String pwd, String _countryCode, EGames _game){
		encrypt_name = KeyEncrypt.encrypt(_key, name);
		encrypt_pwd = KeyEncrypt.encrypt(_key, pwd);
		countryCode = _countryCode;
		setGame(_game);
	}

	public KeyEncrypt getKey() {
		return key;
	}

	public void setKey(KeyEncrypt key) {
		this.key = key;
	}


	public String getEncrypt_pwd() {
		return encrypt_pwd;
	}

	public void setEncrypt_pwd(String encrypt_pwd) {
		this.encrypt_pwd = encrypt_pwd;
	}

	public String getEncrypt_name() {
		return encrypt_name;
	}

	public void setEncrypt_name(String encrypt_name) {
		this.encrypt_name = encrypt_name;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public EGames getGame() {
		return game;
	}

	public void setGame(EGames game) {
		this.game = game;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
}
