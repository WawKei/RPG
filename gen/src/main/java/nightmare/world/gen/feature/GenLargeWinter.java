package nightmare.world.gen.feature;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWood;
import nightmare.util.BlockPos;
import nightmare.util.EnumFacing;
import nightmare.world.World;

import java.util.Random;

public class GenLargeWinter extends NMTreeGenerator {

	public GenLargeWinter() {
		this(false);
	}

	public GenLargeWinter(boolean notify) {
		super(notify);
		treeState = Block.get(Block.LOG, BlockWood.SPRUCE);
		branchState = Block.get(Block.LOG);
		leafState = Block.get(Block.LEAVES);
		rootState = Block.get(Block.LEAVES);
		source = Block.get(Block.SAPLING);
	}

	@Override
	public boolean generate(World world, Random random, BlockPos pos) {
		// determine a height
		int treeHeight = 35;
		if (random.nextInt(3) == 0) {
			treeHeight += random.nextInt(10);

			if (random.nextInt(8) == 0) {
				treeHeight += random.nextInt(10);
			}
		}

		if (pos.getY() >= 255 - treeHeight) {
			return false;
		}

		// check if we're on dirt or grass
		Block state = world.getBlockState(pos.down());
		if (state.getId() != Block.GRASS && state.getId() != Block.DIRT && state.getId() != Block.FARMLAND && state.getId() != Block.PODZOL) {
			return false;
		}

		//okay build a tree!  Go up to the height
		buildTrunk(world, pos, treeHeight);

		// make leaves
		makeLeaves(world, pos, treeHeight);

		// roots!
		int numRoots = 4 + random.nextInt(3);
		float offset = random.nextFloat();
		for (int b = 0; b < numRoots; b++) {
			buildRoot(world, pos, offset, b);
		}

		return true;
	}

	private void makeLeaves(World world, BlockPos pos, int treeHeight) {
		int offGround = 3;
		int leafType = 1;

		for (int dy = 0; dy < treeHeight; dy++) {

			int radius = leafRadius(treeHeight, dy, leafType);

			NMGenerator.makeLeafCircle2(this, world, pos.up(offGround + treeHeight - dy), radius, leafState, false);
			this.makePineBranches(world, pos.up(offGround + treeHeight - dy), radius);
		}
	}

	private void makePineBranches(World world, BlockPos pos, int radius) {
		int branchLength = radius > 4 ? radius - 1 : radius - 2;
		short[] faces = new short[]{
				0,
				0,
				0b1000,
				0b1000,
				0b0100,
				0b0100
		};
		switch (pos.getY() % 2) {
			case 0:
				// branches
				for (int i = 1; i <= branchLength; i++) {
					this.setBlockAndNotifyAdequately(world, pos.add(-i, 0, 0), Block.get(branchState.getId(), (branchState.getDamage() & 0x03) | faces[2]));
					this.setBlockAndNotifyAdequately(world, pos.add(0, 0, i + 1), Block.get(branchState.getId(), (branchState.getDamage() & 0x03) | faces[4]));
					this.setBlockAndNotifyAdequately(world, pos.add(i + 1, 0, 1), Block.get(branchState.getId(), (branchState.getDamage() & 0x03) | faces[2]));
					this.setBlockAndNotifyAdequately(world, pos.add(1, 0, -i), Block.get(branchState.getId(), (branchState.getDamage() & 0x03) | faces[4]));
				}
				break;
			case 1:
				for (int i = 1; i <= branchLength; i++) {
					this.setBlockAndNotifyAdequately(world, pos.add(-1, 0, 1), Block.get(branchState.getId(), (branchState.getDamage() & 0x03) | faces[2]));
					this.setBlockAndNotifyAdequately(world, pos.add(1, 0, i + 1), Block.get(branchState.getId(), (branchState.getDamage() & 0x03) | faces[4]));
					this.setBlockAndNotifyAdequately(world, pos.add(i + 1, 0, 0), Block.get(branchState.getId(), (branchState.getDamage() & 0x03) | faces[2]));
					this.setBlockAndNotifyAdequately(world, pos.add(0, 0, -i), Block.get(branchState.getId(), (branchState.getDamage() & 0x03) | faces[4]));
				}
				break;
		}
	}

	private int leafRadius(int treeHeight, int dy, int functionType) {
		switch (functionType) {
			case 0:
			default:
				return (dy - 1) % 4;
			case 1:
				return (int) (4F * (float) dy / (float) treeHeight + (0.75F * dy % 3));
			case 99:
				return (treeHeight - (dy / 2) - 1) % 4; // bad
		}
	}

	private void buildTrunk(World world, BlockPos pos, int treeHeight) {
		for (int dy = 0; dy < treeHeight; dy++) {
			this.setBlockAndNotifyAdequately(world, pos.add(0, dy, 0), treeState);
			this.setBlockAndNotifyAdequately(world, pos.add(1, dy, 0), treeState);
			this.setBlockAndNotifyAdequately(world, pos.add(0, dy, 1), treeState);
			this.setBlockAndNotifyAdequately(world, pos.add(1, dy, 1), treeState);
		}
	}

}
