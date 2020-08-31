package scratch.api;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class ProjectInfo {
	public int id;
	public String title;
	public String description;
	public String instructions;
	public String visibility;
	
	@SerializedName(value = "public")//public is used by java itself
	public boolean _public;
	public boolean comments_allowed;
	public boolean is_published;
	
	public String image;
	public Author author;
	public Map<String, String> images;
	public History history;
	public Stats stats;
	public Remix remix;
}