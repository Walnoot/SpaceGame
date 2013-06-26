package walnoot.space.screens;

import walnoot.space.InputHandler;
import walnoot.space.SpaceGame;
import walnoot.space.Util;
import walnoot.space.modules.Module;
import walnoot.space.modules.ModuleType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

public class EditScreen extends UpdateScreen{
	public static final float MIN_SNAP_OVERLAP = 0.5f;
	private static final float CAM_SPEED = 3f;
	
	public PerspectiveCamera cam;
	public CameraInputController camController;
	public ModelBatch modelBatch;
	public AssetManager assetManager;
	public Lights lights;
	public boolean loading;
	
	private Module main;
	private Module newModule;
	
	private Array<Module> modules = new Array<Module>();
	
	private ModelInstance grid;
	
	private Plane xyPlane;
	private ModuleType type;
	
	public EditScreen(){
		modelBatch = new ModelBatch();
		lights = new Lights();
		lights.ambientLight.set(0.4f, 0.4f, 0.4f, 1f);
		lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0f, 10f, 0f);
		cam.direction.set(0f, -1f, 0f);
		cam.up.set(0f, 0f, 1f);
		cam.near = 0.1f;
		cam.far = 300f;
		cam.update();
		
		assetManager = new AssetManager();
		assetManager.load("test.obj", Model.class);
		assetManager.load("grid.obj", Model.class);
		loading = true;
		
