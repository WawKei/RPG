package guild.town.customLevel;

import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.Chunk;
import cn.nukkit.level.format.anvil.ChunkSection;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.ThreadCache;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;


public class CustomProvider implements LevelProvider {

	protected Level level;

    protected final String path;

    protected CompoundTag levelData;

    private Vector3 spawn;

    protected CustomRegion lastRegion;

    protected final Map<Long, CustomRegion> regions = new HashMap<Long, CustomRegion>();

    private final Long2ObjectOpenHashMap<BaseFullChunk> chunks = new Long2ObjectOpenHashMap<BaseFullChunk>();
    
    static private final byte[] PAD_256 = new byte[256];
    
    public String mapName;
    
    public int size = 16;

    public static final String CUSTOM_DEFAULT_LEVEL_PATH = "./worlds/default/";
    
    public CustomProvider(Level level, String path) throws IOException {
        
    	this.level = level;
        
        this.path = CUSTOM_DEFAULT_LEVEL_PATH;
        
        
        File file_path = new File(this.path);
        if (!file_path.exists()) {
            file_path.mkdirs();
        }
        
        this.levelData = new CompoundTag();

        if (!this.levelData.contains("generatorName")) {
            this.levelData.putString("generatorName", Generator.getGenerator("DEFAULT").getSimpleName().toLowerCase());
        }

        if (!this.levelData.contains("generatorOptions")) {
            this.levelData.putString("generatorOptions", "");
        }

        this.spawn = new Vector3(0,10,0);
    }

    public BaseFullChunk loadChunk(long index, int chunkX, int chunkZ, boolean create) {

    	int regionX = getRegionIndexX(chunkX);
        int regionZ = getRegionIndexZ(chunkZ);
        CustomRegion region = this.loadRegion(regionX, regionZ);
        this.level.timings.syncChunkLoadDataTimer.startTiming();
    	BaseFullChunk chunk = null;
    	
    	if(chunkX >= 0 && chunkX < size && chunkZ >= 0 && chunkZ < size) {
            try {
                chunk = region.readChunk(chunkX - regionX * 32, chunkZ - regionZ * 32);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    	}
    	boolean f = false;
        if (chunk == null) {
            if (create) {
                chunk = this.getEmptyChunk(chunkX, chunkZ);
                putChunk(index, chunk);
                f = true;
            }
        } else {
            putChunk(index, chunk);
        }
        
        this.level.timings.syncChunkLoadDataTimer.stopTiming();
        
        if(f && !(chunkX >= 0 && chunkX < size && chunkZ >= 0 && chunkZ < size)) {
        	chunk.setGenerated(true);
        }
        
        return chunk;
    }
    
    public Chunk getEmptyChunk(int chunkX, int chunkZ) {
        return Chunk.getEmptyChunk(chunkX, chunkZ, this);
    }
    
    protected CustomRegion loadRegion(int x, int z) {
        CustomRegion tmp = lastRegion;
        if (tmp != null && x == tmp.getX() && z == tmp.getZ()) {
            return tmp;
        }
        long index = Level.chunkHash(x, z);
        CustomRegion region = this.regions.get(index);
        if (region == null) {
            region = new CustomRegion(this, x, z, mapName);
            this.regions.put(index, region);
            return lastRegion = region;
        } else {
            return lastRegion = region;
        }
    }

    public int size() {
        return this.chunks.size();
    }

    public ObjectIterator<BaseFullChunk> getChunks() {
        return chunks.values().iterator();
    }

    protected void putChunk(long index, BaseFullChunk chunk) {
        this.chunks.put(index, chunk);
    }

    public void unloadChunks() {
        Iterator<Map.Entry<Long, BaseFullChunk>> iter = chunks.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Long, BaseFullChunk> entry = iter.next();
            //long index = entry.getKey();
            BaseFullChunk chunk = entry.getValue();
            chunk.unload(true, false);
            iter.remove();
        }
    }

    public String getGenerator() {
        return "FLAT";
    }

    @SuppressWarnings("serial")
	public Map<String, Object> getGeneratorOptions() {
        return new HashMap<String, Object>() {
            {
                put("preset", levelData.getString("generatorOptions"));
            }
        };
    }

    public Map<Long, BaseFullChunk> getLoadedChunks() {
        return this.chunks;
    }

