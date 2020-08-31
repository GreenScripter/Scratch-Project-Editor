package scratch.sb2;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Value2 implements Containable2 {
	
	public Value2() {
		
	}
	
	public Value2(ValueType2 type, Object value) {
		this.type = type;
		switch (type) {
			case BOOLEAN:
				boolValue = (Boolean) value;
				break;
			case DOUBLE:
				doubleValue = (Double) value;
				break;
			case INT:
				intValue = (Integer) value;
				break;
			case STRING:
				this.value = (String) value;
				break;
		}
	}
	
	public Value2(Object value) {
		if (value instanceof Boolean) {
			boolValue = (Boolean) value;
			type = ValueType2.BOOLEAN;
		} else if (value instanceof Double) {
			doubleValue = (Double) value;
			type = ValueType2.DOUBLE;
		} else if (value instanceof Integer) {
			intValue = (Integer) value;
			type = ValueType2.INT;
		} else {
			this.value = value.toString();
			type = ValueType2.STRING;
		}
	}
	
	public String value;
	public int intValue;
	public double doubleValue;
	public boolean boolValue;
	
	ValueType2 type = ValueType2.STRING;
	
	public static final JsonDeserializer<Value2> deser = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
		Value2 v = new Value2();
		if (json.isJsonNull()) {
			v.type = ValueType2.STRING;
			v.value = "";
		} else if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isBoolean()) {
			v.type = ValueType2.BOOLEAN;
			v.boolValue = json.getAsBoolean();
		} else if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
			if (json.getAsNumber().doubleValue() == json.getAsNumber().intValue()) {
				v.type = ValueType2.INT;
				v.intValue = json.getAsNumber().intValue();
			} else {
				v.type = ValueType2.DOUBLE;
				v.doubleValue = json.getAsNumber().doubleValue();
			}
		} else {
			v.type = ValueType2.STRING;
			v.value = json.getAsString();
		}
		return v;
	};
	
	public static final JsonSerializer<Value2> ser = (Value2 b, Type typeOfT, JsonSerializationContext context) -> {
		switch (b.type) {
			case BOOLEAN:
				return context.serialize(b.boolValue);
			case DOUBLE:
				return context.serialize(b.doubleValue);
			case INT:
				return context.serialize(b.intValue);
			case STRING:
				return context.serialize(b.value);
		}
		return null;
	};
	
	public enum ValueType2 {
		STRING, INT, DOUBLE, BOOLEAN
	}
	
	public String toString() {
		switch (type) {
			case BOOLEAN:
				return boolValue + "";
			case DOUBLE:
				return doubleValue + "";
			case INT:
				return intValue + "";
			case STRING:
				return value;
		}
		return "";
	}
}