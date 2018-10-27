package rpg;

import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;

public class Main extends PluginBase implements Listener{
	
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
	}
}
