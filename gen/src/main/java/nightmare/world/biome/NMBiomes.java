package nightmare.world.biome;

public class NMBiomes {

	protected static final BiomeGenBase.Height height_Town = new BiomeGenBase.Height(0.125F, 0.05F);
	protected static final BiomeGenBase.Height height_Village = new BiomeGenBase.Height(0.125F, 0.05F);
	protected static final BiomeGenBase.Height height_Stream = new BiomeGenBase.Height(-0.5F, 0F);
	protected static final BiomeGenBase.Height height_Snow = new BiomeGenBase.Height(0.2F, 0.2F);

	public static final BiomeGenBase town;
	public static final BiomeGenBase village;
	public static final BiomeGenBase stream;
	public static final BiomeGenBase forest;
	public static final BiomeGenBase snow;

	static {
		town = (new BiomeGenTown(8)).setBiomeName("Town").setHeight(height_Town);
		village = (new BiomeGenVillage(9)).setBiomeName("Village").setHeight(height_Village);
		stream = (new BiomeGenStream(7)).setBiomeName("Stream").setHeight(height_Stream).setTemperatureRainfall(0.5F, 0.1F);
		forest = (new BiomeGenForest(4, 0)).setBiomeName("Forest");
		snow = (new BiomeGenSnow(12)).setBiomeName("Snow Forest").setTemperatureRainfall(0.09F, 0.9F).setHeight(height_Snow);
	}
}
