package nightmare.world.biome;

public class NMBiomes {

	protected static final BiomeGenBase.Height height_Lake = new BiomeGenBase.Height(-1.8F, 0.1F);
	protected static final BiomeGenBase.Height height_Town = new BiomeGenBase.Height(0.125F, 0.05F);
	protected static final BiomeGenBase.Height height_Village = new BiomeGenBase.Height(0.125F, 0.05F);
	protected static final BiomeGenBase.Height height_Stream = new BiomeGenBase.Height(-0.5F, 0F);
	protected static final BiomeGenBase.Height height_Snow = new BiomeGenBase.Height(0.2F, 0.2F);

	public static final BiomeGenBase lake;
	public static final BiomeGenBase town;
	public static final BiomeGenBase village;
	public static final BiomeGenBase stream;
	public static final BiomeGenBase forest;
	public static final BiomeGenBase snow;

	static {
		/*(new BiomeGenVillage(3)).setBiomeName("Village").setHeight(height_Village);
		(new BiomeGenVillage(5)).setBiomeName("Village").setHeight(height_Village);
		(new BiomeGenVillage(6)).setBiomeName("Village").setHeight(height_Village);
		(new BiomeGenVillage(8)).setBiomeName("Village").setHeight(height_Village);
		(new BiomeGenVillage(9)).setBiomeName("Village").setHeight(height_Village);
		(new BiomeGenVillage(10)).setBiomeName("Village").setHeight(height_Village);
		(new BiomeGenVillage(11)).setBiomeName("Village").setHeight(height_Village);*/
		lake = (new BiomeGenLake(0)).setBiomeName("Lake").setTemperatureRainfall(0.66F, 1).setHeight(height_Lake);
		town = (new BiomeGenTown(1)).setBiomeName("Town").setHeight(height_Town);
		village = (new BiomeGenVillage(2)).setBiomeName("Village").setHeight(height_Village);
		forest = (new BiomeGenForest(4, 0)).setBiomeName("Forest");
		stream = (new BiomeGenStream(7)).setBiomeName("Stream").setHeight(height_Stream).setTemperatureRainfall(0.5F, 0.1F);
		snow = (new BiomeGenSnow(12)).setBiomeName("Snow Forest").setTemperatureRainfall(0.09F, 0.9F).setHeight(height_Snow);
	}
}
