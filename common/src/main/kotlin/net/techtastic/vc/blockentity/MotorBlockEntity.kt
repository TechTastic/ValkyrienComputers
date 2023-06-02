package net.techtastic.vc.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.techtastic.vc.integrations.cc.ComputerCraftBlockEntities
import net.techtastic.vc.integrations.cc.ComputerCraftBlocks
import net.techtastic.vc.ship.*
import org.joml.*
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.constraints.VSConstraintAndId
import org.valkyrienskies.core.apigame.constraints.VSConstraintId
import org.valkyrienskies.core.apigame.constraints.VSHingeOrientationConstraint
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl.Companion.create
import org.valkyrienskies.mod.common.*
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import java.lang.Math
import kotlin.math.roundToInt

class MotorBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(ComputerCraftBlockEntities.MOTOR.get(), pos, state) {
    companion object {
        val NO_SHIPTRAPTION_ID: Long = -1
    }

    protected var angle = 0f
    protected var assembleNextTick = false

    private var shipID: Long = NO_SHIPTRAPTION_ID

    private var prevAngle = 0f

    private var motorID: Int? = null

    //var motorId : VSConstraintId? = null
    var hingeId : VSConstraintId? = null
    var attachmentConstraintId : VSConstraintId? = null
    var otherPos : BlockPos? = null
    var shipIds : List<Long>? = null
    var activated = false
    var reversed = false
    private var controlData : MotorControlData? = null

    override fun load(tag: CompoundTag) {
        super.load(tag)

        val angleBefore = angle
        activated = tag.getBoolean("ValkyrienComputers\$activated")
        reversed = tag.getBoolean("ValkyrienComputers\$reversed")
        angle = tag.getFloat("ValkyrienComputers\$angle")
        if (tag.contains("ValkyrienComputers\$motorId")) {
            motorID = tag.getInt("ValkyrienComputers\$motorId")
        }
        if (tag.contains("ValkyrienComputers\$shipId")) {
            shipID = tag.getLong("ValkyrienComputers\$shipId")
        }
        if (activated)
            if (shipID == NO_SHIPTRAPTION_ID)
                angle = angleBefore
        else
            shipID = NO_SHIPTRAPTION_ID


        shipIds = tag.getLongArray("shipIds").asList()

        /*if (tag.contains("otherPos")) {
            otherPos = BlockPos.of(tag.getLong("otherPos"))

            VSEvents.shipLoadEvent.on { (otherShip), handler ->
                if (otherShip.chunkClaim.contains(otherPos!!.x / 16, otherPos!!.z / 16)) {
                    handler.unregister()

                    if (!createConstraints()) {
                        VSEvents.shipLoadEvent.on { (ship), handler ->
                            if (ship.chunkClaim.contains(blockPos.x / 16, blockPos.z / 16)) {
                                handler.unregister()

                                if (!createConstraints(otherShip)) throw IllegalStateException("Could not create constraints for bearing block entity!")
                            }
                        }
                    }
                }
            }
        }*/
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.putBoolean("ValkyrienComputers\$activated", activated)
        tag.putBoolean("ValkyrienComputers\$reversed", reversed)
        tag.putFloat("ValkyrienComputers\$angle", angle)
        if (motorID != null) {
            tag.putInt("ValkyrienComputers\$motorId", motorID!!)
        }
        if (shipID != NO_SHIPTRAPTION_ID) {
            tag.putLong("ValkyrienComputers\$shipId", shipID)
        }

        shipIds?.let { tag.putLongArray("shipIds", it) }

        if (otherPos != null) {
            tag.putLong("otherPos", otherPos!!.asLong())
        }

        super.saveAdditional(tag)
    }

    override fun setRemoved() {
        super.setRemoved()
    }

    override fun clearRemoved() {
        super.clearRemoved()
    }

