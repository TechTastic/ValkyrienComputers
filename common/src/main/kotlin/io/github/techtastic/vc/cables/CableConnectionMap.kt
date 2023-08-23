package io.github.techtastic.vc.cables

import net.minecraft.core.BlockPos

class CableConnectionMap {
    val map = mutableSetOf<CableConnection>()

    fun getConnectionOrNull(pos: BlockPos): CableConnection? {
        map.forEach {
            if (it.pointA == pos || it.pointB == pos)
                return it
        }
        return null
    }

    fun addConnection(pointA: BlockPos, pointB: BlockPos) =
            map.add(CableConnection.createDefault(pointA, pointB))

    fun addConnection(pointA: BlockPos, pointB: BlockPos, length: Double) =
            map.add(CableConnection.createWithLength(pointA, pointB, length))

    fun addConnection(connection: CableConnection) =
            map.add(connection)

    fun removeConnection(connection: CableConnection) =
            map.remove(connection)
}