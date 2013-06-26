package walnoot.space.modules;

import com.badlogic.gdx.graphics.g3d.Model;

public class ModuleType{
	public final Model model;
	public final float width, height;
	
	public ModuleType(Model model, float width, float height){
		this.model = model;
		this.width = width;
		this.height = height;
	}
}
