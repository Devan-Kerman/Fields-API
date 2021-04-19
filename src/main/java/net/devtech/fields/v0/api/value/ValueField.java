package net.devtech.fields.v0.api.value;

import net.devtech.fields.v0.api.DataFormatInitializer;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public interface ValueField<T> {
	static <A, F extends ValueField<A>> F create(DataFormatInitializer.Entry<A, F> entry, Identifier fieldId) {
		return entry.fieldCreator.apply(entry, fieldId);
	}

	default T get(World world, BlockPos pos) {
		return this.get(world.getWorldChunk(pos), pos);
	}

	/**
	 * @param pos does not need to be normalized (it still can be though)
	 */
	T get(WorldChunk chunk, BlockPos pos);

	default void set(World world, BlockPos pos, T value) {
		this.set(world.getWorldChunk(pos), pos, value);
	}

	/**
	 * @param pos does not need to be normalized (it still can be though)
	 */
	void set(WorldChunk chunk, BlockPos pos, T value);

	interface Int extends ValueField<Integer> {
		default int getInt(World world, BlockPos pos) {
			return this.getInt(world.getWorldChunk(pos), pos);
		}
		@Override
		default Integer get(WorldChunk chunk, BlockPos pos) { return this.getInt(chunk, pos); }

		@Override
		default void set(WorldChunk chunk, BlockPos pos, Integer value) {
			this.setInt(chunk, pos, value);
		}

		// todo setInt with World/Pos
		int getInt(WorldChunk chunk, BlockPos pos);

		void setInt(WorldChunk chunk, BlockPos pos, int val);
	}
}
