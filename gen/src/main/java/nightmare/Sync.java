package nightmare;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.utils.Hash;
import nightmare.util.BlockPos;

import java.util.HashMap;

public class Sync {

    private static HashMap<BlockPos, Block> map = new HashMap<>();

    public static synchronized void addBlock(BlockPos pos, Block block){
        map.put(pos, block);
    }

    public static synchronized HashMap<BlockPos, Block> getBlocks(){
        HashMap<BlockPos, Block> m = (HashMap<BlockPos, Block>)map.clone();
        map.clear();
        return m;
    }

}
