package formHelper.action;

import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;

public class Routing {

	
	public static class Node{
		
		String url;
		HashMap<String, Object> args = new HashMap<String, Object>();
		
		public Node(String s) {
			url = s;
		}
		
		public Node set(String key, Object value) {
			args.put(key, value);
			return this;
		}
		
	}
	
	HashMap<String, Action> urls = new HashMap<String, Action>();
	
	HashMap<Player, HashMap<String, Node>> map = new HashMap<Player, HashMap<String, Node>>();
	
	public void setRouting(Player player, HashMap<String, Node> nodes) {
		map.put(player, nodes);
	}
	
}
