package scratch.api;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import scratch.generic.FullProject;
import scratch.generic.Project;
import scratch.generic.ProjectAsset;
import scratch.sb2.Costume2;
import scratch.sb2.Project2;
import scratch.sb2.Sound2;
import scratch.sb2.Sprite2;
import scratch.sb3.Costume;
import scratch.sb3.Project3;
import scratch.sb3.Sound;
import scratch.sb3.Target;

public class ProjectFetch {
	
	/**
	 * Get all the asset links names and other information from a project.
	 * 
	 * @param proj
	 * @return list of the project's assets
	 */
	public static List<FetchProjectAsset> getAssets(Project proj) {
		List<FetchProjectAsset> assets = new ArrayList<>();
		Map<String, byte[]> cache = new HashMap<>();
		if (proj.isScratch2()) {
			Project2 p = proj.asScratch2();
			List<Sound2> sounds = new ArrayList<>();
			List<Costume2> costumes = new ArrayList<>();
			if (p.sounds != null) {
				sounds.addAll(p.sounds);
			}
			if (p.costumes != null) {
				costumes.addAll(p.costumes);
			}
			for (Sprite2 s : p.getSprites()) {
				if (s.sounds != null) {
					sounds.addAll(s.sounds);
				}
				if (s.costumes != null) {
					costumes.addAll(s.costumes);
				}
			}
			for (Sound2 s : sounds) {
				FetchProjectAsset pa = new FetchProjectAsset();
				pa.extension = s.md5.substring(s.md5.indexOf(".") + 1);
				pa.md5 = s.md5.substring(0, s.md5.indexOf("."));
				pa.name = s.soundName;
				pa.url = "https://assets.scratch.mit.edu/internalapi/asset/" + s.md5 + "/get/";
				pa.contentCache = cache;
				assets.add(pa);
			}
			for (Costume2 s : costumes) {
				FetchProjectAsset pa = new FetchProjectAsset();
				pa.extension = s.baseLayerMD5.substring(s.baseLayerMD5.indexOf(".") + 1);
				pa.md5 = s.baseLayerMD5.substring(0, s.baseLayerMD5.indexOf("."));
				pa.name = s.costumeName;
				pa.url = "https://assets.scratch.mit.edu/internalapi/asset/" + s.baseLayerMD5 + "/get/";
				pa.contentCache = cache;
				assets.add(pa);
			}
		}
		if (proj.isScratch3()) {
			Project3 p = proj.asScratch3();
			List<Sound> sounds = new ArrayList<>();
			List<Costume> costumes = new ArrayList<>();
			for (Target s : p.targets) {
				if (s.sounds != null) {
					sounds.addAll(s.sounds);
				}
				if (s.costumes != null) {
					costumes.addAll(s.costumes);
				}
			}
			for (Sound s : sounds) {
				FetchProjectAsset pa = new FetchProjectAsset();
				pa.extension = s.md5ext.substring(s.md5ext.indexOf(".") + 1);
				pa.md5 = s.md5ext.substring(0, s.md5ext.indexOf("."));
				pa.name = s.name;
				pa.url = "https://assets.scratch.mit.edu/internalapi/asset/" + s.md5ext + "/get/";
				pa.contentCache = cache;
				assets.add(pa);
			}
			for (Costume s : costumes) {
				FetchProjectAsset pa = new FetchProjectAsset();
				pa.extension = s.md5ext.substring(s.md5ext.indexOf(".") + 1);
				pa.md5 = s.md5ext.substring(0, s.md5ext.indexOf("."));
				pa.name = s.name;
				pa.url = "https://assets.scratch.mit.edu/internalapi/asset/" + s.md5ext + "/get/";
				pa.contentCache = cache;
				assets.add(pa);
			}
		}
		
		return assets;
	}
	
	/**
	 * Fetch a project and all of its assets.
	 * 
	 * @param id the id of the project
	 * @return the full project
	 * @throws IOException
	 */
	public static FullProject fetchCompleteProject(String id) throws IOException {
		return fetchCompleteProject(fetchProject(id, false));
	}
	
