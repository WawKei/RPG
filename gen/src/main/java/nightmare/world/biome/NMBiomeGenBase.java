package nightmare.world.biome;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSand;
import cn.nukkit.block.BlockTallGrass;
import cn.nukkit.entity.mob.*;
import cn.nukkit.entity.passive.EntityBat;
import cn.nukkit.level.biome.Biome;
import jdk.internal.jline.internal.Nullable;
import nightmare.util.BlockPos;
import nightmare.world.World;
import nightmare.world.chunk.ChunkPrimer;
import nightmare.world.chunk.IChunkProvider;
import nightmare.world.gen.feature.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NMBiomeGenBase extends BiomeGenBase {

	protected final WorldGenBigMushroom bigMushroomGen;
	protected final List<SpawnListEntry> undergroundMonsterList;

	public NMBiomeGenBase(int id) {
		super(id);

		this.theBiomeDecorator = new NMBiomeDecorator();
		bigMushroomGen = new WorldGenBigMushroom();

		// remove normal monster spawns
		spawnableMonsterList.clear();
		// remove squids
		spawnableWaterCreatureList.clear();
		// custom creature list.
		spawnableCreatureList.clear();

		undergroundMonsterList = new ArrayList<SpawnListEntry>();

		undergroundMonsterList.add(new SpawnListEntry(EntitySpider.class, 10, 4, 4));
		undergroundMonsterList.add(new SpawnListEntry(EntityZombie.class, 10, 4, 4));
		undergroundMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
		undergroundMonsterList.add(new SpawnListEntry(EntityCreeper.class, 1, 4, 4));
		undergroundMonsterList.add(new SpawnListEntry(EntitySlime.class, 10, 4, 4));
		undergroundMonsterList.add(new SpawnListEntry(EntityEnderman.class, 1, 1, 4));

		this.spawnableCaveCreatureList.clear();
		this.spawnableCaveCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityBat.class, 10, 8, 8));

		getNMBiomeDecorator().setTreesPerChunk(10);
		getNMBiomeDecorator().setGrassPerChunk(2);
	}

	@Override
	public float getSpawningChance() {
		// okay, 20% more animals
		return 0.12F;
	}

	//@Override
	//public BiomeDecorator createBiomeDecorator() {
	//	return new BiomeDecorator();
	//}

	protected NMBiomeDecorator getNMBiomeDecorator() {
		return (NMBiomeDecorator) this.theBiomeDecorator;
	}

	public WorldGenAbstractTree getBigTreeChance(Random random) {
		if (random.nextInt(5) == 0) {
			//return birchGen;
		}
		if (random.nextInt(10) == 0) {
			return new WorldGenBigTree(false);
		} else {
			return super.genBigTreeChance(random);
		}
	}

	@Override
	public WorldGenerator getRandomWorldGenForGrass(Random random) {
		if (random.nextInt(4) == 0) {
			return new WorldGenTallGrass(Block.get(Block.TALL_GRASS));
		} else {
			return new WorldGenTallGrass(Block.get(Block.TALL_GRASS));
		}
	}

	@Override
	public void genTerrainBlocks(World world, Random rand, ChunkPrimer primer, int x, int z, double noiseVal) {
		this.genNMBiomeTerrain(world, rand, primer, x, z, noiseVal);
	}

	// Copy of super's generateBiomeTerrain, relevant edits noted.
	protected void genNMBiomeTerrain(World world, Random rand, ChunkPrimer primer, int x, int z, double noiseVal) {
		int i = world.getSeaLevel(); // TF - set sea level to 31
		Block iblockstate = this.topBlock;
		Block iblockstate1 = this.fillerBlock;
		Block stoneReplacement = getStoneReplacementState(); // TF - Replace stone
		int j = -1;
		int k = (int) (noiseVal / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
		int l = x & 15;
		int i1 = z & 15;
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		boolean generateBedrock = shouldGenerateBedrock(world);

		for (int j1 = 255; j1 >= 0; --j1) {
			// TF - conditional bedrock gen
			if (generateBedrock && j1 <= rand.nextInt(5)) {
				primer.setBlockState(i1, j1, l, Block.get(Block.BEDROCK));
			} else {
				Block iblockstate2 = primer.getBlockState(i1, j1, l);

				// TF - use block check for air
				if (iblockstate2.getId() == Block.AIR) {
					// j = -1; TF - commented out? todo 1.9
				} else if (iblockstate2.getId() == Block.STONE) {
					// TF - Replace stone
					if (stoneReplacement != null) {
						primer.setBlockState(i1, j1, l, stoneReplacement);
					}

					if (j == -1) {
						if (k <= 0) {
							iblockstate = Block.get(Block.AIR);
							iblockstate1 = Block.get(Block.STONE);
						} else if (j1 >= i - 4 && j1 <= i + 1) {
							iblockstate = this.topBlock;
							iblockstate1 = this.fillerBlock;
						}

						// TF - use block check for air
						if (j1 < i && (iblockstate == null || iblockstate.getId() == Block.AIR)) {
							if (this.getFloatTemperature(blockpos$mutableblockpos.set(x, j1, z)) < 0.15F) {
								iblockstate = Block.get(Block.ICE);
							} else {
								iblockstate = Block.get(Block.WATER);
							}
						}

						j = k;

						if (j1 >= i - 1) {
							primer.setBlockState(i1, j1, l, iblockstate);
						} else if (j1 < i - 7 - k) {
							iblockstate = Block.get(Block.AIR);
							iblockstate1 = Block.get(Block.STONE);
							primer.setBlockState(i1, j1, l, Block.get(Block.GRAVEL));
						} else {
							primer.setBlockState(i1, j1, l, iblockstate1);
						}
					} else if (j > 0) {
						--j;
						primer.setBlockState(i1, j1, l, iblockstate1);

						if (j == 0 && iblockstate1.getId() == Block.SAND) {
							j = rand.nextInt(4) + Math.max(0, j1 - 63);
							iblockstate1 = iblockstate1.getDamage() == BlockSand.RED ? Block.get(Block.RED_SANDSTONE) : Block.get(Block.SANDSTONE);
						}
					}
				}
			}
		}
	}

	private static boolean shouldGenerateBedrock(World world) {
		IChunkProvider provider = world.getChunkProvider();
		/*if (provider instanceof ChunkProviderServer) {
			IChunkGenerator generator = provider.chunkGenerator;
			if (generator instanceof ChunkGeneratorNMBase) {
				return ((ChunkGeneratorNMBase) generator).shouldGenerateBedrock();
			}
		}*/
		return true;
	}

	@Nullable
	public Block getStoneReplacementState() {
		return null;
	}

	public List<SpawnListEntry> getUndergroundSpawnableList() {
		return this.undergroundMonsterList;
	}
}
