package nightmare.world.gen.layer;

import nightmare.world.biome.NMBiomes;

public class GenLayerNMCompanionBiomes extends GenLayer {

	public GenLayerNMCompanionBiomes(long l, GenLayer genlayer) {
		super(l);
		parent = genlayer;
	}

	@Override
	public int[] getInts(int x, int z, int width, int depth) {

		int nx = x - 1;
		int nz = z - 1;
		int nwidth = width + 2;
		int ndepth = depth + 2;
		int input[] = parent.getInts(nx, nz, nwidth, ndepth);
		int output[] = IntCache.getIntCache(width * depth);

		int fireSwamp        = NMBiomes.town.biomeID;
		int swamp            = NMBiomes.town.biomeID;
		int glacier          = NMBiomes.town.biomeID;
		int snowyForest      = NMBiomes.snow.biomeID;
		int darkForestCenter = NMBiomes.village.biomeID;
		int darkForest       = NMBiomes.village.biomeID;
		int highlandsCenter  = NMBiomes.village.biomeID;
		int highlands        = NMBiomes.village.biomeID;

		for (int dz = 0; dz < depth; dz++) {
			for (int dx = 0; dx < width; dx++) {

				int right  = input[dx + 0 + (dz + 1) * nwidth];
				int left   = input[dx + 2 + (dz + 1) * nwidth];
				int up     = input[dx + 1 + (dz + 0) * nwidth];
				int down   = input[dx + 1 + (dz + 2) * nwidth];
				int center = input[dx + 1 + (dz + 1) * nwidth];

				if (isKey(fireSwamp, center, right, left, up, down)) {
					output[dx + dz * width] = swamp;
				} else if (isKey(glacier, center, right, left, up, down)) {
					output[dx + dz * width] = snowyForest;
				} else if (isKey(darkForestCenter, center, right, left, up, down)) {
					output[dx + dz * width] = darkForest;
				} else if (isKey(highlandsCenter, center, right, left, up, down)) {
					output[dx + dz * width] = highlands;
				} else {
					output[dx + dz * width] = center;
				}
			}
		}

		return output;
	}

	boolean isKey(int biome, int center, int right, int left, int up, int down) {

		return center != biome && (right == biome || left == biome || up == biome || down == biome);
	}
}
