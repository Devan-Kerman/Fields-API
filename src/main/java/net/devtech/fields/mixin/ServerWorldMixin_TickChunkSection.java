package net.devtech.fields.mixin;

import net.devtech.fields.impl.access.ChunkSectionAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

@Mixin (ServerWorld.class)
public class ServerWorldMixin_TickChunkSection {
	@Inject (method = "tickChunk",
			at = @At (value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"),
			slice = @Slice (from = @At (value = "CONSTANT", args = "stringValue=iceandsnow")),
			locals = LocalCapture.CAPTURE_FAILHARD)
	public void tickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci, ChunkPos chunkPos, boolean bl, int i, int j, Profiler profiler) {
		if (chunk != null) {
			profiler.swap("fields-api");
			for (ChunkSection section : chunk.getSectionArray()) {
				if (section != WorldChunk.EMPTY_SECTION) {
					((ChunkSectionAccess) section).fields_handleTick((ServerWorld) (Object) this, chunk);
				}
			}
		}
	}
}
