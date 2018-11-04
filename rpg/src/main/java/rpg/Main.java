package rpg;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.plugin.PluginBase;
import guild.town.customLevel.CustomLevelManager;
import guild.town.customLevel.CustomProvider;

public class Main extends PluginBase implements Listener{
	
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		
	}
	
	 @EventHandler
	 public void onChat(PlayerChatEvent ev) {
		 if(ev.getPlayer().isOp()) {
			 Boolean cancel = true;
			 String[] strs = ev.getMessage().split(" ");
			 if(strs[0].equals("cl")) {
				 CustomLevelManager.generate(strs[1]);
		 	 }else if(strs[0].equals("ll")) {
		 		CustomLevelManager.load(strs[1]);
			 }else if(strs[0].equals("lt")) {
				CustomLevelManager.teleport(ev.getPlayer(), 1, 10, 1, strs[1]);
		 	}else if(strs[0].equals("sv")) {
		 		 this.getServer().getLevelByName(strs[1]).getProvider().saveChunks();
		 	 }else if(ev.getMessage().charAt(0) == '#') {
				 Server.getInstance().dispatchCommand(ev.getPlayer(), ev.getMessage().substring(1, ev.getMessage().length()));
			 }else {
				 cancel = false;
			 }
			 if(cancel) {
				 ev.getPlayer().sendMessage("Command runned.");
			 }
		 }
	 }
}
