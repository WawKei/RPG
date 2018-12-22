package nightmare.world.gen.layer;

import nightmare.world.biome.NMBiomes;

public class GenLayerNMThornBorder extends GenLayer {

	public GenLayerNMThornBorder(long l, GenLayer genlayer) {
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

		int highlandsCenter = NMBiomes.town.biomeID;
		int thornlands      = NMBiomes.town.biomeID;

		for (int dz = 0; dz < depth; dz++) {
			for (int dx = 0; dx < width; dx++) {

				int right  = input[dx + 0 + (dz + 1) * nwidth];
				int left   = input[dx + 2 + (dz + 1) * nwidth];
				int up     = input[dx + 1 + (dz + 0) * nwidth];
				int down   = input[dx + 1 + (dz + 2) * nwidth];
				int center = input[dx + 1 + (dz + 1) * nwidth];

				int ur = input[dx + 0 + (dz + 0) * nwidth];
				int ul = input[dx + 2 + (dz + 0) * nwidth];
				int dr = input[dx + 0 + (dz + 2) * nwidth];
				int dl = input[dx + 2 + (dz + 2) * nwidth];

				if (onBorder(highlandsCenter, center, right, left, up, down)) {
					output[dx + dz * width] = thornlands;
				} else if (onBorder(highlandsCenter, center, ur, ul, dr, dl)) {
					output[dx + dz * width] = thornlands;
				} else {
					output[dx + dz * width] = center;
				}
			}
		}

		return output;
	}

	private boolean onBorder(int biomeID, int biomeID2, int center, int right, int left, int up, int down) {

		if (center != biomeID) {
			return false;
		}
		if (right == biomeID2) {
			return true;
		}
		if (left == biomeID2) {
			return true;
		}
		if (up == biomeID2) {
			return true;
		}
		if (down == biomeID2) {
			return true;
		}

		return false;
	}

	private boolean onBorder(int biomeID, int center, int right, int left, int up, int down) {

		if (center == biomeID) {
			return false;
		} else if (right == biomeID) {
			return true;
		} else if (left == biomeID) {
			return true;
		} else if (up == biomeID) {
			return true;
		} else if (down == biomeID) {
			return true;
		} else {
			return false;
		}
	}
}
