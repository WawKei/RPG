package nightmare.util;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nightmare.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BlockHelper implements Predicate<Block>
{
    private final Block block;

    private BlockHelper(Block blockType)
    {
        this.block = blockType;
    }

    public static BlockHelper forBlock(Block blockType)
    {
        return new BlockHelper(blockType);
    }

    public boolean apply(Block p_apply_1_)
    {
        return p_apply_1_ != null && p_apply_1_.getId() == this.block.getId() && p_apply_1_.getDamage() == this.block.getDamage();
    }

    public static Block correctFacing(Block t, World worldIn, BlockPos pos, Block state)
    {
        EnumFacing enumfacing = null;

        for (EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL)
        {
            Block iblockstate = worldIn.getBlockState(pos.offset(enumfacing1));

            if (iblockstate == t)
            {
                return state;
            }

            if (iblockstate.isSolid())
            {
                if (enumfacing != null)
                {
                    enumfacing = null;
                    break;
                }

                enumfacing = enumfacing1;
            }
        }

        if (enumfacing != null)
        {
            state.setDamage(enumfacing.
                    getOpposite().
                    getIndex());
            return state;
        }
        else
        {
            EnumFacing enumfacing2 = (EnumFacing)EnumFacing.SOUTH;

            if (worldIn.getBlockState(pos.offset(enumfacing2)).isSolid())
            {
                enumfacing2 = enumfacing2.getOpposite();
            }

            if (worldIn.getBlockState(pos.offset(enumfacing2)).isSolid())
            {
                enumfacing2 = enumfacing2.rotateY();
            }

            if (worldIn.getBlockState(pos.offset(enumfacing2)).isSolid())
            {
                enumfacing2 = enumfacing2.getOpposite();
            }

            state.setDamage(enumfacing2.getIndex());
            return state;
        }
    }

    public static boolean isWater(Block block){
        return block.getId() == Block.WATER || block.getId() == Block.STILL_WATER;
    }

    public static boolean isLiquid(Block block){
        return block.getId() == Block.WATER || block.getId() == Block.STILL_WATER || block.getId() == Block.LAVA || block.getId() == Block.STILL_LAVA;
    }

    public static boolean isLeave(Block block){
        return isLeave(block.getId());
    }

    public static boolean isLeave(int block){
        return block == Block.LEAVES || block == Block.LEAVES2;
    }

    public static boolean canBlockStayTallGrass(World worldIn, BlockPos pos, Block block){
        int id = worldIn.getBlockState(pos).getId();
        return id == Block.GRASS || id == Block.DIRT || id == Block.FARMLAND;
    }

    public static boolean canBePlacedSnowLayer(World worldIn, BlockPos pos){
        Block block = worldIn.getBlockState(pos.down());
        return (block.getId() != Block.ICE && block.getId() != Block.PACKED_ICE && block.isSolid()) || isLeave(block);
    }

    public static boolean canBlockStayFlower(World worldIn, BlockPos pos){
        Block block = worldIn.getBlockState(pos.down());
        return block.isSolid() && !isLeave(block);
    }

    public static boolean canBlockStayCactus(World worldIn, BlockPos pos)
    {
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
        {
            if (worldIn.getBlockState(pos.offset(enumfacing)).isSolid())
            {
                return false;
            }
        }

        Block block = worldIn.getBlockState(pos.down());
        return block.getId() == Block.CACTUS || block.getId() == Block.SAND;
    }

    public static boolean canBlockStayDeadBush(World worldIn, BlockPos pos){
        return canPlaceBlockOnDeadBush(worldIn.getBlockState(pos.down()));
    }

    public static boolean canPlaceBlockOnDeadBush(Block ground)
    {
        return ground.getId() == Block.SAND || ground.getId() == Block.CLAY_BLOCK || ground.getId() == Block.STAINED_HARDENED_CLAY || ground.getId() == Block.DIRT;
    }

    public static boolean canPlaceBlockAtReed(Block t, World worldIn, BlockPos pos)
    {
        Block block = worldIn.getBlockState(pos.down());

        if (block == t)
        {
            return true;
        }
        else if (block.getId() != Block.GRASS && block.getId() != Block.DIRT && block.getId() != Block.SAND)
        {
            return false;
        }
        else
        {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
            {
                if (isWater(worldIn.getBlockState(pos.offset(enumfacing).down())))
                {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean canBlockStayReed(Block t, World worldIn, BlockPos pos)
    {
        return canPlaceBlockAtReed(t, worldIn, pos);
    }

    public static boolean canPlaceBlockAtPumpkin(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).canBeReplaced() && worldIn.getBlockState(pos.down()).isSolid();
    }

    public static boolean canPlaceBlockOnSideVine(World worldIn, BlockPos pos, EnumFacing side)
    {
        switch (side)
        {
            case UP:
                return canPlaceOnVine(worldIn.getBlockState(pos.up()));
            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:
                return canPlaceOnVine(worldIn.getBlockState(pos.offset(side.getOpposite())));
            default:
                return false;
        }
    }

    private static boolean canPlaceOnVine(Block blockIn)
    {
        return blockIn.isSolid();
    }

    public static WeightedRandomChestContent getRandomEnchantedBook(Random rand)
    {
        return getRandomEnchantedBook(rand, 1, 1, 1);
    }

    public static boolean canBlockStayLily(World worldIn, BlockPos pos)
    {
        if (pos.getY() >= 0 && pos.getY() < 256)
        {
            Block iblockstate = worldIn.getBlockState(pos.down());
            return isWater(iblockstate) && iblockstate.getDamage() == 8;
        }
        else
        {
            return false;
        }
    }

    public static WeightedRandomChestContent getRandomEnchantedBook(Random rand, int minChance, int maxChance, int weight)
    {
        Item itemstack = new Item(Item.BOOK, 1, 0);
        addRandomEnchantment(rand, itemstack, 30);
        return new WeightedRandomChestContent(itemstack, minChance, maxChance, weight);
    }

    public static Item addRandomEnchantment(Random p_77504_0_, Item p_77504_1_, int p_77504_2_)
    {
        List<Enchantment> list = buildEnchantmentList(p_77504_0_, p_77504_1_, p_77504_2_);
        boolean flag = p_77504_1_.getId() == Item.BOOK;

        if (flag)
        {
            p_77504_1_ = Item.get(Item.ENCHANTED_BOOK);//TODO Fix
        }

        if (list != null)
        {
            for (Enchantment enchantmentdata : list)
            {
                if (flag)
                {
                    p_77504_1_.addEnchantment(enchantmentdata);
                }
                else
                {
                    p_77504_1_.addEnchantment(enchantmentdata);
                }
            }
        }

        return p_77504_1_;
    }

    public static List<Enchantment> buildEnchantmentList(Random randomIn, Item itemStackIn, int p_77513_2_)
    {
        Item item = itemStackIn;
        int i = item.getEnchantAbility();

        if (i <= 0)
        {
            return null;
        }
        else
        {
            i = i / 2;
            i = 1 + randomIn.nextInt((i >> 1) + 1) + randomIn.nextInt((i >> 1) + 1);
            int j = i + p_77513_2_;
            float f = (randomIn.nextFloat() + randomIn.nextFloat() - 1.0F) * 0.15F;
            int k = (int)((float)j * (1.0F + f) + 0.5F);

            if (k < 1)
            {
                k = 1;
            }

            List<Enchantment> list = null;
            Map<Integer, Enchantment> map = mapEnchantmentData(k, itemStackIn);

            if (map != null && !map.isEmpty())
            {
                Enchantment enchantmentdata = (Enchantment)WeightedRandom.getRandomItemEnchant(randomIn, map.values());

                if (enchantmentdata != null)
                {
                    list = Lists.<Enchantment>newArrayList();
                    list.add(enchantmentdata);

                    for (int l = k; randomIn.nextInt(50) <= l; l >>= 1)
                    {
                        Iterator<Integer> iterator = map.keySet().iterator();

                        while (iterator.hasNext())
                        {
                            Integer integer = (Integer)iterator.next();
                            boolean flag = true;

                            for (Enchantment enchantmentdata1 : list)
                            {
                                if (!enchantmentdata1.isCompatibleWith(Enchantment.getEnchantment(integer.intValue())))
                                {
                                    flag = false;
                                    break;
                                }
                            }

                            if (!flag)
                            {
                                iterator.remove();
                            }
                        }

                        if (!map.isEmpty())
                        {
                            Enchantment enchantmentdata2 = (Enchantment)WeightedRandom.getRandomItemEnchant(randomIn, map.values());
                            list.add(enchantmentdata2);
                        }
                    }
                }
            }

            return list;
        }
    }

    public static Map<Integer, Enchantment> mapEnchantmentData(int p_77505_0_, Item p_77505_1_)
    {
        Item item = p_77505_1_;
        Map<Integer, Enchantment> map = null;
        boolean flag = p_77505_1_.getId() == Item.BOOK;

        for (Enchantment enchantment : Enchantment.getEnchantments())
        {
            if (enchantment != null && (enchantment.type.canEnchantItem(item) || flag))
            {
                for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); ++i)
                {
                    if (p_77505_0_ >= enchantment.getMinEnchantAbility(i) && p_77505_0_ <= enchantment.getMaxEnchantAbility(i))
                    {
                        if (map == null)
                        {
                            map = Maps.<Integer, Enchantment>newHashMap();
                        }

                        map.put(Integer.valueOf(enchantment.id), Enchantment.getEnchantment(enchantment.getId())).setLevel(i);
                    }
                }
            }
        }

        return map;
    }
}
