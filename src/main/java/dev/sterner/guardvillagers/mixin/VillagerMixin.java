package dev.sterner.guardvillagers.mixin;

import dev.sterner.guardvillagers.common.entity.StructureSpawnedVillager;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Replaces the vanilla {@code assignProfessionWhenSpawned} flag removed in 26.3:
 * remembers that a villager was spawned by a structure until a guard spawn has been rolled for it.
 */
@Mixin(Villager.class)
public abstract class VillagerMixin implements StructureSpawnedVillager {
    @Unique
    private static final String STRUCTURE_SPAWNED_KEY = "GuardVillagersStructureSpawned";

    @Unique
    private boolean guardvillagers$structureSpawned;

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void guardvillagers$onFinalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (spawnReason == EntitySpawnReason.STRUCTURE) {
            this.guardvillagers$structureSpawned = true;
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void guardvillagers$save(ValueOutput output, CallbackInfo ci) {
        if (this.guardvillagers$structureSpawned) {
            output.putBoolean(STRUCTURE_SPAWNED_KEY, true);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void guardvillagers$load(ValueInput input, CallbackInfo ci) {
        this.guardvillagers$structureSpawned = input.getBooleanOr(STRUCTURE_SPAWNED_KEY, false);
    }

    @Override
    public boolean guardvillagers$consumeStructureSpawned() {
        boolean structureSpawned = this.guardvillagers$structureSpawned;
        this.guardvillagers$structureSpawned = false;
        return structureSpawned;
    }
}
