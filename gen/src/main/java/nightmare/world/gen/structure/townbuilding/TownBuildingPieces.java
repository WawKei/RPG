package nightmare.world.gen.structure.townbuilding;

import nightmare.world.gen.structure.MapGenStructureIO;

public class TownBuildingPieces {

    public static void registerPieces(){
        MapGenStructureIO.registerStructureComponent(ComponentTownBuildingMain.class, "TBMai");
    }
}
