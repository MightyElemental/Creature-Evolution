package tk.mightyelemental.evolution;

import static tk.mightyelemental.evolution.CreatureEvolution.*;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import tk.mightyelemental.evolution.particles.FoodType;
import tk.mightyelemental.evolution.particles.Particle;
import tk.mightyelemental.evolution.particles.ParticleFood;

public class World {

	public List<Creature>	entities	= new ArrayList<Creature>();
	public List<Creature>	newEntities	= new ArrayList<Creature>();
	public List<Particle>	particles	= new ArrayList<Particle>();

	public int totalDied = 0;

	public Dimension worldSize;

	long ticks = 0;

	public World(int width, int height) {
		worldSize = new Dimension(width * 2, height * 2);
	}

	public void draw(GameContainer gc, StateBasedGame sbg, Graphics g) {

		Creature[] entArr = entities.toArray(new Creature[] {});

		g.setColor(Color.white);
		for ( Particle p : particles.toArray(new Particle[] {}) ) {
			if ( p == null ) continue;
			if ( p instanceof ParticleFood ) {
				g.setColor(((ParticleFood) p).type.getColor());
			} else {
				g.setColor(Color.white);
			}
			g.fill(p);
		}

		for ( Creature c : entArr ) {
			if ( c == null ) continue;
			g.setColor(c.color);
			g.fill(c.hitbox);
			if ( c.energyPoints <= c.getCannibalism() ) {
				g.setColor(Color.red);
				g.fillOval(c.hitbox.getCenterX() - 4, c.hitbox.getCenterY() - 4, 8, 8);
			}
			g.setColor(Color.black);
			g.drawString(c.genCount + "", c.hitbox.getCenterX() - 5, c.hitbox.getCenterY());
			if ( !c.isMale ) {
				g.setColor(Color.blue);
				if ( c.isPregnant() ) g.setColor(Color.red);
				g.fillOval(c.hitbox.getCenterX() - 10, c.hitbox.getCenterY() - 5, 5, 5);
			}
			if ( c.lookingForMate ) {
				g.setColor(Color.black);
				g.fillOval(c.hitbox.getCenterX(), c.hitbox.getCenterY() - 10, c.age % 10 + 1, c.age % 10 + 1);
			}
		}
		g.setColor(Color.white);
		for ( Creature c : entArr ) {
			if ( c == null ) continue;
			g.drawOval(c.hitbox.getCenterX() - c.getVisibilityRange(), c.hitbox.getCenterY() - c.getVisibilityRange(),
					c.getVisibilityRange() * 2, c.getVisibilityRange() * 2);
			if ( c.mp != null ) {
				g.drawLine(c.hitbox.getCenterX(), c.hitbox.getCenterY(), c.mp.getX(), c.mp.getY());
			}
		}

		g.setColor(Color.white);
		g.drawRect(0, 0, worldSize.width, worldSize.height);

	}

	public void update() {
		ticks++;
		for ( Iterator<Creature> iterator = entities.iterator(); iterator.hasNext(); ) {
			Creature c = iterator.next();
			c.update();
			if ( c.energyPoints <= 0 ) {
				iterator.remove();
				totalDied++;
			}
			if ( c.isPregnant() && c.age - c.pregnantStart >= c.gestationPeriod() ) {
				Creature[] children = c.createChildren();
				c.pregnantStart = -1;
				c.childFather = null;
				newEntities.addAll(Arrays.asList(children));
			}
		}
		entities.addAll(newEntities);
		newEntities.clear();
		for ( Iterator<Particle> iterator = particles.iterator(); iterator.hasNext(); ) {
			Particle p = iterator.next();
			if ( p.destroy ) {
				iterator.remove();
			}
		}
		if ( ticks % 3 == 0 ) {
			if ( entities.size() > 0 && getFoodParticles().length < 2000 ) {
				FoodType t = FoodType.values()[rand.nextInt(FoodType.values().length - 1)];
				int amount = rand.nextIntTendToward(6, 10);
				particles.add(new ParticleFood(rand.nextInt(worldSize.width), rand.nextInt(worldSize.height), t, amount));

//				t = FoodType.TYPE_2;
//				particles.add(new ParticleFood((float) rand.normalDeviate(worldSize.width - 200, 200),
//						(float) rand.normalDeviate(worldSize.height - 200, 200), t, amount));
			}
			//spawnIfNone();
		}
	}

	public void spawnIfNone() {
		int males = getCreaturesBySex(true).length;
		if ( males < 1 ) {
			Creature c = new Creature(this);
			c.isMale = true;
			entities.add(c);
		}
		if ( entities.size() - males < 1 ) {
			Creature c = new Creature(this);
			c.isMale = false;
			entities.add(c);
		}
	}

	public void createParticle(float x, float y, float rad) {
		particles.add(new Particle(x, y, rad));
	}

	public void populate() {
		for ( int i = 0; i < rand.nextInt(10) + 10; i++ ) {
			entities.add(new Creature(this));
		}
		for ( int i = 0; i < rand.nextInt(20) + 20; i++ ) {
			FoodType t = FoodType.values()[rand.nextInt(FoodType.values().length - 1)];
			int amount = rand.nextIntTendToward(6, 10);
			particles.add(new ParticleFood(rand.nextInt(worldSize.width), rand.nextInt(worldSize.height), t, amount));
		}
	}

	public ParticleFood[] getFoodParticles() {
		while (true) {
			try {
				return particles.stream().filter(e -> e instanceof ParticleFood).toArray(ParticleFood[]::new);
			} catch (ConcurrentModificationException e) {
			}
		}
	}

	public Creature[] getCreaturesBySex(boolean isMale) {
		while (true) {
			try {
				return entities.stream().filter(c -> c != null && c.isMale == isMale).toArray(Creature[]::new);
			} catch (ConcurrentModificationException e) {
			}
		}
	}

}
