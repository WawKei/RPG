package nightmare.world.gen;

import java.util.Random;
import nightmare.world.World;
import nightmare.world.chunk.ChunkPrimer;
import nightmare.world.chunk.IChunkProvider;

public class MapGenBase
{
    protected int range = 8;
    protected Random rand = new Random();
    protected World world;

    public void generate(IChunkProvider chunkProviderIn, World worldIn, int x, int z, ChunkPrimer chunkPrimerIn)
    {
        int i = this.range;
        this.world = worldIn;
        this.rand.setSeed(worldIn.getSeed());
        long j = this.rand.nextLong();
        long k = this.rand.nextLong();

        for (int l = x - i; l <= x + i; ++l)
        {
            for (int i1 = z - i; i1 <= z + i; ++i1)
            {
                long j1 = (long)l * j;
                long k1 = (long)i1 * k;
                this.rand.setSeed(j1 ^ k1 ^ worldIn.getSeed());
                this.recursiveGenerate(worldIn, l, i1, x, z, chunkPrimerIn);
            }
        }
    }

    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn)
    {
    }
}