package scratch.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchProjectAsset {
	public String url;
	public String name;
	public String md5;
	public String extension;
	private byte[] content;
	public Map<String, byte[]> contentCache;
	
	/**
	 * Get the content of this asset. If it hasn't been fetched from the server yet, it is fetched.
	 * Failure to connect will result in a null byte array.
	 * 
	 * @return the asset content
	 */
	public byte[] getContent() {
		if (content == null) {
			if (contentCache != null) {
				if (contentCache.containsKey(url)) {
					content = contentCache.get(url);
					return content;
				}
			}
			List<String> headers = new ArrayList<>();
			List<String> values = new ArrayList<>();
			headers.add("origin");
			values.add("https://scratch.mit.edu");
			
			headers.add("user-agent");
			values.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
			
			headers.add("accept");
			values.add("*" + "/" + "*");
			
			headers.add("referer");
			values.add(url);
			try {
				content = ProjectFetch.sendRequest("GET", url, headers, values, null);
				contentCache.put(url, content);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}
	
	/**
	 * Save the asset to a given file.
	 * 
	 * @param f the file to use
	 * @throws IOException
	 */
	public void saveToFile(File f) throws IOException {
		getContent();
		OutputStream out = new FileOutputStream(f);
		out.write(content);
		out.close();
	}
	
	public String toString() {
		return "ProjectAsset [" + (url != null ? "url=" + url + ", " : "") + (name != null ? "name=" + name + ", " : "") + (md5 != null ? "md5=" + md5 + ", " : "") + (extension != null ? "extension=" + extension + ", " : "") + ("content=" + content) + "]";
	}
}