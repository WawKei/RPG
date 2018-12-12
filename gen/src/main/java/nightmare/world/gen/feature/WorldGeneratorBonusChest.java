package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.util.WeightedRandomChestContent;
import nightmare.world.World;

import java.util.List;
import java.util.Random;

public class WorldGeneratorBonusChest extends WorldGenerator
{
    private final List<WeightedRandomChestContent> chestItems;
    private final int itemsToGenerateInBonusChest;

    public WorldGeneratorBonusChest(List<WeightedRandomChestContent> p_i45634_1_, int p_i45634_2_)
    {
        this.chestItems = p_i45634_1_;
        this.itemsToGenerateInBonusChest = p_i45634_2_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        Block block;

        while (((block = worldIn.getBlockState(position)).getId() == Block.AIR || BlockHelper.isLeave(block)) && position.getY() > 1)
        {
            position = position.down();
        }

        if (position.getY() < 1)
        {
            return false;
        }
        else
        {
            position = position.up();

            for (int i = 0; i < 4; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(3) - rand.nextInt(3), rand.nextInt(4) - rand.nextInt(4));

                if (worldIn.isAirBlock(blockpos) && worldIn.getBlockState(blockpos.down()).isSolid())
                {
                    worldIn.setBlockState(blockpos, Block.get(Block.CHEST), 2);
                    /*TileEntity tileentity = worldIn.getTileEntity(blockpos);

                    if (tileentity instanceof TileEntityChest)
                    {
                        WeightedRandomChestContent.generateChestContents(rand, this.chestItems, (TileEntityChest)tileentity, this.itemsToGenerateInBonusChest);
                    }*/

                    BlockPos blockpos1 = blockpos.east();
                    BlockPos blockpos2 = blockpos.west();
                    BlockPos blockpos3 = blockpos.north();
                    BlockPos blockpos4 = blockpos.south();

                    if (worldIn.isAirBlock(blockpos2) && worldIn.getBlockState(blockpos2.down()).isSolid())
                    {
                        worldIn.setBlockState(blockpos2, Block.get(Block.TORCH), 2);
                    }

                    if (worldIn.isAirBlock(blockpos1) && worldIn.getBlockState(blockpos1.down()).isSolid())
                    {
                        worldIn.setBlockState(blockpos1, Block.get(Block.TORCH), 2);
                    }

                    if (worldIn.isAirBlock(blockpos3) && worldIn.getBlockState(blockpos3.down()).isSolid())
                    {
                        worldIn.setBlockState(blockpos3, Block.get(Block.TORCH), 2);
                    }

                    if (worldIn.isAirBlock(blockpos4) && worldIn.getBlockState(blockpos4.down()).isSolid())
                    {
                        worldIn.setBlockState(blockpos4, Block.get(Block.TORCH), 2);
                    }

                    return true;
                }
            }

            return false;
        }
    }
}