package waw.campus;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ElementText extends CampusElement{

	private String text;
	private int size;
	private Color color;
	
	public ElementText(String text,Color color, int size) {
		this.text = text;
		this.size = size;
		this.color = color;
	}
	
	@Override
	public void drawTo(BufferedImage image,Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(color);
		g.setFont(new Font(null,Font.PLAIN,size));
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString(text, x, y + fm.getAscent());
	}

	@Override
	public int[] getSize(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g.setFont(new Font(null,Font.PLAIN,size));
		FontMetrics fm = g2d.getFontMetrics();
		int width = 0;
		for(int j=0; j<text.length(); j++){
			width += fm.charWidth(text.charAt(j));
		}
		
		return new int[] {width,fm.getHeight()};
	}
}
