package io.github.techtastic.vc.util

import org.joml.Quaterniondc
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon
import kotlin.math.atan2
import kotlin.math.sqrt

object ShipIntegrationMethods {
    fun getRotationAsRPY(ship: ServerShip): Map<String, Double>? {
        val rpy = convertQuaternionToRPY(ship.transform.shipToWorldRotation)
        return java.util.Map.of("roll", rpy.x, "pitch", rpy.y, "yaw", rpy.z)
    }

    fun getRotationAsQuaternion(ship: ServerShip): Map<String, Double>? {
        val q = ship.transform.shipToWorldRotation
        return java.util.Map.of("x", q.x(), "y", q.y(), "z", q.z(), "w", q.w())
    }

    fun getVelocityFromShip(ship: ServerShip): Map<String, Double>? {
        val vel = ship.velocity
        return java.util.Map.of("x", vel.x(), "y", vel.y(), "z", vel.z())
    }

    fun getCenterOfMassInWorld(ship: ServerShip): Map<String, Double>? {
        val com = ship.transform.positionInWorld
        return java.util.Map.of("x", com.x(), "y", com.y(), "z", com.z())
    }

    fun getCenterOfMassInShip(ship: ServerShip): Map<String, Double>? {
        val com = ship.transform.positionInShip
        return java.util.Map.of("x", com.x(), "y", com.y(), "z", com.z())
    }

    fun getNameFromShip(ship: ServerShip): String {
        return (ship as ShipDataCommon).name
    }

    fun setNameForShip(ship: ServerShip, name: String) {
        (ship as ShipDataCommon).name = name
    }

    fun getScaleFromShip(ship: ServerShip): Map<String, Double>? {
        val scale = ship.transform.shipToWorldScaling
        return java.util.Map.of("x", scale.x(), "y", scale.y(), "z", scale.z())
    }

    fun getIdFromShip(ship: ServerShip): Long {
        return ship.id
    }

    fun getMassFromShip(ship: ServerShip): Double {
        return ship.inertiaData.mass
    }

    fun getSizeFromShip(ship: ServerShip): Map<String, Int>? {
        val aabb = ship.shipAABB ?: return null
        return java.util.Map.of("x", aabb.maxX() - aabb.minX(),
                "y", aabb.maxY() - aabb.minY(),
                "z", aabb.maxZ() - aabb.minZ())
    }

    fun convertQuaternionToRPY(q: Quaterniondc): Vector3d {
        return Vector3d(atan2(2 * (q.w() * q.x() + q.y() * q.z()), 1 - 2 * (q.x() * q.x() + q.y() * q.y())),
                2 * atan2(sqrt(1 + 2 * (q.w() * q.y() - q.x() * q.z())), sqrt(1 - 2 * (q.w() * q.y() - q.x() * q.z()))) - Math.PI / 2,
                atan2(2 * (q.w() * q.z() + q.x() * q.y()), 1 - 2 * (q.y() * q.y() + q.z() * q.z())))
    }
}