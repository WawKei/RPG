package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import nightmare.util.BlockPos;
import nightmare.world.World;

public abstract class NMTreeGenerator extends WorldGenAbstractTree implements IBlockSettable {

	protected Block treeState = Block.get(Block.LOG);
	protected Block branchState = Block.get(Block.LOG);
	protected Block leafState = Block.get(Block.LEAVE);
	protected Block rootState = Block.get(Block.LEAVES);

	protected Block source = Block.get(Block.SAPLING);

	public NMTreeGenerator() {
		this(false);
	}

	public NMTreeGenerator(boolean notify) {
		super(notify);
	}

	@Override
	public final void setBlockAndNotify(World world, BlockPos pos, Block state) {
		setBlockAndNotifyAdequately(world, pos, state);
	}

	//@Override
	//protected boolean canGrowInto(Block blockType) {
		//return TFGenHollowTree.canGrowInto(blockType);
	//}

	protected void buildRoot(World world, BlockPos pos, double offset, int b) {
		BlockPos dest = NMGenerator.translate(pos.down(b + 2), 5, 0.3 * b + offset, 0.8);

		// go through block by block and stop drawing when we head too far into open air
		BlockPos[] lineArray = NMGenerator.getBresehnamArrays(pos.down(), dest);
		for (BlockPos coord : lineArray) {
			this.placeRootBlock(world, coord, rootState);
		}
	}

	protected void placeRootBlock(World world, BlockPos pos, Block state) {
		if (canRootGrowIn(world, pos)) {
			this.setBlockAndNotifyAdequately(world, pos, state);
		}
	}

	public static boolean canRootGrowIn(World world, BlockPos pos) {
		Block blockState = world.getBlockState(pos);
		Block blockID = blockState;

		if (blockID.getId() == Block.AIR) {
			// roots can grow through air if they are near a solid block
			return NMGenerator.isNearSolid(world, pos);
		} else {
			return (blockState.getHardness() >= 0)
					&& blockID.getId() != Block.IRON_BAR
					&& blockID.getId() != Block.PLANK
					&& blockID.getId() != Block.MONSTER_SPAWNER
					&& (blockState.isSolid());
		}
	}

	/*protected void addFirefly(World world, BlockPos pos, int height, double angle) {
		int iAngle = (int) (angle * 4.0);
		if (iAngle == 0) {
			setIfEmpty(world, pos.add( 1, height,  0), TFBlocks.firefly.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.EAST));
		} else if (iAngle == 1) {
			setIfEmpty(world, pos.add(-1, height,  0), TFBlocks.firefly.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.WEST));
		} else if (iAngle == 2) {
			setIfEmpty(world, pos.add( 0, height,  1), TFBlocks.firefly.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.SOUTH));
		} else if (iAngle == 3) {
			setIfEmpty(world, pos.add( 0, height, -1), TFBlocks.firefly.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.NORTH));
		}
	}*/

	private void setIfEmpty(World world, BlockPos pos, Block state) {
		if (world.isAirBlock(pos)) {
			this.setBlockAndNotifyAdequately(world, pos, state);
		}
	}
}