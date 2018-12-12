package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import nightmare.util.BlockPos;
import nightmare.world.World;

import java.util.Random;

public class WorldGenIcePath extends WorldGenerator
{
    private Block block = Block.get(Block.PACKED_ICE);
    private int basePathWidth;

    public WorldGenIcePath(int p_i45454_1_)
    {
        this.basePathWidth = p_i45454_1_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        while (worldIn.isAirBlock(position) && position.getY() > 2)
        {
            position = position.down();
        }

        if (worldIn.getBlockState(position).getId() != Block.SNOW_BLOCK)
        {
            return false;
        }
        else
        {
            int i = rand.nextInt(this.basePathWidth - 2) + 2;
            int j = 1;

            for (int k = position.getX() - i; k <= position.getX() + i; ++k)
            {
                for (int l = position.getZ() - i; l <= position.getZ() + i; ++l)
                {
                    int i1 = k - position.getX();
                    int j1 = l - position.getZ();

                    if (i1 * i1 + j1 * j1 <= i * i)
                    {
                        for (int k1 = position.getY() - j; k1 <= position.getY() + j; ++k1)
                        {
                            BlockPos blockpos = new BlockPos(k, k1, l);
                            Block block = worldIn.getBlockState(blockpos);

                            if (block.getId() == Block.DIRT || block.getId() == Block.SNOW_BLOCK || block.getId() == Block.ICE)
                            {
                                worldIn.setBlockState(blockpos, this.block.clone(), 2);
                            }
                        }
                    }
                }
            }

            return true;
        }
    }
}