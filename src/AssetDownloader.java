import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTextField;

import scratch.api.FetchProjectAsset;
import scratch.api.ProjectFetch;
import scratch.generic.Project;

public class AssetDownloader {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		JTextField field = new JTextField();
		field.addActionListener((e)->{
			try {
				
				downloadProject(field.getText().replaceAll("[^0-9]", ""));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		frame.add(field);
		frame.setSize(300, 200);
		frame.setVisible(true);
	}
	
	public static void downloadProject(String id) throws IOException {
		Project p = ProjectFetch.fetchProject(id, true);
		System.out.println(p);
		List<FetchProjectAsset> pas = ProjectFetch.getAssets(p);
		System.out.println(pas);
		
		File f = new File("AssetExtract");
		f.mkdirs();
		for (FetchProjectAsset pa : pas) {
			pa.saveToFile(new File(f, pa.name.replaceAll("[^A-Za-z0-9]", "") + "-" + pa.md5 + "." + pa.extension));
			System.out.println(pa.name.replaceAll("[^A-Za-z0-9]", "") + "-" + pa.md5 + "." + pa.extension);
		}
	}
}
