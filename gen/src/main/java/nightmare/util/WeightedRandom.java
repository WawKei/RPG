package nightmare.util;

import cn.nukkit.item.enchantment.Enchantment;

import java.util.Collection;
import java.util.Random;

public class WeightedRandom
{
    public static int getTotalWeightEnchant(Collection<Enchantment> collection)
    {
        int i = 0;

        for (Enchantment weightedrandom$item : collection)
        {
            i += weightedrandom$item.getWeight();
        }

        return i;
    }

    public static Enchantment getRandomItemEnchant(Random random, Collection<Enchantment> collection, int totalWeight)
    {
        if (totalWeight <= 0)
        {
            throw new IllegalArgumentException();
        }
        else
        {
            int i = random.nextInt(totalWeight);
            return getRandomItemEnchant(collection, i);
        }
    }

    public static Enchantment getRandomItemEnchant(Collection<Enchantment> collection, int weight)
    {
        for (Enchantment t : collection)
        {
            weight -= t.getWeight();

            if (weight < 0)
            {
                return t;
            }
        }

        return (Enchantment)null;
    }

    public static Enchantment getRandomItemEnchant(Random random, Collection<Enchantment> collection)
    {
        return getRandomItemEnchant(random, collection, getTotalWeightEnchant(collection));
    }

    public static int getTotalWeight(Collection <? extends WeightedRandom.Item > collection)
    {
        int i = 0;

        for (WeightedRandom.Item weightedrandom$item : collection)
        {
            i += weightedrandom$item.itemWeight;
        }

        return i;
    }

    public static <T extends WeightedRandom.Item> T getRandomItem(Random random, Collection<T> collection, int totalWeight)
    {
        if (totalWeight <= 0)
        {
            throw new IllegalArgumentException();
        }
        else
        {
            int i = random.nextInt(totalWeight);
            return getRandomItem(collection, i);
        }
    }

    public static <T extends WeightedRandom.Item> T getRandomItem(Collection<T> collection, int weight)
    {
        for (T t : collection)
        {
            weight -= t.itemWeight;

            if (weight < 0)
            {
                return t;
            }
        }

        return (T)null;
    }

    public static <T extends WeightedRandom.Item> T getRandomItem(Random random, Collection<T> collection)
    {
        return getRandomItem(random, collection, getTotalWeight(collection));
    }

    public static class Item
    {
        public int itemWeight;

        public Item(int itemWeightIn)
        {
            this.itemWeight = itemWeightIn;
        }
    }
}