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

public class Block2 implements Containable2 {
	
	public String nameID;
	public List<Containable2> fields;
	
	public List<Block2> getAllSubBlocks() {
		List<Block2> all = new ArrayList<>();
		for (Containable2 c : fields) {
			if (c instanceof Block2) {
				all.add((Block2) c);
				all.addAll(((Block2) c).getAllSubBlocks());
			} else if (c instanceof Blocks2) {
				all.addAll(((Blocks2) c).getAllSubBlocks());
			}
		}
		
		return all;
	}
	
	public List<Block2> getAllLines() {
		List<Block2> all = new ArrayList<>();
		for (Containable2 c : fields) {
			if (c instanceof Block2) {
			} else if (c instanceof Blocks2) {
				all.addAll(((Blocks2) c).getAllLines());
			}
		}
		
		return all;
	}
	
	public static final JsonDeserializer<Block2> deser = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
		Block2 b = new Block2();
		JsonArray jArray = (JsonArray) json;
		b.nameID = jArray.get(0).getAsString();
		b.fields = new ArrayList<>(jArray.size() - 1);
		for (int i = 1; i < jArray.size(); i++) {
			b.fields.add(context.deserialize(jArray.get(i), Containable2.class));
		}
		return b;
	};
	
	public static final JsonSerializer<Block2> ser = (Block2 b, Type typeOfT, JsonSerializationContext context) -> {
		JsonArray value = new JsonArray();
		value.add(b.nameID);
		for (Containable2 c : b.fields) {
			value.add(context.serialize(c));
		}
		return value;
	};
	
	public String toString() {
		return "Block [" + (nameID != null ? "nameID=" + nameID + ", " : "") + (fields != null ? "fields=" + fields : "") + "]";
	}
	
}