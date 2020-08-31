package scratch.sb3;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ArrayBlock implements Block {
	
	public ABType type = ABType.NUMBER;
	
	//Has either a value or a name and contentID
	public String value;
	
	public String name;
	public String contentID;
	
	//Top level variables have x and y
	public Double x;
	public Double y;
	
	public static final JsonDeserializer<ArrayBlock> deser = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
		ArrayBlock i = new ArrayBlock();
		JsonArray array = json.getAsJsonArray();
		i.type = ABType.getByID(array.get(0).getAsInt());
		
		switch (i.type) {
			
			case LIST:
			case VARIABLE:
				if (array.size() > 3) {
					i.x = array.get(3).getAsDouble();
					i.y = array.get(4).getAsDouble();
				}
			case BROADCAST:
				i.name = array.get(1).getAsString();
				i.contentID = array.get(2).getAsString();
				break;
			
			case POSITIVE_INTEGER:
			case POSITIVE_NUMBER:
			case INTEGER:
			case NUMBER:
			case ANGLE:
				if (array.get(1).getAsJsonPrimitive().isString()) {
					i.value = array.get(1).getAsString();
				} else {
					String s = (array.get(1).getAsDouble() + "");
					if (s.endsWith(".0")) {
						i.value = s.substring(0, s.length() - 2);
					} else {
						i.value = s;
					}
				}
				break;
			
			case COLOR:
			case STRING:
				i.value = array.get(1).getAsString();
				break;
			
			default:
				break;
			
		}
		
		return i;
	};
	
	public static final JsonSerializer<ArrayBlock> ser = (ArrayBlock i, Type typeOfT, JsonSerializationContext context) -> {
		JsonArray array = new JsonArray();
		array.add(i.type.id);
		switch (i.type) {
			
			case LIST:
			case VARIABLE:
			case BROADCAST:
				array.add(i.name);
				array.add(i.contentID);
				break;
			case POSITIVE_INTEGER:
			case POSITIVE_NUMBER:
			case INTEGER:
			case NUMBER:
			case ANGLE:
			case COLOR:
			case STRING:
				array.add(i.value);
				break;
			
			default:
				break;
			
		}
		if (i.x != null) {
			array.add(i.x);
			array.add(i.y);
		}
		return array;
	};
	
	public enum ABType {
		NUMBER(4), POSITIVE_NUMBER(5), POSITIVE_INTEGER(6), INTEGER(7), ANGLE(8), COLOR(9), STRING(10), BROADCAST(11), VARIABLE(12), LIST(13);
		
		public final int id;
		
		private ABType(int id) {
			this.id = id;
		}
		
		public static ABType getByID(int id) {
			if (id == NUMBER.id) return NUMBER;
			if (id == POSITIVE_NUMBER.id) return POSITIVE_NUMBER;
			if (id == POSITIVE_INTEGER.id) return POSITIVE_INTEGER;
			if (id == INTEGER.id) return INTEGER;
			if (id == ANGLE.id) return ANGLE;
			if (id == COLOR.id) return COLOR;
			if (id == STRING.id) return STRING;
			if (id == BROADCAST.id) return BROADCAST;
			if (id == VARIABLE.id) return VARIABLE;
			if (id == LIST.id) return LIST;
			return null;
		}
	}
}
