package nightmare;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import nightmare.util.EnumFacing;
import nightmare.world.World;

import java.util.HashMap;
import java.util.Map;

public class NightmareGenerator extends Generator {

    public static HashMap<Long, World> worlds = new HashMap<>();

    public Map<String, Object> options;
    private ChunkManager level;
    private long seed;


    public NightmareGenerator() {
        this(new HashMap<>());
    }

    public NightmareGenerator(Map<String, Object> options) {
        this.options = options;
    }

    @Override
    public int getId() {
        return TYPE_INFINITE;
    }

    @Override
    public ChunkManager getChunkManager() {
        return level;
    }

    @Override
    public String getName() {
        return "NIGHTMARE";
    }

    @Override
    public Map<String, Object> getSettings() {
        return new HashMap<>();
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.level = level;
        this.seed = level.getSeed();
    }

    @Override
    public void generateChunk(final int chunkX, final int chunkZ) {
        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);
        World world = new World(level, this.seed);
        worlds.put((((long) chunkX) << 32) | (chunkZ & 0xffffffffL), world);
        world.getChunkProvider().provideChunk(chunkX, chunkZ, chunk);
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        World world = new World(this.level, this.seed);
        world.getChunkProvider().populate(world.getChunkProvider(), chunkX, chunkZ);
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(1024, 256, 1024);
    }
}
