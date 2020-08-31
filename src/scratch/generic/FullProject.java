package scratch.generic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FullProject {
	
	public Project project;
	public Map<String, ProjectAsset> assets = new HashMap<>();
	
	/** Completely empty **/
	public FullProject() {
		
	}
	
	/**
	 * Read a complete project from Zip data.
	 * 
	 * @param data the project's zip data
	 * @throws IOException
	 */
	public FullProject(byte[] data) throws IOException {
		Map<String, byte[]> content = Zip.unzipContent(data);
		for (Entry<String, byte[]> e : content.entrySet()) {
			if (e.getKey().equals("project.json")) {
				project = Project.getProject(new String(e.getValue()));
			} else {
				ProjectAsset asset = new ProjectAsset();
				asset.content = e.getValue();
				asset.name = e.getKey();
				assets.put(asset.name.substring(0, asset.name.indexOf(".")), asset);
			}
			
		}
	}
	
	/**
	 * Read an sb2 or sb3 project file.
	 * 
	 * @param project the project file
	 * @throws IOException
	 */
	public FullProject(File project) throws IOException {
		this(fileToBytes(project));
	}
	
	/**
	 * Convert the project back to an sb2 or sb3 as appropriate.
	 * 
	 * @return the project as zip bytes.
	 */
	public byte[] toBytes() {
		Map<String, byte[]> content = new HashMap<>();
		for (ProjectAsset a : assets.values()) {
			content.put(a.name, a.content);
		}
		content.put("project.json", project.toJson().getBytes());
		try {
			return Zip.zipContent(content);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Write the project to disk as an sb2 or sb3 as appropriate.
	 * 
	 * @param destination the file to write the project to.
	 */
	public void write(File destination) throws IOException {
		FileOutputStream o = new FileOutputStream(destination);
		o.write(toBytes());
		o.close();
	}
	
	private static byte[] fileToBytes(File f) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = new FileInputStream(f);
		int read = 0;
		byte[] buffer = new byte[8192];
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
		in.close();
		return out.toByteArray();
	}
	
}
