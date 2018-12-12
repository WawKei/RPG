package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.util.EnumFacing;
import nightmare.world.World;

import java.util.Random;

public class WorldGenPumpkin extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (int i = 0; i < 64; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && worldIn.getBlockState(blockpos.down()).getId() == Block.GRASS && BlockHelper.canPlaceBlockAtPumpkin(worldIn, blockpos))
            {
                worldIn.setBlockState(blockpos, Block.get(Block.PUMPKIN, EnumFacing.Plane.HORIZONTAL.random(rand).getIndex()), 2);
            }
        }

        return true;
    }
}