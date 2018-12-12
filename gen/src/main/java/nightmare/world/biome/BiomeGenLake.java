package nightmare.world.biome;

import cn.nukkit.entity.passive.EntitySquid;

public class BiomeGenLake extends NMBiomeGenBase {

	public BiomeGenLake(int id) {
		super(id);

		this.spawnableWaterCreatureList.add(new SpawnListEntry(EntitySquid.class, 10, 4, 4));
	}

}
