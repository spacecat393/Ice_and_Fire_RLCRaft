package com.github.alexthe666.iceandfire.client.particle.lightning;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;

/*
 * This class was created by <ChickenBones>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
public final class ParticleLightningQuat {
    public final double x;
    public final double y;
    public final double z;
    public final double s;
    private final int hashCode;

    public ParticleLightningQuat(double d, double d1, double d2, double d3) {
        x = d1;
        y = d2;
        z = d3;
        s = d;
        hashCode = Arrays.hashCode(new double[] { d, d1, d2, d3 } );
    }

    public static ParticleLightningQuat aroundAxis(double ax, double ay, double az, double angle) {
        angle *= 0.5D;
        double d4 = Math.sin(angle);
        return new ParticleLightningQuat(Math.cos(angle), ax * d4, ay * d4, az * d4);
    }

    public ParticleLightningVector rotate(ParticleLightningVector vec) {
        double d = -x * vec.x - y * vec.y - z * vec.z;
        double d1 = s * vec.x + y * vec.z - z * vec.y;
        double d2 = s * vec.y - x * vec.z + z * vec.x;
        double d3 = s * vec.z + x * vec.y - y * vec.x;

        double vx = d1 * s - d * x - d2 * z + d3 * y;
        double vy = d2 * s - d * y + d1 * z - d3 * x;
        double vz = d3 * s - d * z - d1 * y + d2 * x;

        return new ParticleLightningVector(vx, vy, vz);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ParticleLightningQuat
                && ((ParticleLightningQuat) o).x == x
                && ((ParticleLightningQuat) o).y == y
                && ((ParticleLightningQuat) o).z == z
                && ((ParticleLightningQuat) o).s == s;
    }

    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringbuilder, Locale.US);
        formatter.format("Quaternion:%n");
        formatter.format("  < %f %f %f %f >%n", s, x, y, z);
        formatter.close();
        return stringbuilder.toString();
    }

    public static ParticleLightningQuat aroundAxis(ParticleLightningVector axis, double angle) {
        return aroundAxis(axis.x, axis.y, axis.z, angle);
    }

}
