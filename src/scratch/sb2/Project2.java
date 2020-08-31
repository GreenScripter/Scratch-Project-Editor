package scratch.sb2;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;

import scratch.generic.Project;

public class Project2 extends Project {
	
	public String objName;
	public List<Costume2> costumes;
	public List<Sound2> sounds;
	public int currentCostumeIndex;
	public String penLayerMD5;
	public int penLayerID;
	public double tempoBPM;
	public double videoAlpha;
	public List<Child2> children;
	public Info2 info;
	
	public List<Variable2> variables;
	public List<ScratchList2> lists;
	public List<Script2> scripts;
	
	public List<Sprite2> getSprites() {
		List<Sprite2> sprites = new ArrayList<>();
		for (Child2 c : children) {
			if (c instanceof Sprite2) {
				sprites.add((Sprite2) c);
			}
		}
		return sprites;
	}
	
	public List<Script2> getScripts() {
		List<Sprite2> sprites = getSprites();
		List<Script2> scripts = new ArrayList<>();
		for (Sprite2 s : sprites) {
			if (s.scripts != null) {
				scripts.addAll(s.scripts);
			}
		}
		if (this.scripts != null) {
			scripts.addAll(this.scripts);
		}
		
		return scripts;
	}
	
	public List<Block2> getBlocks() {
		List<Sprite2> sprites = getSprites();
		List<Block2> scripts = new ArrayList<>();
		for (Sprite2 s : sprites) {
			if (s.scripts != null) {
				scripts.addAll(s.getAllBlocks());
			}
		}
		if (this.scripts != null) {
			for (Script2 s : this.scripts) {
				scripts.addAll(s.getAllSubBlocks());
			}
		}
		
		return scripts;
	}
	
	public static GsonBuilder registerAdapters(GsonBuilder builder) {
		
		builder.registerTypeAdapter(Containable2.class, Containable2.deser);
		builder.registerTypeAdapter(Containable2.class, Containable2.ser);
		
		builder.registerTypeAdapter(Child2.class, Child2.deser);
		builder.registerTypeAdapter(Child2.class, Child2.ser);
		
		builder.registerTypeAdapter(Script2.class, Script2.ser);
		builder.registerTypeAdapter(Script2.class, Script2.deser);
		
		builder.registerTypeAdapter(Block2.class, Block2.deser);
		builder.registerTypeAdapter(Block2.class, Block2.ser);
		
		builder.registerTypeAdapter(Value2.class, Value2.deser);
		builder.registerTypeAdapter(Value2.class, Value2.ser);
		
		return builder;
	}
}