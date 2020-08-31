package scratch.sb3;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ScratchList {
	
	public String name;
	public List<String> value;
	
	public static final JsonDeserializer<ScratchList> deser = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
		ScratchList f = new ScratchList();
		f.name = json.getAsJsonArray().get(0).getAsString();
		String[] lines = context.deserialize(json.getAsJsonArray().get(1), String[].class);
		f.value = new ArrayList<>(Arrays.asList(lines));
		return f;
	};
	
	public static final JsonSerializer<ScratchList> ser = (ScratchList b, Type typeOfT, JsonSerializationContext context) -> {
		JsonArray array = new JsonArray();
		array.add(b.name);
		
		array.add(context.serialize(b.value));
		return array;
	};
}
