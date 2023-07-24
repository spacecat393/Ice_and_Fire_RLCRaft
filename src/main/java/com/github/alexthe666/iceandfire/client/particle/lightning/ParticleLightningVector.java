package com.github.alexthe666.iceandfire.client.particle.lightning;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/*
 * This class was created by <ChickenBones>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
public class ParticleLightningVector
{
    public static final ParticleLightningVector ZERO = new ParticleLightningVector(0, 0, 0);

    public final double x;
    public final double y;
    public final double z;

    public ParticleLightningVector(double d, double d1, double d2) {
        x = d;
        y = d1;
        z = d2;
    }

    public ParticleLightningVector(Vec3d vec) {
        this(vec.x, vec.y, vec.z);
    }

    public static ParticleLightningVector fromBlockPos(BlockPos pos) {
        return new ParticleLightningVector(pos.getX(), pos.getY(), pos.getZ());
    }

    public static ParticleLightningVector fromEntity(Entity e) {
        return new ParticleLightningVector(e.posX, e.posY, e.posZ);
    }

    public static ParticleLightningVector fromEntityCenter(Entity e) {
        return new ParticleLightningVector(e.posX, e.posY - e.getYOffset() + e.height / 2, e.posZ);
    }

    public static ParticleLightningVector fromTileEntity(TileEntity e) {
        return fromBlockPos(e.getPos());
    }

    public static ParticleLightningVector fromTileEntityCenter(TileEntity e) {
        return fromTileEntity(e).add(0.5);
    }

    public double dotProduct(ParticleLightningVector vec) {
        double d = vec.x * x + vec.y * y + vec.z * z;

        if(d > 1 && d < 1.00001)
            d = 1;
        else if(d < -1 && d > -1.00001)
            d = -1;
        return d;
    }

    public double dotProduct(double d, double d1, double d2) {
        return d * x + d1 * y + d2 * z;
    }

    public ParticleLightningVector crossProduct(ParticleLightningVector vec) {
        double d = y * vec.z - z * vec.y;
        double d1 = z * vec.x - x * vec.z;
        double d2 = x * vec.y - y * vec.x;
        return new ParticleLightningVector(d, d1, d2);
    }

    public ParticleLightningVector add(double d, double d1, double d2) {
        return new ParticleLightningVector(x + d, y + d1, z + d2);
    }

    public ParticleLightningVector add(ParticleLightningVector vec) {
        return add(vec.x, vec.y, vec.z);
    }

    public ParticleLightningVector add(double d) {
        return add(d, d, d);
    }

    public ParticleLightningVector subtract(ParticleLightningVector vec) {
        return new ParticleLightningVector(x - vec.x, y - vec.y, z - vec.z);
    }

    public ParticleLightningVector multiply(double d) {
        return multiply(d, d, d);
    }

    public ParticleLightningVector multiply(ParticleLightningVector f) {
        return multiply(f.x, f.y, f.z);
    }

    public ParticleLightningVector multiply(double fx, double fy, double fz) {
        return new ParticleLightningVector(x * fx, y * fy, z * fz);
    }

    public double mag() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double magSquared() {
        return x * x + y * y + z * z;
    }

    public ParticleLightningVector normalize() {
        double d = mag();
        if(d != 0)
            return multiply(1 / d);

        return this;
    }

    @Override
    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "Vector3(" + new BigDecimal(x, cont) + ", " +new BigDecimal(y, cont) + ", " + new BigDecimal(z, cont) + ")";
    }

    public ParticleLightningVector perpendicular() {
        if(z == 0)
            return zCrossProduct();
        return xCrossProduct();
    }

    public ParticleLightningVector xCrossProduct() {
        double d = z;
        double d1 = -y;
        return new ParticleLightningVector(0, d, d1);
    }

    public ParticleLightningVector zCrossProduct() {
        double d = y;
        double d1 = -x;
        return new ParticleLightningVector(d, d1, 0);
    }

    public ParticleLightningVector yCrossProduct() {
        double d = -z;
        double d1 = x;
        return new ParticleLightningVector(d, 0, d1);
    }

    public Vec3d toVec3D() {
        return new Vec3d(x, y, z);
    }

    public double angle(ParticleLightningVector vec) {
        return Math.acos(normalize().dotProduct(vec.normalize()));
    }

    public boolean isInside(AxisAlignedBB aabb) {
        return x >= aabb.minX && y >= aabb.maxY && z >= aabb.minZ && x < aabb.maxX && y < aabb.maxY && z < aabb.maxZ;
    }

    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    public boolean isAxial() {
        return x == 0 ? y == 0 || z == 0 : y == 0 && z == 0;
    }

    public Vector3f vector3f() {
        return new Vector3f((float)x, (float)y, (float)z);
    }

    public Vector4f vector4f() {
        return new Vector4f((float)x, (float)y, (float)z, 1);
    }

    @SideOnly(Side.CLIENT)
    public void glVertex() {
        GL11.glVertex3d(x, y, z);
    }

    public ParticleLightningVector negate() {
        return new ParticleLightningVector(-x, -y, -z);
    }

    public double scalarProject(ParticleLightningVector b) {
        double l = b.mag();
        return l == 0 ? 0 : dotProduct(b)/l;
    }

    public ParticleLightningVector project(ParticleLightningVector b) {
        double l = b.magSquared();
        if(l == 0) {
            return ZERO;
        }

        double m = dotProduct(b)/l;
        return b.multiply(m);
    }

    public ParticleLightningVector rotate(double angle, ParticleLightningVector axis) {
        return ParticleLightningQuat.aroundAxis(axis.normalize(), angle).rotate(this);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ParticleLightningVector))
            return false;

        ParticleLightningVector v = (ParticleLightningVector)o;
        return x == v.x && y == v.y && z == v.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
