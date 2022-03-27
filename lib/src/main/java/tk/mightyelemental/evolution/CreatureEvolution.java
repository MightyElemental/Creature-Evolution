package tk.mightyelemental.evolution;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class CreatureEvolution extends StateBasedGame {

	public static BetterRandom rand = new BetterRandom(System.nanoTime());

	public CreatureEvolution() {
		super("Creature Evolution");
		addState(new StateGame());
	}

	public static void initNatives() {
		String path = "windows"; // default OS

		// Change natives path depending on which OS is in use
		if ( SystemUtils.IS_OS_LINUX ) {
			path = "linux";
		} else if ( SystemUtils.IS_OS_WINDOWS ) {
			path = "windows";
		} else if ( SystemUtils.IS_OS_MAC_OSX ) {
			path = "osx";
		}

		String nativesPath = new File("build/natives/" + path).getAbsolutePath();
		System.setProperty("org.lwjgl.librarypath", nativesPath);
		System.setProperty("net.java.games.input.librarypath", nativesPath);
	}

	public static void main(String[] args) {
		initNatives();
		setupClient();
	}

	@Override
	public void initStatesList(GameContainer arg0) throws SlickException {

	}

	private static void setupClient() {
		AppGameContainer appGc;
		try {
			appGc = new AppGameContainer(new CreatureEvolution());
			appGc.setDisplayMode(1280, (int) (1280 / 16.0 * 9.0), false);
			appGc.setTargetFrameRate(60);
			appGc.setShowFPS(false);
			appGc.setAlwaysRender(true);
			appGc.setUpdateOnlyWhenVisible(false);
			appGc.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

}
