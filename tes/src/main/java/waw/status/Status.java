package waw.status;

import java.awt.Color;

import cn.nukkit.Player;
import waw.campus.Campus;
import waw.campus.ElementImage;
import waw.campus.ElementPanel;
import waw.campus.ElementText;


public class Status extends Campus {

	public Status(Player player ) {
		ElementPanel panel = new ElementPanel(this);
		//this.addElement(new ElementImage(),32,32), TYPE_SIDE_X);
		this.addElement(panel, TYPE_SIDE_X);
		panel.addElement(new ElementText(player.getName(),Color.BLACK,10), TYPE_SIDE_X);
		//panel.addElement(new ElementText("Rank." + pv.getRank(),Color.RED,12), TYPE_SIDE_Y);
		//this.setNextPage();
}
}
