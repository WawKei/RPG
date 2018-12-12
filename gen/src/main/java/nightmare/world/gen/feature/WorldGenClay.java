package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import nightmare.util.BlockPos;
import nightmare.world.World;

import java.util.Random;

public class WorldGenClay extends WorldGenerator
{
    private Block field_150546_a = Block.get(Block.CLAY_BLOCK);
    private int numberOfBlocks;

    public WorldGenClay(int p_i2011_1_)
    {
        this.numberOfBlocks = p_i2011_1_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        int id = worldIn.getBlockState(position).getId();
        if (id != Block.WATER && id != Block.STILL_WATER)
        {
            return false;
        }
        else
        {
            int i = rand.nextInt(this.numberOfBlocks - 2) + 2;
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

                            if (block.getId() == Block.DIRT || block.getId() == Block.CLAY_BLOCK)
                            {
                                worldIn.setBlockState(blockpos, this.field_150546_a.clone(), 2);
                            }
                        }
                    }
                }
            }

            return true;
        }
    }
}