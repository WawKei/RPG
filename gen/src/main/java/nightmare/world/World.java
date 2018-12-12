package nightmare.world;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.PopulatorHelpers;
import com.google.common.collect.Lists;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.world.biome.BiomeCache;
import nightmare.world.biome.BiomeGenBase;
import nightmare.world.biome.NMBiomes;
import nightmare.world.chunk.IChunkProvider;
import nightmare.world.gen.ChunkProviderGenerate;
import nightmare.world.gen.layer.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class World {

    private int seaLevel = 63;

    private ChunkManager level;
    public long seed;
    public final Random rand = new Random();

    private GenLayer genBiomes;
    private GenLayer biomeIndexLayer;
    private BiomeCache biomeCache;
    private List<BiomeGenBase> biomesToSpawnIn;

    private WorldType terrainType = WorldType.DEFAULT;
    private String generatorSettings;

    private String generatorOptions = "";

    private int[] precipitationHeightMap = new int[256];

    public IChunkProvider chunkProvider;

    private final NMBiomeCache mapCache;

    public World(ChunkManager level, long seed){
        this.level = level;
        this.seed = seed;
        this.chunkProvider = new ChunkProviderGenerate(this, seed, true, "");

        this.biomeCache = new BiomeCache(this);
        this.generatorSettings = "";
        this.biomesToSpawnIn = Lists.<BiomeGenBase>newArrayList();
        this.biomesToSpawnIn.add(NMBiomes.town);
        this.biomesToSpawnIn.add(NMBiomes.village);

        makeLayers(getSeed());
        mapCache = new NMBiomeCache(this, 512, true);

        Arrays.fill((int[])this.precipitationHeightMap, (int) - 999);
    }

    private void makeLayers(long seed) {
        GenLayer biomes = new GenLayerNMBiomes(1L);
        biomes = new GenLayerNMKeyBiomes(1000L, biomes);
        biomes = new GenLayerNMCompanionBiomes(1000L, biomes);

        biomes = new GenLayerZoom(1000L, biomes);
        biomes = new GenLayerZoom(1001, biomes);

        biomes = new GenLayerNMBiomeStabilize(700L, biomes);

        //biomes = new GenLayerNMThornBorder(500L, biomes);

        biomes = GenLayerZoom.magnify(1002L, biomes, 4);

        GenLayer riverLayer = new GenLayerNMStream(1L, biomes);
        riverLayer = new GenLayerSmooth(7000L, riverLayer);
        biomes = new GenLayerNMRiverMix(100L, biomes, riverLayer);

        GenLayer genlayervoronoizoom = new GenLayerVoronoiZoom(10L, biomes);

        biomes.initWorldGenSeed(seed);
        genlayervoronoizoom.initWorldGenSeed(seed);

        genBiomes = biomes;
        biomeIndexLayer = genlayervoronoizoom;
    }

    public IChunkProvider getChunkProvider(){
        return this.chunkProvider;
    }

    public BiomeGenBase getBiome(BlockPos pos)
    {
        FullChunk chunk = this.level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        if(chunk == null) return NMBiomes.town;
        int k = chunk.getBiomeId(pos.getX() & 0x0f, pos.getZ() & 0x0f);

        if (k == 255)
        {
            BiomeGenBase biomegenbase = this.getBiomeGenerator(pos, NMBiomes.town);
            k = biomegenbase.biomeID;
            chunk.setBiomeId(pos.getX() & 0x0f, pos.getZ() & 0x0f, k);
        }

        BiomeGenBase biomegenbase1 = BiomeGenBase.getBiome(k);
        return biomegenbase1 == null ? NMBiomes.town : biomegenbase1;
    }

    public List<BiomeGenBase> getBiomesToSpawnIn()
    {
        return this.biomesToSpawnIn;
    }

    public BiomeGenBase getBiomeGenerator(BlockPos pos)
    {
        return this.getBiomeGenerator(pos, (BiomeGenBase)null);
    }

    public BiomeGenBase getBiomeGenerator(BlockPos pos, BiomeGenBase biomeGenBaseIn)
    {
        return this.biomeCache.func_180284_a(pos.getX(), pos.getZ(), biomeGenBaseIn);
    }

    public BiomeGenBase getBiomeGenForCoords(final BlockPos pos)
    {
        if (this.isBlockLoaded(pos))
        {
            FullChunk chunk = this.level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);

            try
            {
                return BiomeGenBase.getBiome(chunk.getBiomeId(pos.getX() & 0x0f, pos.getZ() & 0x0f));
            }
            catch (Throwable throwable)
            {
                System.out.println("Error");
            }
        }
        else
        {
            return this.getBiomeGenerator(pos, NMBiomes.town);
        }
        return null;
    }

    public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int x, int z, int width, int height)
    {
        return getBiomesForGeneration(biomes, x, z, width, height, true);
    }

    public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int x, int z, int width, int height, boolean useCache)
    {
        if (useCache && mapCache.isGridAligned(x, z, width, height)) {
            BiomeGenBase[] cached = mapCache.getBiomes(x, z);
            return Arrays.copyOf(cached, cached.length);
        }
        IntCache.resetIntCache();

        if (biomes == null || biomes.length < width * height)
        {
            biomes = new BiomeGenBase[width * height];
        }

        int[] aint = this.genBiomes.getInts(x, z, width, height);

        try
        {
            for (int i = 0; i < width * height; ++i)
            {
                biomes[i] = BiomeGenBase.getBiomeFromBiomeList(aint[i], NMBiomes.town);
            }

            return biomes;
        }
        catch (Throwable throwable)
        {
            System.out.println("Error");
        }
        return null;
    }

    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] oldBiomeList, int x, int z, int width, int depth)
    {
        return this.getBiomeGenAt(oldBiomeList, x, z, width, depth, true);
    }

    public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] listToReuse, int x, int z, int width, int length, boolean cacheFlag)
    {
        IntCache.resetIntCache();

        if (listToReuse == null || listToReuse.length < width * length)
        {
            listToReuse = new BiomeGenBase[width * length];
        }

        if (cacheFlag && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0)
        {
            BiomeGenBase[] abiomegenbase = this.biomeCache.getCachedBiomes(x, z);
            System.arraycopy(abiomegenbase, 0, listToReuse, 0, width * length);
            return listToReuse;
        }
        else
        {
            int[] aint = this.biomeIndexLayer.getInts(x, z, width, length);

            for (int i = 0; i < width * length; ++i)
            {
                listToReuse[i] = BiomeGenBase.getBiomeFromBiomeList(aint[i], NMBiomes.town);
            }

            return listToReuse;
        }
    }

    public boolean areBiomesViable(int p_76940_1_, int p_76940_2_, int p_76940_3_, List<BiomeGenBase> p_76940_4_)
    {
        IntCache.resetIntCache();
        int i = p_76940_1_ - p_76940_3_ >> 2;
        int j = p_76940_2_ - p_76940_3_ >> 2;
        int k = p_76940_1_ + p_76940_3_ >> 2;
        int l = p_76940_2_ + p_76940_3_ >> 2;
        int i1 = k - i + 1;
        int j1 = l - j + 1;
        int[] aint = this.genBiomes.getInts(i, j, i1, j1);

        try
        {
            for (int k1 = 0; k1 < i1 * j1; ++k1)
            {
                BiomeGenBase biomegenbase = BiomeGenBase.getBiome(aint[k1]);

                if (!p_76940_4_.contains(biomegenbase))
                {
                    return false;
                }
            }

            return true;
        }
        catch (Throwable throwable)
        {
            System.out.println("Error");
        }
        return false;
    }

    public BlockPos findBiomePosition(int x, int z, int range, List<BiomeGenBase> biomes, Random random)
    {
        IntCache.resetIntCache();
        int i = x - range >> 2;
        int j = z - range >> 2;
        int k = x + range >> 2;
        int l = z + range >> 2;
        int i1 = k - i + 1;
        int j1 = l - j + 1;
        int[] aint = this.genBiomes.getInts(i, j, i1, j1);
        BlockPos blockpos = null;
        int k1 = 0;

        for (int l1 = 0; l1 < i1 * j1; ++l1)
        {
            int i2 = i + l1 % i1 << 2;
            int j2 = j + l1 / i1 << 2;
            BiomeGenBase biomegenbase = BiomeGenBase.getBiome(aint[l1]);

            if (biomes.contains(biomegenbase) && (blockpos == null || random.nextInt(k1 + 1) == 0))
            {
                blockpos = new BlockPos(i2, 0, j2);
                ++k1;
            }
        }

        return blockpos;
    }

    public void cleanupCache()
    {
        this.biomeCache.cleanupCache();
    }

    public boolean isBlockLoaded(BlockPos pos){
        return this.level.getChunk(pos.getX() >> 4, pos.getZ() >> 4) != null;
    }

    public boolean setBlockState(BlockPos pos, Block newState, int flags)
    {
        FullChunk chunk = this.level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        if(chunk == null) return false;
        return this.level.getChunk(pos.getX() >> 4, pos.getZ() >> 4).setBlock(pos.getX() & 0x0f, pos.getY() & 0xff, pos.getZ() & 0x0f, newState.getId(), newState.getDamage());
    }

    public boolean setBlockToAir(BlockPos pos)
    {
        return this.setBlockState(pos, Block.get(Block.AIR), 3);
    }

    public boolean isAirBlock(BlockPos pos){
        return this.getBlockState(pos).getId() == Block.AIR;
    }

    public Block getBlockState(BlockPos pos)
    {
        FullChunk chunk = this.level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        if(chunk == null) System.out.println(this.level.getBlockIdAt(pos.getX(), pos.getY(), pos.getZ()));
        if(chunk ==  null || pos.getY() < 0 || pos.getY() > 255) return Block.get(Block.AIR);
        return Block.get(
                chunk.getBlockId(pos.getX() & 0xf, pos.getY() & 0xff, pos.getZ() & 0xf),
                chunk.getBlockData(pos.getX() & 0xf, pos.getY() & 0xff, pos.getZ() & 0xf));
    }

    public float[] getRainfall(float[] listToReuse, int x, int z, int width, int length)
    {
        IntCache.resetIntCache();

        if (listToReuse == null || listToReuse.length < width * length)
        {
            listToReuse = new float[width * length];
        }

        int[] aint = this.biomeIndexLayer.getInts(x, z, width, length);

        for (int i = 0; i < width * length; ++i)
        {
            try
            {
                float f = (float)BiomeGenBase.getBiomeFromBiomeList(aint[i], NMBiomes.town).getIntRainfall() / 65536.0F;

                if (f > 1.0F)
                {
                    f = 1.0F;
                }

                listToReuse[i] = f;
            }
            catch (Throwable throwable)
            {
                System.out.println("Error");
            }
        }

        return listToReuse;
    }

    public BlockPos getTopSolidOrLiquidBlock(BlockPos pos)
    {
        FullChunk chunk = this.level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        BlockPos blockpos;
        BlockPos blockpos1;

        if(chunk == null) return pos;
        int top = 0;
        for(int x = 0;x < 16;x++){
            for(int z = 0;z < 16;z++){
                if(chunk.getHighestBlockAt(x, z) >= top) top = chunk.getHighestBlockAt(x, z);
            }
        }

        for (blockpos = new BlockPos(pos.getX(), top + 16, pos.getZ()); blockpos.getY() >= 0; blockpos = blockpos1)
        {
            blockpos1 = blockpos.down();

            if(blockpos1.getY() == 0) return blockpos1;
            if (BlockHelper.isLeave(chunk.getBlockId(blockpos1.getX() & 0xf, blockpos1.getY(), blockpos1.getZ() & 0xf)))
            {
                break;
            }
        }

        return blockpos;
    }

    public int getAverageGroundLevel()
    {
        return this.terrainType == WorldType.FLAT ? 4 : this.getSeaLevel() + 1;
    }

    public WorldType getTerrainType()
    {
        return this.terrainType;
    }

    public void setTerrainType(WorldType type)
    {
        this.terrainType = type;
    }

    public String getGeneratorOptions()
    {
        return this.generatorOptions;
    }

    public int getSeaLevel()
    {
        return this.seaLevel;
    }

    public void setSeaLevel(int level)
    {
        this.seaLevel = level;
    }

    public long getSeed()
    {
        return this.seed;
    }

    public boolean canBlockFreezeWater(BlockPos pos)
    {
        return this.canBlockFreeze(pos, false);
    }

    public boolean canBlockFreezeNoWater(BlockPos pos)
    {
        return this.canBlockFreeze(pos, true);
    }

    public boolean canBlockFreeze(BlockPos pos, boolean noWaterAdj)
    {
        BiomeGenBase biomegenbase = this.getBiomeGenForCoords(pos);
        float f = biomegenbase.getFloatTemperature(pos);

        if (f > 0.15F)
        {
            return false;
        }
        else
        {
            FullChunk chunk = this.level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);

            //if (pos.getY() >= 0 && pos.getY() < 256 && chunk.getBlockSkyLight(pos.getX() & 0x0f, pos.getY() & 0xff, pos.getZ() & 0x0f) < 10)
            //{
                Block iblockstate = this.getBlockState(pos);
                Block block = iblockstate;

                if ((block.getId() == Block.WATER || block.getId() == Block.STILL_WATER) /*&& iblockstate.getDamage() == 8*/)
                {
                    if (!noWaterAdj)
                    {
                        return true;
                    }

                    boolean flag = this.isWater(pos.west()) && this.isWater(pos.east()) && this.isWater(pos.north()) && this.isWater(pos.south());

                    if (!flag)
                    {
                        return true;
                    }
                }
            //}

            return false;
        }
    }

    private boolean isWater(BlockPos pos)
    {
        int id = this.getBlockState(pos).getId();
        return id == Block.WATER || id == Block.STILL_WATER;
    }

    public boolean canSnowAt(BlockPos pos, boolean checkLight)
    {
        BiomeGenBase biomegenbase = this.getBiomeGenForCoords(pos);
        float f = biomegenbase.getFloatTemperature(pos);

        if (f > 0.15F)
        {
            return false;
        }
        else if (!checkLight)
        {
            return true;
        }
        else
        {
            FullChunk chunk = this.level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
            if(chunk == null) return false;

            //if (pos.getY() >= 0 && pos.getY() < 255 && chunk.getBlockSkyLight(pos.getX() & 0x0f, pos.getY() & 0xff, pos.getZ() & 0x0f) < 10)
            //{
                Block block = this.getBlockState(pos);

                if (block.getId() == Block.AIR && BlockHelper.canBePlacedSnowLayer(this, pos))
                {
                    return true;
                }
            //}

            return false;
        }
    }

    public int getLightFor(BlockPos pos){
        FullChunk chunk = this.level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        return chunk.getBlockSkyLight(pos.getX() & 0x0f, pos.getY() & 0x0f, pos.getZ() & 0x0f);
    }

    public BlockPos getPrecipitationHeight(BlockPos pos)
    {
        int y;
        for (y = 254; y >= 0; --y) {
            if (!PopulatorHelpers.isNonSolid(this.level.getBlockIdAt(pos.getX(), y, pos.getZ()))) {
                break;
            }
        }

        return new BlockPos(pos.getX(), y == 0 ? -1 : ++y, pos.getZ());
    }

    public BlockPos getHeight(BlockPos pos)
    {
        int i;

        if (pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000)
        {
            FullChunk chunk = this.level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
            if (chunk != null)
            {
                i = chunk.getHighestBlockAt(pos.getX() & 15, pos.getZ() & 15);
            }
            else
            {
                i = 0;
            }
        }
        else
        {
            i = this.getSeaLevel() + 1;
        }

        return new BlockPos(pos.getX(), i, pos.getZ());
    }

    public boolean getHasNoSky()
    {
        return false;
    }

    public Random setRandomSeed(int p_72843_1_, int p_72843_2_, int p_72843_3_)
    {
        long i = (long)p_72843_1_ * 341873128712L + (long)p_72843_2_ * 132897987541L + this.getSeed() + (long)p_72843_3_;
        this.rand.setSeed(i);
        return this.rand;
    }
}
