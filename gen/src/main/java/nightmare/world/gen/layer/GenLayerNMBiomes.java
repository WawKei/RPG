package nightmare.world.gen.layer;

import nightmare.world.biome.BiomeGenBase;
import nightmare.world.biome.NMBiomes;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Applies the twilight forest biomes to the map
 *
 * @author Ben
 */
public class GenLayerNMBiomes extends GenLayer {

	private static final int RARE_BIOME_CHANCE = 15;

	protected static final List<Supplier<BiomeGenBase>> commonBiomes = Arrays.asList(
			() -> NMBiomes.forest,
			() -> NMBiomes.snow
	);
	protected static final List<Supplier<BiomeGenBase>> rareBiomes = Arrays.asList(
			() -> NMBiomes.town,
			() -> NMBiomes.village
	);

	public GenLayerNMBiomes(long l, GenLayer genlayer) {
		super(l);
		parent = genlayer;
	}

	public GenLayerNMBiomes(long l) {
		super(l);
	}

	@Override
	public int[] getInts(int x, int z, int width, int depth) {

		int dest[] = IntCache.getIntCache(width * depth);

		for (int dz = 0; dz < depth; dz++) {
			for (int dx = 0; dx < width; dx++) {
				initChunkSeed(dx + x, dz + z);
				if (nextInt(RARE_BIOME_CHANCE) == 0) {
					// make rare biome
					dest[dx + dz * width] = getRandomBiome(rareBiomes).biomeID;
				} else {
					// make common biome
					dest[dx + dz * width] = getRandomBiome(commonBiomes).biomeID;
				}
			}
		}

//		for (int i = 0; i < width * depth; i++)
//		{
//			if (dest[i] < 0 || dest[i] > TFBiomeBase.fireSwamp.biomeID)
//			{
//				System.err.printf("Made a bad ID, %d at %d, %d while generating\n", dest[i], x, z);
//			}
//		}

		return dest;
	}

	private BiomeGenBase getRandomBiome(List<Supplier<BiomeGenBase>> biomes) {
		return biomes.get(nextInt(biomes.size())).get();
	}
}