    public boolean isChunkLoaded(int X, int Z) {
        return this.chunks.containsKey(Level.chunkHash(X, Z));
    }

    public boolean isChunkLoaded(long hash) {
        return this.chunks.containsKey(hash);
    }

    public CustomRegion getRegion(int x, int z) {
        long index = Level.chunkHash(x, z);
        return this.regions.get(index);
    }

    protected static int getRegionIndexX(int chunkX) {
        return chunkX >> 5;
    }

    protected static int getRegionIndexZ(int chunkZ) {
        return chunkZ >> 5;
    }

    public String getPath() {
        return path;
    }

    public Server getServer() {
        return this.level.getServer();
    }

    public Level getLevel() {
        return level;
    }

    public String getName() {
        return this.levelData.getString("LevelName");
    }

    public boolean isRaining() {
        return this.levelData.getBoolean("raining");
    }

    public void setRaining(boolean raining) {
        this.levelData.putBoolean("raining", raining);
    }

    public int getRainTime() {
        return this.levelData.getInt("rainTime");
    }

    public void setRainTime(int rainTime) {
        this.levelData.putInt("rainTime", rainTime);
    }

    public boolean isThundering() {
        return this.levelData.getBoolean("thundering");
    }

    public void setThundering(boolean thundering) {
        this.levelData.putBoolean("thundering", thundering);
    }

    public int getThunderTime() {
        return this.levelData.getInt("thunderTime");
    }

    public void setThunderTime(int thunderTime) {
        this.levelData.putInt("thunderTime", thunderTime);
    }

    public long getCurrentTick() {
        return this.levelData.getLong("Time");
    }

    public void setCurrentTick(long currentTick) {
        this.levelData.putLong("Time", currentTick);
    }

    public long getTime() {
        return this.levelData.getLong("DayTime");
    }

