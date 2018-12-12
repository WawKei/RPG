package nightmare.world.biome;

import cn.nukkit.block.Block;

public class BiomeGenStream extends NMBiomeGenBase
{
    public BiomeGenStream(int p_i1987_1_)
    {
        super(p_i1987_1_);
        this.topBlock = Block.get(Block.STONE);
        this.fillerBlock = Block.get(Block.STONE);
        getNMBiomeDecorator().setWaterlilyPerChunk(2);
        this.spawnableCreatureList.clear();
    }
}