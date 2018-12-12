package nightmare.world.biome;

import cn.nukkit.block.Block;

public class BiomeGenTown extends NMBiomeGenBase
{
    public BiomeGenTown(int p_i1969_1_)
    {
        super(p_i1969_1_);
        this.spawnableCreatureList.clear();
        this.topBlock = Block.get(Block.STONE);
        this.fillerBlock = Block.get(Block.STONE);
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.deadBushPerChunk = 0;
        this.theBiomeDecorator.reedsPerChunk = 0;
        this.theBiomeDecorator.cactiPerChunk = 0;
    }
}