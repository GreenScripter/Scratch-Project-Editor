package scratch.sb3;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import scratch.generic.Project;

public class Project3 extends Project {
	
	//It appears that 3.0 always saves the stage first, but it isn't required by the format.
	public List<Target> targets;
	public List<String> extensions;
	public List<Monitor> monitors;
	public Meta meta;
	
	public static GsonBuilder registerAdapters(GsonBuilder builder) {
		
		builder.registerTypeAdapter(Block.class, Block.deser);
		builder.registerTypeAdapter(Block.class, Block.ser);
		builder.registerTypeAdapter(ArrayBlock.class, ArrayBlock.deser);
		builder.registerTypeAdapter(ArrayBlock.class, ArrayBlock.ser);
		builder.registerTypeAdapter(Input.class, Input.deser);
		builder.registerTypeAdapter(Input.class, Input.ser);
		builder.registerTypeAdapter(Field.class, Field.deser);
		builder.registerTypeAdapter(Field.class, Field.ser);
		builder.registerTypeAdapter(ScratchList.class, ScratchList.deser);
		builder.registerTypeAdapter(ScratchList.class, ScratchList.ser);
		builder.registerTypeAdapter(Variable.class, Variable.deser);
		builder.registerTypeAdapter(Variable.class, Variable.ser);
		builder.registerTypeAdapter(Target.class, Target.deser);
		builder.registerTypeAdapter(Target.class, Target.ser);

		builder.registerTypeAdapter(NormalBlock.class, NormalBlock.ser);

		builder.registerTypeAdapter(Double.class, doubleSer);
		
		return builder;
		
	}
	
	public static final JsonSerializer<Double> doubleSer = (Double i, Type typeOfT, JsonSerializationContext context) -> {
		if (i == (int) (i + 0)) {
			return new JsonPrimitive((int) (i + 0));
		}
		return new JsonPrimitive(i);
	};
}
