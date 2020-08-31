package scratch.sb2;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Script2 {
	
	public int x;
	public int y;
	public List<Block2> blocks;
	
	public boolean isUsable() {
		if (blocks == null) return false;
		if (blocks.size() == 0) return false;
		Block2 b = blocks.get(0);
		if (b.nameID.equals("whenSceneStarts") || b.nameID.equals("whenClicked") || b.nameID.equals("whenKeyPressed") || b.nameID.equals("whenSensorGreaterThan")) {
			return true;
		} else if (b.nameID.equals("whenGreenFlag") || b.nameID.equals("procDef") || b.nameID.equals("whenCloned") || b.nameID.equals("whenIReceive")) {
			return true;
		}
		return false;
	}
	
	public List<Block2> getAllSubBlocks() {
		List<Block2> all = new ArrayList<>();
		for (Block2 b : blocks) {
			all.add(b);
			all.addAll(b.getAllSubBlocks());
		}
		return all;
	}
	
	public List<Block2> getAllLines() {
		List<Block2> all = new ArrayList<>();
		for (Block2 b : blocks) {
			all.add(b);
			all.addAll(b.getAllLines());
		}
		return all;
	}
	
	public static final JsonDeserializer<Script2> deser = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
		Script2 b = new Script2();
		JsonArray jArray = (JsonArray) json;
		b.x = jArray.get(0).getAsInt();
		b.y = jArray.get(1).getAsInt();
		jArray = (JsonArray) jArray.get(2);
		b.blocks = new ArrayList<>(jArray.size());
		for (int i = 0; i < jArray.size(); i++) {
			b.blocks.add(context.deserialize(jArray.get(i), Block2.class));
		}
		return b;
	};
	
	public static final JsonSerializer<Script2> ser = (Script2 b, Type typeOfT, JsonSerializationContext context) -> {
		JsonArray value = new JsonArray();
		value.add(b.x);
		value.add(b.y);
		JsonArray value2 = new JsonArray();
		value.add(value2);
		
		for (Block2 c : b.blocks) {
			value2.add(context.serialize(c));
		}
		return value;
	};
	
	public String toString() {
		return "Script [x=" + x + ", y=" + y + ", " + (blocks != null ? "blocks=" + blocks : "") + "]";
	}
}