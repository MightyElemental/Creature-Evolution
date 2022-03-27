package tk.mightyelemental.evolution;

import static tk.mightyelemental.evolution.CreatureEvolution.*;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;

import tk.mightyelemental.evolution.particles.FoodType;
import tk.mightyelemental.evolution.particles.ParticleFood;

public class Creature {

	public Map<String, Integer> variables = new HashMap<String, Integer>();

	public final int maxEnergy = 3000;

	public int		poisonLevel		= 0;	// when the creature comes into contact with a toxin particle, this goes up
	public Circle	hitbox;
	public float	angle			= 0;	// needed for movement, not for graphics
	public Color	color;
	public int		energyPoints	= 1000;	// when zero, the entity dies
	public int		age				= 2000;	// max of 2000, indicates the transition from child to adult. age does not
											// directly invoke death

	public boolean	isMale			= true;
	public int		pregnantStart	= -1;
	public boolean	lookingForMate;
	public Creature	childFather;

	public int genCount = 1;

	public int waste = 0;

	public World	worldObj;
	public MovePath	mp;

	private FoodType wasteType;

	public Map<FoodType, Float> foodUptakes = new HashMap<FoodType, Float>();

	{
		variables.put("geneStability", 80);// percentage
		variables.put("poisonResistance", 0);// percentage
		variables.put("visibilityRange", 65);// pixels (max 150)
		variables.put("reproductiveUrge", 50);// percentage //higher percentage puts reproduction over other basic instincts
		variables.put("gestation", 500);// age ticks
		variables.put("speed", 20);// pixels * 10
		variables.put("energyTransfer", 50);// percentage - higher means more energy to child
		variables.put("childCountMean", 1);
		variables.put("randomMovement", 50);// percentage change of random direction
		variables.put("cannibalism", 50);// point at which cells become cannibalistic

		// How much of each food type the creature can digest
		foodUptakes.put(FoodType.TYPE_1, 90f);
		foodUptakes.put(FoodType.TYPE_2, 40f);
		foodUptakes.put(FoodType.TYPE_3, 40f);
		foodUptakes.put(FoodType.TYPE_4, 40f);
	}

	public void update() {
		if ( energyPoints < maxEnergy ) {
			updateEat();
		}
		updateMate();
		excreteWaste();
		if ( mp == null || mp.hasReached ) {
			// int x = rand.deviate((int) hitbox.getCenterX(), getVisibilityRange());
			// int y = rand.deviate((int) hitbox.getCenterY(), getVisibilityRange());
			Vector2f p = getTarget();

			mp = new MovePath(p);
		}
		mp.update(this);
		// wrapScreen();
		blockEdges();
		age++;
		if ( age > 1500 ) {
			if ( age % 3 == 0 ) energyPoints--;// TODO: reduce the amount of energy required to live
		} else {
			if ( age % 6 == 0 ) energyPoints--;
		}
		if ( getSize() * 2 != hitbox.getWidth() ) {
			hitbox = new Circle(hitbox.getCenterX(), hitbox.getCenterY(), getSize(), 15);
		}
	}

	private void blockEdges() {
		if ( hitbox.getCenterX() > worldObj.worldSize.width ) {
			hitbox.setCenterX(worldObj.worldSize.width);
			mp.hasReached = true;
		}
		if ( hitbox.getCenterX() < 0 ) {
			hitbox.setCenterX(0);
			mp.hasReached = true;
		}
		if ( hitbox.getCenterY() > worldObj.worldSize.height ) {
			hitbox.setCenterY(worldObj.worldSize.height);
			mp.hasReached = true;
		}
		if ( hitbox.getCenterY() < 0 ) {
			hitbox.setCenterY(0);
			mp.hasReached = true;
		}
	}

	private void excreteWaste() {
		if ( age % 10 == 0 ) {
			if ( waste > 5 * getWasteType().getEnergyDensity() ) {
				int amount = 5;
				worldObj.particles.add(new ParticleFood(hitbox.getCenterX(), hitbox.getCenterY(), getWasteType(), amount));
				waste -= amount * getWasteType().getEnergyDensity();
			}
		}
	}

	public void updateEat() {
		for ( ParticleFood p : worldObj.getFoodParticles() ) {
			if ( p.type == this.wasteType ) continue;
			if ( !p.destroy && hitbox.intersects(p) ) {
				float energy = netEnergyFromFood(p);
				energyPoints += energy;
				float w = calculateWasteEnergy(p);
				waste += w;
				// System.out.println(p.amount * p.type.getEnergyDensity() + "|" + energy + "|"
				// + w);
				p.eat();
			}
		}
		if ( energyPoints <= getCannibalism() ) {
			for ( Creature c : worldObj.entities ) {
				if ( c == this ) continue;
				if ( hitbox.intersects(c.hitbox) ) {
					c.energyPoints -= getCannibalism();
					energyPoints += getCannibalism();
					// System.out.println("cannibalized");
				}
			}
		}
	}

