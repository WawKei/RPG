package nightmare.world.biome;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlower;
import cn.nukkit.block.BlockTallGrass;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.mob.*;
import cn.nukkit.entity.passive.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import nightmare.util.BlockPos;
import nightmare.util.MathHelper;
import nightmare.util.WeightedRandom;
import nightmare.world.World;
import nightmare.world.chunk.ChunkPrimer;
import nightmare.world.gen.NoiseGeneratorPerlin;
import nightmare.world.gen.feature.WorldGenAbstractTree;
import nightmare.world.gen.feature.WorldGenBigTree;
import nightmare.world.gen.feature.WorldGenDoublePlant;
import nightmare.world.gen.feature.WorldGenSwamp;
import nightmare.world.gen.feature.WorldGenTallGrass;
import nightmare.world.gen.feature.WorldGenTrees;
import nightmare.world.gen.feature.WorldGenerator;

public abstract class BiomeGenBase
{
    protected static final Height height_Default = new Height(0.1F, 0.2F);
    private static final BiomeGenBase[] biomeList = new BiomeGenBase[256];
    public static final Set<BiomeGenBase> explorationBiomesList = Sets.<BiomeGenBase>newHashSet();
    public static final Map<String, BiomeGenBase> BIOME_ID_MAP = Maps.<String, BiomeGenBase>newHashMap();
    protected static final NoiseGeneratorPerlin temperatureNoise;
    protected static final NoiseGeneratorPerlin GRASS_COLOR_NOISE;
    protected static final WorldGenDoublePlant DOUBLE_PLANT_GENERATOR;
    public String biomeName;
    public int color;
    public int field_150609_ah;
    public Block topBlock = Block.get(Block.GRASS);
    public Block fillerBlock = Block.get(Block.DIRT);
    public int fillerBlockMetadata = 5169201;
    public float minHeight;
    public float maxHeight;
    public float temperature;
    public float rainfall;
    public int waterColorMultiplier;
    public BiomeDecorator theBiomeDecorator;
    protected List<SpawnListEntry> spawnableMonsterList;
    protected List<SpawnListEntry> spawnableCreatureList;
    protected List<SpawnListEntry> spawnableWaterCreatureList;
    protected List<SpawnListEntry> spawnableCaveCreatureList;
    protected boolean enableSnow;
    protected boolean enableRain;
    public final int biomeID;
    protected WorldGenTrees worldGeneratorTrees;
    protected WorldGenBigTree worldGeneratorBigTree;
    protected WorldGenSwamp worldGeneratorSwamp;

