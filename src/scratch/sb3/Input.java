package scratch.sb3;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Input {
	
	public Shadow shadow;
	
	//has one of these
	public String inputID;
	public ArrayBlock input;
	
	//may have one of these, if Shadow is OBSCURED_SHADOW
	public String coveredInputID;
	public ArrayBlock coveredInput;
	
	public enum Shadow {
		SHADOW, NO_SHADOW, OBSCURED_SHADOW
	}
	
	public static final JsonDeserializer<Input> deser = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
		Input i = new Input();
		JsonArray array = json.getAsJsonArray();
		i.shadow = Shadow.values()[array.get(0).getAsInt() - 1];
		JsonElement value = array.get(1);
		if (value.isJsonArray()) {
			i.input = context.deserialize(value, ArrayBlock.class);
		} else {
			if (!value.isJsonNull()) i.inputID = value.getAsString();
		}
		if (i.shadow.equals(Shadow.OBSCURED_SHADOW)) {
			value = array.get(2);
			if (value.isJsonArray()) {
				i.coveredInput = context.deserialize(value, ArrayBlock.class);
			} else {
				if (value.isJsonNull()) {
					i.coveredInputID = null;
				} else {
					i.coveredInputID = value.getAsString();
				}
			}
		}
		
		return i;
	};
	
	public static final JsonSerializer<Input> ser = (Input i, Type typeOfT, JsonSerializationContext context) -> {
		JsonArray array = new JsonArray();
		array.add(i.shadow.ordinal() + 1);
		if (i.input != null) {
			array.add(context.serialize(i.input));
		} else {
			if (i.inputID != null) {
				array.add(new JsonPrimitive(i.inputID));
			} else {
				array.add(JsonNull.INSTANCE);
			}
		}
		if (i.shadow.equals(Shadow.OBSCURED_SHADOW)) {
			if (i.coveredInputID == null) {
				array.add(context.serialize(i.coveredInput));
			} else {
				array.add(new JsonPrimitive(i.coveredInputID));
			}
		}
		return array;
	};
}
