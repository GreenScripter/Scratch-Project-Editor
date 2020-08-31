package scratch.sb3;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public abstract class Target {
	
	public boolean isStage;
	public String name;
	public Map<String, Variable> variables;
	public Map<String, ScratchList> lists;
	public Map<String, String> broadcasts;
	public Map<String, Block> blocks;
	public Map<String, Comment> comments;
	public int currentCostume;
	public List<Costume> costumes;
	public List<Sound> sounds;
	public double volume;
	public int layerOrder;
	
	public Sprite asSprite() {
		return (Sprite) this;
	}
	
	public Stage asStage() {
		return (Stage) this;
	}
	
	public boolean isSprite() {
		return this instanceof Sprite;
	}
	
	public boolean isStage() {
		return this instanceof Stage;
	}
	
	public static final JsonDeserializer<Target> deser = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
		if (json.getAsJsonObject().has("isStage") && json.getAsJsonObject().get("isStage").getAsBoolean()) {
			return context.deserialize(json, Stage.class);
		} else {
			return context.deserialize(json, Sprite.class);
		}
	};
	
	public static final JsonSerializer<Target> ser = (Target b, Type typeOfT, JsonSerializationContext context) -> {
		if (b instanceof Sprite) {
			return context.serialize(b, Sprite.class);
		}
		if (b instanceof Stage) {
			return context.serialize(b, Stage.class);
		}
		return null;
	};
}
