package dev.sterner.guardvillagers.common.entity;

public interface StructureSpawnedVillager {
    /**
     * @return true once if this villager was spawned by a structure, then resets the flag.
     */
    boolean guardvillagers$consumeStructureSpawned();
}
