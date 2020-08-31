package scratch.sb3;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public interface Block {
	
	public default ArrayBlock asArrayBlock() {
		return (ArrayBlock) this;
	}
	
	public default NormalBlock asNormalBlock() {
		return (NormalBlock) this;
	}
	
	public default boolean isArrayBlock() {
		return this instanceof ArrayBlock;
	}
	
	public default boolean isNormalBlock() {
		return this instanceof NormalBlock;
	}
	
	public static final JsonDeserializer<Block> deser = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
		if (json.isJsonObject()) {
			return context.deserialize(json, NormalBlock.class);
		}
		if (json.isJsonArray()) {
			return context.deserialize(json, ArrayBlock.class);
		}
		return null;
	};
	
	public static final JsonSerializer<Block> ser = (Block b, Type typeOfT, JsonSerializationContext context) -> {
		if (b instanceof NormalBlock) {
			return context.serialize(b, NormalBlock.class);
		}
		if (b instanceof ArrayBlock) {
			return context.serialize(b, ArrayBlock.class);
		}
		return null;
	};
}
