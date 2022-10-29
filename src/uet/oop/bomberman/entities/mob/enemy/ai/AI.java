package uet.oop.bomberman.entities.mob.enemy.ai;

import java.util.Random;

public abstract class AI {
	
	protected Random random = new Random();

	// Thuat toan tim duong di.
	public abstract int calculateDirection();
}