	/**
	 * Fetch a project's assets from the website and compile it into a full project.
	 * 
	 * @param id the id of the project
	 * @return the full project
	 * @throws IOException
	 */
	public static FullProject fetchCompleteProject(Project pr) throws IOException {
		FullProject fp = new FullProject();
		
		fp.project = pr;
		List<FetchProjectAsset> assets = getAssets(fp.project);
		for (FetchProjectAsset a : assets) {
			ProjectAsset asset = new ProjectAsset();
			asset.content = a.getContent();
			asset.name = a.md5 + "." + a.extension;
			
			fp.assets.put(a.md5, asset);
		}
		if (fp.project.isScratch2()) {
			Project2 p = fp.project.asScratch2();
			List<Sound2> sounds = new ArrayList<>();
			List<Costume2> costumes = new ArrayList<>();
			if (p.sounds != null) {
				sounds.addAll(p.sounds);
			}
			if (p.costumes != null) {
				costumes.addAll(p.costumes);
			}
			for (Sprite2 s : p.getSprites()) {
				if (s.sounds != null) {
					sounds.addAll(s.sounds);
				}
				if (s.costumes != null) {
					costumes.addAll(s.costumes);
				}
			}
			Map<String, Integer> ids = new HashMap<>();
			int lid = 0;
			for (Sound2 s : sounds) {
				s.soundID = lid;
				ProjectAsset a = fp.assets.get(s.md5.substring(0, s.md5.indexOf(".")));
				if (a == null) {
					s.soundID = ids.get(s.md5);
				} else {
					a.name = lid + "." + s.md5.substring(s.md5.indexOf(".") + 1);
					fp.assets.remove(s.md5.substring(0, s.md5.indexOf(".")));
					fp.assets.put(a.name, a);
					ids.put(s.md5, lid);
					lid++;
					
				}
			}
			lid = 0;
			for (Costume2 s : costumes) {
				s.baseLayerID = lid;
				ProjectAsset a = fp.assets.get(s.baseLayerMD5.substring(0, s.baseLayerMD5.indexOf(".")));
				if (a == null) {
					s.baseLayerID = ids.get(s.baseLayerMD5);
				} else {
					a.name = lid + "." + s.baseLayerMD5.substring(s.baseLayerMD5.indexOf(".") + 1);
					fp.assets.remove(s.baseLayerMD5.substring(0, s.baseLayerMD5.indexOf(".")));
					fp.assets.put(a.name, a);
					ids.put(s.baseLayerMD5, lid);
					
					lid++;
				}
				
			}
		}
		
		return fp;
		
	}
	
	/**
	 * Fetch a project made in scratch 2 or 3 and optionally its other info.
	 * 
	 * @param id the project's id
	 * @param fetchMeta if the project object should include meta like the author's name and the
	 * project's title
	 * @return the project object, which is either an instance of Project2 or Project3
	 * @throws IOException
	 */
	public static Project fetchProject(String id, boolean fetchMeta) throws IOException {
		List<String> headers = new ArrayList<>();
		List<String> values = new ArrayList<>();
		headers.add("origin");
		values.add("https://scratch.mit.edu");
		
		headers.add("user-agent");
		values.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
		
		headers.add("accept");
		values.add("*" + "/" + "*");
		
		headers.add("referer");
		values.add("https://scratch.mit.edu/projects/" + id + "/editor");
		
		byte[] b = sendRequest("GET", "https://projects.scratch.mit.edu/" + id + "", headers, values, null);
		String s = new String(b);
		Project p = Project.getProject(s);
		if (fetchMeta) {
			try {
				ProjectInfo pi = fetchProjectInfo(id);
				p.name = pi.title;
				p.author = pi.author.username;
				p.icon = pi.image;
				p.info = pi;
			} catch (Exception e) {
				
			}
		}
		return p;
	}
	
