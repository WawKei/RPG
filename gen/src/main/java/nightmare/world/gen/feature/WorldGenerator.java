package nightmare.world.gen.feature;

import java.util.Random;
import cn.nukkit.block.Block;
import nightmare.util.BlockPos;
import nightmare.world.World;

public abstract class WorldGenerator
{
    private final boolean doBlockNotify;

    public WorldGenerator()
    {
        this(false);
    }

    public WorldGenerator(boolean notify)
    {
        this.doBlockNotify = notify;
    }

    public abstract boolean generate(World worldIn, Random rand, BlockPos position);

    public void func_175904_e()
    {
    }

    protected void setBlockAndNotifyAdequately(World worldIn, BlockPos pos, Block state)
    {
        if (this.doBlockNotify)
        {
            worldIn.setBlockState(pos, state, 3);
        }
        else
        {
            worldIn.setBlockState(pos, state, 2);
        }
    }
}