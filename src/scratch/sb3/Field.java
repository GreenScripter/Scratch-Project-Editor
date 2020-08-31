package scratch.sb3;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Field {
	
	public String value;
	public String id;
	
	public static final JsonDeserializer<Field> deser = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
		Field f = new Field();
		if (!json.getAsJsonArray().get(0).isJsonNull()) f.value = json.getAsJsonArray().get(0).getAsString();
		if (json.getAsJsonArray().size() > 1) {
			if (!json.getAsJsonArray().get(1).isJsonNull()) f.id = json.getAsJsonArray().get(1).getAsString();
		}
		return f;
	};
	
	public static final JsonSerializer<Field> ser = (Field b, Type typeOfT, JsonSerializationContext context) -> {
		JsonArray array = new JsonArray();
		array.add(b.value);
		array.add(b.id);
		return array;
	};
}