	/**
	 * Fetch the info about a project, such as icons, author info, love/favorite count.
	 * 
	 * @param id the project's id
	 * @return the info for the given project
	 * @throws IOException
	 */
	public static ProjectInfo fetchProjectInfo(String id) throws IOException {
		
		List<String> headers = new ArrayList<>();
		List<String> values = new ArrayList<>();
		headers.add("origin");
		values.add("https://scratch.mit.edu");
		
		headers.add("user-agent");
		values.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
		
		headers.add("accept");
		values.add("*" + "/" + "*");
		
		headers.add("referer");
		values.add("https://scratch.mit.edu/projects/" + id + "/editor");
		
		byte[] b = sendRequest("GET", "https://api.scratch.mit.edu/projects/" + id + "", headers, values, null);
		return Project.gson_instance.fromJson(new String(b), ProjectInfo.class);
	}
	
	/**
	 * Read a single UTF-8 character from a stream without reading any further.
	 * 
	 * @param in the stream to read from
	 * @return the character
	 * @throws IOException
	 */
	public static char readChar(InputStream in) throws IOException {
		int theByte = in.read();
		if (theByte == -1) {
			throw new EOFException();
			
		}
		boolean a = (theByte & 128) != 0;
		boolean b = (theByte & 64) != 0;
		boolean c = (theByte & 32) != 0;
		boolean d = (theByte & 16) != 0;
		int ai = a ? 1 : 0;
		int bi = b ? 1 : 0;
		int ci = c ? 1 : 0;
		int di = d ? 1 : 0;
		if (ai == 1 & bi == 1) {
			
			if (ci == 1) {
				if (di == 1) {
					
					int b2 = in.read();
					int b3 = in.read();
					int b4 = in.read();
					if (b2 == -1 || b3 == -1 || b4 == -1) {
						throw new EOFException();
					}
					return (char) ((lastBits(theByte, 3) << 18) | (lastBits(b2, 6) << 12) | (lastBits(b3, 6) << 6) | (lastBits(b4, 6)));
				} else {
					int b2 = in.read();
					int b3 = in.read();
					if (b2 == -1 || b3 == -1) {
						throw new EOFException();
					}
					return (char) ((lastBits(theByte, 4) << 12) | (lastBits(b2, 6) << 6) | (lastBits(b3, 6)));
				}
			} else {
				int b2 = in.read();
				if (b2 == -1) {
					throw new EOFException();
				}
				return (char) ((lastBits(theByte, 5) << 6) | (lastBits(b2, 6)));
			}
			
		} else {
			return (char) (lastBits(theByte, 7));
		}
	}
	
	/**
	 * Get the last n bits of the byte by.
	 * 
	 * @param by the byte
	 * @param n the number of bits
	 * @return the partial byte
	 */
	public static int lastBits(int by, int n) {
		int theByte = by;
		boolean a = (theByte & 128) != 0;
		boolean b = (theByte & 64) != 0;
		boolean c = (theByte & 32) != 0;
		boolean d = (theByte & 16) != 0;
		boolean e = (theByte & 8) != 0;
		boolean f = (theByte & 4) != 0;
		boolean g = (theByte & 2) != 0;
		boolean h = (theByte & 1) != 0;
		int ai = a ? 1 : 0;
		int bi = b ? 1 : 0;
		int ci = c ? 1 : 0;
		int di = d ? 1 : 0;
		int ei = e ? 1 : 0;
		int fi = f ? 1 : 0;
		int gi = g ? 1 : 0;
		int hi = h ? 1 : 0;
		int[] number = new int[] { ai, bi, ci, di, ei, fi, gi, hi };
		int out = 0;
		int count = n - 1;
		for (int at = 8 - n; at < 8; at++) {
			out += number[at] << count;
			count -= 1;
			
		}
		return out;
	}
	
	/**
	 * Read one line from a stream without reading any further.
	 * 
	 * @param is the stream to read from
	 * @return the string
	 * @throws IOException
	 */
	public static String readLine(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		char c = readChar(is);
		if (c == '\r') {
			c = readChar(is);
		}
		try {
			while (c != '\n') {
				sb.append(c);
				c = readChar(is);
				if (c == '\r') {
					c = readChar(is);
				}
			}
		} catch (EOFException e) {
			
		}
		
		return sb.toString();
	}
	
