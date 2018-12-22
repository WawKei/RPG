package nightmare.world.gen;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import nightmare.util.BlockPos;
import nightmare.util.ChunkPos;
import nightmare.util.MathHelper;
import nightmare.world.ChunkCoordIntPair;
import nightmare.world.World;
import nightmare.world.WorldType;
import nightmare.world.biome.BiomeGenBase;
import nightmare.world.biome.NMBiomes;
import nightmare.world.chunk.ChunkPrimer;
import nightmare.world.chunk.IChunkProvider;
import nightmare.world.gen.feature.WorldGenDungeons;
import nightmare.world.gen.feature.WorldGenLakes;
import nightmare.world.gen.structure.NMFeature;

public class ChunkProviderGenerate implements IChunkProvider
{
    private Random rand;

    private final NoiseGeneratorOctaves minLimitPerlinNoise;
    private final NoiseGeneratorOctaves maxLimitPerlinNoise;
    private final NoiseGeneratorOctaves mainPerlinNoise;
    private final NoiseGeneratorPerlin surfaceNoise;
    private final NoiseGeneratorOctaves depthNoise;

    private World worldObj;
    private final boolean mapFeaturesEnabled;
    private WorldType terrainType;

    private final double[] heightMap;
    private final float[] biomeWeights;

    private ChunkProviderSettings settings;
    private Block field_177476_s = Block.get(Block.WATER);
    private MapGenBase caveGenerator = new MapGenCaves();
    private MapGenBase ravineGenerator = new MapGenRavine();
    protected double[] depthBuffer = new double[256];
    private BiomeGenBase[] biomesForGeneration;

    private double[] mainNoiseRegion;
    private double[] minLimitRegion;
    private double[] maxLimitRegion;
    private double[] depthRegion;

    public ChunkProviderGenerate(World worldIn, long p_i45636_2_, boolean p_i45636_4_, String p_i45636_5_)
    {
        this.worldObj = worldIn;
        this.mapFeaturesEnabled = p_i45636_4_;
        this.terrainType = worldIn.getTerrainType();
        this.rand = new Random(p_i45636_2_);
        this.minLimitPerlinNoise = new NoiseGeneratorOctaves(this.rand, 16);
        this.maxLimitPerlinNoise = new NoiseGeneratorOctaves(this.rand, 16);
        this.mainPerlinNoise = new NoiseGeneratorOctaves(this.rand, 8);
        this.surfaceNoise = new NoiseGeneratorPerlin(this.rand, 4);
        this.depthNoise = new NoiseGeneratorOctaves(rand, 16);

        this.heightMap = new double[825];
        this.biomeWeights = new float[25];


        for (int j = -2; j <= 2; ++j) {
            for (int k = -2; k <= 2; ++k) {
                float f = 10.0F / MathHelper.sqrt_float((float) (j * j + k * k) + 0.2F);
                this.biomeWeights[j + 2 + (k + 2) * 5] = f;
            }
        }

        if (p_i45636_5_ != null)
        {
            this.settings = ChunkProviderSettings.Factory.jsonToFactory(p_i45636_5_).func_177864_b();
            this.field_177476_s = this.settings.useLavaOceans ? Block.get(Block.LAVA) : Block.get(Block.WATER);
            worldIn.setSeaLevel(this.settings.seaLevel);
        }
    }

    protected final void setBlocksInChunk(int x, int z, ChunkPrimer data) {
        this.biomesForGeneration = this.worldObj.getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);
        this.generateHeightmap(x * 4, 0, z * 4);

