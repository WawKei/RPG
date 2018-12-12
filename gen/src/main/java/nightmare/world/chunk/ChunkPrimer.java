package nightmare.world.chunk;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.block.Block;

public class ChunkPrimer
{
    public FullChunk chunk;

    public ChunkPrimer(FullChunk chunk){
        this.chunk = chunk;
    }

    public Block getBlockState(int x, int y, int z)
    {
        int id = this.chunk.getBlockId(x, y, z);
        int damage = this.chunk.getBlockData(x, y, z);
        return Block.get(id, damage);
    }

    public void setBlockState(int x, int y, int z, Block state)
    {
        this.chunk.setBlock(x, y, z, state.getId(), state.getDamage());
    }

    public void setBiomeId(int x, int z, int id){
        this.chunk.setBiomeId(x, z, id);
    }
}