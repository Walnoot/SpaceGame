package walnoot.space;

import static com.badlogic.gdx.Input.Keys.A;
import static com.badlogic.gdx.Input.Keys.D;
import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.LEFT;
import static com.badlogic.gdx.Input.Keys.RIGHT;
import static com.badlogic.gdx.Input.Keys.S;
import static com.badlogic.gdx.Input.Keys.SPACE;
import static com.badlogic.gdx.Input.Keys.UP;
import static com.badlogic.gdx.Input.Keys.W;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;

public class InputHandler implements InputProcessor{
	public static final InputHandler instance = new InputHandler();
	
	public Key up = new Key(W, UP);
	public Key down = new Key(S, DOWN);
	public Key left = new Key(A, LEFT);
	public Key right = new Key(D, RIGHT);
	
	public Key turnModule = new Key(SPACE);
	
	private ArrayList<Key> keys;
	private boolean keyDown;
	private int scrollAmount;
	private boolean justTouched;
	
	private InputHandler(){
	}
	
	/**
	 * Make sure to call after game logic update() is called
	 */
	public void update(){
		for(int i = 0; i < keys.size(); i++){
			keys.get(i).update();
		}
		
		keyDown = false;
		scrollAmount = 0;
		justTouched = false;
	}
	
	public boolean isAnyKeyDown(){
		return keyDown;
	}
	
	public int getScrollAmount(){
		return scrollAmount;
	}
	
	public boolean isJustTouched(){
		return justTouched;
	}
	
	public boolean keyDown(int keyCode){
		for(int i = 0; i < keys.size(); i++){
			if(keys.get(i).has(keyCode)) keys.get(i).press();
		}
		
		keyDown = true;
		
		return false;
	}
	
	public boolean keyUp(int keyCode){
		for(int i = 0; i < keys.size(); i++){
			if(keys.get(i).has(keyCode)) keys.get(i).release();
		}
		
		return false;
	}
	
	public boolean keyTyped(char character){
		return false;
	}
	
	public boolean touchDown(int x, int y, int pointer, int button){
		if(button == Buttons.LEFT) justTouched = true;
		return false;
	}
	
	public boolean touchUp(int x, int y, int pointer, int button){
		return false;
	}
	
	public boolean touchDragged(int x, int y, int pointer){
		return false;
	}
	
	public boolean touchMoved(int x, int y){
		return false;
	}
	
	public boolean scrolled(int amount){
		scrollAmount += amount;
		
		return false;
	}
	
	public boolean mouseMoved(int screenX, int screenY){
		return false;
	}
	
	public class Key{
		private final int[] keyCodes;
		private boolean pressed, justPressed;
		
		public Key(int... keyCodes){
			this.keyCodes = keyCodes;
			
			if(keys == null) keys = new ArrayList<Key>();
			keys.add(this);
		}
		
		private void update(){
			justPressed = false;
		}
		
		public boolean has(int keyCode){
			for(int i = 0; i < keyCodes.length; i++){
				if(keyCodes[i] == keyCode) return true;
			}
			
			return false;
		}
		
		private void press(){
			pressed = true;
			justPressed = true;
		}
		
		private void release(){
			pressed = false;
		}
		
		public boolean isPressed(){
			return pressed;
		}
		
		public boolean isJustPressed(){
			return justPressed;
		}
	}
}