	public void updateMate() {
		if ( this.isPregnant() ) return;
		for ( Creature c : worldObj.entities ) {
			if ( c == this ) continue;
			if ( !c.lookingForMate ) continue;
			if ( this.isMale == c.isMale ) continue;
			if ( c.isPregnant() ) continue;
			if ( hitbox.intersects(c.hitbox) ) {
				Creature female;
				if ( this.isMale )
					female = c;
				else
					female = this;
				female.pregnantStart = female.age;
				female.childFather = this;
			}
		}
	}

	public void wrapScreen() {
		if ( hitbox.getCenterX() > worldObj.worldSize.width ) {
			hitbox.setCenterX(0);
			mp.hasReached = true;
		}
		if ( hitbox.getCenterX() < 0 ) {
			hitbox.setCenterX(worldObj.worldSize.width - 5);
			mp.hasReached = true;
		}
		if ( hitbox.getCenterY() > worldObj.worldSize.height ) {
			hitbox.setCenterY(0);
			mp.hasReached = true;
		}
		if ( hitbox.getCenterY() < 0 ) {
			hitbox.setCenterY(worldObj.worldSize.height - 5);
			mp.hasReached = true;
		}
	}

	public Creature(World worldObj) {
		this.worldObj = worldObj;
		startingMutation();
	}

	public void startingMutation() {
		variables.put("geneStability", rand.nextInt(30) + 70);
		variables.put("poisonResistance", rand.nextInt(10));
		variables.put("visibilityRange", rand.nextInt(70, 130));
		variables.put("reproductiveUrge", rand.nextInt(30, 70));
		variables.put("gestation", rand.nextInt(300, 600));
		variables.put("speed", rand.nextInt(10, 25));
		variables.put("energyTransfer", rand.nextInt(10, 90));
		variables.put("childCountMean", rand.nextInt(1, 3));
		variables.put("randomMovement", rand.nextInt(0, 100));
		variables.put("cannibalism", rand.nextInt(0, 100));
		isMale = rand.nextBoolean();

		foodUptakes.put(FoodType.TYPE_1, 0f + rand.nextInt(40, 100));
		foodUptakes.put(FoodType.TYPE_2, 0f + rand.nextInt(40, 100));
		foodUptakes.put(FoodType.TYPE_3, 0f + rand.nextInt(40, 100));
		foodUptakes.put(FoodType.TYPE_4, 0f + rand.nextInt(40, 100));

		color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1f);

