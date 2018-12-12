package nightmare.world.gen.structure;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityBat;
import com.google.common.collect.Lists;
import jdk.internal.jline.internal.Nullable;
import nightmare.nbt.NBTTagList;
import nightmare.nbt.NBTTagString;
import nightmare.util.BlockPos;
import nightmare.util.ResourceLocation;
import nightmare.world.World;
import nightmare.world.biome.BiomeGenBase;
import nightmare.world.biome.NMBiomes;
import nightmare.world.gen.MapGenBase;
import nightmare.world.gen.MapGenNMMajorFeature;
import nightmare.world.gen.structure.start.StructureStartTownBuilding;
import nightmare.world.gen.structure.townbuilding.TownBuildingPieces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Arbiting class that decides what feature goes where in the world, in terms of the major features in the world
 */
public enum NMFeature {
	NOTHING    ( 0, "no_feature"       , false) { { this.enableDecorations().disableStructure(); } },
	TOWN_BUILDING ( 2, "town_building", true ) {
		{
			TownBuildingPieces.registerPieces();
		}

		@Override
		public StructureStart provideStructureStart(World world, Random rand, int chunkX, int chunkZ) {
			return new StructureStartTownBuilding(world, this, rand, chunkX, chunkZ);
		}
	};

	public int size;
	public String name;
	private final boolean shouldHaveFeatureGenerator;
	public boolean areChunkDecorationsEnabled;
	public boolean isStructureEnabled;
	public boolean isTerrainAltered;
	private List<List<BiomeGenBase.SpawnListEntry>> spawnableMonsterLists;
	private List<BiomeGenBase.SpawnListEntry> ambientCreatureList;
	private List<BiomeGenBase.SpawnListEntry> waterCreatureList;
	private final ResourceLocation[] requiredAdvancements;
	public boolean hasProtectionAura;

	private MapGenNMMajorFeature featureGenerator;

	private long lastSpawnedHintMonsterTime;

	private static final String BOOK_AUTHOR = "A Forgotten Explorer";

	private static final int maxSize = Arrays.stream(values()).mapToInt(v -> v.size).max().orElse(0);

	private static class NoU {
		private static final MapGenNMMajorFeature NOTHING_GENERATOR = new MapGenNMMajorFeature( NOTHING );
	}

