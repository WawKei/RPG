package nightmare.world.chunk;

import java.util.List;

import cn.nukkit.level.format.FullChunk;
import nightmare.util.BlockPos;
import nightmare.world.World;
import nightmare.world.biome.BiomeGenBase;

public interface IChunkProvider
{
    boolean chunkExists(int x, int z);

    void provideChunk(int x, int z, FullChunk chunk);

    void provideChunk(BlockPos blockPosIn, FullChunk chunk);

    void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_);

    //boolean func_177460_a(IChunkProvider p_177460_1_, Chunk p_177460_2_, int p_177460_3_, int p_177460_4_);

    boolean unloadQueuedChunks();

    boolean canSave();

    String makeString();

    //List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos);

    //BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position);

    int getLoadedChunkCount();

    //void recreateStructures(Chunk p_180514_1_, int p_180514_2_, int p_180514_3_);

    void saveExtraData();
}