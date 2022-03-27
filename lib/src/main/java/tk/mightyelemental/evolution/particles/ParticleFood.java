package tk.mightyelemental.evolution.particles;

public class ParticleFood extends Particle {

	private static final long serialVersionUID = -1666134437192781998L;

	public ParticleFood(float x, float y, FoodType ft, int amount) {
		super(x, y, Math.min(amount * 0.5f + 1, 5.5f));
		this.type = ft;
		this.amount = amount;
	}

	public FoodType	type;
	public int		amount;

	public void eat() {
		destroy = true;
	}

}
