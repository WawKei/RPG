package nightmare.util;

import cn.nukkit.blockentity.BlockEntityContainer;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WeightedRandomChestContent extends WeightedRandom.Item
{
    public Item theItemId;
    public int minStackSize;
    public int maxStackSize;

    public WeightedRandomChestContent(int p_i45311_1_, int p_i45311_2_, int minimumChance, int maximumChance, int itemWeightIn)
    {
        super(itemWeightIn);
        this.theItemId = Item.get(p_i45311_1_, p_i45311_2_, 1);
        this.minStackSize = minimumChance;
        this.maxStackSize = maximumChance;
    }

    public WeightedRandomChestContent(Item stack, int minimumChance, int maximumChance, int itemWeightIn)
    {
        super(itemWeightIn);
        this.theItemId = stack;
        this.minStackSize = minimumChance;
        this.maxStackSize = maximumChance;
    }

    public static void generateChestContents(Random random, List<WeightedRandomChestContent> listIn, Inventory inv, int max)
    {
        for (int i = 0; i < max; ++i)
        {
            WeightedRandomChestContent weightedrandomchestcontent = (WeightedRandomChestContent)WeightedRandom.getRandomItem(random, listIn);
            int j = weightedrandomchestcontent.minStackSize + random.nextInt(weightedrandomchestcontent.maxStackSize - weightedrandomchestcontent.minStackSize + 1);

            if (weightedrandomchestcontent.theItemId.getMaxStackSize() >= j)
            {
                Item itemstack1 = weightedrandomchestcontent.theItemId.clone();
                itemstack1.count = j;
                inv.setItem(random.nextInt(inv.getSize()), itemstack1);
            }
            else
            {
                for (int k = 0; k < j; ++k)
                {
                    Item itemstack = weightedrandomchestcontent.theItemId.clone();
                    itemstack.count = 1;
                    inv.setItem(random.nextInt(inv.getSize()), itemstack);
                }
            }
        }
    }

    public static void generateDispenserContents(Random random, List<WeightedRandomChestContent> listIn, BlockEntityContainer dispenser, int max)
    {
        for (int i = 0; i < max; ++i)
        {
            WeightedRandomChestContent weightedrandomchestcontent = (WeightedRandomChestContent)WeightedRandom.getRandomItem(random, listIn);
            int j = weightedrandomchestcontent.minStackSize + random.nextInt(weightedrandomchestcontent.maxStackSize - weightedrandomchestcontent.minStackSize + 1);

            if (weightedrandomchestcontent.theItemId.getMaxStackSize() >= j)
            {
                Item itemstack1 = weightedrandomchestcontent.theItemId.clone();
                itemstack1.count = j;
                dispenser.setItem(random.nextInt(dispenser.getSize()), itemstack1);
            }
            else
            {
                for (int k = 0; k < j; ++k)
                {
                    Item itemstack = weightedrandomchestcontent.theItemId.clone();
                    itemstack.count = 1;
                    dispenser.setItem(random.nextInt(dispenser.getSize()), itemstack);
                }
            }
        }
    }

    public static List<WeightedRandomChestContent> func_177629_a(List<WeightedRandomChestContent> p_177629_0_, WeightedRandomChestContent... p_177629_1_)
    {
        List<WeightedRandomChestContent> list = Lists.newArrayList(p_177629_0_);
        Collections.addAll(list, p_177629_1_);
        return list;
    }
}