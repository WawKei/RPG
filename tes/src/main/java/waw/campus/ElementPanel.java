package waw.campus;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ElementPanel extends CampusElement{


	private ArrayList<CampusElement> elements = new ArrayList<>();
	
	private Campus campus;
	
	public ElementPanel(Campus campus) {
		this.campus = campus;
	}

	private int yi = 0;
	private int my = 0;
	private int xi = 0;
	private int mx = 0;
	
	private int margin = 5;
	
	public void addElement(CampusElement element, int type) {
		elements.add(element);
		int[] size = element.getSize(campus.campus.getGraphics());
		if(type == Campus.TYPE_SIDE_X) {
			element.setLocation(xi+x, yi+y);	
			xi += size[0] + margin;
		}else if(type == Campus.TYPE_SIDE_Y) {
			xi = 0;
			yi += my + margin;
			element.setLocation(xi+x, yi+y);
		}
		if(my < size[1]) {
			my = size[1];
		}
		if(mx < xi) {
			mx = xi;
		}
	}
	
	@Override
	public void drawTo(BufferedImage image,Graphics g) {
		for(CampusElement elemet : elements) {
			elemet.drawTo(image, g);
		}
	}

	@Override
	public int[] getSize(Graphics g) {
		return new int[] {x,y+my};
	}
}
