package nightmare.world.gen.layer;


import nightmare.world.biome.BiomeGenBase;
import nightmare.world.biome.NMBiomes;

public class GenLayerNMRiverMix extends GenLayer {

	private GenLayer biomeLayer;
	private GenLayer riverLayer;

	public GenLayerNMRiverMix(long seed, GenLayer biomeLayer, GenLayer riverLayer) {
		super(seed);
		this.biomeLayer = biomeLayer;
		this.riverLayer = riverLayer;
	}

	@Override
	public void initWorldGenSeed(long seed) {
		this.biomeLayer.initWorldGenSeed(seed);
		this.riverLayer.initWorldGenSeed(seed);
		super.initWorldGenSeed(seed);
	}

	@Override
	public int[] getInts(int x, int z, int width, int depth) {

		int[] biomeInputs = this.biomeLayer.getInts(x, z, width, depth);
		int[] riverInputs = this.riverLayer.getInts(x, z, width, depth);
		int[] outputs = IntCache.getIntCache(width * depth);

		int stream = NMBiomes.stream.biomeID;

		for (int i = 0; i < width * depth; ++i) {
			if (riverInputs[i] == stream) {
				outputs[i] = riverInputs[i] & 255;
			} else {
				outputs[i] = biomeInputs[i];
			}
		}

		return outputs;
	}
}
