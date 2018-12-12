package nightmare.world.biome;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDirt;
import cn.nukkit.entity.mob.EntityCreeper;
import cn.nukkit.entity.mob.EntitySkeleton;
import cn.nukkit.entity.mob.EntitySlime;
import nightmare.util.BlockPos;
import nightmare.world.World;
import nightmare.world.chunk.ChunkPrimer;
import nightmare.world.gen.feature.*;

import java.util.Random;


public class BiomeGenHighlands extends NMBiomeGenBase {
	private static final WorldGenTaiga1 taigaGen1 = new WorldGenTaiga1();
	private static final WorldGenTaiga2 taigaGen2 = new WorldGenTaiga2(false);
	private static final WorldGenMegaPineTree megaPineGen1 = new WorldGenMegaPineTree(false, false);
	private static final WorldGenMegaPineTree megaPineGen2 = new WorldGenMegaPineTree(false, true);
	private static final WorldGenBlockBlob genBoulder = new WorldGenBlockBlob(Block.get(Block.MOSSY_STONE), 0);


	public BiomeGenHighlands(int id) {
		super(id);

		//theBiomeDecorator.hasCanopy = false;

		this.theBiomeDecorator.grassPerChunk = 7;
		this.theBiomeDecorator.deadBushPerChunk = 1;
		//this.theBiomeDecorator.generateFalls = false;

		undergroundMonsterList.clear();
		undergroundMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
		undergroundMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityCreeper.class, 1, 4, 4));
		undergroundMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySlime.class, 10, 4, 4));
	}

	@Override
	public void genTerrainBlocks(World world, Random rand, ChunkPrimer primer, int x, int z, double noiseVal) {
		this.topBlock = Block.get(Block.GRASS);
		this.fillerBlock = Block.get(Block.DIRT);

		if (noiseVal > 1.75D) {
			this.topBlock = Block.get(Block.DIRT, 1);
		} else if (noiseVal > -0.95D) {
			this.topBlock = Block.get(Block.PODZOL);
		}

		this.genNMBiomeTerrain(world, rand, primer, x, z, noiseVal);
	}

	@Override
	public void decorate(World world, Random rand, BlockPos pos) {
		int dx, dy, dz;

		// boulders
		int maxBoulders = rand.nextInt(2);
		for (int i = 0; i < maxBoulders; ++i) {
			dx = pos.getX() + rand.nextInt(16) + 8;
			dz = pos.getZ() + rand.nextInt(16) + 8;
			genBoulder.generate(world, rand, world.getHeight(new BlockPos(dx, 0, dz)));
		}

		// giant ferns
		//DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.FERN);
		for (int i = 0; i < 7; ++i) {
			dx = pos.getX() + rand.nextInt(16) + 8;
			dz = pos.getZ() + rand.nextInt(16) + 8;
			dy = rand.nextInt(world.getHeight(new BlockPos(dx, 0, dz)).getY() + 32);
			DOUBLE_PLANT_GENERATOR.generate(world, rand, new BlockPos(dx, dy, dz));
		}

		super.decorate(world, rand, pos);
	}
}
