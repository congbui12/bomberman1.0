package uet.oop.bomberman.entities.mob;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.Message;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.DirectionalExplosion;
import uet.oop.bomberman.entities.mob.enemy.Enemy;
import uet.oop.bomberman.entities.tile.powerup.Powerup;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.input.Keyboard;
import uet.oop.bomberman.level.Coordinates;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player extends Mob {
	
	private List<Bomb> _bombs;
	protected Keyboard _input;

	/**
	 * nếu giá trị này < 0 thì cho phép đặt đối tượng Bomb tiếp theo.
	 * cứ mỗi lần đặt 1 Bomb mới, giá trị này sẽ được reset về 0 và giảm dần trong mỗi lần update()
	 */
	protected int _timeBetweenPutBombs = 0;
	public static List<Powerup> _powerups = new ArrayList<Powerup>();
	
	public Player(int x, int y, Board board) {
		super(x, y, board);
		_bombs = _board.getBombs();
		_input = _board.getInput();
		_sprite = Sprite.player_right;
	}

	@Override
	public void update() {
		clearBombs();
		if(_alive == false) {
			afterKill();
			return;
		}
		
		if(_timeBetweenPutBombs < -7500) {
			_timeBetweenPutBombs = 0;
		} else {
			_timeBetweenPutBombs--;
		}
		
		animate();
		
		calculateMove();
		
		detectPlaceBomb();
	}
	
	@Override
	public void render(Screen screen) {
		calculateXOffset();
		
		if(_alive) {
			chooseSprite();
		} else {
			_sprite = Sprite.player_dead1;
		}
		
		screen.renderEntity((int)_x, (int)_y - _sprite.SIZE, this);
	}
	
	public void calculateXOffset() {
		int xScroll = Screen.calculateXOffset(_board, this);
		Screen.setOffset(xScroll, 0);
	}


	/**
	 * detectPlaceBomb : kiem tra xem dat bom duoc khong.
	 * _input.space : kiem tra phim dat bom da duoc go hay chua.
	 * Game.getBombRate() : tra ve so luong bom co the dat lien tiep tai thoi diem hien tai.
	 * _timeBetweenPutBombs : ngan player dat 2 bom tai cung 1 vi tri trong 1 khoang thoi gian qua ngan.
	 */
	private void detectPlaceBomb() {
		if(_input.space && Game.getBombRate() > 0 && _timeBetweenPutBombs < 0) {
			
			int xt = Coordinates.pixelToTile(_x + _sprite.getSize() / 2);
			int yt = Coordinates.pixelToTile( (_y + _sprite.getSize() / 2) - _sprite.getSize() );
			
			placeBomb(xt,yt);
			Game.addBombRate(-1);
			
			_timeBetweenPutBombs = 30;
		}
	}

	/**
	 * dat bom vao vi tri (x, y).
	 */
	protected void placeBomb(int x, int y) {
		Bomb b = new Bomb(x, y, _board);
		_board.addBomb(b);
	}
	
	private void clearBombs() {
		Iterator<Bomb> bs = _bombs.iterator();
		Bomb b;
		while(bs.hasNext()) {
			b = bs.next();
			if(b.isRemoved())  {
				bs.remove();
				Game.addBombRate(1);
			}
		}
	}

	@Override
	public void kill() {
		if(!_alive) return;
		
		_alive = false;
		
		_board.addLives(-1);

		Message msg = new Message("-1 LIVE", getXMessage(), getYMessage(), 2, Color.white, 14);
		_board.addMessage(msg);
	}
	
	@Override
	protected void afterKill() {
		if(_timeAfter > 0) --_timeAfter;
		else {
			if(_bombs.size() == 0) {
				
				if(_board.getLives() == 0)
					_board.endGame();
				else
					_board.restartLevel();
			}
		}
	}

	@Override
	protected void calculateMove() {
		int xa = 0, ya = 0;
		if(_input.up) ya--;
		if(_input.down) ya++;
		if(_input.left) xa--;
		if(_input.right) xa++;
		
		if(xa != 0 || ya != 0)  {
			move(xa * Game.getPlayerSpeed(), ya * Game.getPlayerSpeed());
			_moving = true;
		} else {
			_moving = false;
		}
		
	}
	
	@Override
	/**
	 * kiem tra xem tai vi tri (x, y) co doi tuong khac va player co the di chuyen toi do khong.
	 */
	public boolean canMove(double x, double y) {
		for (int c = 0; c < 4; c++) {
			double xt = ((_x + x) + c % 2 * 11) / Game.TILES_SIZE;
			double yt = ((_y + y) + c / 2 * 12 - 13) / Game.TILES_SIZE;
			
			Entity a = _board.getEntity(xt, yt, this);
			
			if(!a.collide(this))
				return false;
		}
		
		return true;
	}

	@Override
	/**
	 * di chuyen player.
	 */
	public void move(double xa, double ya) {
		if(xa > 0) _direction = 1;
		if(xa < 0) _direction = 3;
		if(ya > 0) _direction = 2;
		if(ya < 0) _direction = 0;
		
		if(canMove(0, ya)) {
			_y += ya;
		}
		
		if(canMove(xa, 0)) {
			_x += xa;
		}
	}
	
	@Override
	/**
	 * kiem tra xem player co bi va cham voi DE hay Enemy khong.
	 */
	public boolean collide(Entity e) {
		if(e instanceof DirectionalExplosion) {
			kill();
			return false;
		}
		
		if(e instanceof Enemy) {
			kill();
			return true;
		}
		
		return true;
	}

	public void addPowerup(Powerup p) {
		if(p.isRemoved()) return;
		
		_powerups.add(p);
		
		p.setValues();
	}
	
	public void clearUsedPowerups() {
		Powerup p;
		for (int i = 0; i < _powerups.size(); i++) {
			p = _powerups.get(i);
			if(p.isActive() == false)
				_powerups.remove(i);
		}
	}
	
	public void removePowerups() {
		for (int i = 0; i < _powerups.size(); i++) {
				_powerups.remove(i);
		}
	}

	private void chooseSprite() {
		switch(_direction) {
		case 0:
			_sprite = Sprite.player_up;
			if(_moving) {
				_sprite = Sprite.movingSprite(Sprite.player_up_1, Sprite.player_up_2, _animate, 20);
			}
			break;
		case 1:
			_sprite = Sprite.player_right;
			if(_moving) {
				_sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
			}
			break;
		case 2:
			_sprite = Sprite.player_down;
			if(_moving) {
				_sprite = Sprite.movingSprite(Sprite.player_down_1, Sprite.player_down_2, _animate, 20);
			}
			break;
		case 3:
			_sprite = Sprite.player_left;
			if(_moving) {
				_sprite = Sprite.movingSprite(Sprite.player_left_1, Sprite.player_left_2, _animate, 20);
			}
			break;
		default:
			_sprite = Sprite.player_right;
			if(_moving) {
				_sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
			}
			break;
		}
	}
}
