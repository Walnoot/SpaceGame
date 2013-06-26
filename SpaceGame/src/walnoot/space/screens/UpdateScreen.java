package walnoot.space.screens;

import com.badlogic.gdx.Screen;

public abstract class UpdateScreen implements Screen{
	@Override
	public void render(float delta){
		render();
	}
	
	protected abstract void render();
	
	public abstract void update();
}