		xyPlane = new Plane(Vector3.Y, 0f);
	}
	
	private void doneLoading(){
		loading = false;
		
		grid = new ModelInstance(assetManager.get("grid.obj", Model.class));
		
		Model ship = assetManager.get("test.obj", Model.class);
		type = new ModuleType(ship, 2f, 2f);
		
		main = new Module(type);
		main.addModule(new Module(2.5f, 0f, type));
		
		modules.add(main);
		modules.addAll(main.getChildren());
		
		newModule = new Module(type);
	}
	
	@Override
	public void render(){
		if(loading && assetManager.update()) doneLoading();
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		modelBatch.begin(cam);
		
		if(!loading){
			main.render(modelBatch, lights);
			newModule.render(modelBatch, lights);
			
			modelBatch.render(grid, lights);
		}
		
		modelBatch.end();
	}
	
	@Override
	public void update(){
		if(InputHandler.instance.turnModule.isJustPressed()) newModule.rotate(1);
		
		if(!loading) handleMouse(Gdx.input.getX(), Gdx.input.getY());
		
		if(InputHandler.instance.up.isPressed()){
			cam.translate(0f, 0f, SpaceGame.SECONDS_PER_UPDATE * CAM_SPEED);
			cam.update(false);
		}
		if(InputHandler.instance.down.isPressed()){
			cam.translate(0f, 0f, -SpaceGame.SECONDS_PER_UPDATE * CAM_SPEED);
			cam.update(false);
		}
		if(InputHandler.instance.left.isPressed()){
			cam.translate(SpaceGame.SECONDS_PER_UPDATE * CAM_SPEED, 0f, 0f);
			cam.update(false);
		}
		if(InputHandler.instance.right.isPressed()){
			cam.translate(-SpaceGame.SECONDS_PER_UPDATE * CAM_SPEED, 0f, 0f);
			cam.update(false);
		}
	}
	
	private void handleMouse(int x, int y){
		Ray pickRay = cam.getPickRay(x, y);
		Vector3 intersection = Util.TMP_3;
		
		Intersector.intersectRayPlane(pickRay, xyPlane, intersection);
		
		newModule.setPosition(-(intersection.x + cam.position.x), intersection.z + cam.position.z);
		
		Module dest = handleSnap();
		
		Rectangle newBB = Rectangle.tmp;//of newModule
		Rectangle checkBB = Rectangle.tmp2;//of the modules of ship
		
		setBB(newBB, newModule);
		
		boolean intersects = false;
		
		for(Module module : modules){
			setBB(checkBB, module);
			
			module.setColor(Module.DEFAULT_COLOR);
			
			if(newBB.overlaps(checkBB)){
				intersects = true;
				module.setColor(Color.BLUE);
				
				break;
			}
		}
		
		boolean canPlace = !intersects && (dest != null);
		
		if(canPlace) newModule.setColor(Color.GREEN);
		else newModule.setColor(Color.RED);
		
		if(canPlace && InputHandler.instance.isJustTouched()){
			newModule.setColor(Module.DEFAULT_COLOR);
			dest.addModule(newModule);
			
			modules.add(newModule);
			
			newModule = new Module(type);
		}
	}
	
	private void setBB(Rectangle bb, Module module){
		float width = module.getWidth() - 0.001f;
		float height = module.getHeight() - 0.001f;
		
		bb.set(module.getPos().x - width * 0.5f, module.getPos().y - height * 0.5f, width, height);
	}
	
	/**
	 * @return - The module the new module snaps to, null if no snappage occurs.
	 */
	private Module handleSnap(){
		float snapCoordinate = 0f;//x or y coordinate of the snapping point
		boolean snapX = false, snapY = false;//whether to snap  horizontally or vertically, if at all
		float snapDist = .5f;//distance to snapping line, closest line gets picked
		Module dest = null;
		
		for(Module module : modules){
			if(Math.abs(newModule.getPos().y - module.getPos().y) + MIN_SNAP_OVERLAP < (module.getHeight() + newModule
					.getHeight()) * 0.5f){
				float targetXLeft = module.getPos().x - (module.getWidth() + newModule.getWidth()) * 0.5f;
				float targetXRight = module.getPos().x + (module.getWidth() + newModule.getWidth()) * 0.5f;
				
				float snapLeft = snapX(targetXLeft, snapDist);
				float snapRight = snapX(targetXRight, snapDist);
				
				float targetX;
				
				float newSnapDist;
				
				if(snapLeft < snapDist || snapRight < snapDist){
					if(snapLeft < snapRight){
						newSnapDist = snapLeft;
						targetX = targetXLeft;
					}else{
						newSnapDist = snapRight;
						targetX = targetXRight;
					}
					
					snapDist = newSnapDist;
					
					snapX = true;
					snapY = false;
					
					dest = module;
					
					snapCoordinate = targetX;
				}
			}
			
			if(Math.abs(newModule.getPos().x - module.getPos().x) + MIN_SNAP_OVERLAP < (module.getWidth() + newModule
					.getWidth()) * 0.5f){
				float targetYBottom = module.getPos().y - (module.getHeight() + newModule.getHeight()) * 0.5f;
				float targetYTop = module.getPos().y + (module.getHeight() + newModule.getHeight()) * 0.5f;
				
				float snapLeft = snapY(targetYBottom, snapDist);
				float snapRight = snapY(targetYTop, snapDist);
				
				float targetY;
				
				float newSnapDist;
				
				if(snapLeft < snapDist || snapRight < snapDist){
					if(snapLeft < snapRight){
						newSnapDist = snapLeft;
						targetY = targetYBottom;
					}else{
						newSnapDist = snapRight;
						targetY = targetYTop;
					}
					
					snapDist = newSnapDist;
					
					snapX = false;
					snapY = true;
					
					dest = module;
					
					snapCoordinate = targetY;
				}
			}
		}
		
		if(snapX && snapY) throw new IllegalStateException("Can't snap both x and y");
		if(snapX) newModule.setPosition(snapCoordinate, newModule.getPos().y);
		if(snapY) newModule.setPosition(newModule.getPos().x, snapCoordinate);
		
		return dest;
	}
	
	private float snapX(float targetX, float snapDist){
		float dx = newModule.getPos().x - targetX;
		
		if(Math.abs(dx) < snapDist){
			snapDist = Math.abs(dx);
		}
		
		return snapDist;
	}
	
	private float snapY(float targetY, float snapDist){
		float dy = newModule.getPos().y - targetY;
		
		if(Math.abs(dy) < snapDist){
			snapDist = Math.abs(dy);
		}
		
		return snapDist;
	}
	
	@Override
	public void resize(int width, int height){
	}
	
	@Override
	public void show(){
	}
	
	@Override
	public void hide(){
	}
	
	@Override
	public void pause(){
	}
	
	@Override
	public void resume(){
	}
	
	@Override
	public void dispose(){
		modelBatch.dispose();
		assetManager.dispose();
	}
}
