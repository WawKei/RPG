package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.world.World;

import java.util.Random;

public class WorldGenDoublePlant extends WorldGenerator
{
    private int field_150549_a;

    public void setPlantType(int p_180710_1_)
    {
        this.field_150549_a = p_180710_1_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        boolean flag = false;

        for (int i = 0; i < 64; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && (!worldIn.getHasNoSky() || blockpos.getY() < 254) && BlockHelper.canBlockStayFlower(worldIn, blockpos))
            {
                worldIn.setBlockState(blockpos, Block.get(Block.DOUBLE_PLANT, this.field_150549_a), 2);
                flag = true;
            }
        }

        return flag;
    }
}