		int x = rand.nextInt(worldObj.worldSize.width - getSize());
		int y = rand.nextInt(worldObj.worldSize.height - getSize());
		hitbox = new Circle(x, y, getSize(), 15);
	}

	public Vector2f getTarget() {
		float mul = (100 - getReproductiveUrge()) / 100f;
		Vector2f p = null;
		lookingForMate = false;

		if ( energyPoints <= getCannibalism() ) {
			Creature c = getClosestCreature();
			if ( c != null ) {
				p = c.hitbox.getLocation();
			}
		}

		if ( p == null && energyPoints >= 2000 * mul ) {
			lookingForMate = true;
			Creature c = getClosestMate();// TODO: fix this
			if ( c != null ) {
				p = c.hitbox.getLocation();
				// System.out.println(c.hitbox.getLocation());
			}
		}

		if ( p == null && (energyPoints < 2000 * mul || lookingForMate) && energyPoints < maxEnergy ) {
			ParticleFood pf = getClosestFood();
			if ( pf != null ) p = pf.getLocation();
		}

		if ( p == null ) {
			float x = rand.linearDeviate((int) hitbox.getCenterX(), (int) getVisibilityRange());
			float y = rand.linearDeviate((int) hitbox.getCenterY(), (int) getVisibilityRange());
			if ( rand.nextInt(100) > getRandomMovementChance() ) {
				float length = rand.nextFloat() * getVisibilityRange();
				x = hitbox.getCenterX() + (float) (Math.cos(Math.toRadians(angle)) * length);
				y = hitbox.getCenterY() + (float) (Math.sin(Math.toRadians(angle)) * length);
			}
			p = new Vector2f(x, y);
		}
		return p;
	}

	private Creature getClosestCreature() {
		Creature close = null;
		float dist = 1000000;
		for ( Creature c : worldObj.entities ) {
			if ( c == this ) continue;
			float tempDist = Utils.getDistance(hitbox.getCenterX(), hitbox.getCenterY(), c.hitbox.getCenterX(), c.hitbox.getCenterY());
			if ( tempDist < getVisibilityRange() && tempDist < dist ) {
				close = c;
				dist = tempDist;
			}
		}
		return close;
	}

	public ParticleFood getClosestFood() {
		ParticleFood close = null;
		float dist = 1000000;
		for ( ParticleFood p : worldObj.getFoodParticles() ) {
			if ( p.type == this.wasteType ) continue;
			float tempDist = Utils.getDistance(hitbox.getCenterX(), hitbox.getCenterY(), p.getCenterX(), p.getCenterY());
			if ( tempDist < getVisibilityRange() && tempDist < dist ) {
				close = p;
				dist = tempDist;
			}
		}
		return close;
	}

	public Creature getClosestMate() {
		if ( this.isPregnant() ) return null;
		Creature close = null;
		float dist = 1000000;
		for ( Creature c : worldObj.entities ) {
			if ( c == this ) continue;
			if ( this.isMale == c.isMale ) continue;
			if ( !c.lookingForMate ) continue;
			if ( c.isPregnant() ) continue;
			float tempDist = Utils.getDistance(hitbox.getCenterX(), hitbox.getCenterY(), c.hitbox.getCenterX(), c.hitbox.getCenterY());
			if ( tempDist < getVisibilityRange() && tempDist < dist ) {
				close = c;
				dist = tempDist;
			}
		}
		return close;
	}

	public int getSize() {
		return (int) (Math.max(5 + Math.min(energyPoints / 750f, 3) * Math.min(25f / getSpeed(), 40) * ageDebuff(), 10));
	}

	public float getFoodUptake() {// percentage of food converted into hunger points, affects digestion speed
		return Math.max(getSize() / 105f, 1);
	}

	public float getFoodUptakeOfType(FoodType ft) {
		return foodUptakes.get(ft);
	}

	public float energyToDigest(ParticleFood pf) {
		double o = getFoodUptake();
		double a = getFoodUptakeOfType(pf.type) / 100.0;
		return (float) Math.exp(1.6 * o * a - 0.05 * pf.type.getEnergyDensity()) - 1;
	}

	public float energyFromFood(ParticleFood pf) {
		return (float) (pf.amount * pf.type.getEnergyDensity() * getFoodUptake() * getFoodUptakeOfType(pf.type) / 100f);
	}

	public float netEnergyFromFood(ParticleFood pf) {
		return energyFromFood(pf) - energyToDigest(pf);
	}

	public float calculateWasteEnergy(ParticleFood pf) {
		return (100 - foodUptakes.getOrDefault(pf.type, 100f)) / 100f * pf.amount * pf.type.getEnergyDensity();
	}

	public float getCannibalism() {
		return variables.get("cannibalism");
	}

	public int getRandomMovementChance() {
		return variables.get("randomMovement");
	}

	public int getEnergyTransfer() {
		return variables.get("energyTransfer");
	}

	public int getChildCount() {
		return variables.get("childCountMean");
	}

	public FoodType getWasteType() {
		if ( wasteType == null ) {
			wasteType = Utils.keys(foodUptakes, Utils.minimumValue(foodUptakes)).findFirst().get();
		}
		return wasteType;
	}

	public float ageDebuff() {
		return (Math.min(age + 100, 2000) / 2000f);
	}

	public float getPoisonResistance() {
		return variables.get("poisonResistance") * ageDebuff();
	}

	public float getVisibilityRange() {
		return variables.get("visibilityRange") * ageDebuff();
	}

	public float getSpeed() {
		float speed = variables.getOrDefault("speed", 20) / 10f;
		float youthBoost = -0.05f * (age - 40 * speed);
		if ( youthBoost > speed ) return youthBoost;
		return speed;
	}

	public float getReproductiveUrge() {
		return ageDebuff() >= 1 ? variables.get("reproductiveUrge") : 0;
	}

	public boolean isPregnant() {
		return pregnantStart != -1;
	}

	public int gestationPeriod() {
		return variables.get("gestation");
	}

	public Creature[] createChildren() {
		int amount = (int) rand.normalDeviate(getChildCount(), 2);
		if ( amount < 1 ) amount = 1;
		Creature[] children = new Creature[amount];
		for ( int i = 0; i < amount; i++ ) {
			children[i] = createChild();
			children[i].energyPoints = 200 + (int) (energyPoints / amount * getEnergyTransfer() / 100f);
		}
		energyPoints = (int) (energyPoints * ((100 - getEnergyTransfer()) / 100f));

		return children;
	}

	public Creature createChild() {
		Creature child = new Creature(worldObj);
		for ( String key : variables.keySet() ) {
			if ( rand.nextInt(100) < 50 ) {// pick mother for dominant trait
				int value = rand.nextIntTendToward(this.variables.get(key), childFather.variables.get(key));
				child.variables.put(key, value);
			} else {// pick father for dominant trait
				child.variables.put(key, rand.nextIntTendToward(childFather.variables.get(key), this.variables.get(key)));
			}
		}

		child.isMale = rand.chance(30, 100);

		for ( FoodType key : foodUptakes.keySet() ) {
			if ( rand.nextInt(100) < 50 ) {// pick mother for dominant trait
				child.foodUptakes.put(key, getMutatedValue(
						rand.nextFloatTendToward(this.foodUptakes.get(key).intValue(), childFather.foodUptakes.get(key).intValue())));
			} else {// pick father for dominant trait
				child.foodUptakes.put(key, getMutatedValue(
						rand.nextFloatTendToward(childFather.foodUptakes.get(key).intValue(), this.foodUptakes.get(key).intValue())));
			}
		}
		child.age = gestationPeriod();
		child.color = Utils.getAverageColor(this.color, childFather.color);
		child.genCount = Math.max(this.genCount, childFather.genCount) + 1;
		return child;
	}

	public float getMutatedValue(float val) {
		return (float) rand.normalDeviate(val, (100 - getGeneStability()) / 50.0);
	}

	private float getGeneStability() {
		return variables.get("geneStability");
	}

}
