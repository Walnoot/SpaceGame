package walnoot.space;

import walnoot.space.screens.EditScreen;
import walnoot.space.screens.UpdateScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;

public class SpaceGame extends Game{
	public static final float UPDATES_PER_SECOND = 60f;
	public static final float SECONDS_PER_UPDATE = 1 / UPDATES_PER_SECOND;
	
	private UpdateScreen updateScreen;
	private float unprocessedSeconds;
	
	@Override
	public void create(){
		setScreen(new EditScreen());
		
		Gdx.input.setInputProcessor(InputHandler.instance);
	}
	
	@Override
	public void render(){
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)) Gdx.app.exit();
		
		unprocessedSeconds += Gdx.graphics.getDeltaTime();
		while(unprocessedSeconds > SECONDS_PER_UPDATE){
			unprocessedSeconds -= SECONDS_PER_UPDATE;
			
			updateScreen.update();
			InputHandler.instance.update();
		}
		
		super.render();
	}
	
	public boolean needsGL20(){
		return true;
	}
	
	@Override
	public void setScreen(Screen screen){
		if(!(screen instanceof UpdateScreen))
			throw new IllegalArgumentException("Screen must be instace of UpdateScreen");
		
		super.setScreen(screen);
		updateScreen = (UpdateScreen) screen;
	}
}
