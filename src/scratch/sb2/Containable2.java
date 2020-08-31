package scratch.sb2;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public interface Containable2 {
	
	public static final JsonDeserializer<Containable2> deser = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
		if (json.isJsonArray()) {
			if (json.getAsJsonArray().size() == 0) {
				return context.deserialize(json, Blocks2.class);
			}
			if (json.getAsJsonArray().get(0).isJsonArray()) {
				return context.deserialize(json, Blocks2.class);
			} else
				return context.deserialize(json, Block2.class);
		} else {
			return context.deserialize(json, Value2.class);
		}
	};
	public static final JsonSerializer<Containable2> ser = (Containable2 b, Type typeOfT, JsonSerializationContext context) -> {
		if (b instanceof Block2) {
			return context.serialize(b, Block2.class);
		}
		if (b instanceof Value2) {
			return context.serialize(b, Value2.class);
		}
		if (b instanceof Blocks2) {
			return context.serialize(b, Blocks2.class);
		}
		return null;
	};
}