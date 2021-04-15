package net.devtech.fields.v0.api.data;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.devtech.fields.v0.api.DataFormat;
import net.devtech.fields.v0.api.DataFormatInitializer;
import net.devtech.fields.v0.api.value.ValueField;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkSection;

public abstract class PalettedDataFormat<A, F extends ValueField<A>> extends AbstractDataFormat<A, F> {
	protected PackedIntegerArray array;
	protected List<A> pallet;
	public PalettedDataFormat(DataFormatInitializer.Entry<A, F> entry, ChunkSection section, Identifier id) {
		super(entry, section, id);
	}

	@Override
	public Tag toTag() {
		if(this.array != null) {
			CompoundTag tag = new CompoundTag();
			tag.putLongArray("array", this.array.getStorage());
			tag.put("pallet", this.write(this.pallet));
			return tag;
		}
		return null;
	}

	protected abstract Tag write(List<A> pallet);
	protected abstract List<A> read(Tag tag);

	@Override
	public void fromTag(Tag tag) {
		CompoundTag nbt = (CompoundTag) tag;
		long[] storage = ((CompoundTag) tag).getLongArray("array");
		this.array = new PackedIntegerArray((storage.length * 64) / 4096, 4096, storage);
		this.pallet = this.read(nbt.get("pallet"));
	}

	@Override
	public A get(int subchunkX, int subchunkY, int subchunkZ) {
		int index = this.array.get(subchunkX << 8 | subchunkY << 4 | subchunkZ);
		return this.pallet.get(index);
	}

	@Override
	public void set(int subchunkX, int subchunkY, int subchunkZ, A a) {
		int pallet = this.array == null ? -1 : this.pallet.indexOf(a);
		if(pallet == -1) {
			if(this.pallet == null) {
				this.pallet = new ArrayList<>();
			}
			pallet = this.pallet.size();
			this.pallet.add(a);
			boolean realloc = this.array == null;
			int newBits = MathHelper.log2DeBruijn(this.pallet.size() + 1);
			if(!realloc) {
				long[] storage = this.array.getStorage();
				int bits = (storage.length * 64) / 4096;
				if(newBits != bits) {
					realloc = true;
				}
			}
			if(realloc) {
				// reallocation time
				PackedIntegerArray alloc = new PackedIntegerArray(newBits, 4096);
				if(this.array != null) {
					PackedIntegerArray old = this.array;
					for (int i = 0; i < old.getSize(); i++) {
						alloc.set(i, old.get(i));
					}
				}
				this.array = alloc;
			}
		}
		this.array.set(subchunkX << 8 | subchunkY << 4 | subchunkZ, pallet);
	}

	public static class Int extends PalettedDataFormat<Integer, ValueField.Int> implements DataFormat.Int {
		public Int(DataFormatInitializer.Entry entry, ChunkSection section, Identifier id) {
			super(entry, section, id);
		}

		@Override
		protected Tag write(List<Integer> pallet) {
			return new IntArrayTag(pallet);
		}

		@Override
		protected List<Integer> read(Tag tag) {
			return IntArrayList.wrap(((IntArrayTag)tag).getIntArray());
		}

		@Override
		public int getInt(int subchunkX, int subchunkY, int subchunkZ) {
			if(this.array == null) {
				return 0;
			}
			int index = this.array.get(subchunkX << 8 | subchunkY << 4 | subchunkZ);
			return this.pallet.get(index);
		}

		@Override
		public void setInt(int subchunkX, int subchunkY, int subchunkZ, int val) {
			IntList list = (IntList) this.pallet;
			int pallet = this.array == null ? -1 : list.indexOf(val);
			if(pallet == -1) {
				if(this.pallet == null) {
					this.pallet = list = new IntArrayList();
				}
				pallet = list.size();
				list.add(val);
				boolean realloc = this.array == null;
				int newBits = MathHelper.log2DeBruijn(list.size());
				if(!realloc) {
					long[] storage = this.array.getStorage();
					int bits = (storage.length * 64) / 4096;
					if(newBits != bits) {
						realloc = true;
					}
				}
				if(realloc) {
					// reallocation time
					PackedIntegerArray alloc = new PackedIntegerArray(newBits, 4096);
					PackedIntegerArray old = this.array;
					for (int i = 0; i < old.getSize(); i++) {
						alloc.set(i, old.get(i));
					}
					this.array = alloc;
				}
			}
			this.array.set(subchunkX << 8 | subchunkY << 4 | subchunkZ, pallet);
		}
	}
}
