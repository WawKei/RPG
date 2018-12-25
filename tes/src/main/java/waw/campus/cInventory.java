package waw.campus;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

public class cInventory {
	
	private Player player;
	private Item[] slots = new Item[9];
	private Item[] slots2 = new Item[9];
	private int show = 2;
	public Boolean change = false;
	
	public cInventory(Player player) {
		this.player = player;
		for(int i = 0; i < 9; i++) {
			this.setItem(Item.get(Item.AIR),i);	
		}
	}
	
	public int getShow() {
		return this.show;
	}
	
	public void setItem(Item item, int index) {
		this.updateSlots2();
		if(index >= 0 && index <= 9) {
			slots[index] = item;	
		}
	}
	
	public void showItem(Item item, int index) {
		//if(this.getShow() == 1) {
			player.getInventory().setItem(index, item);
		//}
	}
	
	public void updateSlots2() {
		if(this.show == 2) {
			for(int i = 0; i < 9; i++) {
				slots2[i] = player.getInventory().getItem(i);
				System.out.println(slots2[i]);
			}
		}
	}
	
	public void setSlots() {
		this.updateSlots2();
		this.show = 2;
		for(int i = 0; i < 9; i++) {
			player.getInventory().setItem(i,slots[i]);
		}
		this.show = 1;
	}
	
	public void setSlots2() {
		this.change = true;
		this.show = 2;
		for(int i = 0; i < 9; i++) {
			player.getInventory().setItem(i,slots2[i]);
		}
		this.change = false;
	}
	
	
}