package net.devtech.fields.impl;

import net.devtech.fields.impl.access.ChunkSectionAccess;
import net.devtech.fields.v0.api.DataHandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.ChunkSection;

public class DataHandlerSerializer {
	public static void deserialize(ChunkSection section, CompoundTag tags) {
		try {
			ChunkSectionAccess access = (ChunkSectionAccess) section;
			for (String key : tags.getKeys()) {
				CompoundTag data = tags.getCompound(key);
				Identifier valueId = new Identifier(key);
				DataHandler<?, ?> format = access.getOrCreate(valueId, DataHandler.REGISTRY.get(new Identifier(data.getString("format_id"))));
				format.fromTag(data.get("data"));
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static CompoundTag serialize(ChunkSection section, boolean packet) {
		try {
			ChunkSectionAccess access = (ChunkSectionAccess) section;
			CompoundTag tags = new CompoundTag();
			for (Identifier identifier : access.fields_getStored()) {
				DataHandler<?, ?> format = access.get(identifier);
				Tag tag = format.toTag();
				if (tag != null || !(format.shouldSync() && packet)) {
					CompoundTag data = new CompoundTag();
					data.put("data", tag);
					data.putString("format_id", format.getEntry().id.toString());
					tags.put(identifier.toString(), data);
				}
			}
			return tags;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
}
