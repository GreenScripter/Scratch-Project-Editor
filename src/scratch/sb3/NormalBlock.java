package scratch.sb3;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class NormalBlock implements Block {
	
	public String opcode;
	public String next;
	public String parent;
	public boolean shadow;
	public boolean topLevel;
	public Map<String, Field> fields;
	public Map<String, Input> inputs;
	
	//optional, x and y for top level blocks only.
	public Double x;
	public Double y;
	public String comment;
	//so far it appears this field is only used for defining custom blocks. 
	//It's used in a weird way and not fully represented in this library. 
	//To interact with it, see the Obfuscater class for examples, or look at the value in a project.json file.
	public JsonElement mutation;
	
	public static final JsonSerializer<NormalBlock> ser = (NormalBlock i, Type typeOfT, JsonSerializationContext context) -> {
		JsonObject o = new JsonObject();
		o.add("opcode", new JsonPrimitive(i.opcode));
		if (i.next == null) {
			o.add("next", JsonNull.INSTANCE);
		} else
			o.add("next", new JsonPrimitive(i.next));
		if (i.parent == null) {
			o.add("parent", JsonNull.INSTANCE);
		} else
			o.add("parent", new JsonPrimitive(i.parent));
		o.add("shadow", new JsonPrimitive(i.shadow));
		o.add("topLevel", new JsonPrimitive(i.topLevel));
		o.add("fields", context.serialize(i.fields));
		o.add("inputs", context.serialize(i.inputs));
		if (i.x != null) o.add("x", new JsonPrimitive(i.x));
		if (i.y != null) o.add("y", new JsonPrimitive(i.y));
		if (i.comment != null) o.add("comment", new JsonPrimitive(i.comment));
		if (i.mutation != null) o.add("mutation", i.mutation);
		
		return o;
	};
	
	public static final JsonSerializer<Object> genericSer = (Object i, Type typeOfT, JsonSerializationContext context) -> {
		
		return Project3.registerAdapters(new GsonBuilder()).create().toJsonTree(i);
	};
}
