package com.github.alexthe666.iceandfire.util;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class ParticleHelper {

    /**
     * Rehandles particle spawning to bypass a Vanilla bug where WorldServer::spawnParticle() has the incorrect parameters which causes it to not properly override the correct method in World::spawnParticle()
     * Other mods such as RandomPatches and RLMixins fixes this outright for all mods and Vanilla, but use this workaround anyways incase those mods are not loaded
     *
     * NOTE: Particles that use color like SPELL_MOB use x/y/z speed as float r/g/b, not the additional parameters
     *
     * NOTE: Particles that use additional parameters must match given parameters with count defined in EnumParticleTypes, otherwise it can crash
     * Ex. ITEM_CRACK requires ItemID and Metadata (0 if default)
     */
    public static void spawnParticle(World world, EnumParticleTypes particleType, boolean longDistance, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        if(world instanceof WorldServer) {
            if(parameters.length == particleType.getArgumentCount()) ((WorldServer)world).spawnParticle(particleType, longDistance, xCoord, yCoord, zCoord, 0, xSpeed, ySpeed, zSpeed, 1.0, parameters);
            else IceAndFire.logger.warn("ParticleHelper received particle: " + particleType.name() + " with incorrect amount of additional parameters: " + parameters.length + " for argument count: " + particleType.getArgumentCount());
        }
        else if(world.isRemote) {
            //Clientside only
            world.spawnParticle(particleType, longDistance, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
        }
        //Should never reach here, but just incase
        else world.spawnParticle(particleType, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    public static void spawnParticle(World world, EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        spawnParticle(world, particleType, false, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }
}