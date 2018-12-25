package waw.campus;

import cn.nukkit.Player;
import cn.nukkit.item.ItemMap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Campus {

	public static int width = 128;
	public static int height = 128;
	
	public static final int TYPE_SIDE_X = 0;
	public static final int TYPE_SIDE_Y = 1;
	
	BufferedImage campus = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB);
	
	public ArrayList<CampusElement> elements = new ArrayList<>();
	
	private int y = 0;
	private int my = 0;
	private int x = 0;
	
	private int margin = 10;
	
	private Campus next_page  = null;
	
	public static HashMap<Player,Campus> showCampus = new HashMap<>();
	
	public static Boolean allClose = false;
	
	//private static HashMap<Player,Item> ItemMap = new HashMap<>();
	
	private static HashMap<Player,Campus> nexst = new HashMap<>();
	
	
	public void setNextPage(Campus c) {
		this.next_page = c;
	}
	
	protected void addElement(CampusElement element, int type) {
		elements.add(element);
		int[] size = element.getSize(campus.getGraphics());
		if(type == TYPE_SIDE_X) {
			element.setLocation(x, y);	
			x += size[0] + margin;
		}else if(type == TYPE_SIDE_Y) {
			x = 0;
			y += my + margin;
			element.setLocation(x, y);
		}
		if(my < size[1]) {
			my = size[1];
		}
	}
	
	public BufferedImage getImage() {
		Graphics g = campus.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0,0, width, height);
		g.setColor(Color.BLACK);
		for(CampusElement element : elements) {
			element.drawTo(campus,g);
		}
		g.dispose();
		return campus;
	}
	
	public static void setNexst(Player player,Campus c) {
		nexst.put(player, c);
	}
	

	public void sendItemMap(Player player) {
		ItemMap map = (ItemMap) new ItemMap().setCustomName("image");
		cInventoryManager.getInventory(player).showItem(map, 1);
		player.getInventory().setHeldItemSlot(1);
		this.sendImage(player);
	}
	
	public void sendImage(Player player) {
		if(player.getInventory().getItemInHand() instanceof ItemMap) {
			ItemMap map = (ItemMap) player.getInventory().getItemInHand();
			map.setImage(getImage());
			if(this.next_page != null) {
				setNexst(player,this.next_page);
			}
			if(player.isOnline()) {
				map.sendImage(player);	
			}
			showCampus.put(player, this);
		}
	}
	
	public static void closeAll() {
		allClose = true;
		for(Entry<Player, Campus> e : showCampus.entrySet()) {
			close(e.getKey());
		}
	}
	
	public static void close(Player player) {
		if(showCampus.containsKey(player) && showCampus.get(player).canClose(player)) {
				if(nexst.containsKey(player) && !allClose) {
					Campus c = nexst.get(player);
					nexst.remove(player);
					c.sendImage(player);
				}else {
					cInventoryManager.getInventory(player).setSlots();
					showCampus.remove(player);
				}
		}
	} 
	
	public Boolean canClose(Player player) {
		return true;
	}
}
