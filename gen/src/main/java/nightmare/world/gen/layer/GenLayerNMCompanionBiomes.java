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

		int town        = NMBiomes.town.biomeID;
		int forest        = NMBiomes.forest.biomeID;
		int village = NMBiomes.village.biomeID;
		int snow = NMBiomes.snow.biomeID;

		for (int dz = 0; dz < depth; dz++) {
			for (int dx = 0; dx < width; dx++) {

				int right  = input[dx + 0 + (dz + 1) * nwidth];
				int left   = input[dx + 2 + (dz + 1) * nwidth];
				int up     = input[dx + 1 + (dz + 0) * nwidth];
				int down   = input[dx + 1 + (dz + 2) * nwidth];
				int center = input[dx + 1 + (dz + 1) * nwidth];

				if (isKey(town, center, right, left, up, down)) {
					output[dx + dz * width] = forest;
				} else if (isKey(village, center, right, left, up, down)) {
					output[dx + dz * width] = snow;
				}
			}
		}

		return output;
	}

	boolean isKey(int biome, int center, int right, int left, int up, int down) {

		return center != biome && (right == biome || left == biome || up == biome || down == biome);
	}
}
