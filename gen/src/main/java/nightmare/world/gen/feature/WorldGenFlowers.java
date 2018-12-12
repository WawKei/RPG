package nightmare.world.gen.feature;

import java.util.Random;

import cn.nukkit.block.Block;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.world.World;

public class WorldGenFlowers extends WorldGenerator
{
    private Block field_175915_b;

    public WorldGenFlowers(Block p_i45632_1_)
    {
        this.setGeneratedBlock(p_i45632_1_);
    }

    public void setGeneratedBlock(Block p_175914_1_)
    {
        this.field_175915_b = p_175914_1_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (int i = 0; i < 64; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && (!worldIn.getHasNoSky() || blockpos.getY() < 255) && BlockHelper.canBlockStayFlower(worldIn, blockpos))
            {
                worldIn.setBlockState(blockpos, this.field_175915_b.clone(), 2);
            }
        }

        return true;
    }
}