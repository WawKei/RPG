package nightmare.world.biome;

public class BiomeGenMushrooms extends NMBiomeGenBase {

	public BiomeGenMushrooms(int id) {
		super(id);

		getNMBiomeDecorator().setTreesPerChunk(8);
		getNMBiomeDecorator().setMushroomsPerChunk(8);
		getNMBiomeDecorator().setBigMushroomsPerChunk(2);
		getNMBiomeDecorator().alternateCanopyChance = 0.2F;
	}

}
