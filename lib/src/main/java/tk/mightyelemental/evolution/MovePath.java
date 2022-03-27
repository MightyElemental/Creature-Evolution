package tk.mightyelemental.evolution;

import java.awt.Point;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

public class MovePath {

	protected Point destination;

	public boolean hasReached;

	public MovePath(float x, float y) {
		this.destination = new Point((int) x, (int) y);
	}

	public MovePath(Vector2f v) {
		this(v.x, v.y);
	}

	public void update(Creature creature) {
		Shape ent = creature.hitbox;
		creature.angle = 180 + MathHelper.getAngle(ent.getCenterX(), ent.getCenterY(), getX(), getY());
		float amountToMoveX = (creature.getSpeed() * (float) Math.cos(Math.toRadians(creature.angle)));
		float amountToMoveY = (creature.getSpeed() * (float) Math.sin(Math.toRadians(creature.angle)));
		if ( !hasReached ) {
			ent.setCenterX(ent.getCenterX() + amountToMoveX);
			ent.setCenterY(ent.getCenterY() + amountToMoveY);
		}
		if ( ent.getCenterY() > destination.getY() - 5 && ent.getCenterY() < destination.getY() + 5 ) {
			if ( ent.getCenterX() > destination.getX() - 5 && ent.getCenterX() < destination.getX() + 5 ) {
				hasReached = true;
			}
		}
	}

	public int getX() {
		return destination.x;
	}

	public int getY() {
		return destination.y;
	}

}
