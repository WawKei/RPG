package waw.campus;

import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.item.Item;


public class cInventoryManager implements Listener{
	
	private static HashMap<Player, cInventory> inventory = new HashMap<>();
	
	private static Item Book = Item.get(Item.ENCHANT_BOOK).setCustomName("status");
	private static Item Movie = Item.get(Item.BOOK).setCustomName("Movie");
	private static Item INFO = Item.get(Item.PAPER).setCustomName("お知らせ");
	
	@EventHandler
	public void inventoryTrans(PlayerItemHeldEvent ev) {
		Player player = ev.getPlayer();
		if(ev.getItem().getId() == Item.AIR || ev.getSlot() > 9) {
			if(getInventory(player).getShow() == 1) {
				getInventory(player).setSlots2();
				ev.setCancelled(true);	
			}
		}
	}
	
	@EventHandler
	public void inventoryTrans(EntityInventoryChangeEvent ev) {
		if(ev.getEntity() instanceof Player) {
			//System.out.println(ev.getSlot() + " : " + ev.getNewItem().getName() + " : " + ev.getOldItem().getName());
			Player player = (Player)ev.getEntity();
			if(!ev.getNewItem().getCustomName().equals("gif-animation") && 
				getInventory(player).getShow() == 1) {
				getInventory(player).setSlots2();
				ev.setCancelled(true);
			}
			if(ev.getSlot() == 0 && !ev.getNewItem().equals(Book)){
				ev.setCancelled();
			}
			if(ev.getNewItem().equals(Book) && ev.getSlot() != 0) {
				ev.setCancelled();
			}
			if(getInventory(player).getShow() == 2 && !ev.isCancelled() && !getInventory(player).change) {
					getInventory(player).updateSlots2();
			}
			//System.out.println(ev.isCancelled());
		}
	}
	
	 @EventHandler
	 public void login(PlayerLoginEvent ev) {	
		 Player player = ev.getPlayer();
		 player.getInventory().setItem(0,Book);
		 getInventory(player);
		 setItem(player, Book, 0);
		 setItem(player, Movie, 1);
		 setItem(player, INFO, 2);
	 }
	 
	 @EventHandler
	 public void logout(PlayerQuitEvent ev) {	
		 Player player = ev.getPlayer();
		 if(getInventory(player).getShow() == 2) {
				getInventory(player).updateSlots2();
		 }
		 getInventory(player).setSlots2();
	 }
	 
	 public static void closeAll() {
		 for(Player player : Server.getInstance().getOnlinePlayers().values()) {
			 if(getInventory(player).getShow() == 2) {
					getInventory(player).updateSlots2();
			 }
			 getInventory(player).setSlots2();
		 }
	 }
	 
	@EventHandler
	public void onPlayerTouch(PlayerInteractEvent ev) {
		if(ev.getItem().equals(Book)) {
			getInventory(ev.getPlayer()).setSlots();
		}
	}
	public static void setItem(Player player, Item item, int i) {
		getInventory(player).setItem(item, i);
	}
	
	public static cInventory getInventory(Player player) {
		if(!inventory.containsKey(player)) {
			inventory.put(player, new cInventory(player));
		}
		return inventory.get(player);
	}
	
}