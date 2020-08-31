package scratch.sb2;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public interface Child2 {
	
	public static final JsonDeserializer<Child2> deser = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
		if (json.getAsJsonObject().has("listName")) {
			return context.deserialize(json, ScratchList2.class);
		} else if (json.getAsJsonObject().has("target")) {
			return context.deserialize(json, FieldViewer2.class);
		} else if (json.getAsJsonObject().has("objName")) {
			return context.deserialize(json, Sprite2.class);
		}
		return null;
	};
	public static final JsonSerializer<Child2> ser = (Child2 b, Type typeOfT, JsonSerializationContext context) -> {
		if (b instanceof ScratchList2) {
			return context.serialize(b, ScratchList2.class);
		}
		if (b instanceof FieldViewer2) {
			return context.serialize(b, FieldViewer2.class);
		}
		if (b instanceof Sprite2) {
			return context.serialize(b, Sprite2.class);
		}
		return null;
	};
}