	/**
	 * Request an http or https page.
	 * 
	 * @param method the method such as GET or POST
	 * @param url the URL to request
	 * @param headers the headers
	 * @param values the values corresponding to each header
	 * @param content the body content
	 * @return the server's response
	 * @throws IOException if an IO error occurs or if the server denies the request
	 */
	public static byte[] sendRequest(String method, String url, List<String> headers, List<String> values, byte[] content) throws IOException {
		InputStream in = null;
		OutputStream out = System.out;
		String host;
		String path = "/";
		int port = -1;
		if (headers != null && values != null) {
			if (headers.size() != values.size()) {
				throw new IOException("Must have the same number of headers and values. " + headers.size() + " != " + values.size());
			}
		}
		try {
			host = url.substring(url.indexOf("//") + 2);
			
			if (host.contains("/")) {
				path = host.substring(host.indexOf("/"));
				if (path.endsWith("/") && !path.equals("/")) {
					path = path.substring(0, path.length() - 1);
				}
				host = host.substring(0, host.indexOf("/"));
				
			}
			if (host.contains(":")) {
				port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
				host = host.substring(0, host.indexOf(":"));
			}
		} catch (Exception e) {
			throw new IOException("Invalid URL " + url, e);
		}
		if (url.startsWith("https://")) {
			
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			
			SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(host, port == -1 ? 443 : port);
			
			in = new BufferedInputStream(sslsocket.getInputStream());
			out = sslsocket.getOutputStream();
			
		} else if (url.startsWith("http://")) {
			@SuppressWarnings("resource")
			Socket s = new Socket(host, port == -1 ? 80 : port);
			in = new BufferedInputStream(s.getInputStream());
			out = s.getOutputStream();
		} else {
			throw new IOException("Invalid URL " + url);
		}
		
		StringBuilder request = new StringBuilder();
		request.append(method);
		request.append(" ");
		request.append(path);
		request.append(" HTTP/1.1");
		request.append("\r\n");
		
		if (headers != null && values != null) {
			for (int i = 0; i < headers.size(); i++) {
				request.append(headers.get(i));
				request.append(": ");
				request.append(values.get(i));
				request.append("\r\n");
				
			}
		}
		request.append("host");
		request.append(": ");
		request.append(host);
		request.append("\r\n");
		if (content != null) {
			request.append("content-length");
			request.append(": ");
			request.append(content.length);
			request.append("\r\n");
		}
		
		request.append("\r\n");
		
		out.write(request.toString().getBytes());
		if (content != null) {
			out.write(content);
		}
		
		String inputLine;
		List<String> inHeaders = new ArrayList<>();
		List<String> inValues = new ArrayList<>();
		int responseCode = 0;
		String responseText = "";
		int c = 0;
		while ((inputLine = readLine(in)) != null) {
			if (c == 0) {
				responseCode = Integer.parseInt(inputLine.substring(inputLine.indexOf(" ") + 1, inputLine.indexOf(" ") + 4));
				try {
					responseText = inputLine.substring(inputLine.indexOf(" ") + 5);
				} catch (Exception e) {
					
				}
			} else {
				if (inputLine.length() == 0) {
					break;
				}
				String headerLine = inputLine.substring(0, inputLine.indexOf(": "));
				String valueLine = inputLine.substring(inputLine.indexOf(": ") + 2);
				inHeaders.add(headerLine);
				inValues.add(valueLine);
			}
			c++;
		}
		int length = 0;
		for (int i = 0; i < inHeaders.size(); i++) {
			if (inHeaders.get(i).equalsIgnoreCase("Content-Length")) {
				length = Integer.parseInt(inValues.get(i));
				break;
			}
		}
		
		byte[] body = new byte[length];
		DataInputStream ind = new DataInputStream(in);
		ind.readFully(body);
		if ((responseCode + "").charAt(0) == '2') {
			try {
				in.close();
				out.close();
			} catch (Exception e) {
				
			}
			return body;
		} else {
			throw new IOException(responseCode + " " + responseText);
		}
		
	}
	
}
