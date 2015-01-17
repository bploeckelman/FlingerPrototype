package lando.systems.prototype.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import lando.systems.prototype.Prototype;
import lando.systems.prototype.View;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width     = View.VIEW_WIDTH;
		config.height    = View.VIEW_HEIGHT;
		config.resizable = false;
		new LwjglApplication(new Prototype(), config);
	}
}
