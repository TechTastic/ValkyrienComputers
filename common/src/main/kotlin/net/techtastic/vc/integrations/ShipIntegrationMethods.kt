package net.techtastic.vc.integrations

import org.valkyrienskies.core.api.ships.ServerShip
import kotlin.math.atan2
import kotlin.math.sqrt

class ShipIntegrationMethods {
    companion object {
        fun getRotationFromShip(ship: ServerShip, quat: Boolean): Map<String, Double> {
            val q = ship.transform.shipToWorldRotation

            return if (quat)
                mapOf(
                        Pair("x", q.x()),
                        Pair("y", q.y()),
                        Pair("z", q.z()),
                        Pair("w", q.w())
                )
            else
                mapOf(
                        Pair("roll",
                                atan2(2 * (q.w() * q.x() + q.y() * q.z()), 1 - 2 * (q.x() * q.x() + q.y() * q.y()))),
                        Pair("pitch",
                                2 * atan2(sqrt(1 + 2 * (q.w() * q.y() - q.x() * q.z())), sqrt(1 - 2 * (q.w() * q.y() - q.x() * q.z()))) - Math.PI / 2),
                        Pair("yaw",
                                atan2(2 * (q.w() * q.z() + q.x() * q.y()), 1 - 2 * (q.y() * q.y() + q.z() * q.z()))),
                )
        }

        fun getVelocityFromShip(ship: ServerShip): Map<String, Double> {
            val vel = ship.velocity
            return mapOf(
                    Pair("x", vel.x()),
                    Pair("y", vel.y()),
                    Pair("z", vel.z())
            )
        }

        fun getPositionFromShip(ship: ServerShip, shipyard : Boolean): Map<String, Double> {
            val pos = if (shipyard) ship.transform.positionInShip else ship.transform.positionInWorld

            return mapOf(
                    Pair("x", pos.x()),
                    Pair("y", pos.y()),
                    Pair("z", pos.z())
            )
        }

        fun getNameFromShip(ship: ServerShip): String = ship.slug ?: "no-name"

        fun setNameForShip(ship: ServerShip, name: String) {
            ship.slug = name
        }

        fun getScaleFromShip(ship: ServerShip): Map<String, Double> {
            val scale = ship.transform.shipToWorldScaling

            return mapOf(
                    Pair("x", scale.x()),
                    Pair("y", scale.y()),
                    Pair("z", scale.z())
            )
        }

        fun getIdFromShip(ship: ServerShip): Long = ship.id

        fun getMassFromShip(ship: ServerShip): Double = ship.inertiaData.mass

        fun getSizeFromShip(ship: ServerShip) : Map<String, Int> {
            val aabb = ship.shipAABB!!
            return mapOf(
                    Pair("x", aabb.maxX() - aabb.minX()),
                    Pair("y", aabb.maxY() - aabb.minY()),
                    Pair("z", aabb.maxZ() - aabb.minZ())
            )
        }
    }
}