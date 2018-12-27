package waw;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.plugin.PluginBase;
import waw.campus.cInventoryManager;
import waw.database.userStatus.UserDao;
import waw.itemImage.ItemImageLoader;
import waw.status.Status;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import com.sun.tools.internal.ws.resources.GeneratorMessages;

public class Main extends PluginBase implements Listener {

    public static String dataPath;

    public static Connection con = null;

    @Override
    public void onEnable () {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new cInventoryManager(), this);
        dataPath = this.getServer().getDataPath();

        connectDbServer();

        ItemImageLoader.loadItemImagePath();
       // ItemImageLoader.downloadImage(Item.APPLE, ItemImageLoader.URL_APPLE);
    }

    public void connectDbServer(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/rpgdb?useSSL=false", "root", "1qaz!QAZ");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getSQLState());
            System.out.println(e.getMessage());
            this.getServer().getLogger().error("Cannot connect database server.");
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable(){
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Cannot disconnect database server");
            }
        }
    }

    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent event) {
        String geometryData =  event.getPlayer().getSkin().getGeometryData();
        String geometryName = event.getPlayer().getSkin().getGeometryName();

        System.out.println(geometryData);
        System.out.println(geometryName);

        UserDao dao = new UserDao();
        try {
            if(dao.selectUserByName(event.getPlayer().getName()) == null){
                dao.insertUser(event.getPlayer().getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @EventHandler
    public void onPlayerTouch(PlayerInteractEvent event){
        /*Block block = event.getBlock();
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
    */

            new Status(event.getPlayer(),event.getItem()).sendItemMap(event.getPlayer());
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