    public void setTime(long value) {
        this.levelData.putLong("DayTime", value);
    }

    
    public long getSeed() {
        return this.levelData.getLong("RandomSeed");
    }

    
    public void setSeed(long value) {
        this.levelData.putLong("RandomSeed", value);
    }

    
    public Vector3 getSpawn() {
        return spawn;
    }

    
    public void setSpawn(Vector3 pos) {
        this.levelData.putInt("SpawnX", (int) pos.x);
        this.levelData.putInt("SpawnY", (int) pos.y);
        this.levelData.putInt("SpawnZ", (int) pos.z);
        spawn = pos;
    }

    
    public GameRules getGamerules() {
        GameRules rules = GameRules.getDefault();

        if (this.levelData.contains("GameRules"))
            rules.readNBT(this.levelData.getCompound("GameRules"));

        return rules;
    }

    
    public void setGameRules(GameRules rules) {
        this.levelData.putCompound("GameRules", rules.writeNBT());
    }

    
    public void doGarbageCollection() {
        int limit = (int) (System.currentTimeMillis() - 50);
        for (Entry<Long, CustomRegion> entry : this.regions.entrySet()) {
            long index = entry.getKey();
            CustomRegion region = entry.getValue();
            if (region.lastUsed <= limit) {
                try {
                    region.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                lastRegion = null;
                this.regions.remove(index);
            }
        }
    }

    
    public void saveChunks() {
        for (BaseFullChunk chunk : this.chunks.values()) {
            if (chunk.getChanges() != 0) {
                chunk.setChanged(false);
                this.saveChunk(chunk.getX(), chunk.getZ());
            }
        }
    }

    public CompoundTag getLevelData() {
        return levelData;
    }

    
    public void saveLevelData() {
        try {
            NBTIO.writeGZIPCompressed(new CompoundTag().putCompound("Data", this.levelData), new FileOutputStream(this.getPath() + "level.dat"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateLevelName(String name) {
        if (!this.getName().equals(name)) {
            this.levelData.putString("LevelName", name);
        }
    }

    
    public boolean loadChunk(int chunkX, int chunkZ) {
        return this.loadChunk(chunkX, chunkZ, false);
    }

    
    public boolean loadChunk(int chunkX, int chunkZ, boolean create) {
        long index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunks.containsKey(index)) {
            return true;
        }
        return loadChunk(index, chunkX, chunkZ, create) != null;
    }

    
    public boolean unloadChunk(int X, int Z) {
        return this.unloadChunk(X, Z, true);
    }

    
    public boolean unloadChunk(int X, int Z, boolean safe) {
        long index = Level.chunkHash(X, Z);
        BaseFullChunk chunk = this.chunks.get(index);
        if (chunk != null && chunk.unload(false, safe)) {
            lastChunk = null;
            this.chunks.remove(index, chunk);
            return true;
        }
        return false;
    }

    
    public BaseFullChunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, false);
    }

    private volatile BaseFullChunk lastChunk;

    
    public BaseFullChunk getLoadedChunk(int chunkX, int chunkZ) {
        BaseFullChunk tmp = lastChunk;
        if (tmp != null && tmp.getX() == chunkX && tmp.getZ() == chunkZ) {
            return tmp;
        }
        long index = Level.chunkHash(chunkX, chunkZ);
        lastChunk = tmp = chunks.get(index);
        return tmp;
    }

    
    public BaseFullChunk getLoadedChunk(long hash) {
        BaseFullChunk tmp = lastChunk;
        if (tmp != null && tmp.getIndex() == hash) {
            return tmp;
        }
        lastChunk = tmp = chunks.get(hash);
        return tmp;
    }

    
    public BaseFullChunk getChunk(int chunkX, int chunkZ, boolean create) {
        BaseFullChunk tmp = lastChunk;
        if (tmp != null && tmp.getX() == chunkX && tmp.getZ() == chunkZ) {
            return tmp;
        }
        long index = Level.chunkHash(chunkX, chunkZ);
        lastChunk = tmp = chunks.get(index);
        if (tmp != null) {
            return tmp;
        } else {
            tmp = this.loadChunk(index, chunkX, chunkZ, create);
            lastChunk = tmp;
            return tmp;
        }
    }

    
    public void setChunk(int chunkX, int chunkZ, FullChunk chunk) {
        if (!(chunk instanceof BaseFullChunk)) {
            throw new ChunkException("Invalid Chunk class");
        }
        chunk.setProvider(this);
        chunk.setPosition(chunkX, chunkZ);
        long index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunks.containsKey(index) && !this.chunks.get(index).equals(chunk)) {
            this.unloadChunk(chunkX, chunkZ, false);
        }
        this.chunks.put(index, (BaseFullChunk) chunk);
    }

    
    public boolean isChunkPopulated(int chunkX, int chunkZ) {
        BaseFullChunk chunk = this.getChunk(chunkX, chunkZ);
        return chunk != null && chunk.isPopulated();
    }

    
    public synchronized void close() {
        this.unloadChunks();
        Iterator<Entry<Long, CustomRegion>> iter = this.regions.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Long, CustomRegion> entry = iter.next();
            //long index = entry.getKey();
            CustomRegion region = entry.getValue();
            try {
                region.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            lastRegion = null;
            iter.remove();
        }
        this.level = null;
    }

    
    public boolean isChunkGenerated(int chunkX, int chunkZ) {
        CustomRegion region = this.getRegion(chunkX >> 5, chunkZ >> 5);
        return region != null && region.chunkExists(chunkX - region.getX() * 32, chunkZ - region.getZ() * 32) && this.getChunk(chunkX - region.getX() * 32, chunkZ - region.getZ() * 32, true).isGenerated();
    }

	
	public AsyncTask requestChunkTask(int x, int z) {
		Chunk chunk = (Chunk) this.getChunk(x, z, false);
        if (chunk == null) {
            throw new ChunkException("Invalid Chunk Set");
        }

        long timestamp = chunk.getChanges();

        byte[] blockEntities = new byte[0];

        if (!chunk.getBlockEntities().isEmpty()) {
            List<CompoundTag> tagList = new ArrayList<CompoundTag>();

            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                if (blockEntity instanceof BlockEntitySpawnable) {
                    tagList.add(((BlockEntitySpawnable) blockEntity).getSpawnCompound());
                }
            }

            try {
                blockEntities = NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Map<Integer, Integer> extra = chunk.getBlockExtraDataArray();
        BinaryStream extraData;
        if (!extra.isEmpty()) {
            extraData = new BinaryStream();
            extraData.putVarInt(extra.size());
            for (Map.Entry<Integer, Integer> entry : extra.entrySet()) {
                extraData.putVarInt(entry.getKey());
                extraData.putLShort(entry.getValue());
            }
        } else {
            extraData = null;
        }

        BinaryStream stream = ThreadCache.binaryStream.get().reset();
        int count = 0;
        cn.nukkit.level.format.ChunkSection[] sections = chunk.getSections();
        for (int i = sections.length - 1; i >= 0; i--) {
            if (!sections[i].isEmpty()) {
                count = i + 1;
                break;
            }
        }
        stream.putByte((byte) count);
        for (int i = 0; i < count; i++) {
            stream.putByte((byte) 0);
            stream.put(sections[i].getBytes());
        }
        for (byte height : chunk.getHeightMapArray()) {
            stream.putByte(height);
        }
        stream.put(PAD_256);
        stream.put(chunk.getBiomeIdArray());
        stream.putByte((byte) 0);
        if (extraData != null) {
            stream.put(extraData.getBuffer());
        } else {
            stream.putVarInt(0);
        }
        stream.put(blockEntities);

        this.getLevel().chunkRequestCallback(timestamp, x, z, stream.getBuffer());

        return null;
	}

	
	public void saveChunk(int X, int Z) {
		BaseFullChunk chunk = this.getChunk(X, Z);
        if (chunk != null) {
            try {
            	if((X >= 0 && X < size && Z >= 0 && Z < size)) {
                    this.loadRegion(X >> 5, Z >> 5).writeChunk(chunk);	
            	}
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
	}

	
	public void saveChunk(int x, int z, FullChunk chunk) {
		//if(x >> 5 != 0 || z >> 5 != 0) return;
		if (!(chunk instanceof Chunk)) {
            throw new ChunkException("Invalid Chunk class");
        }
        int regionX = x >> 5;
        int regionZ = z >> 5;
        this.loadRegion(regionX, regionZ);
        chunk.setX(x);
        chunk.setZ(z);
        try {
        	if((x >= 0 && x < size && z >= 0 && z < size)) {
                this.getRegion(regionX, regionZ).writeChunk(chunk);	
        	}
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
	
	public static void generate(String path, String name, long seed, Class<? extends Generator> generator) throws IOException {
        generate(path, name, seed, generator, new HashMap<String, String>());
    }

    public static void generate(String path, String name, long seed, Class<? extends Generator> generator, Map<String, String> options) throws IOException {
        if (!new File(CUSTOM_DEFAULT_LEVEL_PATH + "/region").exists()) {
            new File(CUSTOM_DEFAULT_LEVEL_PATH + "/region").mkdirs();
        }

        CompoundTag levelData = new CompoundTag("Data")
                .putCompound("GameRules", new CompoundTag())

                .putLong("DayTime", 0)
                .putInt("GameType", 0)
                .putString("generatorName", Generator.getGeneratorName(generator))
                .putString("generatorOptions", options.containsKey("preset") ? options.get("preset") : "")
                .putInt("generatorVersion", 1)
                .putBoolean("hardcore", false)
                .putBoolean("initialized", true)
                .putLong("LastPlayed", System.currentTimeMillis() / 1000)
                .putString("LevelName", name)
                .putBoolean("raining", false)
                .putInt("rainTime", 0)
                .putLong("RandomSeed", seed)
                .putInt("SpawnX", 0)
                .putInt("SpawnY", 10)
                .putInt("SpawnZ", 0)
                .putBoolean("thundering", false)
                .putInt("thunderTime", 0)
                .putInt("version", 1)
                .putLong("Time", 0)
                .putLong("SizeOnDisk", 0);

        NBTIO.writeGZIPCompressed(new CompoundTag().putCompound("Data", levelData), new FileOutputStream(CUSTOM_DEFAULT_LEVEL_PATH + "level.dat"), ByteOrder.BIG_ENDIAN);
    }
    
    public static boolean usesChunkSection() {
        return true;
    }
    
    public static ChunkSection createChunkSection(int y) {
        ChunkSection cs = new ChunkSection(y);
        return cs;
    }


    public static boolean isValid(String path) {
    	String[] s = path.split("/");
    	String name = s[s.length - 2];
    	//System.out.println(name);
    	String path2 = CustomLevelManager.CHUNK_DATA_DIR_PATH + name + "-0-0-" + ".mca";
    	if(new File(path2).exists()) {
            return true;	
    	}
    	return false;
    }
    
    public static String getProviderName() {
        return "custom";
    }

    public static byte getProviderOrder() {
        return ORDER_YZX;
    }

}
