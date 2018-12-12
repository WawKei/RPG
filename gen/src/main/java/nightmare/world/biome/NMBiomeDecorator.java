package nightmare.world.biome;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockStone;
import nightmare.util.BlockPos;
import nightmare.util.WeightedRandom;
import nightmare.world.World;
import nightmare.world.gen.ChunkProviderSettings;
import nightmare.world.gen.feature.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NMBiomeDecorator extends BiomeDecorator {

	private WorldGenLakes extraLakeGen = new WorldGenLakes(Block.get(Block.WATER));
	private WorldGenLakes extraLavaPoolGen = new WorldGenLakes(Block.get(Block.LAVA));

	private WorldGenLiquids caveWaterGen = new WorldGenLiquids(Block.get(Block.STILL_WATER));

	public float canopyPerChunk = 1.7F;
	public boolean hasCanopy = true;
	public float alternateCanopyChance = 0;
	public int myceliumPerChunk = 0;
	public int mangrovesPerChunk = 0;
	public int lakesPerChunk = 0;
	public float lavaPoolChance = 0;

	public boolean generateFalls = true;

	private static final List<RuinEntry> ruinList = new ArrayList<>();

	static {
		//ruinList.add(new RuinEntry(new GenDruidHut()           , 10     ));
	}

	private static class RuinEntry extends WeightedRandom.Item {
		public final WorldGenerator generator;

		public RuinEntry(WorldGenerator generator, int weight) {
			super(weight);
			this.generator = generator;
		}
	}

	@Override
	public void decorate(World world, Random rand, BiomeGenBase biome, BlockPos pos) {

		// check for features
		/*TFFeature nearFeature = TFFeature.getNearestFeature(pos.getX() >> 4, pos.getZ() >> 4, world);

		if (!nearFeature.areChunkDecorationsEnabled) {
			// no normal decorations here, these parts supply their own decorations.
			decorateUnderground(world, rand, pos);
			decorateOnlyOres(world, rand, pos);
		} else {
//    		// hollow trees!
//	    	if (rand.nextInt(24) == 0) {
//		        int rx = mapX + rand.nextInt(16) + 8;
//		        int rz = mapZ + rand.nextInt(16) + 8;
//		        int ry = world.getHeightValue(rx, rz);
//	    		hollowTreeGen.generate(world, rand, rx, ry, rz);
//	    	}*/

			// regular decorations
			super.decorate(world, rand, biome, pos);
		//}
	}

	@Override
	protected void genDecorations(BiomeGenBase biome, World world, Random randomGenerator) {
		// random features!
		/*if (randomGenerator.nextInt(6) == 0) {
			int rx = field_180294_c.getX() + randomGenerator.nextInt(14) + 8;
			int rz = field_180294_c.getZ() + randomGenerator.nextInt(14) + 8;
			WorldGenerator rf = randomFeature(randomGenerator);
			rf.generate(world, randomGenerator, world.getHeight(new BlockPos(rx, 0, rz)));
		}*/

		for (int i = 0; i < lakesPerChunk; i++) {
			int rx = field_180294_c.getX() + randomGenerator.nextInt(16) + 8;
			int rz = field_180294_c.getZ() + randomGenerator.nextInt(16) + 8;
			extraLakeGen.generate(world, randomGenerator, world.getHeight(new BlockPos(rx, 0, rz)));
		}

		if (randomGenerator.nextFloat() <= lavaPoolChance) {
			int rx = field_180294_c.getX() + randomGenerator.nextInt(16) + 8;
			int rz = field_180294_c.getZ() + randomGenerator.nextInt(16) + 8;
			extraLavaPoolGen.generate(world, randomGenerator, world.getHeight(new BlockPos(rx, 0, rz)));
		}

		super.genDecorations(biome, world, randomGenerator);

		decorateUnderground(world, randomGenerator, field_180294_c);


	}

	/**
	 * Generate the Twilight Forest underground decorations
	 */
	protected void decorateUnderground(World world, Random rand, BlockPos pos) {

		// extra underground water sources
		if (this.generateFalls) {
			for (int i = 0; i < 50; ++i) {
				int rx = pos.getX() + rand.nextInt(16) + 8;
				int ry = rand.nextInt(24) + 4;
				int rz = pos.getZ() + rand.nextInt(16) + 8;
				caveWaterGen.generate(world, rand, new BlockPos(rx, ry, rz));
			}
		}
	}

	public void decorateOnlyOres(World world, Random rand, BlockPos pos) {
		this.field_180294_c = pos;
		if (this.chunkProviderSettings == null) {
			this.chunkProviderSettings = ChunkProviderSettings.Factory.jsonToFactory("").func_177864_b();
			this.field_180294_c = pos;
			this.dirtGen = new WorldGenMinable(Block.get(Block.DIRT), this.chunkProviderSettings.dirtSize);
			this.gravelGen = new WorldGenMinable(Block.get(Block.GRAVEL), this.chunkProviderSettings.gravelSize);
			this.graniteGen = new WorldGenMinable(Block.get(Block.STONE, BlockStone.GRANITE), this.chunkProviderSettings.graniteSize);
			this.dioriteGen = new WorldGenMinable(Block.get(Block.STONE, BlockStone.DIORITE), this.chunkProviderSettings.dioriteSize);
			this.andesiteGen = new WorldGenMinable(Block.get(Block.STONE, BlockStone.ANDESITE), this.chunkProviderSettings.andesiteSize);
			this.coalGen = new WorldGenMinable(Block.get(Block.COAL_ORE), this.chunkProviderSettings.coalSize);
			this.ironGen = new WorldGenMinable(Block.get(Block.IRON_ORE), this.chunkProviderSettings.ironSize);
			this.goldGen = new WorldGenMinable(Block.get(Block.GOLD_ORE), this.chunkProviderSettings.goldSize);
			this.redstoneGen = new WorldGenMinable(Block.get(Block.REDSTONE_ORE), this.chunkProviderSettings.redstoneSize);
			this.diamondGen = new WorldGenMinable(Block.get(Block.DIAMOND_ORE), this.chunkProviderSettings.diamondSize);
			this.lapisGen = new WorldGenMinable(Block.get(Block.LAPIS_ORE), this.chunkProviderSettings.lapisSize);
			
		}
		this.generateOres();
	}

	public WorldGenerator randomFeature(Random rand) {
		return WeightedRandom.getRandomItem(rand, ruinList).generator;
	}

	public void setTreesPerChunk(int treesPerChunk) {
		this.treesPerChunk = treesPerChunk;
	}

	public void setBigMushroomsPerChunk(int bigMushroomsPerChunk) {
		this.bigMushroomsPerChunk = bigMushroomsPerChunk;
	}

	public void setClayPerChunk(int clayPerChunk) {
		this.clayPerChunk = clayPerChunk;
	}

	public void setDeadBushPerChunk(int deadBushPerChunk) {
		this.deadBushPerChunk = deadBushPerChunk;
	}

	public void setMushroomsPerChunk(int mushroomsPerChunk) {
		this.mushroomsPerChunk = mushroomsPerChunk;
	}

	public void setFlowersPerChunk(int flowersPerChunk) {
		this.flowersPerChunk = flowersPerChunk;
	}

	public void setReedsPerChunk(int reedsPerChunk) {
		this.reedsPerChunk = reedsPerChunk;
	}

	public void setWaterlilyPerChunk(int waterlilyPerChunk) {
		this.waterlilyPerChunk = waterlilyPerChunk;
	}

	public void setGrassPerChunk(int grassPerChunk) {
		this.grassPerChunk = grassPerChunk;
	}


}
