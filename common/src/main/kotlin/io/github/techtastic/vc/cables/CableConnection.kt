package io.github.techtastic.vc.cables

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import org.joml.Vector3d
import org.joml.Vector4f
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.apigame.constraints.VSConstraintId
import org.valkyrienskies.core.apigame.constraints.VSRopeConstraint
import org.valkyrienskies.mod.common.allShips
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.squaredDistanceBetweenInclShips
import org.valkyrienskies.mod.common.util.toJOMLD

data class CableConnection(val pointA: BlockPos, val pointB: BlockPos, val shipA: Ship?, val shipB: Ship?, var ropeConstraintId: VSConstraintId?, val maxDistance: Double, val type: CableConnectionType) {
    companion object {
        fun createWorldToWorld(pointA: BlockPos, pointB: BlockPos, maxDistance: Double) =
                CableConnection(pointA, pointB, null, null, null, maxDistance, CableConnectionType.WORLD_TO_WORLD)

        fun createShipToWorld(pointA: BlockPos, pointB: BlockPos, shipA: Ship, ropeConstraintId: Int, maxDistance: Double) =
                CableConnection(pointA, pointB, shipA, null, ropeConstraintId, maxDistance, CableConnectionType.SHIP_TO_WORLD)

        fun createWorldToShip(pointA: BlockPos, pointB: BlockPos, shipB: Ship, ropeConstraintId: Int, maxDistance: Double) =
                CableConnection(pointA, pointB, shipB, null, ropeConstraintId, maxDistance, CableConnectionType.WORLD_TO_SHIP)

        fun createShipToShip(pointA: BlockPos, pointB: BlockPos, shipA: Ship, shipB: Ship, ropeConstraintId: Int, maxDistance: Double) =
                CableConnection(pointA, pointB, shipA, shipB, ropeConstraintId, maxDistance, CableConnectionType.SHIP_TO_SHIP)

        fun loadFromNbt(level: ServerLevel, tag: CompoundTag): CableConnection {
            val pointA = BlockPos.of(tag.getLong("vc\$pointA"))
            val pointB = BlockPos.of(tag.getLong("vc\$pointB"))
            val maxDistance = tag.getDouble("vc\$maxDistance")
            val type = CableConnectionType.valueOf(tag.getString("vc\$type"))

            val shipA = if (tag.contains("vc\$shipA"))
                level.allShips.getById(tag.getLong("vc\$shipA"))
            else
                null
            val shipB = if (tag.contains("vc\$shipB"))
                level.allShips.getById(tag.getLong("vc\$shipB"))
            else
                null

            val newRope = VSRopeConstraint(
                    shipA?.id ?: -1,
                    shipB?.id ?: -1,
                    1.0,
                    pointA.toJOMLD(),
                    pointB.toJOMLD(),
                    20.0,
                    maxDistance
            )

            val rope: VSConstraintId? = if (tag.contains("vc\$rope")) {
                level.server.shipObjectWorld.updateConstraint(tag.getInt("vc\$rope"), newRope)
                tag.getInt("vc\$rope")
            } else if (shipA != null || shipB != null)
                level.server.shipObjectWorld.createNewConstraint(newRope)
            else
                null

            return CableConnection(
                    pointA,
                    pointB,
                    shipA,
                    shipB,
                    rope,
                    maxDistance,
                    type
            )
        }
    }

    fun saveToNbt(tag: CompoundTag) {
        tag.putLong("vc\$pointA", pointA.asLong())
        tag.putLong("vc\$pointB", pointB.asLong())
        if (shipA != null)
            tag.putLong("vc\$shipA", shipA.id)
        if (shipB != null)
            tag.putLong("vc\$shipB", shipB.id)
        if (ropeConstraintId != null)
            tag.putInt("vc\$rope", ropeConstraintId!!)
        tag.putDouble("vc\$distance", maxDistance)
        tag.putString("vc\$type", type.name)
    }

    fun makeInitialConstraint(level: ServerLevel) {
        if (shipA == null && shipB == null)
            return

        val rope = VSRopeConstraint(
                shipA?.id ?: -1,
                shipB?.id ?: -1,
                1.0,
                pointA.toJOMLD(),
                pointB.toJOMLD(),
                20.0,
                maxDistance
        )

        ropeConstraintId = level.server.shipObjectWorld.createNewConstraint(rope)
    }

