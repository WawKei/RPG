package nightmare.world.biome;

import cn.nukkit.entity.passive.EntityWolf;
import nightmare.world.gen.feature.GenLargeWinter;
import nightmare.world.gen.feature.WorldGenAbstractTree;
import nightmare.world.gen.feature.WorldGenTaiga1;
import nightmare.world.gen.feature.WorldGenTaiga2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BiomeGenSnow extends NMBiomeGenBase {


	private static final int MONSTER_SPAWN_RATE = 10;
	private Random monsterRNG = new Random(53439L);
	private ArrayList<SpawnListEntry> emptyList = new ArrayList<SpawnListEntry>();

	public BiomeGenSnow(int id) {
		super(id);

		getNMBiomeDecorator().setTreesPerChunk(7);
		getNMBiomeDecorator().setGrassPerChunk(1);

		getNMBiomeDecorator().hasCanopy = false;
		getNMBiomeDecorator().generateFalls = false;

		this.spawnableMonsterList.add(new SpawnListEntry(EntityWolf.class, 5, 1, 4));

	}

	@Override
	public WorldGenAbstractTree genBigTreeChance(Random random) {
		if (random.nextInt(3) == 0) {
			return new WorldGenTaiga1();
		} else if (random.nextInt(8) == 0) {
			return new GenLargeWinter();
		} else {
			return new WorldGenTaiga2(true);
		}
	}

	@Override
	public boolean getEnableSnow() {
		return true;
	}

	/*@Override
	public List<SpawnListEntry> getSpawnableList(EnumCreatureType creatureType) {
		if (creatureType == EnumCreatureType.MONSTER) {
			return monsterRNG.nextInt(MONSTER_SPAWN_RATE) == 0 ? this.spawnableMonsterList : emptyList;
		}
		return super.getSpawnableList(creatureType);
	}*/


}
