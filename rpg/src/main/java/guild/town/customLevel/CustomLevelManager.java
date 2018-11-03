package guild.town.customLevel;

import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Location;

public class CustomLevelManager {

	//ここで指定したフォルダ内にファイルが生成される。
	public static final String CHUNK_DATA_DIR_PATH = "./WorldDatas/";
	
	public static void generate(String filename) {
		 Server.getInstance().generateLevel(filename,  new java.util.Random().nextLong(), null, new HashMap<String,Object>(), CustomProvider.class);
		 Server.getInstance().loadLevel(filename);
		 CustomProvider provider = (CustomProvider) Server.getInstance().getLevelByName(filename).getProvider();
		 provider.mapName = filename;
	}
	
	public static void load(String filename) {
		Server.getInstance().loadLevel(filename);
		CustomProvider provider = (CustomProvider) Server.getInstance().getLevelByName(filename).getProvider();
		provider.mapName = filename;
	}
	
	/*
	 * ワールドのサイズ指定
	 *　size*16 x size*16
	 *　のワールドになる。（その領域のチャンクが読み込み、保存される。）
	 * 最大32
	 */
	
	public static void setSize(String filename, int size) {
		if(size > 32) {
			size = 32;
		}
		CustomProvider provider = (CustomProvider) Server.getInstance().getLevelByName(filename).getProvider();
		provider.size = size;
	}
	
	public static void teleport(Player player, int x, int y, int z, String filename) {
		player.teleport(new Location(0, 10, 0, Server.getInstance().getLevelByName(filename)));
	}
}
