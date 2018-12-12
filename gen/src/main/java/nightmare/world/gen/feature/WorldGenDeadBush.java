package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.world.World;

import java.util.Random;

public class WorldGenDeadBush extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        Block block;

        while (((block = worldIn.getBlockState(position)).getId() == Block.AIR || BlockHelper.isLeave(block)) && position.getY() > 0)
        {
            position = position.down();
        }

        for (int i = 0; i < 4; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && BlockHelper.canBlockStayDeadBush(worldIn, blockpos))
            {
                worldIn.setBlockState(blockpos, Block.get(Block.DEAD_BUSH), 2);
            }
        }

        return true;
    }
}