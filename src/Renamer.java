import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import scratch.generic.FullProject;
import scratch.sb3.Block;
import scratch.sb3.Field;
import scratch.sb3.Input;
import scratch.sb3.NormalBlock;
import scratch.sb3.Project3;
import scratch.sb3.Target;

public class Renamer {
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(1000, 200);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel top = new JPanel();
		JPanel bottom = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
		frame.setLayout(new BorderLayout());
		
		frame.add(top, BorderLayout.NORTH);
		frame.add(bottom, BorderLayout.SOUTH);
		
		top.add(new MButton("Open", Renamer::open));
		top.add(new MButton("Save", Renamer::save));
		
		bottom.add(new MButton("Broadcast", Renamer::broadcast));
		
		frame.setVisible(true);
	}
	
	static FullProject project = new FullProject();
	
	public static void broadcast(ActionEvent e) {
		List<String> b = getBroadcasts(project.project.asScratch3());
		JComboBox<String> dropdown = new JComboBox<>();
		
		for (String s : b) {
			dropdown.addItem(s);
		}
		
		JFrame frame = new JFrame();
		frame.setSize(300, 200);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.add(dropdown, BorderLayout.NORTH);
		frame.add(new MButton("Done", (ae) -> {
			frame.setVisible(false);
			String original = (String) dropdown.getSelectedItem();
			String replacement = JOptionPane.showInputDialog("Enter the broadcast's new name");
			renameBroadcast(project.project.asScratch3(), original, replacement);
		}));
		
		frame.setVisible(true);
		
	}
	
	public static List<String> getBroadcasts(Project3 p) {
		List<String> names = new ArrayList<>();
		for (Target t : p.targets) {
			for (String s : t.broadcasts.keySet()) {
				System.out.println(s);
				names.add(t.broadcasts.get(s));
				//				names.put(s, t.broadcasts.get(s));
			}
		}
		return names;
	}
	
	public static String renameBroadcast(Project3 p, String original, String replacement) {
		
		//		Map<String, String> names = new HashMap<>();
		for (Target t : p.targets) {
			for (String s : t.broadcasts.keySet()) {
				System.out.println(s);
				if (t.broadcasts.get(s).equals(original)) {
					t.broadcasts.put(s, replacement);
					System.out.println("Found " + original);
				}
				//				names.put(s, t.broadcasts.get(s));
			}
		}
		for (Target t : p.targets) {
			for (Block b : t.blocks.values()) {
				if (b.isNormalBlock()) {
					NormalBlock n = b.asNormalBlock();
					if (n.opcode.equals("event_whenbroadcastreceived")) {
						Field f = n.fields.get("BROADCAST_OPTION");
						System.out.println(f.value);
						
						if (f.value.equals(original)) {
							f.value = replacement;
							System.out.println("Found field " + original);
							
						}
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
	
	static JFileChooser fc = new JFileChooser();
	
	public static void open(ActionEvent e) {
		fc.showOpenDialog(null);
		try {
			project = new FullProject(fc.getSelectedFile());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void save(ActionEvent e) {
		fc.showSaveDialog(null);
		try {
			project.write(fc.getSelectedFile());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}

class MButton extends JButton {
	
	private static final long serialVersionUID = -8950898789779345745L;
	
	public MButton(String text, ActionListener action) {
		this.setText(text);
		this.addActionListener(action);
	}
}