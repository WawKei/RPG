package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.world.World;

import java.util.Random;

public class WorldGenReed extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (int i = 0; i < 20; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), 0, rand.nextInt(4) - rand.nextInt(4));

            if (worldIn.isAirBlock(blockpos))
            {
                BlockPos blockpos1 = blockpos.down();

                if (BlockHelper.isWater(worldIn.getBlockState(blockpos1.west())) || BlockHelper.isWater(worldIn.getBlockState(blockpos1.east())) || BlockHelper.isWater(worldIn.getBlockState(blockpos1.north())) || BlockHelper.isWater(worldIn.getBlockState(blockpos1.south())))
                {
                    int j = 2 + rand.nextInt(rand.nextInt(3) + 1);

                    for (int k = 0; k < j; ++k)
                    {
                        if (BlockHelper.canBlockStayReed(Block.get(Block.REEDS), worldIn, blockpos))
                        {
                            worldIn.setBlockState(blockpos.up(k), Block.get(Block.REEDS), 2);
                        }
                    }
                }
            }
        }

        return true;
    }
}