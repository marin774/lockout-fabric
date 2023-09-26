package me.marin.lockoutbutbetter.mixin;

import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Mixin(DebugHud.class)
public abstract class DebugHudMixin {

    @Shadow @Final
    private MinecraftClient client;

    @Shadow
    private ChunkPos pos;

    @Shadow
    private void drawText(DrawContext context, List<String> text, boolean left) {}

    @Shadow
    public void resetChunk() {}

    @Shadow
    private WorldChunk getClientChunk() {
        throw new AbstractMethodError("Shadow");
    }

    @Shadow
    private static String getBiomeString(RegistryEntry<Biome> biome) {
        throw new AbstractMethodError("Shadow");
    }

    @Inject(method = "drawLeftText", at = @At("HEAD"), cancellable = true)
    public void drawLeftText(DrawContext context, CallbackInfo ci) {
        List<String> text = new ArrayList<>();

        Entity entity = this.client.getCameraEntity();
        BlockPos blockPos = entity.getBlockPos();
        Direction direction = entity.getHorizontalFacing();

        String directionString;
        switch (direction) {
            case NORTH:
                directionString = "Towards negative Z";
                break;
            case SOUTH:
                directionString = "Towards positive Z";
                break;
            case WEST:
                directionString = "Towards negative X";
                break;
            case EAST:
                directionString = "Towards positive X";
                break;
            default:
                directionString = "Invalid";
        }

        ChunkPos chunkPos = new ChunkPos(blockPos);
        if (!Objects.equals(this.pos, chunkPos)) {
            this.pos = chunkPos;
            this.resetChunk();
        }

        text.add("Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType()) + ")");
        text.add(this.client.fpsDebugString);
        text.add(this.client.worldRenderer.getChunksDebugString()); // C value
        text.add(this.client.worldRenderer.getEntitiesDebugString()); // E value
        text.add("");
        text.add(String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.client.getCameraEntity().getX(), this.client.getCameraEntity().getY(), this.client.getCameraEntity().getZ()));
        text.add(String.format(Locale.ROOT, "Block: %d %d %d [%d %d %d]", blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15));
        text.add(String.format(Locale.ROOT, "Chunk: %d %d %d", chunkPos.x, ChunkSectionPos.getSectionCoord(blockPos.getY()), chunkPos.z));
        text.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, directionString, MathHelper.wrapDegrees(entity.getYaw()), MathHelper.wrapDegrees(entity.getPitch())));

        WorldChunk worldChunk = this.getClientChunk();
        if (worldChunk.isEmpty()) {
            text.add("Waiting for chunk...");
        } else {
            if (blockPos.getY() >= this.client.world.getBottomY() && blockPos.getY() < this.client.world.getTopY()) {
                RegistryEntry var27 = this.client.world.getBiome(blockPos);
                text.add("Biome: " + getBiomeString(var27));
            }
        }

        this.drawText(context, text, true);
        ci.cancel();
    }

    @Inject(method = "drawRightText", at = @At("HEAD"), cancellable = true)
    public void drawRightText(DrawContext context, CallbackInfo ci) {
        // TODO: add targeted block + fluid
        ci.cancel();
    }

}
