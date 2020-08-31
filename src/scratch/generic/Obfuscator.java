package scratch.generic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;

import scratch.sb3.ArrayBlock;
import scratch.sb3.Block;
import scratch.sb3.Costume;
import scratch.sb3.Field;
import scratch.sb3.Input;
import scratch.sb3.Monitor;
import scratch.sb3.NormalBlock;
import scratch.sb3.Project3;
import scratch.sb3.ScratchList;
import scratch.sb3.Sound;
import scratch.sb3.Sprite;
import scratch.sb3.Target;
import scratch.sb3.Variable;

/**
 * Class that performs obfuscation of scratch 3.0 projects.
 */
public class Obfuscator {
	
	/**
	 * Rename all the custom blocks in a project.
	 */
	public static void renameCustomBlocks(Project3 p) {
		Map<String, String> proccodeMap = new HashMap<>();
		int c2 = 0;
		for (Target t : p.targets) {
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					if (n.opcode.equals("procedures_prototype")) {
						
						String proccode = n.mutation.getAsJsonObject().get("proccode").getAsString();
						if (!proccodeMap.containsKey(proccode)) {
							
							Pattern patt = Pattern.compile("%[sbn]");
							Matcher m = patt.matcher(proccode);
							String newproccode = generateHex() + c2++;
							System.out.println(newproccode);
							while (m.find()) {
								newproccode += " " + m.group();
								System.out.println(newproccode);
							}
							n.mutation.getAsJsonObject().add("proccode", new JsonPrimitive(newproccode));
							
							proccodeMap.put(proccode, newproccode);
						} else {
							n.mutation.getAsJsonObject().add("proccode", new JsonPrimitive(proccodeMap.get(proccode)));
						}
					}
				}
			}
		}
		
		for (Target t : p.targets) {
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					if (n.opcode.equals("procedures_call")) {
						String proccode = n.mutation.getAsJsonObject().get("proccode").getAsString();
						if (proccodeMap.containsKey(proccode)) {
							n.mutation.getAsJsonObject().add("proccode", new JsonPrimitive(proccodeMap.get(proccode)));
						}
					}
				}
			}
		}
	}
	
	/**
	 * Rename all the custom block parameters in a project.
	 */
	public static void renameParams(Project3 p) {
		Map<String, String> nameMap = new HashMap<>();
		int c = 0;
		for (Target t : p.targets) {
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					if (n.opcode.equals("procedures_prototype")) {
						String namesText = n.mutation.getAsJsonObject().get("argumentnames").getAsString();
						String[] names = new Gson().fromJson(namesText, String[].class);
						for (int i = 0; i < names.length; i++) {
							if (!nameMap.containsKey(names[i])) {
								nameMap.put(names[i], generateHex() + c++);
							}
							names[i] = nameMap.get(names[i]);
						}
						n.mutation.getAsJsonObject().add("argumentnames", new JsonPrimitive(new Gson().toJson(names)));
						
					}
				}
			}
		}
		
		for (Target t : p.targets) {
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					if (n.opcode.equals("argument_reporter_string_number") || n.opcode.equals("argument_reporter_boolean")) {
						if (nameMap.containsKey(n.fields.get("VALUE").value)) n.fields.get("VALUE").value = nameMap.get(n.fields.get("VALUE").value);
					}
				}
			}
		}
	}
	
	/**
	 * Rename all the broadcasts in a project. If broadcasts are broadcast using methods other than
	 * the drop down menu, the project won't work.
	 * 
	 * @return A warning message if the project has the potential to contain dynamic broadcasts,
	 * otherwise null
	 */
	public static String renameBroadcasts(Project3 p) {
		int broadcastCount = 0;
		
		Map<String, String> names = new HashMap<>();
		for (Target t : p.targets) {
			for (String s : t.broadcasts.keySet()) {
				t.broadcasts.put(s, generateHex() + broadcastCount++);
				names.put(s, t.broadcasts.get(s));
			}
		}
		for (Target t : p.targets) {
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					if (n.opcode.equals("event_whenbroadcastreceived")) {
						Field f = n.fields.get("BROADCAST_OPTION");
						f.value = names.get(f.id);
					}
				}
			}
		}
		for (Target t : p.targets) {
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					if (n.opcode.equals("event_broadcast") || n.opcode.equals("event_broadcastandwait")) {
						if (n.inputs.get("BROADCAST_INPUT").shadow.equals(Input.Shadow.OBSCURED_SHADOW)) {
							return "This project may contain dynamic broadcasts.";
						}
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Remove any code that doesn't have a hat block at the beginning, which would cause to to not
	 * be run. Does not support hat blocks from extensions.
	 */
	public static void removeUnusedBlocks(Project3 p) {
		List<String> hatids = Arrays.asList(new String[] { "procedures_definition", "control_start_as_clone", "event_whenbroadcastreceived", "event_whengreaterthan", "event_whenbackdropswitchesto", "event_whenthisspriteclicked", "event_whenkeypressed", "event_whenflagclicked" });
		
		for (Target t : p.targets) {
			Set<String> ids = new HashSet<>(t.blocks.keySet());
			for (String id : ids) {
				Block b = t.blocks.get(id);
				if (b != null) {
					if (b.isNormalBlock()) {
						NormalBlock n = b.asNormalBlock();
						if (n.topLevel) {
							if (!hatids.contains(n.opcode)) {
								deleteScript(t.blocks, id);
							}
						}
					} else {
						t.blocks.remove(id);
					}
				}
			}
		}
	}
	
	/**
	 * Recursively delete a script and all of the blocks in it given the top block.
	 * 
	 * @param block the blocks available
	 * @param id the top block in the script's id
	 */
	public static void deleteScript(Map<String, Block> block, String id) {
		if (block.get(id).isNormalBlock()) {
			NormalBlock b = block.get(id).asNormalBlock();
			if (b.next != null) {
				deleteScript(block, b.next);
			}
			for (Input i : b.inputs.values()) {
				if (i.inputID != null) {
					deleteScript(block, i.inputID);
				}
				if (i.coveredInputID != null) {
					deleteScript(block, i.coveredInputID);
				}
			}
			block.remove(id);
		}
	}
	
	/**
	 * Move every block in a project to 0,0
	 */
	public static void moveBlocks(Project3 p) {
		for (Target t : p.targets) {
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					if (n.topLevel) {
						n.x = 0.0;
						n.y = 0.0;
					}
					
				} else {
					ArrayBlock a = b.asArrayBlock();
					a.x = 0.0;
					a.y = 0.0;
				}
			}
		}
	}
	
	/**
	 * Remove all the comments in a project
	 */
	public static void removeComments(Project3 p) {
		for (Target t : p.targets) {
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					n.comment = null;
				}
			}
			t.comments.clear();
		}
	}
	
	/**
	 * Rename all the sounds in a project. If sounds are played using methods other than the drop
	 * down menu, the project won't work.
	 * 
	 * @return A warning message if the project has the potential to contain dynamic sound playing,
	 * otherwise null
	 */
	public static String renameSounds(Project3 p) {
		int soundCount = 0;
		Map<String, String> names = new HashMap<>();
		for (Target t : p.targets) {
			for (Sound s : t.sounds) {
				if (!names.containsKey(s.name)) names.put(s.name, generateHex() + soundCount++);
				s.name = names.get(s.name);
			}
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					if (n.opcode.equals("sound_sounds_menu")) {
						n.fields.get("SOUND_MENU").value = names.get(n.fields.get("SOUND_MENU").value);
					}
				}
			}
		}
		
		for (Target t : p.targets) {
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					if (n.opcode.equals("sound_playuntildone") || n.opcode.equals("sound_play")) {
						if (n.inputs.get("SOUND_MENU").shadow.equals(Input.Shadow.OBSCURED_SHADOW)) {
							return "This project may contain dynamic sounds.";
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Rename all the sprites in a project. If sprites are accessed using methods other than the
	 * drop down menu for selecting a sprite, the project won't work.
	 * 
	 * @return A warning message if the project has the potential to contain dynamic sprite access,
	 * otherwise null
	 */
	public static String renameSprites(Project3 p) {
		int spriteCount = 0;
		Map<String, String> names = new HashMap<>();
		for (Target t : p.targets) {
			if (t.isSprite()) {
				Sprite s = t.asSprite();
				names.put(s.name, generateHex() + spriteCount++);
				s.name = names.get(s.name);
			}
		}
		List<String> menusToChange = Arrays.asList(new String[] { "sensing_distancetomenu", "sensing_touchingobjectmenu", "sensing_of_object_menu", "motion_pointtowards_menu", "motion_glideto_menu", "motion_goto_menu" });
		for (Target t : p.targets) {
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					if (menusToChange.contains(n.opcode)) {
						for (Field f : n.fields.values()) {
							if (names.containsKey(f.value)) f.value = names.get(f.value);
						}
					}
				}
			}
		}
		for (Monitor m : p.monitors) {
			if (names.containsKey(m.spriteName)) {
				if (names.containsKey(m.spriteName)) m.spriteName = names.get(m.spriteName);
			}
		}
		for (Target t : p.targets) {
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					if (n.opcode.equals("motion_goto") || n.opcode.equals("motion_glideto")) {
						if (n.inputs.get("TO").shadow.equals(Input.Shadow.OBSCURED_SHADOW)) {
							return "This project may contain dynamic sprite access.";
						}
					}
					if (n.opcode.equals("motion_pointtowards")) {
						if (n.inputs.get("TOWARDS").shadow.equals(Input.Shadow.OBSCURED_SHADOW)) {
							return "This project may contain dynamic sprite access.";
						}
					}
					if (n.opcode.equals("control_create_clone_of")) {
						if (n.inputs.get("CLONE_OPTION").shadow.equals(Input.Shadow.OBSCURED_SHADOW)) {
							return "This project may contain dynamic sprite access.";
						}
					}
					if (n.opcode.equals("sensing_touchingobject")) {
						if (n.inputs.get("TOUCHINGOBJECTMENU").shadow.equals(Input.Shadow.OBSCURED_SHADOW)) {
							return "This project may contain dynamic sprite access.";
						}
					}
					if (n.opcode.equals("sensing_distanceto")) {
						if (n.inputs.get("DISTANCETOMENU").shadow.equals(Input.Shadow.OBSCURED_SHADOW)) {
							return "This project may contain dynamic sprite access.";
						}
					}
					if (n.opcode.equals("sensing_of")) {
						if (n.inputs.get("OBJECT").shadow.equals(Input.Shadow.OBSCURED_SHADOW)) {
							return "This project may contain dynamic sprite access.";
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Rename all the costumes in a project. If costumes are changed using methods other than the
	 * drop down menu or by number, the project won't work. This method may give warnings for any
	 * project that uses numerical costume changing to switch to the previous costume, as well as
	 * any break any case detection scripts.
	 * 
	 * @return A warning message if the project has the potential to contain dynamic costume
	 * changing, otherwise null
	 */
	public static String renameCostumes(Project3 p) {
		int costumeCount = 0;
		Map<String, String> names = new HashMap<>();
		for (Target t : p.targets) {
			for (Costume c : t.costumes) {
				if (!names.containsKey(c.name)) names.put(c.name, generateHex() + "" + costumeCount++);
				c.name = names.get(c.name);
			}
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					if (n.opcode.equals("looks_costume")) {
						n.fields.get("COSTUME").value = names.get(n.fields.get("COSTUME").value);
					}
					if (n.opcode.equals("looks_backdrops") || n.opcode.equals("event_whenbackdropswitchesto")) {
						n.fields.get("BACKDROP").value = names.get(n.fields.get("BACKDROP").value);
					}
				}
			}
		}
		for (Target t : p.targets) {
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					if (n.opcode.equals("looks_switchcostumeto")) {
						if (n.inputs.get("COSTUME").shadow.equals(Input.Shadow.OBSCURED_SHADOW)) {
							return "This project may contain dynamic costumes.";
						}
					}
					if (n.opcode.equals("looks_switchbackdropto") || n.opcode.equals("looks_switchbackdroptoandwait")) {
						if (n.inputs.get("BACKDROP").shadow.equals(Input.Shadow.OBSCURED_SHADOW)) {
							return "This project may contain dynamic costumes.";
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Rename all the variables in a project.
	 */
	public static void renameVars(Project3 p) {
		int varCount = 0;
		for (Target t : p.targets) {
			for (Variable v : t.variables.values()) {
				if (v.isCloud) continue;
				v.name = generateHex() + varCount++;
			}
			for (ScratchList v : t.lists.values()) {
				v.name = generateHex() + varCount++;
			}
		}
	}
	
	/**
	 * Rename all the variable and list monitors in a project. This will skip the names of any
	 * monitors that were showing when the project was saved, as well as any monitors that can be
	 * shown by the code, as long as they are not in large mode, in which case the name isn't
	 * visible.
	 */
	public static void renameMonitors(Project3 p) {
		Set<String> showable = new HashSet<>();
		for (Target t : p.targets) {
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					if (n.opcode.equals("data_showvariable")) {
						showable.add(n.fields.get("VARIABLE").id);
					}
				}
			}
		}
		for (Monitor m : p.monitors) {
			if (showable.contains(m.id) && !"large".equals(m.mode)) {
				continue;
			}
			if (m.visible && !"large".equals(m.mode)) {
				continue;
			}
			if (m.params.containsKey("VARIABLE")) m.params.put("VARIABLE", generateHex());
			if (m.params.containsKey("LIST")) m.params.put("LIST", generateHex());
		}
	}
	
	/**
	 * Deletes the content under blocks that are in slots. This prevents variable blocks from being
	 * removed from slots, and any text box under an input also disappears.
	 */
	public static void bakeInputs(Project3 p) {
		for (Target t : p.targets) {
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					for (Input i : n.inputs.values()) {
						if (i.shadow.equals(Input.Shadow.OBSCURED_SHADOW)) {
							i.shadow = Input.Shadow.SHADOW;
							i.coveredInputID = null;
							i.coveredInput = null;
						}
						if (i.input != null) {
							if (i.input.type.equals(ArrayBlock.ABType.COLOR)) {
								i.input.type = ArrayBlock.ABType.STRING;
							}
						}
					}
				}
			}
			
		}
		//This turned out to just be a workaround for an unrelated problem this method was causing. As the issue has been patched, it is no longer needed.
		//		//Add a blank sprite to prevent glitches.
		//		Sprite s = new Sprite();
		//		s.visible = false;
		//		s.y = 0;
		//		s.x = 0;
		//		s.volume = 0;
		//		s.rotationStyle = "all around";
		//		s.variables = new HashMap<>();
		//		s.sounds = new ArrayList<>();
		//		s.costumes = new ArrayList<>();
		//		
		//		//Sprite needs a costume.
		//		final String SVG = "<svg version=\"1.1\" width=\"2\" height=\"2\" viewBox=\"-1 -1 1 1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"></svg>";
		//		Costume c = new Costume();
		//		c.dataFormat = "svg";
		//		c.name = "Blank";
		//		c.assetId = Md5.getMd5(SVG.getBytes());
		//		c.md5ext = c.assetId + "." + c.dataFormat;
		//		
		//		s.costumes.add(c);
		//		
		//		s.name = "Sprite-Bake";
		//		s.lists = new HashMap<>();
		//		s.comments = new HashMap<>();
		//		s.broadcasts = new HashMap<>();
		//		s.layerOrder = 1;
		//		s.blocks = new HashMap<>();
		//		p.targets.add(1, s);
		//		
		//		//Add the costume content to the project.
		//		ProjectAsset a = new ProjectAsset();
		//		a.name = c.md5ext;
		//		a.content = SVG.getBytes();
		//		
		//		fp.assets.put(c.assetId, a);
	}
	
	//	public static byte[] fileToBytes(File f) throws IOException {
	//		ByteArrayOutputStream out = new ByteArrayOutputStream();
	//		InputStream in = new FileInputStream(f);
	//		int read = 0;
	//		byte[] buffer = new byte[8192];
	//		while ((read = in.read(buffer)) != -1) {
	//			out.write(buffer, 0, read);
	//		}
	//		in.close();
	//		return out.toByteArray();
	//	}
	
	/**
	 * Generates short sequences of hex digits to use as names.
	 */
	public static String generateHex() {
		return UUID.randomUUID().toString().substring(0, 4);
	}
}
