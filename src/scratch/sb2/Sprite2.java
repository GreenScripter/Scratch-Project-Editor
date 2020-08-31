package scratch.sb2;

import java.util.ArrayList;
import java.util.List;

public class Sprite2 implements Child2 {
	
	public String objName;
	public List<Script2> scripts;
	
	public List<Variable2> variables;
	public List<ScratchList2> lists;
	public List<Sound2> sounds;
	public List<Costume2> costumes;
	public int currentCostumeIndex;
	public int indexInLibrary;
	public double scratchX;
	public double scratchY;
	public double scale;
	public double direction;
	public String rotationStyle;
	public boolean isDraggable;
	public boolean visible;
	public SpriteInfo2 spriteInfo;
	
	public List<Block2> getAllBlocks() {
		List<Block2> all = new ArrayList<>();
		if (scripts != null) for (Script2 s : scripts) {
			all.addAll(s.getAllSubBlocks());
		}
		return all;
	}
	
	public String toString() {
		return "Sprite [" + (objName != null ? "objName=" + objName + ", " : "") + (variables != null ? "variables=" + variables + ", " : "") + (lists != null ? "lists=" + lists + ", " : "") + (sounds != null ? "sounds=" + sounds + ", " : "") + (costumes != null ? "costumes=" + costumes + ", " : "") + (scripts != null ? "scripts=" + scripts + ", " : "") + "currentCostumeIndex=" + currentCostumeIndex + ", indexInLibrary=" + indexInLibrary + ", scratchX=" + scratchX + ", scratchY=" + scratchY + ", scale=" + scale + ", direction=" + direction + ", " + (rotationStyle != null ? "rotationStyle=" + rotationStyle + ", " : "") + "isDraggable=" + isDraggable + ", visible=" + visible + ", " + (spriteInfo != null ? "spriteInfo=" + spriteInfo : "") + "]";
	}
}