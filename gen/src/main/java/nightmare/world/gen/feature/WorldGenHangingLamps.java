package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import nightmare.util.BlockHelper;
import nightmare.util.BlockPos;
import nightmare.world.World;

import java.util.Random;

public class WorldGenHangingLamps extends NMGenerator {

	private static final int MAX_HANG = 8;

	@Override
	public boolean generate(World world, Random random, BlockPos pos) {
		// this must be an air block, surrounded by air
		if (!world.isAirBlock(pos) || !NMGenerator.surroundedByAir(world, pos)) {
			return false;
		}

		// we need to be at least 4 above ground
		if (!isClearBelow(world, pos)) {
			return false;
		}

		// there should be leaves or wood within 12 blocks above
		int dist = findLeavesAbove(world, pos);
		if (dist < 0) {
			return false;
		}

		// generate lamp
		world.setBlockState(pos, Block.get(Block.GLOWSTONE), 2);
		for (int cy = 1; cy < dist; cy++) {
			world.setBlockState(pos.up(cy), Block.get(Block.FENCE), 2);
		}

		return true;
	}

	private int findLeavesAbove(World world, BlockPos pos) {
		for (int cy = 1; cy < MAX_HANG; cy++) {
			Block above = world.getBlockState(pos.up(cy));
			if (above.isSolid() || BlockHelper.isLeave(above)) {
				return cy;
			}
		}
		return -1;
	}

	private boolean isClearBelow(World world, BlockPos pos) {
		for (int cy = 1; cy < 4; cy++) {
			if (world.getBlockState(pos.down(cy).up()).isSolid()) {
				return false;
			}
		}
		return true;
	}
}
