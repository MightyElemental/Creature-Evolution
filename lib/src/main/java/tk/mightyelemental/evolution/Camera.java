package tk.mightyelemental.evolution;

import org.newdawn.slick.geom.Vector2f;

public class Camera {

	public float	scale			= 1f;
	public float	xOffset			= 0;
	public float	yOffset			= 0;
	public boolean	bStickyPlayer	= true;

	public void zoom(float value, float relX, float relY) {// TEST: zoom about center
		// System.out.println(dScale);
		if ( value < 0 ) {
			if ( scale <= 0.1f ) {
				scale = 0.1f;
			} else {
				scale *= 0.9;
				xOffset = -relX * (0.9f - 1f) + 0.9f * xOffset;
				yOffset = -relY * (0.9f - 1f) + 0.9f * yOffset;
			}
		} else {
			if ( scale >= 3f ) {
				scale = 3f;
			} else {
				scale *= 1.1;
				xOffset = -relX * (1.1f - 1f) + 1.1f * xOffset;
				yOffset = -relY * (1.1f - 1f) + 1.1f * yOffset;
			}
		}

		// if ( dScale < 0.1f ) {
		// dScale = 0.1f;
		// }
		// if ( dScale > 3f ) {
		// dScale = 3f;
		// }
	}

	public void drag(int oldx, int oldy, int newx, int newy) {
		xOffset -= oldx - newx;
		yOffset -= oldy - newy;
	}

	public Vector2f getAdjustedPoint(float x, float y) {
		return new Vector2f((x * scale + xOffset), (y * scale + yOffset));
	}

}
