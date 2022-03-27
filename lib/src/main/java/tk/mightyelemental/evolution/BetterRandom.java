package tk.mightyelemental.evolution;

import java.util.Map;
import java.util.Random;

public class BetterRandom extends Random {

	private static final long serialVersionUID = -8780148215314110260L;

	public BetterRandom(long currentTimeMillis) {
		super(currentTimeMillis);
	}

	public <T> T randFromArray(T[] arr) {
		return arr[this.nextInt(arr.length)];
	}

	@SuppressWarnings("unchecked")
	public <T, K> T randKeyFromMap(Map<T, K> map) {
		return (T) randFromArray(map.keySet().toArray());
	}

	public int nextInt(int min, int max) {
		// System.out.println(min + "|" + max + "|" + (nextDouble() * (max - min) +
		// min));
		return (int) Math.round(nextDouble() * (max - min) + min);
	}

	public int linearDeviate(int base, int sd) {
		return nextInt(base - sd, base + sd);
	}

	public double normalDeviate(double base, double sd) {
		double result = (this.nextDouble() - this.nextDouble()) * sd + base;
		return result;
	}

	public double nextBoolInt(double base, double diff) {
		return (nextDouble() > 0.5 ? 1 : -1) * diff + base;
	}

	public int nextIntTendToward(int min, int max) {
		double result = Math.abs(this.nextDouble() - this.nextDouble()) * (max - min) + min;
		return (int) Math.floor(result);
	}

	public float nextFloatTendToward(float min, float max) {
		double result = Math.abs(this.nextDouble() - this.nextDouble()) * (max - min) + min;
		return (float) result;
	}

	public <T> T tendTowardStart(T[] arr) {
		double result = Math.abs(this.nextDouble() - this.nextDouble()) * (arr.length);
		return arr[(int) Math.floor(result)];
	}

	/**
	 * The chance of true.<br>
	 * e.g. chance(30, 100) would be 30% chance of being true<br>
	 * or chance(70,80) would be 87.5% chance of being true
	 */
	public boolean chance(float thresh, float total) {
		return this.nextFloat() * total < thresh;
	}

}
