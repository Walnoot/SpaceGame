package walnoot.space.modules;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Module{
	public static final Color DEFAULT_COLOR = Color.LIGHT_GRAY;
	
	private final Vector2 pos = new Vector2();//position relative to the ship's main module
	private Module parent;
	private Array<Module> children = new Array<Module>(false, 4);
	private ModuleType type;
	private ModelInstance instance;
	
	private int rotation = 0;
	
	public Module(float x, float y, ModuleType type){
		pos.set(x, y);
		this.type = type;
		
		instance = new ModelInstance(type.model, 0f, 0f, 0f);
		updateTransform();
		
		setColor(DEFAULT_COLOR);
	}
	
	public Module(Vector2 pos, ModuleType type){
		this(pos.x, pos.y, type);
	}
	
	/**
	 * Creates main module.
	 */
	public Module(ModuleType type){
		this(0f, 0f, type);
	}
	
	public void render(ModelBatch modelBatch, Lights lights){
		modelBatch.render(instance, lights);
		
		for(Module child : children)
			child.render(modelBatch, lights);
	}
	
	private void updateTransform(){
		instance.transform.idt().translate(-pos.x, 0f, pos.y).rotate(Vector3.Y, 90f * rotation);
	}
	
	public void addModule(Module module){
		children.add(module);
		
		module.parent = this;
	}
	
	/**
	 * @param rotation
	 *            - The new rotation, where degrees = 90 * rotation,
	 *            meaning an rotation of 1 will rotate the module 90 degrees counter-clockwise.
	 */
	public void setRotation(int rotation){
		this.rotation = rotation;
		
		updateTransform();
	}
	
	public void setPosition(float x, float y){
		pos.set(x, y);
		
		updateTransform();
	}
	
	public void rotate(int i){
		rotation += i;
		
		updateTransform();
	}
	
	public void setColor(Color color){
		((ColorAttribute) instance.materials.get(0).get(ColorAttribute.Diffuse)).color.set(color);
	}
	
	public Array<Module> getChildren(){
		return children;
	}
	
	public ModuleType getType(){
		return type;
	}
	
	public float getWidth(){
		return (rotation % 2 == 0) ? type.width : type.height;
	}
	
	public float getHeight(){
		return (rotation % 2 == 1) ? type.width : type.height;
	}
	
	public Vector2 getPos(){
		return pos;
	}
}