    public BiomeGenBase(int id)
    {
        this.minHeight = height_Default.rootHeight;
        this.maxHeight = height_Default.variation;
        this.temperature = 0.5F;
        this.rainfall = 0.5F;
        this.waterColorMultiplier = 16777215;
        this.spawnableMonsterList = Lists.<SpawnListEntry>newArrayList();
        this.spawnableCreatureList = Lists.<SpawnListEntry>newArrayList();
        this.spawnableWaterCreatureList = Lists.<SpawnListEntry>newArrayList();
        this.spawnableCaveCreatureList = Lists.<SpawnListEntry>newArrayList();
        this.enableRain = true;
        this.worldGeneratorTrees = new WorldGenTrees(false);
        this.worldGeneratorBigTree = new WorldGenBigTree(false);
        this.worldGeneratorSwamp = new WorldGenSwamp();
        this.biomeID = id;
        biomeList[id] = this;
        this.theBiomeDecorator = this.createBiomeDecorator();
        this.spawnableCreatureList.add(new SpawnListEntry(EntitySheep.class, 12, 4, 4));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityRabbit.class, 10, 3, 3));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityPig.class, 10, 4, 4));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 10, 4, 4));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityCow.class, 8, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySpider.class, 100, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityZombie.class, 100, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 100, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 100, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 100, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 10, 1, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityWitch.class, 5, 1, 1));
        this.spawnableWaterCreatureList.add(new SpawnListEntry(EntitySquid.class, 10, 4, 4));
        this.spawnableCaveCreatureList.add(new SpawnListEntry(EntityBat.class, 10, 8, 8));
    }

    public BiomeDecorator createBiomeDecorator()
    {
        return new BiomeDecorator();
    }

    public BiomeGenBase setTemperatureRainfall(float temperatureIn, float rainfallIn)
    {
        if (temperatureIn > 0.1F && temperatureIn < 0.2F)
        {
            throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
        }
        else
        {
            this.temperature = temperatureIn;
            this.rainfall = rainfallIn;
            return this;
        }
    }

    public final BiomeGenBase setHeight(Height heights)
    {
        this.minHeight = heights.rootHeight;
        this.maxHeight = heights.variation;
        return this;
    }

    public BiomeGenBase setDisableRain()
    {
        this.enableRain = false;
        return this;
    }

    public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        return (WorldGenAbstractTree)(rand.nextInt(10) == 0 ? this.worldGeneratorBigTree : this.worldGeneratorTrees);
    }

    public WorldGenerator getRandomWorldGenForGrass(Random rand)
    {
        return new WorldGenTallGrass(Block.get(Block.TALL_GRASS));
    }

    public Block pickRandomFlower(Random rand, BlockPos pos)
    {
        return rand.nextInt(3) > 0 ? Block.get(BlockFlower.DANDELION) : Block.get(BlockFlower.POPPY);
    }

    public BiomeGenBase setEnableSnow()
    {
        this.enableSnow = true;
        return this;
    }

    public BiomeGenBase setBiomeName(String name)
    {
        this.biomeName = name;
        return this;
    }

    public BiomeGenBase setFillerBlockMetadata(int meta)
    {
        this.fillerBlockMetadata = meta;
        return this;
    }

    public BiomeGenBase setColor(int colorIn)
    {
        this.func_150557_a(colorIn, false);
        return this;
    }

    public BiomeGenBase func_150563_c(int p_150563_1_)
    {
        this.field_150609_ah = p_150563_1_;
        return this;
    }

    public BiomeGenBase func_150557_a(int p_150557_1_, boolean p_150557_2_)
    {
        this.color = p_150557_1_;

        if (p_150557_2_)
        {
            this.field_150609_ah = (p_150557_1_ & 16711422) >> 1;
        }
        else
        {
            this.field_150609_ah = p_150557_1_;
        }

        return this;
    }

    /*public List<SpawnListEntry> getSpawnableList(EnumCreatureType creatureType)
    {
        switch (creatureType)
        {
            case MONSTER:
                return this.spawnableMonsterList;
            case CREATURE:
                return this.spawnableCreatureList;
            case WATER_CREATURE:
                return this.spawnableWaterCreatureList;
            case AMBIENT:
                return this.spawnableCaveCreatureList;
            default:
                return Collections.<SpawnListEntry>emptyList();
        }
    }*/

    public boolean getEnableSnow()
    {
        return this.isSnowyBiome();
    }

    public boolean canSpawnLightningBolt()
    {
        return this.isSnowyBiome() ? false : this.enableRain;
    }

    public boolean isHighHumidity()
    {
        return this.rainfall > 0.85F;
    }

    public float getSpawningChance()
    {
        return 0.1F;
    }

    public final int getIntRainfall()
    {
        return (int)(this.rainfall * 65536.0F);
    }

    public final float getFloatTemperature(BlockPos pos)
    {
        if (pos.getY() > 64)
        {
            float f = (float)(temperatureNoise.func_151601_a((double)pos.getX() * 1.0D / 8.0D, (double)pos.getZ() * 1.0D / 8.0D) * 4.0D);
            return this.temperature - (f + (float)pos.getY() - 64.0F) * 0.05F / 30.0F;
        }
        else
        {
            return this.temperature;
        }
    }

    public void decorate(World worldIn, Random rand, BlockPos pos)
    {
        this.theBiomeDecorator.decorate(worldIn, rand, this, pos);
    }

    public boolean isSnowyBiome()
    {
        return this.enableSnow;
    }

    public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int p_180622_4_, int p_180622_5_, double p_180622_6_)
    {
        this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, p_180622_4_, p_180622_5_, p_180622_6_);
    }

    public final void generateBiomeTerrain(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int p_180628_4_, int p_180628_5_, double p_180628_6_)
    {
        int i = worldIn.getSeaLevel();
        Block iblockstate = this.topBlock;
        Block iblockstate1 = this.fillerBlock;
        int j = -1;
        int k = (int)(p_180628_6_ / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
        int l = p_180628_4_ & 15;
        int i1 = p_180628_5_ & 15;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int j1 = 255; j1 >= 0; --j1)
        {
            if (j1 <= rand.nextInt(5))
            {
                chunkPrimerIn.setBlockState(i1, j1, l, Block.get(Block.BEDROCK));
            }
            else
            {
                Block iblockstate2 = chunkPrimerIn.getBlockState(i1, j1, l);

                if (iblockstate2.getId() == Block.AIR)
                {
                    j = -1;
                }
                else if (iblockstate2.getId() == Block.STONE)
                {
                    if (j == -1)
                    {
                        if (k <= 0)
                        {
                            iblockstate = null;
                            iblockstate1 = Block.get(Block.STONE);
                        }
                        else if (j1 >= i - 4 && j1 <= i + 1)
                        {
                            iblockstate = this.topBlock;
                            iblockstate1 = this.fillerBlock;
                        }

                        if (j1 < i && (iblockstate == null || iblockstate.getId() == Block.AIR))
                        {
                            if (this.getFloatTemperature(blockpos$mutableblockpos.set(p_180628_4_, j1, p_180628_5_)) < 0.15F)
                            {
                                iblockstate = Block.get(Block.ICE);
                            }
                            else
                            {
                                iblockstate = Block.get(Block.WATER);
                            }
                        }

                        j = k;

                        if (j1 >= i - 1)
                        {
                            if(iblockstate != null) chunkPrimerIn.setBlockState(i1, j1, l, iblockstate);
                            else System.out.println("EHHHHHHHHHHHHHHHHHHHHHHHHHHHI");
                        }
                        else if (j1 < i - 7 - k)
                        {
                            iblockstate = null;
                            iblockstate1 = Block.get(Block.STONE);
                            chunkPrimerIn.setBlockState(i1, j1, l, Block.get(Block.GRAVEL));
                        }
                        else
                        {
                            chunkPrimerIn.setBlockState(i1, j1, l, iblockstate1);
                        }
                    }
                    else if (j > 0)
                    {
                        --j;
                        chunkPrimerIn.setBlockState(i1, j1, l, iblockstate1);

                        if (j == 0 && iblockstate1.getId() == Block.SAND && k > 1)
                        {
                            j = rand.nextInt(4) + Math.max(0, j1 - 63);
                            iblockstate1 = iblockstate1.getDamage() == 0x01 ? Block.get(Block.RED_SANDSTONE) : Block.get(Block.SANDSTONE);
                        }
                    }
                }
            }
        }
    }

    /*public BiomeGenBase createMutation()
    {
        return this.createMutatedBiome(this.biomeID + 128);
    }

    public BiomeGenBase createMutatedBiome(int p_180277_1_)
    {
        return new BiomeGenMutated(p_180277_1_, this);
    }*/

    public Class <? extends BiomeGenBase > getBiomeClass()
    {
        return this.getClass();
    }

    public boolean isEqualTo(BiomeGenBase biome)
    {
        return biome == this ? true : (biome == null ? false : this.getBiomeClass() == biome.getBiomeClass());
    }

    public TempCategory getTempCategory()
    {
        return (double)this.temperature < 0.2D ? TempCategory.COLD : ((double)this.temperature < 1.0D ? TempCategory.MEDIUM : TempCategory.WARM);
    }

    public static BiomeGenBase[] getBiomeGenArray()
    {
        return biomeList;
    }

    public static BiomeGenBase getBiome(int id)
    {
        return getBiomeFromBiomeList(id, (BiomeGenBase)null);
    }

    public static BiomeGenBase getBiomeFromBiomeList(int biomeId, BiomeGenBase biome)
    {
        if (biomeId >= 0 && biomeId <= biomeList.length)
        {
            BiomeGenBase biomegenbase = biomeList[biomeId];
            return biomegenbase == null ? biome : biomegenbase;
        }
        else
        {
            return NMBiomes.town;
        }
    }

    static
    {

        for (BiomeGenBase biomegenbase : biomeList)
        {
            if (biomegenbase != null)
            {
                if (BIOME_ID_MAP.containsKey(biomegenbase.biomeName))
                {
                    throw new Error("Biome \"" + biomegenbase.biomeName + "\" is defined as both ID " + ((BiomeGenBase)BIOME_ID_MAP.get(biomegenbase.biomeName)).biomeID + " and " + biomegenbase.biomeID);
                }

                BIOME_ID_MAP.put(biomegenbase.biomeName, biomegenbase);
            }
        }
        temperatureNoise = new NoiseGeneratorPerlin(new Random(1234L), 1);
        GRASS_COLOR_NOISE = new NoiseGeneratorPerlin(new Random(2345L), 1);
        DOUBLE_PLANT_GENERATOR = new WorldGenDoublePlant();
    }

    public static class Height
        {
            public float rootHeight;
            public float variation;

            public Height(float rootHeightIn, float variationIn)
            {
                this.rootHeight = rootHeightIn;
                this.variation = variationIn;
            }

            public Height attenuate()
            {
                return new Height(this.rootHeight * 0.8F, this.variation * 0.6F);
            }
        }

    public static class SpawnListEntry extends WeightedRandom.Item
        {
            public Class <? extends Entity> entityClass;
            public int minGroupCount;
            public int maxGroupCount;

            public SpawnListEntry(Class <? extends Entity > entityclassIn, int weight, int groupCountMin, int groupCountMax)
            {
                super(weight);
                this.entityClass = entityclassIn;
                this.minGroupCount = groupCountMin;
                this.maxGroupCount = groupCountMax;
            }

            public String toString()
            {
                return this.entityClass.getSimpleName() + "*(" + this.minGroupCount + "-" + this.maxGroupCount + "):" + this.itemWeight;
            }
        }

    public static enum TempCategory
    {
        OCEAN,
        COLD,
        MEDIUM,
        WARM;
    }
}