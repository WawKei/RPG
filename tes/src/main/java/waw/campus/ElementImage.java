package waw.campus;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ElementImage extends CampusElement{

	private BufferedImage image;
	private int w;
	private int h;

	public ElementImage(File file,int w, int h) {
		this(loadImage(file),w,h);
	}
	
	public ElementImage(BufferedImage image,int w, int h) {
		this.image = image;
		this.w = w;
		this.h = h;
	}
	
	@Override
	public void drawTo(BufferedImage image,Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
	    g2d.drawImage(this.image, x, y, w, h, null);
	}

	@Override
	public int[] getSize(Graphics g) {
		return new int[] {w,h};
	}
	
	public static BufferedImage loadImage(File f) {
		try {
			BufferedImage img = ImageIO.read(f);
			return img;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static BufferedImage loadImage(String url) {
		try {
			URL u = new URL(url);
			BufferedImage img = ImageIO.read(u);
			return img;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
}
