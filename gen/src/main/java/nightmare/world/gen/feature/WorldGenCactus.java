package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.world.World;

import java.util.Random;

public class WorldGenCactus extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (int i = 0; i < 10; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos))
            {
                int j = 1 + rand.nextInt(rand.nextInt(3) + 1);

                for (int k = 0; k < j; ++k)
                {
                    if (BlockHelper.canBlockStayCactus(worldIn, blockpos))
                    {
                        worldIn.setBlockState(blockpos.up(k), Block.get(Block.CACTUS), 2);
                    }
                }
            }
        }

        return true;
    }
}