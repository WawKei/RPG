package waw;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.plugin.PluginBase;
import waw.campus.cInventoryManager;

import java.util.Random;
//import com.sun.tools.internal.ws.resources.GeneratorMessages;

public class Main extends PluginBase implements Listener {

    @Override
    public void onEnable () {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new cInventoryManager(), this);
    }

    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent event) {
        String geometryData =  event.getPlayer().getSkin().getGeometryData();
        String geometryName = event.getPlayer().getSkin().getGeometryName();

        System.out.println(geometryData);
        System.out.println(geometryName);

    }

    @EventHandler
    public void onPlayerTouch(PlayerInteractEvent event){
        Block block = event.getBlock();
        CompoundTag tag = new CompoundTag().putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("", block.getX() + 0.5))
                .add(new DoubleTag("", block.getY() + 1))
                .add(new DoubleTag("", block.getZ() + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", new Random().nextFloat() * 360))
                        .add(new FloatTag("", 0)))
                .putShort("Health", 20)
                .putCompound("Skin", new CompoundTag()
                    .putByteArray("Data", event.getPlayer().getSkin().getSkinData())
                    .putString("ModelId", event.getPlayer().getSkin().getSkinId())
                );

        EntityHuman human = new EntityHuman(event.getBlock().getLevel().getChunk(event.getPlayer().getChunkX(),event.getPlayer().getChunkZ()),tag );
        human.setPosition(new Vector3(event.getPlayer().getFloorX(),event.getPlayer().getFloorY(),event.getPlayer().getFloorZ()));
        human.getSkin().setGeometryName("geometry.pig");
        human.spawnTo(event.getPlayer());
    }

    @EventHandler
    public void onPlayerTouch(PlayerInteractEntityEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof EntityHuman){
            EntityHuman human = (EntityHuman)entity;
            System.out.println(human.getSkin().getGeometryName());
            System.out.println(human.getSkin().getGeometryData());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        player.sendTip("Chunk Coord: "+((int)(player.getX()) >> 4)+" ,"+((int)(player.getZ()) >> 4));
        player.sendPopup("BiomeId: "+player.getLevel().getBiomeId((int)player.getX(), (int)player.getZ()));
    }
}