    fun <T : BlockEntity?> tick(level: Level?, pos: BlockPos?, state: BlockState?, be: T) {
        tick()

        /*if (level?.shipObjectWorld != null && !level.isClientSide) {
            val level = level as ServerLevel

            if (hingeId != null && shipIds != null) {
                //val shipId = shipIds!![0]
                val otherShipId = shipIds!![1]
                /*val lookingTowards = blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD()
                val x = Vector3d(1.0, 0.0, 0.0)
                val xCross = Vector3d(lookingTowards).cross(x)
                val hingeOrientation = if (xCross.lengthSquared() < 1e-6)
                    Quaterniond()
                else
                    Quaterniond(AxisAngle4d(lookingTowards.angle(x), xCross.normalize()))*/

                val otherShip = level.shipObjectWorld.loadedShips.getById(otherShipId)
                if (otherShip != null && otherShip is LoadedServerShip) {
                    var control = otherShip.getAttachment(MotorControl::class.java)
                    if (control == null) control = MotorControl()

                    controlData = pos?.let { level.getBlockState(it) }?.let { generateData(it) }
                    control.controlData = controlData

                    otherShip.setAttachment(MotorControl::class.java, control)
                }

                val ship = pos?.let { level.getShipManagingPos(it) }
                if (ship != null && ship is LoadedServerShip) {
                    var baseControl = ship.getAttachment(MotorBaseControl::class.java)
                    if (baseControl == null) baseControl = MotorBaseControl()

                    if (controlData != null) baseControl.motorsMap[pos] = controlData!!

                    ship.setAttachment(MotorBaseControl::class.java, baseControl)
                }
            }
        }*/
    }

    fun assemble() {
        if (level!!.isClientSide) return

        val level = level as ServerLevel

        val direction: Direction = blockState.getValue(BlockStateProperties.FACING)
        val center = worldPosition.relative(direction)

        val shipOn : ServerShip? = level.getShipObjectManagingPos(worldPosition)

        // does other ship exist here?
        var otherShip : ServerShip? = level.getShipManagingPos(center)
        if (otherShip == null) {
            val inWorldPos = worldPosition.toJOMLD().let {
                shipOn?.shipToWorld?.transformPosition(it) ?: it
            }
            val inWorldBlockPos = BlockPos(inWorldPos.x, inWorldPos.y, inWorldPos.z)

            otherShip = level.shipObjectWorld.createNewShipAtBlock(
                    inWorldBlockPos.offset(blockState.getValue(BlockStateProperties.FACING).normal).toJOML(),
                    false,
                    shipOn?.transform?.shipToWorldScaling?.x() ?: 1.0,
                    level.dimensionId
            )

            val shipCenterPos = BlockPos(
                    (otherShip.transform.positionInShip.x() - 0.5).roundToInt(),
                    (otherShip.transform.positionInShip.y() - 0.5).roundToInt(),
                    (otherShip.transform.positionInShip.z() - 0.5).roundToInt()
            )

            val towards = blockState.getValue(BlockStateProperties.FACING)
            val topPos = shipCenterPos.offset(towards.normal)

            level.setBlock(topPos, ComputerCraftBlocks.TOP.get().defaultBlockState()
                    .setValue(BlockStateProperties.FACING, towards), 11)

            topPos to otherShip
        }

        shipID = otherShip.id

        //bearing data
        val pos: Vector3dc = worldPosition.toJOMLD()
        val axis: Vector3dc = direction.normal.toJOMLD()
        var otherShipID: ShipId? = level.shipObjectWorld.dimensionToGroundBodyIdImmutable.get(level.dimensionId)
        if (shipOn != null)
            otherShipID = shipOn.id
        val rotationQuaternion: Quaterniond = when (direction) {
            Direction.DOWN ->
                Quaterniond(AxisAngle4d(Math.PI, Vector3d(1.0, 0.0, 0.0)))
            Direction.NORTH ->
                Quaterniond(AxisAngle4d(Math.PI, Vector3d(0.0, 1.0, 0.0))).mul(Quaterniond(AxisAngle4d(Math.PI / 2.0, Vector3d(1.0, 0.0, 0.0)))).normalize()
            Direction.EAST ->
                Quaterniond(AxisAngle4d(0.5 * Math.PI, Vector3d(0.0, 1.0, 0.0))).mul(Quaterniond(AxisAngle4d(Math.PI / 2.0, Vector3d(1.0, 0.0, 0.0)))).normalize()
            Direction.SOUTH ->
                Quaterniond(AxisAngle4d(Math.PI / 2.0, Vector3d(1.0, 0.0, 0.0))).normalize()
            Direction.WEST ->
                Quaterniond(AxisAngle4d(1.5 * Math.PI, Vector3d(0.0, 1.0, 0.0))).mul(Quaterniond(AxisAngle4d(Math.PI / 2.0, Vector3d(1.0, 0.0, 0.0)))).normalize()
            else ->
                // UP or null
                Quaterniond()
        }
        val posInOwnerShip: Vector3dc = worldPosition.relative(blockState.getValue(BlockStateProperties.FACING), 1).toJOMLD().add(0.5, 0.5, 0.5)
        var posInWorld: Vector3dc? = otherShip.transform.positionInWorld // posInOwnerShip;
        var rotInWorld: Quaterniondc = Quaterniond()
        var scaling: Vector3dc = Vector3d(1.0, 1.0, 1.0)
        val shipChunkX = otherShip.chunkClaim.xMiddle
        val shipChunkZ = otherShip.chunkClaim.zMiddle
        val centerInShip: Vector3dc = Vector3d(
                (
                        (shipChunkX shl 4) + (center.x and 15)).toDouble(), center.y.toDouble(),
                (
                        (shipChunkZ shl 4) + (center.z and 15)
                        ).toDouble()
        )
        if (shipOn != null) {
            scaling = shipOn.transform.shipToWorldScaling
            val offset: Vector3dc = otherShip.inertiaData.centerOfMassInShip.sub(centerInShip, Vector3d())
            posInWorld = shipOn.transform.shipToWorld.transformPosition(posInOwnerShip.add(offset, Vector3d()), Vector3d())
            rotInWorld = shipOn.transform.shipToWorldRotation
        }
        (otherShip as ShipDataCommon).transform = create(posInWorld!!, otherShip.inertiaData.centerOfMassInShip, rotInWorld, scaling)
        val bearingPos: Vector3dc = centerInShip.add(0.5, 0.5, 0.5, Vector3d())
        val constraint = VSAttachmentConstraint(otherShip.id, otherShipID!!, 1e-10, bearingPos, posInOwnerShip, 1e10, 0.0)
        val hingeOrientation: Quaterniondc = rotationQuaternion.mul(Quaterniond(AxisAngle4d(Math.toRadians(90.0), 0.0, 0.0, 1.0)), Quaterniond()).normalize()
        val hingeConstraint = VSHingeOrientationConstraint(otherShip.id, otherShipID, 1e-8, hingeOrientation, hingeOrientation, 1e10)

        // Add position damping to make the hinge more stable
        // VSPosDampingConstraint posDampingConstraint = new VSPosDampingConstraint(shiptraptionID, otherShipID, 1e-10, posInBearingContraption, posInOwnerShip, 1e10, 1e-2);

        // Add rotation damping to make the hinge more stable
        // VSRotDampingConstraint perpendicularRotDampingConstraint = new VSRotDampingConstraint(shiptraptionID, otherShipID, 1e-10, hingeOrientation, hingeOrientation, 1e10, 1e-2, VSRotDampingAxes.ALL_AXES);
        val constraintID: VSConstraintId? = level.shipObjectWorld.createNewConstraint(constraint)
        val hingeID: VSConstraintId? = level. shipObjectWorld.createNewConstraint(hingeConstraint)
        // Integer posDamperID = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).createNewConstraint(posDampingConstraint);
        // Integer rotDamperID = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).createNewConstraint(perpendicularRotDampingConstraint);
        val contraptionConstraint = constraintID?.let { VSConstraintAndId(it, constraint) }
        val hingeContraptionConstraint = hingeID?.let { VSConstraintAndId(it, hingeConstraint) }
        // VSConstraintAndId posDampingContraptionConstraint = new VSConstraintAndId(posDamperID, posDampingConstraint);
        // VSConstraintAndId rotDampingContraptionConstraint = new VSConstraintAndId(rotDamperID, perpendicularRotDampingConstraint);
        val data = MotorCreateData(pos, axis, angle.toDouble(), 64.0f, activated, otherShipID, contraptionConstraint, hingeContraptionConstraint, null, null)
        if (!level!!.isClientSide) {
            shipID = MotorController.getOrCreate(otherShip).addPhysBearing(data)
        }
        activated = false
        angle = 0f
    }

    fun destroy() {
        if (level != null) {
            if (!level!!.isClientSide) {
                var level = level as ServerLevel?
                val ship: ServerShip? = level.shipObjectWorld.allShips.getById(shipID)
                if (ship != null) {
                    val controller: MotorController = MotorController.getOrCreate(ship)
                    val attachID: Int? = controller.motorData.get(motorID)?.attachID
                    if (attachID != null)
                        level.shipObjectWorld.removeConstraint(attachID)

                    val hingeID: Int? = controller.motorData.get(motorID)?.hingeID
                    if (hingeID != null)
                        level.shipObjectWorld.removeConstraint(hingeID)

                    motorID?.let { controller.removePhysBearing(it) }
                }
            }
        }
    }

    fun tick() {
        if (!level!!.isClientSide) {
            val level = level as ServerLevel

            val ship: ServerShip? = level.shipObjectWorld.allShips.getById(shipID)
            if (ship != null && motorID != null) {
                val bearingData: MotorData? = MotorController.getOrCreate(ship).motorData.get(motorID)
                if (bearingData != null) {
                    val (shipId0, _, compliance, localPos0, localPos1, maxForce, fixedDistance) = bearingData.attachConstraint
                    val (shipId01, _, compliance1, localRot0, localRot1, maxTorque) = bearingData.hingeConstraint

                    //todo TEMP, REPLACE ONCE TRIODE FIXES
                    val shipOn: Ship? = level.getShipManagingPos(worldPosition)
                    var shipOnID: ShipId? = level.shipObjectWorld.dimensionToGroundBodyIdImmutable.get(level!!.dimensionId)
                    if (shipOn != null) {
                        shipOnID = shipOn.id
                    } else {
                        // The ship was deleted, delete this bearing
                        if (level.isBlockInShipyard(worldPosition)) {
                            activated = false
                            assembleNextTick = false
                            return
                        }
                    }
                    val attachConstraint = shipOnID?.let { VSAttachmentConstraint(shipId0, it, compliance, localPos0, localPos1, maxForce, fixedDistance) }
                    val hingeConstraint = shipOnID?.let { VSHingeOrientationConstraint(shipId01, it, compliance1, localRot0, localRot1, maxTorque) }
                    //todo
                    var createdAttachment = false
                    var createdHinge = false
                    if (attachConstraint != null) {
                        val attachID: VSConstraintId? = level.shipObjectWorld.createNewConstraint(attachConstraint)
                        if (attachID != null) {
                            MotorController.getOrCreate(ship).motorData.get(motorID)?.attachConstraint = attachConstraint
                            MotorController.getOrCreate(ship).motorData.get(motorID)?.attachID = attachID
                            createdAttachment = true
                        }
                    }
                    if (hingeConstraint != null) {
                        val hingeID: VSConstraintId? = level.shipObjectWorld.createNewConstraint(hingeConstraint)
                        if (hingeID != null) {
                            MotorController.getOrCreate(ship).motorData.get(motorID)?.hingeConstraint = hingeConstraint
                            MotorController.getOrCreate(ship).motorData.get(motorID)?.hingeID = hingeID
                            createdHinge = true
                        }
                    }
                }
            }
        }
        if (!activated) return
        if (shipID != NO_SHIPTRAPTION_ID) {
            val angularSpeed: Float = 64f
            val newAngle = angle + angularSpeed
            angle = (newAngle % 360)
        }
        if (activated) {
            if (!level!!.isClientSide) {
                if (shipID != NO_SHIPTRAPTION_ID) {
                    val ship: ServerShip? = (level as ServerLevel?).shipObjectWorld.allShips.getById(shipID)
                    if (ship != null) {
                        //todo add locked mode
                        if (MotorController.getOrCreate(ship).motorData.get(motorID) == null)
                            return
                        val (shipId0, shipId1, compliance, localRot0, localRot1, maxTorque) = MotorController.getOrCreate(ship).motorData.get(motorID)!!.hingeConstraint
                        val (shipId01, shipId11, compliance1, localRot01, localRot11, maxTorque1) = MotorController.getOrCreate(ship).motorData.get(motorID)!!.angleConstraint!!
                        //                        if (PhysBearingController.getOrCreate(ship).bearingData.get(bearingID).hingeID == null) {
//                            return;
//                        }
//                        if (movementMode.get() == LockedMode.LOCKED) {
//                            Vector3dc facing = VectorConversionsMCKt.toJOMLD(getBlockState().getValue(BlockStateProperties.FACING).getNormal());
//                            Quaterniond localRot0 = new Quaterniond(hingeConstraint.getLocalRot0());
//                            localRot0 = localRot0.premul(new Quaterniond(new AxisAngle4d(Math.toRadians(angle), facing))).normalize();
//                            angleConstraint = new VSFixedOrientationConstraint(hingeConstraint.getShipId0(), hingeConstraint.getShipId1(), 1e-10, localRot0, hingeConstraint.getLocalRot1(), 1e8);
//                            VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).updateConstraint(PhysBearingController.getOrCreate(ship).bearingData.get(bearingID).hingeID, angleConstraint);
//                        } else if (movementMode.get() == LockedMode.UNLOCKED) {
//                            hingeConstraint = PhysBearingController.getOrCreate(ship).bearingData.get(bearingID).hingeConstraint;
//                            VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).updateConstraint(PhysBearingController.getOrCreate(ship).bearingData.get(bearingID).hingeID, hingeConstraint);
//
//                        }
                        val data = MotorUpdateData(angle.toDouble(), 64f, activated, null, null)
                        motorID?.let { MotorController.getOrCreate(ship).updatePhysBearing(it, data) }
                    }
                }
            }
        }
    }

    /*fun makeOrGetTop(level: ServerLevel, pos: BlockPos) {
        if (level.isClientSide) return

        val lookingTowards = blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD()

        val ship = level.getShipObjectManagingPos(blockPos)

        val clipResult = level.clipIncludeShips(
                ClipContext(
                        (Vector3d(basePoint()).let {
                            ship?.shipToWorld?.transformPosition(it) ?: it
                        }).toMinecraft(),
                        (blockPos.toJOMLD()
                                .add(0.5, 0.5,0.5)
                                .add(Vector3d(lookingTowards).mul(0.8))
                                .let {
                                    ship?.shipToWorld?.transformPosition(it) ?: it
                                }).toMinecraft(),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        null), false)

        val (otherAttachmentPoint, otherShip) = if (clipResult.type == HitResult.Type.MISS) {
            val inWorldPos = blockPos.toJOMLD().let {
                ship?.shipToWorld?.transformPosition(it) ?: it
            }
            val inWorldBlockPos = BlockPos(inWorldPos.x, inWorldPos.y, inWorldPos.z)

            val otherShip = level.shipObjectWorld.createNewShipAtBlock(
                    inWorldBlockPos.offset(blockState.getValue(BlockStateProperties.FACING).normal).toJOML(),
                    false,
                    ship?.transform?.shipToWorldScaling?.x() ?: 1.0,
                    level.dimensionId
            )

            val shipCenterPos = BlockPos(
                    (otherShip.transform.positionInShip.x() - 0.5).roundToInt(),
                    (otherShip.transform.positionInShip.y() - 0.5).roundToInt(),
                    (otherShip.transform.positionInShip.z() - 0.5).roundToInt()
            )

            val towards = blockState.getValue(BlockStateProperties.FACING)
            val topPos = shipCenterPos.offset(towards.normal)

            level.setBlock(topPos, ComputerCraftBlocks.TOP.get().defaultBlockState()
                    .setValue(BlockStateProperties.FACING, towards), 11)

            topPos to otherShip
        } else {
            level.getShipObjectManagingPos(clipResult.blockPos)?.let { otherShip ->
                val otherPos = clipResult.blockPos.offset(blockState.getValue(BlockStateProperties.FACING).opposite.normal)

                if (!level.getBlockState(otherPos).`is`(ComputerCraftBlocks.TOP.get())) {
                    level.setBlock(
                            otherPos, ComputerCraftBlocks.TOP.get().defaultBlockState().setValue(
                            BlockStateProperties.FACING, blockState.getValue(BlockStateProperties.FACING)
                    ), 11)
                }

                otherPos to otherShip
            } ?: (clipResult.blockPos to null)
        }

        this.otherPos = otherAttachmentPoint
        createConstraints(otherShip)
        if (otherShip is LoadedServerShip) {
            otherShip.let {
                val control = MotorControl()
                control.controlData = controlData
                it.setAttachment(MotorControl::class.java, control)
            }
        }
    }*/

    /*fun createConstraints(otherShip: ServerShip? = null): Boolean {
        if (otherPos != null && level != null
                && !level!!.isClientSide) {
            val level = level as ServerLevel
            val shipId = level.getShipObjectManagingPos(blockPos)?.id ?: level.shipObjectWorld.dimensionToGroundBodyIdImmutable[level.dimensionId]!!
            val otherShipId = otherShip?.id ?: level.getShipObjectManagingPos(otherPos!!)?.id ?: level.shipObjectWorld.dimensionToGroundBodyIdImmutable[level.dimensionId]!!

            // If both ships aren't loaded don't make the constraint (itl crash vs2)
            if (level.getShipManagingPos(blockPos) != null && level.getShipManagingPos(blockPos)!!.id != shipId) return false
            if (level.getShipManagingPos(otherPos!!) != null && level.getShipManagingPos(otherPos!!)!!.id != otherShipId) return false

            // Orientation
            val lookingTowards = blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD()
            val x = Vector3d(1.0, 0.0, 0.0)
            val xCross = Vector3d(lookingTowards).cross(x)
            val hingeOrientation = if (xCross.lengthSquared() < 1e-6)
                Quaterniond()
            else
                Quaterniond(AxisAngle4d(lookingTowards.angle(x), xCross.normalize()))

            /*val motorConstraint = otherShip?.transform?.let {
                VSHingeTargetAngleConstraint(
                        shipId, otherShipId, constraintComplience, hingeOrientation, hingeOrientation, maxForce, currentAngle, currentAngle
                )
            }*/

            val hingeConstraint = VSHingeOrientationConstraint(
                    shipId, otherShipId, constraintComplience, hingeOrientation, hingeOrientation, maxForce
            )

            val attachmentConstraint = VSAttachmentConstraint(
                    shipId, otherShipId, constraintComplience, basePoint(), otherPoint()!!,
                    maxForce, baseOffset
            )

            //motorId = motorConstraint?.let { level.shipObjectWorld.createNewConstraint(it) }
            shipIds = listOf(shipId, otherShipId)
            hingeId = level.shipObjectWorld.createNewConstraint(hingeConstraint)
            attachmentConstraintId = level.shipObjectWorld.createNewConstraint(attachmentConstraint)
            this.setChanged()
            return true
        }

        return false
    }

    fun destroyConstraints() {
        if (!level!!.isClientSide) {
            val level = level as ServerLevel

            val otherShipId = shipIds?.get(1)
            val otherShip = otherShipId?.let { level.shipObjectWorld.loadedShips.getById(it) }
            if (otherShip != null) {
                otherShip.getAttachment(MotorControl::class.java)?.controlData = null
            }

            shipIds = null
            currentAngle = 0.0
            hingeId?.let { level.shipObjectWorld.removeConstraint(it) }
            //hingeId = null
            attachmentConstraintId?.let { level.shipObjectWorld.removeConstraint(it) }
            //attachmentConstraintId = null
            otherPos = null

            this.setChanged()
        }
    }

    private fun generateData(state : BlockState) : MotorControlData {
        return MotorControlData(
                activated,
                reversed,
                state.getValue(BlockStateProperties.FACING),
                2.0,
                null,
                level!!.getShipManagingPos(blockPos) == null
        )
    }

    private fun basePoint(): Vector3d = blockPos.toJOMLD()
            .add(0.5, 0.5,0.5)
            .add(blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD().mul(0.5 + baseOffset))

    private fun otherPoint(): Vector3d? = otherPos?.toJOMLD()
            ?.add(0.5, 0.5, 0.5)
            ?.add(blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD().mul(0.5))

    companion object {
        private val baseOffset = 0.0
        private val constraintComplience = 1e-10
        private val maxForce = 1e10
    }*/
}