    fun getDistanceBetweenEndpoints(level: Level): Double =
            level.squaredDistanceBetweenInclShips(pointA.x.toDouble(), pointA.y.toDouble(), pointA.z.toDouble(),
                    pointB.x.toDouble(), pointB.y.toDouble(), pointB.z.toDouble())

    fun renderCable(level: ClientLevel, ps: PoseStack, buffer: MultiBufferSource) {
        when (type) {
            CableConnectionType.WORLD_TO_WORLD -> {
                //LevelRenderer.
            }

            CableConnectionType.WORLD_TO_SHIP -> TODO()
            CableConnectionType.SHIP_TO_WORLD -> TODO()
            CableConnectionType.SHIP_TO_SHIP -> TODO()
        }
    }

    fun drawCable(ps: PoseStack, buffer: MultiBufferSource, level: ClientLevel, partialTick: Float) {
        val endpointA: Vector3d = pointA.toJOMLD()
        val endpointB: Vector3d = pointB.toJOMLD()
        val color = Vector4f(0.5f, 1.0f, 0.5f, 1f)
        val normal = Vector3d(endpointA.sub(endpointB)).normalize()
        val origin = getMinCorner(endpointA, endpointB)
        val vb = buffer.getBuffer(RenderType.lines())



        /*
        val nodeAstate = level.getBlockState(conduit.getPosition().getNodeApos());
        val nodeBstate = level.getBlockState(conduit.getPosition().getNodeBpos());
        if (nodeAstate.getBlock() is IConduitConnector nodeAconnector && nodeBstate.getBlock() is IConduitConnector nodeBconnector) {
            ConduitNode nodeA = nodeAconnector . getConduitNode (level, conduit.getPosition().getNodeApos(), nodeAstate, conduit.getPosition().getNodeAid());
            ConduitNode nodeB = nodeBconnector . getConduitNode (level, conduit.getPosition().getNodeBpos(), nodeBstate, conduit.getPosition().getNodeBid());
            if (nodeA != null && nodeB != null) {
                Vec3d nodeAworldPosition = nodeA . getWorldPosition (level, conduit.getPosition().getNodeApos());
                Vec3d nodeBworldPosition = nodeB . getWorldPosition (level, conduit.getPosition().getNodeBpos());

                Vec4f color = new Vec4f(0.5F, 1.0F, 0.5F, 1F); // TODO Color representing physic-mode

                Vec3f normal = new Vec3f(nodeAworldPosition.sub(nodeBworldPosition)).normalize();
                Vec3d nodeOrigin = MathUtility . getMinCorner (nodeAworldPosition, nodeBworldPosition).sub(0.5, 0.5, 0.5);

                VertexConsumer vertexconsumer = bufferSource . getBuffer (RenderType.lines());

                for (int i = 0; i < conduit.getShape().nodes.length - 1; i++) {

                    Vec3d node1 = conduit . getShape ().nodes[i].add(nodeOrigin);
                    Vec3d node2 = conduit . getShape ().nodes[i + 1].add(nodeOrigin);

                    vertexconsumer.vertex(matrixStack.last().pose(), (float) node1 . x, (float) node1 . y, (float) node1 . z).color(color.x * 0.5F, color.y * 0.5F, color.z * 0.5F, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
                    vertexconsumer.vertex(matrixStack.last().pose(), (float) node2 . x, (float) node2 . y, (float) node2 . z).color(color.x * 0.5F, color.y * 0.5F, color.z * 0.5F, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();


                }
                vertexconsumer.vertex(matrixStack.last().pose(), (float) nodeAworldPosition . x, (float) nodeAworldPosition . y, (float) nodeAworldPosition . z).color(color.x, color.y, color.z, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
                vertexconsumer.vertex(matrixStack.last().pose(), (float) nodeBworldPosition . x, (float) nodeBworldPosition . y, (float) nodeBworldPosition . z).color(color.x, color.y, color.z, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
            }
        }*/
    }

    fun getMinCorner(pos1: Vector3d, pos2: Vector3d): Vector3d {
        return Vector3d(
                pos1.x.coerceAtMost(pos2.x),
                pos1.y.coerceAtMost(pos2.y),
                pos1.z.coerceAtMost(pos2.z)
        )
    }
}
