package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import nightmare.util.BlockPos;
import nightmare.world.World;

public interface IBlockSettable {
	// [VanillaCopy] pin to signature of WorldGenerator.setBlockAndNotifyAdequately
	// But cannot have exact same name as the subclass methods will get reobf-ed but the interface one won't
	void setBlockAndNotify(World world, BlockPos pos, Block state);
}
