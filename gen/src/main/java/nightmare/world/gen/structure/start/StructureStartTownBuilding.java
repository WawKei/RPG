package nightmare.world.gen.structure.start;

import nightmare.world.World;
import nightmare.world.gen.structure.NMFeature;
import nightmare.world.gen.structure.StructureComponent;
import nightmare.world.gen.structure.StructureStart;
import nightmare.world.gen.structure.townbuilding.ComponentTownBuildingMain;

import java.util.Random;

public class StructureStartTownBuilding extends StructureStart {

    public StructureStartTownBuilding(World world, NMFeature feature, Random rand, int chunkX, int chunkZ){

    }

    protected StructureComponent makeFirstComponent(World world, NMFeature feature, Random rand, int x, int y, int z){
        return new ComponentTownBuildingMain(feature, world, rand, x, y, z);
    }
}
