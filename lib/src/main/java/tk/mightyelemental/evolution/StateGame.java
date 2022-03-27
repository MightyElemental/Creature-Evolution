package tk.mightyelemental.evolution;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class StateGame extends BasicGameState {

	public StateGame() {

	}

	public World worldObj;

	public Camera cam = new Camera();

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		worldObj = new World(gc.getWidth(), gc.getHeight());
		worldObj.populate();
		simThread.start();
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.translate(cam.xOffset, cam.yOffset);
		g.scale(cam.scale, cam.scale);
		worldObj.draw(gc, sbg, g);
		g.resetTransform();
		drawHUD(gc, sbg, g);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {

	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void drawHUD(GameContainer gc, StateBasedGame sbg, Graphics g) {
		int males = worldObj.getCreaturesBySex(true).length;
		int females = worldObj.entities.size() - males;
		g.setColor(Color.white);
		g.drawString("Males: " + males, 10, 10);
		g.drawString("Females: " + females, 10, 25);
		g.drawString("Total Died: " + worldObj.totalDied, 10, 40);
		g.drawString("Update Speed: " + pauseTime + " (" + (15.0 / pauseTime) + "x)", 10, 55);
	}

	public int		pauseTime	= 15;
	public boolean	boost		= false;

	Thread simThread = new Thread("sim_thread") {
		public void run() {
			while (true) {
				worldObj.update();
				try {
					if ( !boost ) {
						sleep(pauseTime);
					} else {
						sleep(0);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	@Override
	public void keyPressed(int key, char c) {
		if ( key == Input.KEY_NUMPAD0 ) {
			boost = true;
		}
	}

	@Override
	public void keyReleased(int key, char c) {
		if ( key == Input.KEY_ADD ) {
			pauseTime++;
		}
		if ( key == Input.KEY_SUBTRACT ) {
			pauseTime--;
			if ( pauseTime < 0 ) pauseTime = 0;
		}
		if ( key == Input.KEY_NUMPAD0 ) {
			boost = false;
		}
	}

	int mouseX, mouseY;

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		mouseX = newx;
		mouseY = newy;
		super.mouseMoved(oldx, oldy, newx, newy);
	}

	@Override
	public void mouseWheelMoved(int newValue) {
		cam.zoom(newValue, mouseX, mouseY);
		super.mouseWheelMoved(newValue);
	}

	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		cam.drag(oldx, oldy, newx, newy);
		super.mouseDragged(oldx, oldy, newx, newy);
	}

}
