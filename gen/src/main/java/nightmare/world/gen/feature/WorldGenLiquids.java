package nightmare.world.gen.feature;

import java.util.Random;
import cn.nukkit.block.Block;
import nightmare.util.BlockPos;
import nightmare.world.World;

public class WorldGenLiquids extends WorldGenerator
{
    private Block block;

    public WorldGenLiquids(Block p_i45465_1_)
    {
        this.block = p_i45465_1_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        if (worldIn.getBlockState(position.up()).getId() != Block.STONE)
        {
            return false;
        }
        else if (worldIn.getBlockState(position.down()).getId() != Block.STONE)
        {
            return false;
        }
        else if (worldIn.getBlockState(position).getId() != Block.AIR && worldIn.getBlockState(position).getId() != Block.STONE)
        {
            return false;
        }
        else
        {
            int i = 0;

            if (worldIn.getBlockState(position.west()).getId() == Block.STONE)
            {
                ++i;
            }

            if (worldIn.getBlockState(position.east()).getId() == Block.STONE)
            {
                ++i;
            }

            if (worldIn.getBlockState(position.north()).getId() == Block.STONE)
            {
                ++i;
            }

            if (worldIn.getBlockState(position.south()).getId() == Block.STONE)
            {
                ++i;
            }

            int j = 0;

            if (worldIn.isAirBlock(position.west()))
            {
                ++j;
            }

            if (worldIn.isAirBlock(position.east()))
            {
                ++j;
            }

            if (worldIn.isAirBlock(position.north()))
            {
                ++j;
            }

            if (worldIn.isAirBlock(position.south()))
            {
                ++j;
            }

            if (i == 3 && j == 1)
            {
                worldIn.setBlockState(position, this.block.clone(), 2);
                //worldIn.forceBlockUpdateTick(this.block, position, rand);
            }

            return true;
        }
    }
}