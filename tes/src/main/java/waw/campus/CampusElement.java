package waw.campus;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public abstract class CampusElement {
	
	protected int x;
	protected int y;
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public abstract int[] getSize(Graphics g);
	
	public abstract void drawTo(BufferedImage image, Graphics g);
}
