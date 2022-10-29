package uet.oop.bomberman.entities.bomb;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.mob.Mob;
import uet.oop.bomberman.graphics.Screen;

public class DirectionalExplosion extends Entity {
	/**
	 * _direction : huong cua DE.
	 * _radius : do dai max cua DE.
	 * xOrigin, yOrigin : hoanh do va tung do ban dau cua DE.
	 */
	protected Board _board;
	protected int _direction;
	private int _radius;
	protected int xOrigin, yOrigin;
	protected Explosion[] _explosions;
	
	public DirectionalExplosion(int x, int y, int direction, int radius, Board board) {
		xOrigin = x;
		yOrigin = y;
		_x = x;
		_y = y;
		_direction = direction;
		_radius = radius;
		_board = board;
		
		_explosions = new Explosion[ calculatePermitedDistance() ];
		createExplosions();
		}

	/**
	 * tao cac Explosions.
	 */
	private void createExplosions() {
		boolean last = false;
		
		int x = (int)_x;
		int y = (int)_y;
		for (int i = 0; i < _explosions.length; i++) {
			last = i == _explosions.length -1 ? true : false;
			
			switch (_direction) {
				case 0: y--; break;
				case 1: x++; break;
				case 2: y++; break;
				case 3: x--; break;
			}
			_explosions[i] = new Explosion(x, y, _direction, last, _board);
		}
	}

	/**
	 * tinh toan do dai cua DE.
	 */
	private int calculatePermitedDistance() {
		int radius = 0;
		int x = (int)_x;
		int y = (int)_y;
		while(radius < _radius) {
			if(_direction == 0) y--;
			if(_direction == 1) x++;
			if(_direction == 2) y++;
			if(_direction == 3) x--;
			
			Entity a = _board.getEntity(x, y, null);
			
			if(a instanceof Bomb) ++radius;

			if(a.collide(this) == false)
				break;
			
			++radius;
		}
		return radius;
	}
	
	public Explosion explosionAt(int x, int y) {
		for (int i = 0; i < _explosions.length; i++) {
			if(_explosions[i].getX() == x && _explosions[i].getY() == y) 
				return _explosions[i];
		}
		return null;
	}

	@Override
	public void update() {}
	
	@Override
	public void render(Screen screen) {
		
		for (int i = 0; i < _explosions.length; i++) {
			_explosions[i].render(screen);
		}
	}

	@Override
	public boolean collide(Entity e) {
		return true;
	}
}
