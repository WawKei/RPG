package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.world.World;

import java.util.Random;

public class WorldGenWaterlily extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (int i = 0; i < 10; ++i)
        {
            int j = position.getX() + rand.nextInt(8) - rand.nextInt(8);
            int k = position.getY() + rand.nextInt(4) - rand.nextInt(4);
            int l = position.getZ() + rand.nextInt(8) - rand.nextInt(8);

            if (worldIn.isAirBlock(new BlockPos(j, k, l)) && BlockHelper.canBlockStayLily(worldIn, new BlockPos(j, k, l)))
            {
                worldIn.setBlockState(new BlockPos(j, k, l), Block.get(Block.WATER_LILY), 2);
            }
        }

        return true;
    }
}