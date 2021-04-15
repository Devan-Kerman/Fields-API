package net.devtech.fields.mixin;

import net.devtech.fields.impl.access.ChunkSectionAccess;
import net.devtech.fields.v0.api.DataFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.poi.PointOfInterestStorage;

@Mixin (ChunkSerializer.class)
public class ChunkSerializerMixin_ChunkSectionSerialize {

	@Inject (method = "deserialize",
			at = @At (value = "INVOKE",
					target = "Lnet/minecraft/world/chunk/ChunkSection;getContainer()Lnet/minecraft/world/chunk/PalettedContainer;"),
			locals = LocalCapture.CAPTURE_FAILHARD)
	private static void deserialize(ServerWorld world,
			StructureManager structureManager,
			PointOfInterestStorage poiStorage,
			ChunkPos pos,
			CompoundTag tag,
			CallbackInfoReturnable<ProtoChunk> cir,
			ChunkGenerator chunkGenerator,
			BiomeSource biomeSource,
			CompoundTag compoundTag,
			BiomeArray biomeArray,
			UpgradeData upgradeData,
			ChunkTickScheduler chunkTickScheduler,
			ChunkTickScheduler chunkTickScheduler2,
			boolean bl,
			ListTag listTag,
			int i,
			ChunkSection chunkSections[],
			boolean bl2,
			ChunkManager chunkManager,
			LightingProvider lightingProvider,
			int j,
			CompoundTag compoundTag2,
			int k,
			ChunkSection chunkSection) {
		try {
			ChunkSectionAccess access = (ChunkSectionAccess) chunkSection;
			CompoundTag tags = compoundTag2.getCompound("fields_section_data");
			for (String key : tags.getKeys()) {
				CompoundTag data = tags.getCompound(key);
				Identifier valueId = new Identifier(key);
				DataFormat<?, ?> format = access.getOrCreate(valueId, DataFormat.REGISTRY.get(new Identifier(data.getString("format_id"))));
				format.fromTag(data.get("data"));
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Inject (method = "serialize",
			at = @At (value = "INVOKE",
					target = "Lnet/minecraft/world/chunk/ChunkSection;getContainer()Lnet/minecraft/world/chunk/PalettedContainer;"),
			locals = LocalCapture.CAPTURE_FAILHARD)
	private static void serialize(ServerWorld world,
			Chunk chunk,
			CallbackInfoReturnable<CompoundTag> cir,
			ChunkPos chunkPos,
			CompoundTag compoundTag,
			CompoundTag compoundTag2,
			ChunkSection chunkSections[],
			ListTag listTag,
			LightingProvider lightingProvider,
			boolean bl,
			int i,
			int j,
			ChunkSection chunkSection,
			ChunkNibbleArray chunkNibbleArray,
			ChunkNibbleArray chunkNibbleArray2,
			CompoundTag compoundTag3) {
		try {
			ChunkSectionAccess access = (ChunkSectionAccess) chunkSection;
			CompoundTag tags = new CompoundTag();
			for (Identifier identifier : access.fields_getStored()) {
				DataFormat<?, ?> format = access.get(identifier);
				Tag tag = format.toTag();
				if (tag != null) {
					CompoundTag data = new CompoundTag();
					data.put("data", tag);
					data.putString("format_id", format.getEntry().id.toString());
					tags.put(identifier.toString(), data);
				}
			}
			compoundTag3.put("fields_section_data", tags);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
