package nightmare.world.gen.structure.townbuilding;

import cn.nukkit.block.Block;
import nightmare.nbt.NBTTagCompound;
import nightmare.world.World;
import nightmare.world.gen.structure.NMFeature;
import nightmare.world.gen.structure.StructureBoundingBox;
import nightmare.world.gen.structure.StructureComponent;

import java.util.List;
import java.util.Random;

public class ComponentTownBuildingMain extends StructureComponent {


    public int size;
    protected int height;

    public ComponentTownBuildingMain(NMFeature feature, World world, Random rand, int x, int y, int z){
        this.size = 1;
        this.height = 1;
    }

    @Override
    protected void writeStructureToNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("towerSize", this.size);
        tagCompound.setInteger("towerHeight", this.height);
    }

    @Override
    protected void readStructureFromNBT(NBTTagCompound tagCompound) {
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        this.fillWithBlocks(world, sbb, 6, 0, 0, 6, 0, 8, Block.get(Block.STONE), Block.get(Block.STONE), false);
        return true;
    }



}