	NMFeature(int parSize, String parName, boolean featureGenerator, ResourceLocation... requiredAdvancements) {
		this.size = parSize;
		this.name = parName;
		this.areChunkDecorationsEnabled = false;
		this.isStructureEnabled = true;
		this.isTerrainAltered = false;
		this.spawnableMonsterLists = new ArrayList<>();
		this.ambientCreatureList = new ArrayList<>();
		this.waterCreatureList = new ArrayList<>();
		this.hasProtectionAura = true;

		ambientCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityBat.class, 10, 8, 8));

		this.requiredAdvancements = requiredAdvancements;

		shouldHaveFeatureGenerator = featureGenerator;
	}

	public static int getMaxSize() {
		return maxSize;
	}

	public MapGenNMMajorFeature getFeatureGenerator() {
		return this.shouldHaveFeatureGenerator ? this.featureGenerator == null ? (this.featureGenerator = new MapGenNMMajorFeature(this)) : this.featureGenerator : NoU.NOTHING_GENERATOR;
	}

	public static NMFeature getFeatureByName(String name) {
		for (NMFeature feature : NMFeature.values()) {
			if (feature != null && feature.name.equalsIgnoreCase(name))
				return feature;
		}
		return NOTHING;
	}

	public static NMFeature getFeatureByID(int id) {
		return id < NMFeature.values().length ? NMFeature.values()[id] : NOTHING;
	}

	public static int getFeatureID(int mapX, int mapZ, World world) {
		return getFeatureAt(mapX, mapZ, world).ordinal();
	}

	public static NMFeature getFeatureAt(int mapX, int mapZ, World world) {
		return generateFeature(mapX >> 4, mapZ >> 4, world);
	}

	public static boolean isInFeatureChunk(World world, int mapX, int mapZ) {
		int chunkX = mapX >> 4;
		int chunkZ = mapZ >> 4;
		BlockPos cc = getNearestCenterXYZ(chunkX, chunkZ, world);

		return chunkX == (cc.getX() >> 4) && chunkZ == (cc.getZ() >> 4);
	}

	public NMFeature enableDecorations() {
		this.areChunkDecorationsEnabled = true;
		return this;
	}

	public NMFeature disableStructure() {
		this.isStructureEnabled = false;
		return this;
	}

	public NMFeature enableTerrainAlterations() {
		this.isTerrainAltered = true;
		return this;
	}

	public NMFeature disableProtectionAura() {
		this.hasProtectionAura = false;
		return this;
	}

	public NMFeature addMonster(Class<? extends Entity> monsterClass, int weight, int minGroup, int maxGroup) {
		this.addMonster(0, monsterClass, weight, minGroup, maxGroup);
		return this;
	}

	public NMFeature addMonster(int listIndex, Class<? extends Entity> monsterClass, int weight, int minGroup, int maxGroup) {
		List<BiomeGenBase.SpawnListEntry> monsterList;
		if (this.spawnableMonsterLists.size() > listIndex) {
			monsterList = this.spawnableMonsterLists.get(listIndex);
		} else {
			monsterList = new ArrayList<>();
			this.spawnableMonsterLists.add(listIndex, monsterList);
		}

		monsterList.add(new BiomeGenBase.SpawnListEntry(monsterClass, weight, minGroup, maxGroup));
		return this;
	}

	public NMFeature addWaterCreature(Class<? extends Entity> monsterClass, int weight, int minGroup, int maxGroup) {
		this.waterCreatureList.add(new BiomeGenBase.SpawnListEntry(monsterClass, weight, minGroup, maxGroup));
		return this;
	}

	public static NMFeature getFeatureDirectlyAt(int chunkX, int chunkZ, World world) {
		if (isInFeatureChunk(world, chunkX << 4, chunkZ << 4))
			return getFeatureAt(chunkX << 4, chunkZ << 4, world);

		return NOTHING;
	}

	@SuppressWarnings("ConstantConditions")
	public static NMFeature generateFeature(int chunkX, int chunkZ, World world) {
		chunkX = Math.round(chunkX / 16F) * 16;
		chunkZ = Math.round(chunkZ / 16F) * 16;

		BiomeGenBase biomeAt = world.getBiome(new BlockPos((chunkX << 4) + 8, 0, (chunkZ << 4) + 8));

		if (biomeAt == NMBiomes.town) {
			return TOWN_BUILDING;
		}
		return NOTHING;
	}

	public static NMFeature getNearestFeature(int cx, int cz, World world) {
		for (int rad = 1; rad <= maxSize; rad++) {
			for (int x = -rad; x <= rad; x++) {
				for (int z = -rad; z <= rad; z++) {
					NMFeature directlyAt = getFeatureDirectlyAt(x + cx, z + cz, world);
					if (directlyAt.size == rad) {
						return directlyAt;
					}
				}
			}
		}

		return NOTHING;
	}

	@Nullable
	public static BlockPos findNearestFeaturePosBySpacing(World worldIn, NMFeature feature, BlockPos blockPos, int p_191069_3_, int p_191069_4_, int p_191069_5_, boolean p_191069_6_, int p_191069_7_, boolean findUnexplored) {
		int i = blockPos.getX() >> 4;
		int j = blockPos.getZ() >> 4;
		int k = 0;

		for (Random random = new Random(); k <= p_191069_7_; ++k) {
			for (int l = -k; l <= k; ++l) {
				boolean flag = l == -k || l == k;

				for (int i1 = -k; i1 <= k; ++i1) {
					boolean flag1 = i1 == -k || i1 == k;

					if (flag || flag1) {
						int j1 = i + p_191069_3_ * l;
						int k1 = j + p_191069_3_ * i1;

						if (j1 < 0) {
							j1 -= p_191069_3_ - 1;
						}

						if (k1 < 0) {
							k1 -= p_191069_3_ - 1;
						}

						int l1 = j1 / p_191069_3_;
						int i2 = k1 / p_191069_3_;
						Random random1 = worldIn.setRandomSeed(l1, i2, p_191069_5_);
						l1 = l1 * p_191069_3_;
						i2 = i2 * p_191069_3_;

						if (p_191069_6_) {
							l1 = l1 + (random1.nextInt(p_191069_3_ - p_191069_4_) + random1.nextInt(p_191069_3_ - p_191069_4_)) / 2;
							i2 = i2 + (random1.nextInt(p_191069_3_ - p_191069_4_) + random1.nextInt(p_191069_3_ - p_191069_4_)) / 2;
						} else {
							l1 = l1 + random1.nextInt(p_191069_3_ - p_191069_4_);
							i2 = i2 + random1.nextInt(p_191069_3_ - p_191069_4_);
						}

						//MapGenBase.(worldIn.getSeed(), random, l1, i2);//setSeed
						random.nextInt();

						// Check changed for TFFeature
						if (NMFeature.getFeatureAt(l1 << 4, i2 << 4, worldIn) == feature) {
							if (!findUnexplored) {
								return new BlockPos((l1 << 4) + 8, 64, (i2 << 4) + 8);
							}
						} else if (k == 0) {
							break;
						}
					}
				}

				if (k == 0) {
					break;
				}
			}
		}

		return null;
	}

	public static NMFeature getFeatureForRegion(int chunkX, int chunkZ, World world) {
		//just round to the nearest multiple of 16 chunks?
		int featureX = Math.round(chunkX / 16F) * 16;
		int featureZ = Math.round(chunkZ / 16F) * 16;

		return NMFeature.generateFeature(featureX, featureZ, world);
	}

	public static NMFeature getFeatureForRegionPos(int posX, int posZ, World world) {
		//just round to the nearest multiple of 16 chunks?
		int featureX = Math.round((posX >> 4) / 16F) * 16;
		int featureZ = Math.round((posZ >> 4) / 16F) * 16;

		return NMFeature.generateFeature(featureX, featureZ, world);
	}

	public static int[] getNearestCenter(int cx, int cz, World world) {
		for (int rad = 1; rad <= maxSize; rad++) {
			for (int x = -rad; x <= rad; x++) {
				for (int z = -rad; z <= rad; z++) {
					if (getFeatureDirectlyAt(x + cx, z + cz, world).size == rad) {
						return new int[]{x * 16 + 8, z * 16 + 8};
					}
				}
			}
		}
		int[] no = {0, 0};
		return no;
	}

	public static BlockPos getNearestCenterXYZ(int cx, int cz, World world) {
		// generate random number for the whole biome area
		int regionX = (cx + 8) >> 4;
		int regionZ = (cz + 8) >> 4;

		long seed = (long) (regionX * 3129871) ^ (long) regionZ * 116129781L;
		seed = seed * seed * 42317861L + seed * 7L;

		int num0 = (int) (seed >> 12 & 3L);
		int num1 = (int) (seed >> 15 & 3L);
		int num2 = (int) (seed >> 18 & 3L);
		int num3 = (int) (seed >> 21 & 3L);

		// slightly randomize center of biome (+/- 3)
		int centerX = 8 + num0 - num1;
		int centerZ = 8 + num2 - num3;

		// centers are offset strangely depending on +/-
		int ccz;
		if (regionZ >= 0) {
			ccz = (regionZ * 16 + centerZ - 8) * 16 + 8;
		} else {
			ccz = (regionZ * 16 + (16 - centerZ) - 8) * 16 + 9;
		}

		int ccx;
		if (regionX >= 0) {
			ccx = (regionX * 16 + centerX - 8) * 16 + 8;
		} else {
			ccx = (regionX * 16 + (16 - centerX) - 8) * 16 + 9;
		}

		return new BlockPos(ccx, world.getSeaLevel(), ccz);//  Math.abs(chunkX % 16) == centerX && Math.abs(chunkZ % 16) == centerZ;
	}

	/*public List<BiomeGenBase.SpawnListEntry> getSpawnableList(EnumCreatureType creatureType) {
		switch (creatureType) {
			case MONSTER:
				return this.getSpawnableList(EnumCreatureType.MONSTER, 0);
			case AMBIENT:
				return this.ambientCreatureList;
			case WATER_CREATURE:
				return this.waterCreatureList;
			default:
				return Lists.newArrayList();
		}
	}*/

	/*public List<SpawnListEntry> getSpawnableList(EnumCreatureType creatureType, int index) {
		if (creatureType != EnumCreatureType.MONSTER) {
			return getSpawnableList(creatureType);
		}
		if (index >= 0 && index < this.spawnableMonsterLists.size()) {
			return this.spawnableMonsterLists.get(index);
		}
		return Lists.newArrayList();
	}*/

	public StructureStart provideStructureStart(World world, Random rand, int chunkX, int chunkZ) {
		return null;//new StructureStart(world, rand, chunkX, chunkZ);
	}
}
