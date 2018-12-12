package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockWood;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.world.World;

import java.util.Random;

public class WorldGenTaiga1 extends WorldGenAbstractTree
{
    private static final Block field_181636_a = Block.get(Block.LOG, BlockWood.SPRUCE);
    private static final Block field_181637_b = Block.get(Block.LEAVES, BlockLeaves.SPRUCE);

    public WorldGenTaiga1()
    {
        super(false);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        int i = rand.nextInt(5) + 7;
        int j = i - rand.nextInt(2) - 3;
        int k = i - j;
        int l = 1 + rand.nextInt(k + 1);
        boolean flag = true;

        if (position.getY() >= 1 && position.getY() + i + 1 <= 256)
        {
            for (int i1 = position.getY(); i1 <= position.getY() + 1 + i && flag; ++i1)
            {
                int j1 = 1;

                if (i1 - position.getY() < j)
                {
                    j1 = 0;
                }
                else
                {
                    j1 = l;
                }

                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                for (int k1 = position.getX() - j1; k1 <= position.getX() + j1 && flag; ++k1)
                {
                    for (int l1 = position.getZ() - j1; l1 <= position.getZ() + j1 && flag; ++l1)
                    {
                        if (i1 >= 0 && i1 < 256)
                        {
                            if (!this.func_150523_a(worldIn.getBlockState(blockpos$mutableblockpos.set(k1, i1, l1))))
                            {
                                flag = false;
                            }
                        }
                        else
                        {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag)
            {
                return false;
            }
            else
            {
                Block block = worldIn.getBlockState(position.down());

                if ((block.getId() == Block.GRASS || block.getId() == Block.DIRT) && position.getY() < 256 - i - 1)
                {
                    this.func_175921_a(worldIn, position.down());
                    int k2 = 0;

                    for (int l2 = position.getY() + i; l2 >= position.getY() + j; --l2)
                    {
                        for (int j3 = position.getX() - k2; j3 <= position.getX() + k2; ++j3)
                        {
                            int k3 = j3 - position.getX();

                            for (int i2 = position.getZ() - k2; i2 <= position.getZ() + k2; ++i2)
                            {
                                int j2 = i2 - position.getZ();

                                if (Math.abs(k3) != k2 || Math.abs(j2) != k2 || k2 <= 0)
                                {
                                    BlockPos blockpos = new BlockPos(j3, l2, i2);

                                    if (!worldIn.getBlockState(blockpos).isSolid())
                                    {
                                        this.setBlockAndNotifyAdequately(worldIn, blockpos, field_181637_b);
                                    }
                                }
                            }
                        }

                        if (k2 >= 1 && l2 == position.getY() + j + 1)
                        {
                            --k2;
                        }
                        else if (k2 < l)
                        {
                            ++k2;
                        }
                    }

                    for (int i3 = 0; i3 < i - 1; ++i3)
                    {
                        Block block1 = worldIn.getBlockState(position.up(i3));

                        if (block1.getId() == Block.AIR || BlockHelper.isLeave(block1))
                        {
                            this.setBlockAndNotifyAdequately(worldIn, position.up(i3), field_181636_a);
                        }
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }
}