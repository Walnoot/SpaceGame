package walnoot.space;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class SpaceMain{
	public static void main(String[] args){
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "SpaceGame";
		cfg.useGL20 = true;
		cfg.width = 480;
		cfg.height = 320;
		
		new LwjglApplication(new SpaceGame(), cfg);
	}
}
