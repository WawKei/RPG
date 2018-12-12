package nightmare.world.gen.feature;

import java.util.Random;
import cn.nukkit.block.Block;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.world.World;

public abstract class WorldGenAbstractTree extends WorldGenerator
{
    public WorldGenAbstractTree(boolean p_i45448_1_)
    {
        super(p_i45448_1_);
    }

    protected boolean func_150523_a(Block p_150523_1_)
    {
        return p_150523_1_.getId() == Block.AIR || BlockHelper.isLeave(p_150523_1_) || p_150523_1_.getId() == Block.GRASS || p_150523_1_.getId() == Block.DIRT || p_150523_1_.getId() == Block.LOG || p_150523_1_.getId() == Block.LOG2 || p_150523_1_.getId() == Block.SAPLING || p_150523_1_.getId() == Block.VINE;
    }

    public void func_180711_a(World worldIn, Random p_180711_2_, BlockPos p_180711_3_)
    {
    }

    protected void func_175921_a(World worldIn, BlockPos p_175921_2_)
    {
        if (worldIn.getBlockState(p_175921_2_).getId() != Block.DIRT)
        {
            this.setBlockAndNotifyAdequately(worldIn, p_175921_2_, Block.get(Block.DIRT));
        }
    }
}