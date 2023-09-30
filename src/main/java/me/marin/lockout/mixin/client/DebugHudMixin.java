package me.marin.lockout.mixin.client;

import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.property.Property;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Stream;

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

    private final DebugHud thiz = (DebugHud) (Object) this;

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
        text.add(String.format(Locale.ROOT, "Block: %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        text.add(String.format(Locale.ROOT, "Chunk: %d %d %d in %d %d %d", blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15, chunkPos.x, ChunkSectionPos.getSectionCoord(blockPos.getY()), chunkPos.z));
        text.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, directionString, MathHelper.wrapDegrees(entity.getYaw()), MathHelper.wrapDegrees(entity.getPitch())));

        WorldChunk worldChunk = this.getClientChunk();
        if (worldChunk.isEmpty()) {
            text.add("Waiting for chunk...");
        } else {
            if (blockPos.getY() >= this.client.world.getBottomY() && blockPos.getY() < this.client.world.getTopY()) {
                RegistryEntry<Biome> var27 = this.client.world.getBiome(blockPos);
                text.add("Biome: " + getBiomeString(var27));
            }
        }

        Map.Entry<Property<?>, Comparable<?>> entry;
        if (thiz.blockHit.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
            blockPos = ((BlockHitResult)thiz.blockHit).getBlockPos();
            BlockState blockState = this.client.world.getBlockState(blockPos);
            text.add("");
            text.add(Formatting.UNDERLINE + "Targeted Block: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
            text.add(String.valueOf(Registries.BLOCK.getId(blockState.getBlock())));

            for (Map.Entry<Property<?>, Comparable<?>> propertyComparableEntry : blockState.getEntries().entrySet()) {
                entry = propertyComparableEntry;
                text.add(thiz.propertyToString(entry));
            }
        }

        if (thiz.fluidHit.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
            blockPos = ((BlockHitResult)thiz.fluidHit).getBlockPos();
            FluidState fluidState = this.client.world.getFluidState(blockPos);
            text.add("");
            text.add(Formatting.UNDERLINE + "Targeted Fluid: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
            text.add(String.valueOf(Registries.FLUID.getId(fluidState.getFluid())));

            for (Map.Entry<net.minecraft.state.property.Property<?>, Comparable<?>> propertyComparableEntry : fluidState.getEntries().entrySet()) {
                entry = propertyComparableEntry;
                text.add(thiz.propertyToString(entry));
            }
        }

        entity = this.client.targetedEntity;
        if (entity != null) {
            text.add("");
            text.add(Formatting.UNDERLINE + "Targeted Entity");
            text.add(String.valueOf(Registries.ENTITY_TYPE.getId(entity.getType())));
        }

        text.add("");
        text.add(Formatting.UNDERLINE + "Debug charts:");
        text.add("[F3+1] Pie Chart");
        text.add("[F3+2] FPS + TPS");
        text.add("[F3+3] Bandwidth + Ping");

        this.drawText(context, text, true);
        ci.cancel();
    }

    @Inject(method = "drawRightText", at = @At("HEAD"), cancellable = true)
    public void drawRightText(DrawContext context, CallbackInfo ci) {
        ci.cancel();
    }

}
