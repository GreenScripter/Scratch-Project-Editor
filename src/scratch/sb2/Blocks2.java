package scratch.sb2;

import java.util.ArrayList;
import java.util.List;

public class Blocks2 extends ArrayList<Block2> implements Containable2 {
	
	private static final long serialVersionUID = -450921349132386566L;
	
	public List<Block2> getAllSubBlocks() {
		List<Block2> all = new ArrayList<>();
		for (Block2 c : this) {
			if (c instanceof Block2) {
				all.add((Block2) c);
				all.addAll(((Block2) c).getAllSubBlocks());
			}
		}
		
		return all;
	}
	
	public List<Block2> getAllLines() {
		List<Block2> all = new ArrayList<>();
		for (Block2 c : this) {
			if (c instanceof Block2) {
				all.add((Block2) c);
				all.addAll(((Block2) c).getAllLines());
			}
		}
		
		return all;
	}
}