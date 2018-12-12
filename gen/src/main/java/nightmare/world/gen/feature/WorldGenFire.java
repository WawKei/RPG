package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import nightmare.util.BlockPos;
import nightmare.world.World;

import java.util.Random;

public class WorldGenFire extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (int i = 0; i < 64; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && worldIn.getBlockState(blockpos.down()).getId() == Block.NETHERRACK)
            {
                worldIn.setBlockState(blockpos, Block.get(Block.FIRE), 2);
            }
        }

        return true;
    }
}