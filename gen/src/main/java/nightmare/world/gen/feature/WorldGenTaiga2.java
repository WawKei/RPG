package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockWood;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.world.World;

import java.util.Random;

public class WorldGenTaiga2 extends WorldGenAbstractTree
{
    private static final Block field_181645_a = Block.get(Block.LOG, BlockWood.SPRUCE);
    private static final Block field_181646_b = Block.get(Block.LEAVES, BlockLeaves.SPRUCE);

    public WorldGenTaiga2(boolean p_i2025_1_)
    {
        super(p_i2025_1_);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        int i = rand.nextInt(4) + 6;
        int j = 1 + rand.nextInt(2);
        int k = i - j;
        int l = 2 + rand.nextInt(2);
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
                            Block block = worldIn.getBlockState(blockpos$mutableblockpos.set(k1, i1, l1));

                            if (block.getId() != Block.AIR && !BlockHelper.isLeave(block))
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
                Block block1 = worldIn.getBlockState(position.down());

                if ((block1.getId()== Block.GRASS || block1.getId() == Block.DIRT || block1.getId() == Block.FARMLAND) && position.getY() < 256 - i - 1)
                {
                    this.func_175921_a(worldIn, position.down());
                    int i3 = rand.nextInt(2);
                    int j3 = 1;
                    int k3 = 0;

                    for (int l3 = 0; l3 <= k; ++l3)
                    {
                        int j4 = position.getY() + i - l3;

                        for (int i2 = position.getX() - i3; i2 <= position.getX() + i3; ++i2)
                        {
                            int j2 = i2 - position.getX();

                            for (int k2 = position.getZ() - i3; k2 <= position.getZ() + i3; ++k2)
                            {
                                int l2 = k2 - position.getZ();

                                if (Math.abs(j2) != i3 || Math.abs(l2) != i3 || i3 <= 0)
                                {
                                    BlockPos blockpos = new BlockPos(i2, j4, k2);

                                    if (!worldIn.getBlockState(blockpos).isSolid())
                                    {
                                        this.setBlockAndNotifyAdequately(worldIn, blockpos, field_181646_b);
                                    }
                                }
                            }
                        }

                        if (i3 >= j3)
                        {
                            i3 = k3;
                            k3 = 1;
                            ++j3;

                            if (j3 > l)
                            {
                                j3 = l;
                            }
                        }
                        else
                        {
                            ++i3;
                        }
                    }

                    int i4 = rand.nextInt(3);

                    for (int k4 = 0; k4 < i - i4; ++k4)
                    {
                        Block block2 = worldIn.getBlockState(position.up(k4));

                        if (block2.getId() == Block.AIR || BlockHelper.isLeave(block2))
                        {
                            this.setBlockAndNotifyAdequately(worldIn, position.up(k4), field_181645_a);
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