package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.util.EnumFacing;
import nightmare.world.World;

import java.util.Random;

public class WorldGenVines extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (; position.getY() < 128; position = position.up())
        {
            if (worldIn.isAirBlock(position))
            {
                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL.facings())
                {
                    if (BlockHelper.canPlaceBlockOnSideVine(worldIn, position, enumfacing))
                    {
                        Block iblockstate = Block.get(Block.VINE, enumfacing.getIndex());
                        worldIn.setBlockState(position, iblockstate, 2);
                        break;
                    }
                }
            }
            else
            {
                position = position.add(rand.nextInt(4) - rand.nextInt(4), 0, rand.nextInt(4) - rand.nextInt(4));
            }
        }

        return true;
    }
}