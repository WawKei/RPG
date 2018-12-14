package nightmare;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.plugin.PluginBase;
import com.sun.tools.internal.ws.resources.GeneratorMessages;

public class Main extends PluginBase implements Listener {

    @Override
    public void onEnable () {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().setPropertyString("level-type", "NIGHTMARE");
        Generator.addGenerator(NightmareGenerator.class, "NIGHTMARE", Generator.TYPE_INFINITE);
    }

    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Test");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        player.sendTip("Chunk Coord: "+((int)(player.getX()) >> 4)+" ,"+((int)(player.getZ()) >> 4));
        player.sendPopup("BiomeId: "+player.getLevel().getBiomeId((int)player.getX(), (int)player.getZ()));
    }
}
