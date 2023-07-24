package com.github.alexthe666.iceandfire.client.particle.lightning;

/*
 * This class was created by <ChickenBones>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
public class ParticleLightningBoltPoint {

    public final ParticleLightningVector point;
    public final ParticleLightningVector basepoint;
    public final ParticleLightningVector offsetvec;

    public ParticleLightningBoltPoint(ParticleLightningVector basepoint, ParticleLightningVector offsetvec) {
        point = basepoint.add(offsetvec);
        this.basepoint = basepoint;
        this.offsetvec = offsetvec;
    }
}
