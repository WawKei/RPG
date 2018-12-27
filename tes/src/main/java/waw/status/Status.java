package waw.status;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import waw.campus.Campus;
import waw.campus.ElementImage;
import waw.campus.ElementPanel;
import waw.campus.ElementText;
import waw.itemImage.ItemImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;


public class Status extends Campus {

	public Status(Player player, Item item) {
		BufferedImage img = null;
		if((img =  ItemImageLoader.getImage(item.getId(),item.getDamage())) != null){
			ElementPanel panel = new ElementPanel(this);
			this.addElement(new ElementImage(img,32,32), TYPE_SIDE_X);
			this.addElement(panel, TYPE_SIDE_X);
			panel.addElement(new ElementText(item.getName(), Color.BLACK,10), TYPE_SIDE_X);
		}
	}
}
