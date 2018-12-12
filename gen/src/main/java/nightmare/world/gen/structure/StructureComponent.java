package nightmare.world.gen.structure;

import java.util.List;
import java.util.Random;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDoor;
import nightmare.nbt.NBTTagCompound;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.util.EnumFacing;
import nightmare.util.WeightedRandomChestContent;
import nightmare.world.World;

public abstract class StructureComponent
{
    protected StructureBoundingBox boundingBox;
    protected EnumFacing coordBaseMode;
    protected int componentType;

    //public StructureDecorator deco = null;
    public int spawnListIndex = 0;
    protected NMFeature feature = NMFeature.NOTHING;

    public StructureComponent()
    {
    }

    protected StructureComponent(int type)
    {
        this.componentType = type;
    }

    public StructureComponent(NMFeature feature, int i) {
        this(i);
        this.feature = feature;
    }

    public NMFeature getFeatureType() {
        return feature;
    }

    public NBTTagCompound createStructureBaseNBT()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("id", MapGenStructureIO.getStructureComponentName(this));
        nbttagcompound.setTag("BB", this.boundingBox.toNBTTagIntArray());
        nbttagcompound.setInteger("O", this.coordBaseMode == null ? -1 : this.coordBaseMode.getHorizontalIndex());
        nbttagcompound.setInteger("GD", this.componentType);
        this.writeStructureToNBT(nbttagcompound);
        return nbttagcompound;
    }

    protected abstract void writeStructureToNBT(NBTTagCompound tagCompound);

    public void readStructureBaseNBT(World worldIn, NBTTagCompound tagCompound)
    {
        if (tagCompound.hasKey("BB"))
        {
            this.boundingBox = new StructureBoundingBox(tagCompound.getIntArray("BB"));
        }

        int i = tagCompound.getInteger("O");
        this.coordBaseMode = i == -1 ? null : EnumFacing.getHorizontal(i);
        this.componentType = tagCompound.getInteger("GD");
        this.readStructureFromNBT(tagCompound);
    }

    protected abstract void readStructureFromNBT(NBTTagCompound tagCompound);

    public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand)
    {
    }

    public abstract boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn);

    public StructureBoundingBox getBoundingBox()
    {
        return this.boundingBox;
    }

    public int getComponentType()
    {
        return this.componentType;
    }

    public static StructureComponent findIntersecting(List<StructureComponent> listIn, StructureBoundingBox boundingboxIn)
    {
        for (StructureComponent structurecomponent : listIn)
        {
            if (structurecomponent.getBoundingBox() != null && structurecomponent.getBoundingBox().intersectsWith(boundingboxIn))
            {
                return structurecomponent;
            }
        }

        return null;
    }

    public BlockPos getBoundingBoxCenter()
    {
        return new BlockPos(this.boundingBox.getCenter());
    }

    protected boolean isLiquidInStructureBoundingBox(World worldIn, StructureBoundingBox boundingboxIn)
    {
        int i = Math.max(this.boundingBox.minX - 1, boundingboxIn.minX);
        int j = Math.max(this.boundingBox.minY - 1, boundingboxIn.minY);
        int k = Math.max(this.boundingBox.minZ - 1, boundingboxIn.minZ);
        int l = Math.min(this.boundingBox.maxX + 1, boundingboxIn.maxX);
        int i1 = Math.min(this.boundingBox.maxY + 1, boundingboxIn.maxY);
        int j1 = Math.min(this.boundingBox.maxZ + 1, boundingboxIn.maxZ);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int k1 = i; k1 <= l; ++k1)
        {
            for (int l1 = k; l1 <= j1; ++l1)
            {
                if (BlockHelper.isLiquid(worldIn.getBlockState(blockpos$mutableblockpos.set(k1, j, l1))))
                {
                    return true;
                }

                if (BlockHelper.isLiquid(worldIn.getBlockState(blockpos$mutableblockpos.set(k1, i1, l1))))
                {
                    return true;
                }
            }
        }

        for (int i2 = i; i2 <= l; ++i2)
        {
            for (int k2 = j; k2 <= i1; ++k2)
            {
                if (BlockHelper.isLiquid(worldIn.getBlockState(blockpos$mutableblockpos.set(i2, k2, k))))
                {
                    return true;
                }

                if (BlockHelper.isLiquid(worldIn.getBlockState(blockpos$mutableblockpos.set(i2, k2, j1))))
                {
                    return true;
                }
            }
        }

        for (int j2 = k; j2 <= j1; ++j2)
        {
            for (int l2 = j; l2 <= i1; ++l2)
            {
                if (BlockHelper.isLiquid(worldIn.getBlockState(blockpos$mutableblockpos.set(i, l2, j2))))
                {
                    return true;
                }

                if (BlockHelper.isLiquid(worldIn.getBlockState(blockpos$mutableblockpos.set(l, l2, j2))))
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected int getXWithOffset(int x, int z)
    {
        if (this.coordBaseMode == null)
        {
            return x;
        }
        else
        {
            switch (this.coordBaseMode)
            {
                case NORTH:
                case SOUTH:
                    return this.boundingBox.minX + x;
                case WEST:
                    return this.boundingBox.maxX - z;
                case EAST:
                    return this.boundingBox.minX + z;
                default:
                    return x;
            }
        }
    }

    protected int getYWithOffset(int y)
    {
        return this.coordBaseMode == null ? y : y + this.boundingBox.minY;
    }

    protected int getZWithOffset(int x, int z)
    {
        if (this.coordBaseMode == null)
        {
            return z;
        }
        else
        {
            switch (this.coordBaseMode)
            {
                case NORTH:
                    return this.boundingBox.maxZ - z;
                case SOUTH:
                    return this.boundingBox.minZ + z;
                case WEST:
                case EAST:
                    return this.boundingBox.minZ + x;
                default:
                    return z;
            }
        }
    }

    protected int getMetadataWithOffset(Block blockIn, int meta)
    {
        if (blockIn.getId() == Block.RAIL)
        {
            if (this.coordBaseMode == EnumFacing.WEST || this.coordBaseMode == EnumFacing.EAST)
            {
                if (meta == 1)
                {
                    return 0;
                }

                return 1;
            }
        }
        else if (blockIn instanceof BlockDoor)
        {
            if (this.coordBaseMode == EnumFacing.SOUTH)
            {
                if (meta == 0)
                {
                    return 2;
                }

                if (meta == 2)
                {
                    return 0;
                }
            }
            else
            {
                if (this.coordBaseMode == EnumFacing.WEST)
                {
                    return meta + 1 & 3;
                }

                if (this.coordBaseMode == EnumFacing.EAST)
                {
                    return meta + 3 & 3;
                }
            }
        }
        else if (blockIn.getId() != Block.COBBLESTONE_STAIRS && blockIn.getId() != Block.OAK_WOOD_STAIRS && blockIn.getId() != Block.NETHER_BRICKS_STAIRS && blockIn.getId() != Block.STONE_BRICK_STAIRS && blockIn.getId() != Block.RED_SANDSTONE_STAIRS)
        {
            if (blockIn.getId() == Block.LADDER)
            {
                if (this.coordBaseMode == EnumFacing.SOUTH)
                {
                    if (meta == EnumFacing.NORTH.getIndex())
                    {
                        return EnumFacing.SOUTH.getIndex();
                    }

                    if (meta == EnumFacing.SOUTH.getIndex())
                    {
                        return EnumFacing.NORTH.getIndex();
                    }
                }
                else if (this.coordBaseMode == EnumFacing.WEST)
                {
                    if (meta == EnumFacing.NORTH.getIndex())
                    {
                        return EnumFacing.WEST.getIndex();
                    }

                    if (meta == EnumFacing.SOUTH.getIndex())
                    {
                        return EnumFacing.EAST.getIndex();
                    }

                    if (meta == EnumFacing.WEST.getIndex())
                    {
                        return EnumFacing.NORTH.getIndex();
                    }

                    if (meta == EnumFacing.EAST.getIndex())
                    {
                        return EnumFacing.SOUTH.getIndex();
                    }
                }
                else if (this.coordBaseMode == EnumFacing.EAST)
                {
                    if (meta == EnumFacing.NORTH.getIndex())
                    {
                        return EnumFacing.EAST.getIndex();
                    }

                    if (meta == EnumFacing.SOUTH.getIndex())
                    {
                        return EnumFacing.WEST.getIndex();
                    }

                    if (meta == EnumFacing.WEST.getIndex())
                    {
                        return EnumFacing.NORTH.getIndex();
                    }

                    if (meta == EnumFacing.EAST.getIndex())
                    {
                        return EnumFacing.SOUTH.getIndex();
                    }
                }
            }
            else if (blockIn.getId() == Block.STONE_BUTTON)
            {
                if (this.coordBaseMode == EnumFacing.SOUTH)
                {
                    if (meta == 3)
                    {
                        return 4;
                    }

                    if (meta == 4)
                    {
                        return 3;
                    }
                }
                else if (this.coordBaseMode == EnumFacing.WEST)
                {
                    if (meta == 3)
                    {
                        return 1;
                    }

                    if (meta == 4)
                    {
                        return 2;
                    }

                    if (meta == 2)
                    {
                        return 3;
                    }

                    if (meta == 1)
                    {
                        return 4;
                    }
                }
                else if (this.coordBaseMode == EnumFacing.EAST)
                {
                    if (meta == 3)
                    {
                        return 2;
                    }

                    if (meta == 4)
                    {
                        return 1;
                    }

                    if (meta == 2)
                    {
                        return 3;
                    }

                    if (meta == 1)
                    {
                        return 4;
                    }
                }
            }
            else if (blockIn.getId() != Block.TRIPWIRE_HOOK)//&& not Directional
            {
                if (blockIn.getId() == Block.PISTON || blockIn.getId() == Block.STICKY_PISTON || blockIn.getId() == Block.LEVER || blockIn.getId() == Block.DISPENSER)
                {
                    if (this.coordBaseMode == EnumFacing.SOUTH)
                    {
                        if (meta == EnumFacing.NORTH.getIndex() || meta == EnumFacing.SOUTH.getIndex())
                        {
                            return EnumFacing.getFront(meta).getOpposite().getIndex();
                        }
                    }
                    else if (this.coordBaseMode == EnumFacing.WEST)
                    {
                        if (meta == EnumFacing.NORTH.getIndex())
                        {
                            return EnumFacing.WEST.getIndex();
                        }

                        if (meta == EnumFacing.SOUTH.getIndex())
                        {
                            return EnumFacing.EAST.getIndex();
                        }

                        if (meta == EnumFacing.WEST.getIndex())
                        {
                            return EnumFacing.NORTH.getIndex();
                        }

                        if (meta == EnumFacing.EAST.getIndex())
                        {
                            return EnumFacing.SOUTH.getIndex();
                        }
                    }
                    else if (this.coordBaseMode == EnumFacing.EAST)
                    {
                        if (meta == EnumFacing.NORTH.getIndex())
                        {
                            return EnumFacing.EAST.getIndex();
                        }

                        if (meta == EnumFacing.SOUTH.getIndex())
                        {
                            return EnumFacing.WEST.getIndex();
                        }

                        if (meta == EnumFacing.WEST.getIndex())
                        {
                            return EnumFacing.NORTH.getIndex();
                        }

                        if (meta == EnumFacing.EAST.getIndex())
                        {
                            return EnumFacing.SOUTH.getIndex();
                        }
                    }
                }
            }
            else
            {
                EnumFacing enumfacing = EnumFacing.getHorizontal(meta);

                if (this.coordBaseMode == EnumFacing.SOUTH)
                {
                    if (enumfacing == EnumFacing.SOUTH || enumfacing == EnumFacing.NORTH)
                    {
                        return enumfacing.getOpposite().getHorizontalIndex();
                    }
                }
                else if (this.coordBaseMode == EnumFacing.WEST)
                {
                    if (enumfacing == EnumFacing.NORTH)
                    {
                        return EnumFacing.WEST.getHorizontalIndex();
                    }

                    if (enumfacing == EnumFacing.SOUTH)
                    {
                        return EnumFacing.EAST.getHorizontalIndex();
                    }

                    if (enumfacing == EnumFacing.WEST)
                    {
                        return EnumFacing.NORTH.getHorizontalIndex();
                    }

                    if (enumfacing == EnumFacing.EAST)
                    {
                        return EnumFacing.SOUTH.getHorizontalIndex();
                    }
                }
                else if (this.coordBaseMode == EnumFacing.EAST)
                {
                    if (enumfacing == EnumFacing.NORTH)
                    {
                        return EnumFacing.EAST.getHorizontalIndex();
                    }

                    if (enumfacing == EnumFacing.SOUTH)
                    {
                        return EnumFacing.WEST.getHorizontalIndex();
                    }

                    if (enumfacing == EnumFacing.WEST)
                    {
                        return EnumFacing.NORTH.getHorizontalIndex();
                    }

                    if (enumfacing == EnumFacing.EAST)
                    {
                        return EnumFacing.SOUTH.getHorizontalIndex();
                    }
                }
            }
        }
        else if (this.coordBaseMode == EnumFacing.SOUTH)
        {
            if (meta == 2)
            {
                return 3;
            }

            if (meta == 3)
            {
                return 2;
            }
        }
        else if (this.coordBaseMode == EnumFacing.WEST)
        {
            if (meta == 0)
            {
                return 2;
            }

            if (meta == 1)
            {
                return 3;
            }

            if (meta == 2)
            {
                return 0;
            }

            if (meta == 3)
            {
                return 1;
            }
        }
        else if (this.coordBaseMode == EnumFacing.EAST)
        {
            if (meta == 0)
            {
                return 2;
            }

            if (meta == 1)
            {
                return 3;
            }

            if (meta == 2)
            {
                return 1;
            }

            if (meta == 3)
            {
                return 0;
            }
        }

        return meta;
    }

    protected void setBlockState(World worldIn, Block blockstateIn, int x, int y, int z, StructureBoundingBox boundingboxIn)
    {
        BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));

        if (boundingboxIn.isVecInside(blockpos))
        {
            worldIn.setBlockState(blockpos, blockstateIn, 2);
        }
    }

    protected Block getBlockStateFromPos(World worldIn, int x, int y, int z, StructureBoundingBox boundingboxIn)
    {
        int i = this.getXWithOffset(x, z);
        int j = this.getYWithOffset(y);
        int k = this.getZWithOffset(x, z);
        BlockPos blockpos = new BlockPos(i, j, k);
        return !boundingboxIn.isVecInside(blockpos) ? Block.get(Block.AIR) : worldIn.getBlockState(blockpos);
    }

    protected void fillWithAir(World worldIn, StructureBoundingBox structurebb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
    {
        for (int i = minY; i <= maxY; ++i)
        {
            for (int j = minX; j <= maxX; ++j)
            {
                for (int k = minZ; k <= maxZ; ++k)
                {
                    this.setBlockState(worldIn, Block.get(Block.AIR), j, i, k, structurebb);
                }
            }
        }
    }

    protected void fillWithBlocks(World worldIn, StructureBoundingBox boundingboxIn, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, Block boundaryBlockState, Block insideBlockState, boolean existingOnly)
    {
        for (int i = yMin; i <= yMax; ++i)
        {
            for (int j = xMin; j <= xMax; ++j)
            {
                for (int k = zMin; k <= zMax; ++k)
                {
                    if (!existingOnly || this.getBlockStateFromPos(worldIn, j, i, k, boundingboxIn).getId() != Block.AIR)
                    {
                        if (i != yMin && i != yMax && j != xMin && j != xMax && k != zMin && k != zMax)
                        {
                            this.setBlockState(worldIn, insideBlockState, j, i, k, boundingboxIn);
                        }
                        else
                        {
                            this.setBlockState(worldIn, boundaryBlockState, j, i, k, boundingboxIn);
                        }
                    }
                }
            }
        }
    }

    protected void fillWithRandomizedBlocks(World worldIn, StructureBoundingBox boundingboxIn, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean alwaysReplace, Random rand, BlockSelector blockselector)
    {
        for (int i = minY; i <= maxY; ++i)
        {
            for (int j = minX; j <= maxX; ++j)
            {
                for (int k = minZ; k <= maxZ; ++k)
                {
                    if (!alwaysReplace || this.getBlockStateFromPos(worldIn, j, i, k, boundingboxIn).getId() != Block.AIR)
                    {
                        blockselector.selectBlocks(rand, j, i, k, i == minY || i == maxY || j == minX || j == maxX || k == minZ || k == maxZ);
                        this.setBlockState(worldIn, blockselector.getBlockState(), j, i, k, boundingboxIn);
                    }
                }
            }
        }
    }

    protected void func_175805_a(World worldIn, StructureBoundingBox boundingboxIn, Random rand, float chance, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block blockstate1, Block blockstate2, boolean p_175805_13_)
    {
        for (int i = minY; i <= maxY; ++i)
        {
            for (int j = minX; j <= maxX; ++j)
            {
                for (int k = minZ; k <= maxZ; ++k)
                {
                    if (rand.nextFloat() <= chance && (!p_175805_13_ || this.getBlockStateFromPos(worldIn, j, i, k, boundingboxIn).getId() != Block.AIR))
                    {
                        if (i != minY && i != maxY && j != minX && j != maxX && k != minZ && k != maxZ)
                        {
                            this.setBlockState(worldIn, blockstate2, j, i, k, boundingboxIn);
                        }
                        else
                        {
                            this.setBlockState(worldIn, blockstate1, j, i, k, boundingboxIn);
                        }
                    }
                }
            }
        }
    }

    protected void randomlyPlaceBlock(World worldIn, StructureBoundingBox boundingboxIn, Random rand, float chance, int x, int y, int z, Block blockstateIn)
    {
        if (rand.nextFloat() < chance)
        {
            this.setBlockState(worldIn, blockstateIn, x, y, z, boundingboxIn);
        }
    }

    protected void randomlyRareFillWithBlocks(World worldIn, StructureBoundingBox boundingboxIn, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block blockstateIn, boolean p_180777_10_)
    {
        float f = (float)(maxX - minX + 1);
        float f1 = (float)(maxY - minY + 1);
        float f2 = (float)(maxZ - minZ + 1);
        float f3 = (float)minX + f / 2.0F;
        float f4 = (float)minZ + f2 / 2.0F;

        for (int i = minY; i <= maxY; ++i)
        {
            float f5 = (float)(i - minY) / f1;

            for (int j = minX; j <= maxX; ++j)
            {
                float f6 = ((float)j - f3) / (f * 0.5F);

                for (int k = minZ; k <= maxZ; ++k)
                {
                    float f7 = ((float)k - f4) / (f2 * 0.5F);

                    if (!p_180777_10_ || this.getBlockStateFromPos(worldIn, j, i, k, boundingboxIn).getId() != Block.AIR)
                    {
                        float f8 = f6 * f6 + f5 * f5 + f7 * f7;

                        if (f8 <= 1.05F)
                        {
                            this.setBlockState(worldIn, blockstateIn, j, i, k, boundingboxIn);
                        }
                    }
                }
            }
        }
    }

    protected void clearCurrentPositionBlocksUpwards(World worldIn, int x, int y, int z, StructureBoundingBox structurebb)
    {
        BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));

        if (structurebb.isVecInside(blockpos))
        {
            while (!worldIn.isAirBlock(blockpos) && blockpos.getY() < 255)
            {
                worldIn.setBlockState(blockpos, Block.get(Block.AIR), 2);
                blockpos = blockpos.up();
            }
        }
    }

    protected void replaceAirAndLiquidDownwards(World worldIn, Block blockstateIn, int x, int y, int z, StructureBoundingBox boundingboxIn)
    {
        int i = this.getXWithOffset(x, z);
        int j = this.getYWithOffset(y);
        int k = this.getZWithOffset(x, z);

        if (boundingboxIn.isVecInside(new BlockPos(i, j, k)))
        {
            while ((worldIn.isAirBlock(new BlockPos(i, j, k)) || BlockHelper.isLiquid(worldIn.getBlockState(new BlockPos(i, j, k)))) && j > 1)
            {
                worldIn.setBlockState(new BlockPos(i, j, k), blockstateIn, 2);
                --j;
            }
        }
    }

    protected boolean generateChestContents(World worldIn, StructureBoundingBox boundingBoxIn, Random rand, int x, int y, int z, List<WeightedRandomChestContent> listIn, int max)
    {
        /*BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));

        if (boundingBoxIn.isVecInside(blockpos) && worldIn.getBlockState(blockpos).getId() != Block.CHEST)
        {
            Block iblockstate = Block.get(Block.CHEST);
            worldIn.setBlockState(blockpos, Blocks.chest.correctFacing(worldIn, blockpos, iblockstate), 2);
            TileEntity tileentity = worldIn.getTileEntity(blockpos);

            if (tileentity instanceof TileEntityChest)
            {
                WeightedRandomChestContent.generateChestContents(rand, listIn, (TileEntityChest)tileentity, max);
            }

            return true;
        }
        else
        {
            return false;
        }*/
        return false;
    }

    protected boolean generateDispenserContents(World worldIn, StructureBoundingBox boundingBoxIn, Random rand, int x, int y, int z, int meta, List<WeightedRandomChestContent> listIn, int max)
    {
        /*BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));

        if (boundingBoxIn.isVecInside(blockpos) && worldIn.getBlockState(blockpos).getBlock() != Blocks.dispenser)
        {
            worldIn.setBlockState(blockpos, Blocks.dispenser.getStateFromMeta(this.getMetadataWithOffset(Blocks.dispenser, meta)), 2);
            BlockEntity tileentity = worldIn.getTileEntity(blockpos);

            if (tileentity instanceof BlockEntityContainer)
            {
                WeightedRandomChestContent.generateDispenserContents(rand, listIn, (TileEntityDispenser)tileentity, max);
            }

            return true;
        }
        else
        {
            return false;
        }*/
        return false;
    }

    protected void placeDoorCurrentPosition(World worldIn, StructureBoundingBox boundingBoxIn, Random rand, int x, int y, int z, EnumFacing facing)
    {
        BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));

        if (boundingBoxIn.isVecInside(blockpos))
        {
            //ItemDoor.placeDoor(worldIn, blockpos, facing.rotateYCCW(), Blocks.oak_door);
        }
    }

    public void func_181138_a(int p_181138_1_, int p_181138_2_, int p_181138_3_)
    {
        this.boundingBox.offset(p_181138_1_, p_181138_2_, p_181138_3_);
    }

    public boolean isComponentProtected() {
        return true;
    }

    public abstract static class BlockSelector
        {
            protected Block blockstate = Block.get(Block.AIR);

            public abstract void selectBlocks(Random rand, int x, int y, int z, boolean p_75062_5_);

            public Block getBlockState()
            {
                return this.blockstate;
            }
        }
}