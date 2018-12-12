package nightmare.world.gen;

import java.util.Random;
import cn.nukkit.block.Block;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.world.World;
import nightmare.world.gen.feature.WorldGenerator;

public class GeneratorBushFeature extends WorldGenerator
{
    private Block field_175908_a;

    public GeneratorBushFeature(Block p_i45633_1_)
    {
        this.field_175908_a = p_i45633_1_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (int i = 0; i < 64; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && (!worldIn.getHasNoSky() || blockpos.getY() < 255) && BlockHelper.canBlockStayFlower(worldIn, blockpos))
            {
                worldIn.setBlockState(blockpos, this.field_175908_a.clone(), 2);
            }
        }

        return true;
    }
}