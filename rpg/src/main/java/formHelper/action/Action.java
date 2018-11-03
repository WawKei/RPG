package formHelper.action;

import java.util.HashMap;

import cn.nukkit.Player;

public abstract class Action {

	public abstract void action(Player player, HashMap<String,Object> args);
	
}
