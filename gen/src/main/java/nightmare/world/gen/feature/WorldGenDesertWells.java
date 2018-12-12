package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSlab;
import cn.nukkit.block.BlockSlabStone;
import com.google.common.base.Predicates;
import java.util.Random;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.util.EnumFacing;
import nightmare.world.World;

public class WorldGenDesertWells extends WorldGenerator
{
    private static final BlockHelper field_175913_a = BlockHelper.forBlock(Block.get(Block.SAND));
    private final Block field_175911_b = Block.get(Block.STONE_SLAB, BlockSlabStone.SANDSTONE);
    private final Block field_175912_c = Block.get(Block.SANDSTONE);
    private final Block field_175910_d = Block.get(Block.STILL_WATER);

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        while (worldIn.isAirBlock(position) && position.getY() > 2)
        {
            position = position.down();
        }

        if (!field_175913_a.apply(worldIn.getBlockState(position)))
        {
            return false;
        }
        else
        {
            for (int i = -2; i <= 2; ++i)
            {
                for (int j = -2; j <= 2; ++j)
                {
                    if (worldIn.isAirBlock(position.add(i, -1, j)) && worldIn.isAirBlock(position.add(i, -2, j)))
                    {
                        return false;
                    }
                }
            }

            for (int l = -1; l <= 0; ++l)
            {
                for (int l1 = -2; l1 <= 2; ++l1)
                {
                    for (int k = -2; k <= 2; ++k)
                    {
                        worldIn.setBlockState(position.add(l1, l, k), this.field_175912_c, 2);
                    }
                }
            }

            worldIn.setBlockState(position, this.field_175910_d, 2);

            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
            {
                worldIn.setBlockState(position.offset(enumfacing), this.field_175910_d, 2);
            }

            for (int i1 = -2; i1 <= 2; ++i1)
            {
                for (int i2 = -2; i2 <= 2; ++i2)
                {
                    if (i1 == -2 || i1 == 2 || i2 == -2 || i2 == 2)
                    {
                        worldIn.setBlockState(position.add(i1, 1, i2), this.field_175912_c, 2);
                    }
                }
            }

            worldIn.setBlockState(position.add(2, 1, 0), this.field_175911_b, 2);
            worldIn.setBlockState(position.add(-2, 1, 0), this.field_175911_b, 2);
            worldIn.setBlockState(position.add(0, 1, 2), this.field_175911_b, 2);
            worldIn.setBlockState(position.add(0, 1, -2), this.field_175911_b, 2);

            for (int j1 = -1; j1 <= 1; ++j1)
            {
                for (int j2 = -1; j2 <= 1; ++j2)
                {
                    if (j1 == 0 && j2 == 0)
                    {
                        worldIn.setBlockState(position.add(j1, 4, j2), this.field_175912_c, 2);
                    }
                    else
                    {
                        worldIn.setBlockState(position.add(j1, 4, j2), this.field_175911_b, 2);
                    }
                }
            }

            for (int k1 = 1; k1 <= 3; ++k1)
            {
                worldIn.setBlockState(position.add(-1, k1, -1), this.field_175912_c, 2);
                worldIn.setBlockState(position.add(-1, k1, 1), this.field_175912_c, 2);
                worldIn.setBlockState(position.add(1, k1, -1), this.field_175912_c, 2);
                worldIn.setBlockState(position.add(1, k1, 1), this.field_175912_c, 2);
            }

            return true;
        }
    }
}