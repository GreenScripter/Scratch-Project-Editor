package scratch.sb3;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Variable {
	
	public String name;
	public String value;
	public boolean isCloud;
	
	public static final JsonDeserializer<Variable> deser = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
		Variable f = new Variable();
		f.name = json.getAsJsonArray().get(0).getAsString();
		if (json.getAsJsonArray().get(1).getAsJsonPrimitive().isNumber()) {
			String s = (json.getAsJsonArray().get(1).getAsDouble() + "");
			if (s.endsWith(".0")) {
				f.value = s.substring(0, s.length() - 2);
			} else {
				f.value = s;
			}
		} else {
			f.value = json.getAsJsonArray().get(1).getAsString();
		}
		if (json.getAsJsonArray().size() == 3) {
			f.isCloud = json.getAsJsonArray().get(2).getAsBoolean();
		}
		return f;
	};
	
	public static final JsonSerializer<Variable> ser = (Variable b, Type typeOfT, JsonSerializationContext context) -> {
		JsonArray array = new JsonArray();
		array.add(b.name);
		try {
			if (Double.isFinite(Double.parseDouble(b.value))) {
				array.add(context.serialize(Double.parseDouble(b.value)));
			} else {
				array.add(b.value);
			}
		} catch (Exception e) {
			array.add(b.value);
		}
		if (b.isCloud) {
			array.add(b.isCloud);
		}
		return array;
	};
}
