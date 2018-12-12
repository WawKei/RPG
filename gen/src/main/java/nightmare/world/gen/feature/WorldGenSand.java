package nightmare.world.gen.feature;

import java.util.Random;
import cn.nukkit.block.Block;
import nightmare.util.BlockPos;
import nightmare.world.World;

public class WorldGenSand extends WorldGenerator
{
    private Block block;
    private int radius;

    public WorldGenSand(Block p_i45462_1_, int p_i45462_2_)
    {
        this.block = p_i45462_1_;
        this.radius = p_i45462_2_;
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
            int i = rand.nextInt(this.radius - 2) + 2;
            int j = 2;

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

                            if (block.getId() == Block.DIRT || block.getId() == Block.GRASS)
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