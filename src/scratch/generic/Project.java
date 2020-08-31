package scratch.generic;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import scratch.api.ProjectInfo;
import scratch.sb2.Project2;
import scratch.sb3.NormalBlock;
import scratch.sb3.Project3;

public abstract class Project {
	
	public transient String name;
	public transient String author;
	public transient String icon;
	public transient ProjectInfo info;
	
	/**
	 * Get a project from it's project.json.
	 * 
	 * @param s the project's json
	 * @return the Project's object
	 */
	public static Project getProject(String s) {
		return gson_instance.fromJson(s, Project.class);
	}
	
	/**
	 * Completely empty project, all fields will need to be initialized.
	 */
	public Project() {
		
	}
	
	/**
	 * Get the project converted to json.
	 * 
	 * @return the project as json
	 */
	public String toJson() {
		return gson_instance.toJson(this);
	}
	
	/**
	 * Check if the project was made in scratch 3.
	 */
	public boolean isScratch3() {
		return this instanceof Project3;
	}
	
	/**
	 * Check if the project was made in scratch 2.
	 */
	public boolean isScratch2() {
		return this instanceof Project2;
	}
	
	/**
	 * Get the project as a scratch 3 project. This doesn't actually convert it, it just casts it.
	 */
	public Project3 asScratch3() {
		return (Project3) this;
	}
	
	/**
	 * Get the project as a scratch 2 project. This doesn't actually convert it, it just casts it.
	 */
	public Project2 asScratch2() {
		return (Project2) this;
	}
	
	/**
	 * Get the project converted to json.
	 * 
	 * @return the project as json
	 */
	public String serialize() {
		return gson_instance.toJson(this);
		
	}
	
	public String toString() {
		return this.getClass().getSimpleName() + " [name=" + name + ", author=" + author + ", isScratch3=" + isScratch3() + ", isScratch2=" + isScratch2() + "]";
	}
	
	public static GsonBuilder registerAdapters(GsonBuilder builder) {
		
		Project3.registerAdapters(builder);
		Project2.registerAdapters(builder);
		
		builder.registerTypeAdapter(Project.class, Project.deser);
		builder.registerTypeAdapter(Project.class, Project.ser);
		
		//I can't remember what the point of this was, pretty sure it solves some sort of dependency loop or something.
		builder.registerTypeAdapter(Object.class, NormalBlock.genericSer);
		
		return builder;
		
	}
	
	public static final JsonDeserializer<Project> deser = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
		if (json.isJsonObject()) {
			if (json.getAsJsonObject().has("targets")) {
				return context.deserialize(json, Project3.class);
			} else {
				return context.deserialize(json, Project2.class);
			}
		} else {
			return null;
		}
	};
	
	public static final JsonSerializer<Project> ser = (Project b, Type typeOfT, JsonSerializationContext context) -> {
		if (b instanceof Project2) {
			return context.serialize(b, Project2.class);
		}
		if (b instanceof Project3) {
			return context.serialize(b, Project3.class);
		}
		return null;
	};
	public static transient Gson gson_instance = registerAdapters(new GsonBuilder()).serializeNulls().create();
	
}
