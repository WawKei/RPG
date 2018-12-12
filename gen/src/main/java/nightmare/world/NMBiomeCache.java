package nightmare.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import nightmare.world.biome.BiomeGenBase;

import java.util.ArrayList;
import java.util.List;

public class NMBiomeCache {

	private final Long2ObjectMap<Entry> entryMap = new Long2ObjectOpenHashMap<>();
	private final List<Entry> entries = new ArrayList<>();
	private final World provider;
	private final int gridSize;
	private final boolean offset;
	private long lastCleanupTime;

	public NMBiomeCache(World provider, int gridSize, boolean offset) {
		this.provider = provider;
		this.gridSize = gridSize;
		this.offset = offset;
	}

	private final class Entry {

		BiomeGenBase[] biomes;
		int x, z;
		long lastAccessTime;

		Entry(int x, int z) {
			this.biomes = new BiomeGenBase[gridSize * gridSize];
			this.x = x;
			this.z = z;
			provider.getBiomesForGeneration(biomes, fromGrid(x), fromGrid(z), gridSize, gridSize, false);
		}
	}

	private Entry getEntry(int x, int z) {

		x = toGrid(x);
		z = toGrid(z);

		long key = getKey(x, z);
		Entry entry = this.entryMap.get(key);

		if (entry == null) {

			entry = new Entry(x, z);
			this.entryMap.put(key, entry);
			this.entries.add(entry);
		}

		entry.lastAccessTime = System.currentTimeMillis();
		return entry;
	}

	public BiomeGenBase[] getBiomes(int x, int z) {
		return getEntry(x, z).biomes;
	}

	public void cleanup() {

		long currentTime = System.currentTimeMillis();
		long timeSinceCleanup = currentTime - this.lastCleanupTime;

		if (timeSinceCleanup > 7500L || timeSinceCleanup < 0L) {

			this.lastCleanupTime = currentTime;

			for (int i = 0; i < this.entries.size(); ++i) {

				Entry entry = this.entries.get(i);
				long timeSinceAccess = currentTime - entry.lastAccessTime;

				if (timeSinceAccess > 30000L || timeSinceAccess < 0L) {

					this.entries.remove(i--);
					long key = getKey(entry.x, entry.z);
					this.entryMap.remove(key);
				}
			}
		}
	}

	public boolean isGridAligned(int x, int z, int width, int height) {
		return width == gridSize && height == gridSize && gridOffset(x) == 0 && gridOffset(z) == 0;
	}

	private int gridOffset(int n) {
		return (n + (offset ? gridSize / 2 : 0)) % gridSize;
	}

	private int toGrid(int n) {
		return (n + (offset ? gridSize / 2 : 0)) / gridSize;
	}

	private int fromGrid(int n) {
		return n * gridSize - (offset ? gridSize / 2 : 0);
	}

	private static long getKey(int x, int z) {
		return Integer.toUnsignedLong(x) | Integer.toUnsignedLong(z) << 32;
	}
}
