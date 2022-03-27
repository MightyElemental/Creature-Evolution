package tk.mightyelemental.evolution.particles;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.state.StateBasedGame;

import tk.mightyelemental.evolution.CreatureEvolution;

public class Particle extends Circle {

	private static final long serialVersionUID = -6890044408552697725L;

	public int		direction;
	public float	speed;
	public boolean	lingering;

	public boolean destroy = false;

	public Particle(float x, float y, float radius) {
		super(x, y, radius, 6);
		direction = CreatureEvolution.rand.nextInt(360);
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		float x = (speed / 17f * delta * (float) Math.cos(Math.toRadians(direction)));
		float y = (speed / 17f * delta * (float) Math.sin(Math.toRadians(direction)));
		this.setCenterX(getCenterX() + x);
		this.setCenterY(getCenterY() + y);
	}

}
