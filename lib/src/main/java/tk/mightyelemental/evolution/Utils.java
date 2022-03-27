package tk.mightyelemental.evolution;

import java.util.Map;
import java.util.stream.Stream;

import org.newdawn.slick.Color;

public class Utils {

	public static <K, V> Stream<K> keys(Map<K, V> map, V value) {
	    return map
	      .entrySet()
	      .stream()
	      .filter(entry -> value.equals(entry.getValue()))
	      .map(Map.Entry::getKey);
	}
	
	public static <K> float minimumValue(Map<K,Float> map) {
		return map.values().stream().min(Float::compare).get();
	}
	
	public static float getDistance(float x1, float y1, float x2, float y2) {
		double d1 = x1 - x2;
		double d2 = y1 - y2;
		d1 = Math.pow(d1, 2);
		d2 = Math.pow(d2, 2);
		float d = (float) Math.sqrt(d1 + d2);
		return d;
	}
	
	public static Color getAverageColor(Color c1, Color c2) {
		float r = (c1.r + c2.r)/2f;
		float g = (c1.g + c2.g)/2f;
		float b = (c1.b + c2.b)/2f;
		return new Color(r,g,b,1f);
	}
	
}
