package net.devtech.fields.v0.api.data;

import net.devtech.fields.v0.api.DataHandler;
import net.devtech.fields.v0.api.DataFormatInitializer;
import net.devtech.fields.v0.api.value.ValueField;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkSection;

public class GradientPackedDataHandler extends AbstractDataHandler<Integer, ValueField.Int> implements DataHandler.Int {
	protected PackedIntegerArray array;
	protected int min, max;

	public GradientPackedDataHandler(DataFormatInitializer.Entry<Integer, ValueField.Int> entry, ChunkSection section, Identifier id) {
		super(entry, section, id);
	}

	@Override
	public Tag toTag() {
		if(this.array != null) {
			CompoundTag tag = new CompoundTag();
			tag.putLongArray("array", this.array.getStorage());
			tag.putInt("min", this.min);
			tag.putInt("max", this.max);
			return tag;
		}
		return null;
	}

	@Override
	public void fromTag(Tag tag) {
		CompoundTag nbt = (CompoundTag) tag;
		long[] storage = ((CompoundTag) tag).getLongArray("array");
		this.array = new PackedIntegerArray((storage.length * 64) / 4096, 4096, storage);
		this.min = nbt.getInt("min");
		this.max = nbt.getInt("max");
	}

	@Override
	public int getInt(int subchunkX, int subchunkY, int subchunkZ) {
		if (this.array == null || subchunkX < 0 || subchunkX > 15 || subchunkY < 0 || subchunkY > 15 || subchunkZ < 0 || subchunkZ > 15) {
			return this.min;
		}

		return this.array.get(subchunkX << 8 | subchunkY << 4 | subchunkZ) + this.min;
	}

	@Override
	public boolean setInt(int subchunkX, int subchunkY, int subchunkZ, int val) {
		if (subchunkX < 0 || subchunkX > 15 || subchunkY < 0 || subchunkY > 15 || subchunkZ < 0 || subchunkZ > 15) {
			return false;
		}

		boolean shouldRecompute = false, shouldResize = false;

		int neededBits = -1, min = this.min;
		if(this.array == null) {
			shouldResize = true;
			if(val < min) {
				this.min = val;
			} else if(val > this.max) {
				this.max = val;
			}
		} else if(val < min) {
			shouldRecompute = true;
			neededBits = this.neededBits(val);
			shouldResize = neededBits != this.currentBits();
			this.min = val;
		} else if(val > this.max) {
			neededBits = this.neededBits(val);
			shouldResize = neededBits != this.currentBits();
			this.max = val;
		}

		if(shouldResize) {
			// reallocation time
			if(neededBits == -1) neededBits = this.neededBits(val);
			PackedIntegerArray alloc = new PackedIntegerArray(neededBits, 4096);
			if(this.array != null) {
				PackedIntegerArray old = this.array;
				for (int i = 0; i < old.getSize(); i++) {
					alloc.set(i, (old.get(i) + min) - this.min);
				}
			}
			this.array = alloc;
		} else if(shouldRecompute) {
			PackedIntegerArray arr = this.array;
			for (int i = 0; i < arr.getSize(); i++) {
				arr.set(i, (arr.get(i) + min) - this.min);
			}
		}
		this.array.set(subchunkX << 8 | subchunkY << 4 | subchunkZ, val - this.min);
		return true;
	}

	public int neededBits(int val) {
		return MathHelper.log2DeBruijn(Math.max(this.max, val) - this.min) + 1;
	}

	public int currentBits() {
		long[] storage = this.array.getStorage();
		return (storage.length * 64) / 4096;
	}
}