        for (int k = 0; k < 4; ++k) {
            int l = k * 5;
            int i1 = (k + 1) * 5;

            for (int j1 = 0; j1 < 4; ++j1) {
                int k1 = (l + j1) * 33;
                int l1 = (l + j1 + 1) * 33;
                int i2 = (i1 + j1) * 33;
                int j2 = (i1 + j1 + 1) * 33;

                for (int k2 = 0; k2 < 32; ++k2) {
                    double d0 = 0.125D;
                    double d1 = this.heightMap[k1 + k2];
                    double d2 = this.heightMap[l1 + k2];
                    double d3 = this.heightMap[i2 + k2];
                    double d4 = this.heightMap[j2 + k2];
                    double d5 = (this.heightMap[k1 + k2 + 1] - d1) * d0;
                    double d6 = (this.heightMap[l1 + k2 + 1] - d2) * d0;
                    double d7 = (this.heightMap[i2 + k2 + 1] - d3) * d0;
                    double d8 = (this.heightMap[j2 + k2 + 1] - d4) * d0;

                    for (int l2 = 0; l2 < 8; ++l2) {
                        double d9 = 0.25D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;

                        for (int i3 = 0; i3 < 4; ++i3) {
                            double d14 = 0.25D;
                            double d16 = (d11 - d10) * d14;
                            double d15 = d10 - d16;

                            for (int k3 = 0; k3 < 4; ++k3) {
                                if ((d15 += d16) > 0.0D) {
                                    // stone here
                                    data.setBlockState(k * 4 + i3, k2 * 8 + l2, j1 * 4 + k3, Block.get(Block.STONE));
                                } /* else if (k2 * 8 + l2 < seaLevel) */ {
                                    // water below sea level left until later
                                }
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }

    public void replaceBiomeBlocks(int x, int z, ChunkPrimer primer, BiomeGenBase[] biomesIn) {
        double d0 = 0.03125D;
        this.depthBuffer = this.surfaceNoise.getRegion(this.depthBuffer, (double)(x * 16), (double)(z * 16), 16, 16, 0.0625D, 0.0625D, 1.0D);

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                BiomeGenBase biome = biomesIn[j + i * 16];
                biome.genTerrainBlocks(this.worldObj, this.rand, primer, x * 16 + i, z * 16 + j, this.depthBuffer[j + i * 16]);
            }
        }
    }

    public void provideChunk(int x, int z, FullChunk fullChunk)
    {
        this.rand.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
        ChunkPrimer chunkprimer = new ChunkPrimer(fullChunk);
        this.setBlocksInChunk(x, z, chunkprimer);
        this.biomesForGeneration = this.worldObj.loadBlockGeneratorData(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        this.replaceBiomeBlocks(x, z, chunkprimer, this.biomesForGeneration);

        if (this.settings.useCaves)
        {
            this.caveGenerator.generate(this, this.worldObj, x, z, chunkprimer);
        }

        if (this.settings.useRavines)
        {
            this.ravineGenerator.generate(this, this.worldObj, x, z, chunkprimer);
        }

        generateFeatures(x, z, chunkprimer);

        for (int i = 0; i < 16; ++i)
        {
            for (int j = 0; j < 16; ++j)
            {
                BiomeGenBase biomegenbase = this.biomesForGeneration[j + i * 16];
                fullChunk.setBiomeId(j, i, biomegenbase.biomeID);
            }
        }
    }

    protected final void generateFeatures(int x, int z, ChunkPrimer primer) {
        for (NMFeature feature : NMFeature.values()) {
            if (feature != NMFeature.NOTHING) {
                feature.getFeatureGenerator().generate(this, this.worldObj, x, z, primer);
            }
        }
    }

    private void generateHeightmap(int x, int zero, int z) {

        this.depthRegion = this.depthNoise.generateNoiseOctaves(this.depthRegion, x, z, 5, 5, 200.0D, 200.0D, 0.5D);
        this.mainNoiseRegion = this.mainPerlinNoise.generateNoiseOctaves(this.mainNoiseRegion, x, zero, z, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
        this.minLimitRegion = this.minLimitPerlinNoise.generateNoiseOctaves(this.minLimitRegion, x, zero, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        this.maxLimitRegion = this.maxLimitPerlinNoise.generateNoiseOctaves(this.maxLimitRegion, x, zero, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        int terrainIndex = 0;
        int noiseIndex = 0;

        for (int ax = 0; ax < 5; ++ax) {
            for (int az = 0; az < 5; ++az) {
                float totalVariation = 0.0F;
                float totalHeight = 0.0F;
                float totalFactor = 0.0F;
                byte two = 2;
                BiomeGenBase biome = this.biomesForGeneration[ax + 2 + (az + 2) * 10];

                for (int ox = -two; ox <= two; ++ox) {
                    for (int oz = -two; oz <= two; ++oz) {
                        BiomeGenBase biome1 = this.biomesForGeneration[ax + ox + 2 + (az + oz + 2) * 10];
                        float rootHeight = this.settings.biomeDepthOffSet + biome1.minHeight * this.settings.biomeDepthWeight;
                        float heightVariation = this.settings.biomeScaleOffset + biome1.maxHeight * this.settings.biomeScaleWeight;

                        /*if (this.terrainType == WorldType.AMPLIFIED && rootHeight > 0.0F) {
                            rootHeight = 1.0F + rootHeight * 2.0F;
                            heightVariation = 1.0F + heightVariation * 4.0F;
                        }*/

                        float heightFactor = this.biomeWeights[ox + 2 + (oz + 2) * 5] / (rootHeight + 2.0F);

                        if (biome1.minHeight > biome.minHeight) {
                            heightFactor /= 2.0F;
                        }

                        totalVariation += heightVariation * heightFactor;
                        totalHeight += rootHeight * heightFactor;
                        totalFactor += heightFactor;
                    }
                }

                totalVariation /= totalFactor;
                totalHeight /= totalFactor;
                totalVariation = totalVariation * 0.9F + 0.1F;
                totalHeight = (totalHeight * 4.0F - 1.0F) / 8.0F;
                double terrainNoise = this.depthRegion[noiseIndex] / 8000.0D;

                if (terrainNoise < 0.0D) {
                    terrainNoise = -terrainNoise * 0.3D;
                }

                terrainNoise = terrainNoise * 3.0D - 2.0D;

                if (terrainNoise < 0.0D) {
                    terrainNoise /= 2.0D;

                    if (terrainNoise < -1.0D) {
                        terrainNoise = -1.0D;
                    }

                    terrainNoise /= 1.4D;
                    terrainNoise /= 2.0D;
                } else {
                    if (terrainNoise > 1.0D) {
                        terrainNoise = 1.0D;
                    }

                    terrainNoise /= 8.0D;
                }

                ++noiseIndex;
                double heightCalc = (double) totalHeight;
                double variationCalc = (double) totalVariation;
                heightCalc += terrainNoise * 0.2D;
                heightCalc = heightCalc * 8.5D / 8.0D;
                double d5 = 8.5D + heightCalc * 4.0D;

                for (int ay = 0; ay < 33; ++ay) {
                    double d6 = ((double) ay - d5) * 12.0D * 128.0D / 256.0D / variationCalc;

                    if (d6 < 0.0D) {
                        d6 *= 4.0D;
                    }

                    double d7 = this.minLimitRegion[terrainIndex] / 512.0D;
                    double d8 = this.maxLimitRegion[terrainIndex] / 512.0D;
                    double d9 = (this.mainNoiseRegion[terrainIndex] / 10.0D + 1.0D) / 2.0D;
                    double terrainCalc = MathHelper.clampedLerp(d7, d8, d9) - d6;

                    if (ay > 29) {
                        double d11 = (double) ((float) (ay - 29) / 3.0F);
                        terrainCalc = terrainCalc * (1.0D - d11) + -10.0D * d11;
                    }

                    this.heightMap[terrainIndex] = terrainCalc;
                    ++terrainIndex;
                }
            }
        }
    }

    public boolean chunkExists(int x, int z)
    {
        return true;
    }

    public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_)
    {
        int i = p_73153_2_ * 16;
        int j = p_73153_3_ * 16;
        BlockPos blockpos = new BlockPos(i, 0, j);
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(blockpos.add(16, 0, 16));
        this.rand.setSeed(this.worldObj.getSeed());
        long k = this.rand.nextLong() / 2L * 2L + 1L;
        long l = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long)p_73153_2_ * k + (long)p_73153_3_ * l ^ this.worldObj.getSeed());
        boolean flag = false;
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(p_73153_2_, p_73153_3_);

        boolean disableFeatures = false;

        //for (NMFeature feature : NMFeature.values()) {
         //   if (feature != NMFeature.NOTHING && feature.getFeatureGenerator().generateStructure(this.worldObj, rand, chunkcoordintpair)) {
         //       disableFeatures = true;
         //   }
        //}

        disableFeatures = disableFeatures || !NMFeature.getNearestFeature(p_73153_2_, p_73153_3_, this.worldObj).areChunkDecorationsEnabled;

        if (!disableFeatures && this.settings.useWaterLakes && !flag && this.rand.nextInt(this.settings.waterLakeChance) == 0)
        {
            int i1 = this.rand.nextInt(16) + 8;
            int j1 = this.rand.nextInt(256);
            int k1 = this.rand.nextInt(16) + 8;
            (new WorldGenLakes(Block.get(Block.WATER))).generate(this.worldObj, this.rand, blockpos.add(i1, j1, k1));
        }

        if (!disableFeatures && !flag && this.rand.nextInt(this.settings.lavaLakeChance / 10) == 0 && this.settings.useLavaLakes)
        {
            int i2 = this.rand.nextInt(16) + 8;
            int l2 = this.rand.nextInt(this.rand.nextInt(248) + 8);
            int k3 = this.rand.nextInt(16) + 8;

            if (l2 < this.worldObj.getSeaLevel() || this.rand.nextInt(this.settings.lavaLakeChance / 8) == 0)
            {
                (new WorldGenLakes(Block.get(Block.LAVA))).generate(this.worldObj, this.rand, blockpos.add(i2, l2, k3));
            }
        }

        if (this.settings.useDungeons)
        {
            for (int j2 = 0; j2 < this.settings.dungeonChance; ++j2)
            {
                int i3 = this.rand.nextInt(16) + 8;
                int l3 = this.rand.nextInt(256);
                int l1 = this.rand.nextInt(16) + 8;
                (new WorldGenDungeons()).generate(this.worldObj, this.rand, blockpos.add(i3, l3, l1));
            }
        }

        biomegenbase.decorate(this.worldObj, this.rand, new BlockPos(i, 0, j));
        //spawn(this.worldObj, biomegenbase, i + 8, j + 8, 16, 16, this.rand);
        blockpos = blockpos.add(8, 0, 8);

        for (int k2 = 0; k2 < 16; ++k2)
        {
            for (int j3 = 0; j3 < 16; ++j3)
            {
                BlockPos blockpos1 = this.worldObj.getPrecipitationHeight(blockpos.add(k2, 0, j3));
                BlockPos blockpos2 = blockpos1.down();

                if (this.worldObj.canBlockFreezeWater(blockpos2))
                {
                    this.worldObj.setBlockState(blockpos2, Block.get(Block.ICE), 2);
                }

                if (this.worldObj.canSnowAt(blockpos1, true))
                {
                    this.worldObj.setBlockState(blockpos1, Block.get(Block.SNOW_LAYER), 2);
                }
            }
        }
    }

    /*public boolean func_177460_a(IChunkProvider p_177460_1_, Chunk p_177460_2_, int p_177460_3_, int p_177460_4_)
    {
        boolean flag = false;

        if (this.settings.useMonuments && this.mapFeaturesEnabled && p_177460_2_.getInhabitedTime() < 3600L)
        {
            flag |= this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, new ChunkCoordIntPair(p_177460_3_, p_177460_4_));
        }

        return flag;
    }*/

    public void saveExtraData()
    {
    }

    public boolean unloadQueuedChunks()
    {
        return false;
    }

    public boolean canSave()
    {
        return true;
    }

    public String makeString()
    {
        return "RandomLevelSource";
    }

    /*public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(pos);

        if (this.mapFeaturesEnabled)
        {
            if (creatureType == EnumCreatureType.MONSTER && this.scatteredFeatureGenerator.func_175798_a(pos))
            {
                return this.scatteredFeatureGenerator.getScatteredFeatureSpawnList();
            }

            if (creatureType == EnumCreatureType.MONSTER && this.settings.useMonuments && this.oceanMonumentGenerator.func_175796_a(this.worldObj, pos))
            {
                return this.oceanMonumentGenerator.func_175799_b();
            }
        }

        return biomegenbase.getSpawnableList(creatureType);
    }*/

    /*public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position)
    {
        return "Stronghold".equals(structureName) && this.strongholdGenerator != null ? this.strongholdGenerator.getClosestStrongholdPos(worldIn, position) : null;
    }*/

    public int getLoadedChunkCount()
    {
        return 0;
    }

    /*public void recreateStructures(Chunk p_180514_1_, int p_180514_2_, int p_180514_3_)
    {
        if (this.settings.useMineShafts && this.mapFeaturesEnabled)
        {
            this.mineshaftGenerator.generate(this, this.worldObj, p_180514_2_, p_180514_3_, (ChunkPrimer)null);
        }

        if (this.settings.useVillages && this.mapFeaturesEnabled)
        {
            this.villageGenerator.generate(this, this.worldObj, p_180514_2_, p_180514_3_, (ChunkPrimer)null);
        }

        if (this.settings.useStrongholds && this.mapFeaturesEnabled)
        {
            this.strongholdGenerator.generate(this, this.worldObj, p_180514_2_, p_180514_3_, (ChunkPrimer)null);
        }

        if (this.settings.useTemples && this.mapFeaturesEnabled)
        {
            this.scatteredFeatureGenerator.generate(this, this.worldObj, p_180514_2_, p_180514_3_, (ChunkPrimer)null);
        }

        if (this.settings.useMonuments && this.mapFeaturesEnabled)
        {
            this.oceanMonumentGenerator.generate(this, this.worldObj, p_180514_2_, p_180514_3_, (ChunkPrimer)null);
        }
    }*/

    public void provideChunk(BlockPos blockPosIn, FullChunk chunk)
    {
        this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4, chunk);
    }
}