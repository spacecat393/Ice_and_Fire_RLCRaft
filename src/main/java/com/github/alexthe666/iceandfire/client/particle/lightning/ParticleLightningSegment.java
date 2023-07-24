package com.github.alexthe666.iceandfire.client.particle.lightning;

/*
 * This class was created by <ChickenBones>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
public class ParticleLightningSegment {

    public final ParticleLightningBoltPoint startPoint;
    public final ParticleLightningBoltPoint endPoint;

    public ParticleLightningVector diff;

    public ParticleLightningSegment prev;
    public ParticleLightningSegment next;

    public ParticleLightningVector nextDiff;
    public ParticleLightningVector prevDiff;

    public float sinPrev;
    public float sinNext;
    public final float light;

    public final int segmentNo;
    public final int splitNo;

    public ParticleLightningSegment(ParticleLightningBoltPoint start, ParticleLightningBoltPoint end, float light, int segmentnumber, int splitnumber) {
        startPoint = start;
        endPoint = end;
        this.light = light;
        segmentNo = segmentnumber;
        splitNo = splitnumber;

        calcDiff();
    }

    public ParticleLightningSegment(ParticleLightningVector start, ParticleLightningVector end) {
        this(new ParticleLightningBoltPoint(start, new ParticleLightningVector(0, 0, 0)), new ParticleLightningBoltPoint(end, new ParticleLightningVector(0, 0, 0)), 1, 0, 0);
    }

    public void calcDiff() {
        diff = endPoint.point.subtract(startPoint.point);
    }

    public void calcEndDiffs() {
        if(prev != null) {
            ParticleLightningVector prevdiffnorm = prev.diff.normalize();
            ParticleLightningVector thisdiffnorm = diff.normalize();

            prevDiff = thisdiffnorm.add(prevdiffnorm).normalize();
            sinPrev = (float) Math.sin(thisdiffnorm.angle(prevdiffnorm.multiply(-1)) / 2);
        } else {
            prevDiff = diff.normalize();
            sinPrev = 1;
        }

        if(next != null) {
            ParticleLightningVector nextdiffnorm = next.diff.normalize();
            ParticleLightningVector thisdiffnorm = diff.normalize();

            nextDiff = thisdiffnorm.add(nextdiffnorm).normalize();
            sinNext = (float) Math.sin(thisdiffnorm.angle(nextdiffnorm.multiply(-1)) / 2);
        } else {
            nextDiff = diff.normalize();
            sinNext = 1;
        }
    }

    @Override
    public String toString() {
        return startPoint.point.toString() + " " + endPoint.point.toString();
    }
}
