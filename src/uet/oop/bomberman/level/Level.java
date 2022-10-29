package uet.oop.bomberman.level;

import uet.oop.bomberman.Board;

public abstract class Level implements ILevel {

	protected int _width, _height, _level;
	protected String[] _lineTiles;
	protected Board _board;

	protected static String[] codes = {
		"dnibpb5uqy",
		"cuq0vaxstb",
		"38y418wriq",
		"34h8k0chcs",
		"9qztxh6l4s",
		};

	public Level(String path, Board board) {
		loadLevel(path);
		_board = board;
	}

	@Override
	public abstract void loadLevel(String path);
	
	public abstract void createEntities();

	/*
	|--------------------------------------------------------------------------
	| Codes
	|--------------------------------------------------------------------------
	 */
	public int validCode(String str) {
		for (int i = 0; i < codes.length; i++) {
			if (codes[i].equals(str)) {
				return i;
			}
		}
		return -1;
	}

	public String getActualCode() {
		return codes[_level -1];
	}

	public int getWidth() {
		return _width;
	}

	public int getHeight() {
		return _height;
	}

	public int getLevel() {
		return _level;
	}

}
