package nightmare.world.gen;

import com.google.common.base.Predicates;
import jdk.internal.jline.internal.Nullable;
import nightmare.util.BlockPos;
import nightmare.world.World;
import nightmare.world.gen.structure.*;

import static nightmare.world.gen.structure.NMFeature.NOTHING;

public class MapGenNMMajorFeature extends MapGenStructure {
    private final NMFeature FEATURE;

    public MapGenNMMajorFeature() {
        this.FEATURE = NOTHING;
    }

    public MapGenNMMajorFeature(NMFeature feature) {
        this.FEATURE = feature;
    }

    @SuppressWarnings("ConstantConditions")
    public NMFeature getFeature() {
        return FEATURE != null ? FEATURE : NOTHING;
    }

    @Override
    public String getStructureName() {
        return this.getFeature().name.toLowerCase();
    }

    /*@Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
        this.world = worldIn;
        return findNearestStructurePosBySpacing(worldIn, this, pos, 20, 11, 10387313, true, 100, findUnexplored);
    }*/

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        return FEATURE.isStructureEnabled && NMFeature.getFeatureDirectlyAt(chunkX, chunkZ, world) == FEATURE;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        // fix rand
        this.rand.setSeed(world.getSeed());
        long rand1 = this.rand.nextLong();
        long rand2 = this.rand.nextLong();
        long chunkXr1 = (long) (chunkX) * rand1;
        long chunkZr2 = (long) (chunkZ) * rand2;
        this.rand.setSeed(chunkXr1 ^ chunkZr2 ^ world.getSeed());
        this.rand.nextInt();

        //NMFeature feature = NMFeature.getFeatureDirectlyAt(chunkX, chunkZ, world);

        return this.getFeature().provideStructureStart(world, rand, chunkX, chunkZ);
    }

    /**
     * Returns true if the structure generator has generated a structure located at the given position tuple.
     */
    public int getSpawnListIndexAt(BlockPos pos) {
        int highestFoundIndex = -1;

        for (StructureStart start : this.structureMap.values()) {
            if (start.isSizeableStructure() && start.getBoundingBox().intersectsWith(pos.getX(), pos.getZ(), pos.getX(), pos.getZ())) {

                for (StructureComponent component : start.getComponents()) {
                    if (component.getBoundingBox().isVecInside(pos)) {
                        if (component instanceof StructureComponent) {
                            StructureComponent tfComponent = (StructureComponent) component;

                            //if (tfComponent.spawnListIndex > highestFoundIndex) {
                            //    highestFoundIndex = tfComponent.spawnListIndex;
                            //}
                        } else {
                            return 0;
                        }
                    }
                }
            }
        }

        return highestFoundIndex;
    }

    @Nullable
    public StructureBoundingBox getSBBAt(BlockPos pos) {
        StructureBoundingBox boxFound = null;

        for (StructureStart start : this.structureMap.values()) {
            if (start.isSizeableStructure() && start.getBoundingBox().intersectsWith(pos.getX(), pos.getZ(), pos.getX(), pos.getZ())) {

                for (StructureComponent component : start.getComponents()) {
                    if (component.getBoundingBox().isVecInside(pos)) {
                        boxFound = component.getBoundingBox();
                    }
                }
            }
        }

        return boxFound;
    }

    public NMFeature getFeatureAt(BlockPos pos) {
        for (StructureStart start : this.structureMap.values())
            if (start.isSizeableStructure() && start.getBoundingBox().intersectsWith(pos.getX(), pos.getZ(), pos.getX(), pos.getZ()))
                for (StructureComponent component : start.getComponents())
                    if (component.getBoundingBox().isVecInside(pos))
                        if (component instanceof StructureComponent)
                            return ((StructureComponent) component).getFeatureType();
        return NOTHING;
    }

    /**
     * Is the block at the coordinates given a protected one?
     */
    public boolean isBlockProtectedAt(BlockPos pos) {
        boolean blockProtected = false;

        for (StructureStart start : this.structureMap.values()) {
            if (start.isSizeableStructure() && start.getBoundingBox().intersectsWith(pos.getX(), pos.getZ(), pos.getX(), pos.getZ())) {

                for (StructureComponent component : start.getComponents()) {
                    if (component.getBoundingBox().isVecInside(pos)) {

                        if (component instanceof StructureComponent) {
                            StructureComponent tfComp = (StructureComponent) component;

                            blockProtected = tfComp.isComponentProtected();

                        } else {
                            blockProtected = true;
                        }

                        // check if it's a twilight forest component, then check if it's protected
                    }
                }
            }
        }

        return blockProtected;
    }

    public void setStructureConquered(int mapX, int mapY, int mapZ, boolean flag) {
        for (StructureStart start : this.structureMap.values()) {
            if (start.isSizeableStructure() && start.getBoundingBox().intersectsWith(mapX, mapZ, mapX, mapZ)) {
                if (start instanceof StructureStart) {
                    StructureStart featureStart = (StructureStart) start;
                    //featureStart.isConquered = flag;
                    //this.structureData.writeInstance(featureStart.writeStructureComponentsToNBT(start.getChunkPosX(), start.getChunkPosZ()), start.getChunkPosX(), start.getChunkPosZ());
                    //this.structureData.setDirty(true);
                    //if (flag) {
                    //}
                }
            }
        }
    }

    public boolean isStructureConquered(BlockPos pos) {
        boolean conquered = false;

        for (StructureStart start : this.structureMap.values())
            if (start.isSizeableStructure() && start.getBoundingBox().intersectsWith(pos.getX(), pos.getZ(), pos.getX(), pos.getZ()))
                if (start instanceof StructureStart)
                    conquered = ((StructureStart) start).isConquered;

        return conquered;
    }

    public boolean isBlockInFullStructure(int mapX, int mapZ) {
        for (StructureStart start : this.structureMap.values()) {
            if (start.isSizeableStructure() && start.getBoundingBox().intersectsWith(mapX, mapZ, mapX, mapZ)) {
                return true;
            }
        }
        return false;
    }

    public boolean isBlockNearFullStructure(int mapX, int mapZ, int range) {
        StructureBoundingBox rangeBB = new StructureBoundingBox(mapX - range, mapZ - range, mapX + range, mapZ + range);
        for (StructureStart start : this.structureMap.values()) {
            if (start.isSizeableStructure() && start.getBoundingBox().intersectsWith(rangeBB)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public StructureBoundingBox getFullSBBAt(int mapX, int mapZ) {
        for (StructureStart start : this.structureMap.values()) {
            if (start.isSizeableStructure() && start.getBoundingBox().intersectsWith(mapX, mapZ, mapX, mapZ)) {
                return start.getBoundingBox();
            }
        }
        return null;
    }

    @Nullable
    public StructureBoundingBox getFullSBBNear(int mapX, int mapZ, int range) {
        StructureBoundingBox rangeBB = new StructureBoundingBox(mapX - range, mapZ - range, mapX + range, mapZ + range);
        for (StructureStart start : this.structureMap.values()) {
            if (start.isSizeableStructure() && start.getBoundingBox().intersectsWith(rangeBB)) {
                return start.getBoundingBox();
            }
        }
        return null;
    }
}
