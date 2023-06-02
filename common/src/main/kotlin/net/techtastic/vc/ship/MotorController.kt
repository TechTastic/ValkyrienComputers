package net.techtastic.vc.ship

import net.techtastic.vc.blockentity.MotorBlockEntity
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.HashMap
import kotlin.math.abs
import kotlin.math.sign

class MotorController : ShipForcesInducer {
    val motorData : HashMap<Int, MotorData> = hashMapOf()
    val motorUpdateData : ConcurrentHashMap<Int, MotorUpdateData> = ConcurrentHashMap()
    val createdMotors : ConcurrentLinkedQueue<Pair<Int, MotorCreateData>> = ConcurrentLinkedQueue()
    val removedMotors : ConcurrentLinkedQueue<Int> = ConcurrentLinkedQueue()
    var nextMotorID = 0;

    override fun applyForces(physShip: PhysShip) {
        // Do nothing, actual work is in applyForcesAndLookupPhysShips()
    }

    override fun applyForcesAndLookupPhysShips(physShip: PhysShip, lookupPhysShip: (ShipId) -> PhysShip?) {
        while (!createdMotors.isEmpty()) {
            val createData: Pair<Int, MotorCreateData> = createdMotors.remove()
            motorData[createData.component1()] = MotorData(
                    createData.component2().motorPos,
                    createData.component2().motorAxis,
                    createData.component2().motorAngle,
                    createData.component2().motorRPM,
                    createData.component2().locked,
                    createData.component2().shiptraptionID,
                    createData.component2().constraint,
                    createData.component2().hingeConstraint,
                    createData.component2().posDampConstraint,
                    createData.component2().rotDampConstraint
            )
        }

        while (!removedMotors.isEmpty()) {
            motorData.remove(removedMotors.remove())
        }

        for (pair in motorUpdateData) {
            val id = pair.key
            val data = pair.value

            val physData = motorData[id] ?: return
            physData.motorAngle = data.motorAngle
            physData.motorRPM = data.motorRPM
            physData.locked = data.locked
//            physData.hingeConstraint = data.hingeConstraint();
//            physData.angleConstraint = data.angleConstraint();
        }

        motorUpdateData.clear()

        for (data in motorData.values) {
            if (data.angleConstraint == null) {
                val physShipBearingIsOnId: Long = data.hingeConstraint.shipId1;
                if (physShipBearingIsOnId == MotorBlockEntity.NO_SHIPTRAPTION_ID) {
                    // Constraint connects to world
                    val torque: Vector3dc = computeRotationalForce(data, physShip as PhysShipImpl, null);
                    physShip.applyInvariantTorque(torque);
                } else {
                    val physShipBearingIsOn: PhysShip? = lookupPhysShip.invoke(physShipBearingIsOnId)
                    if (physShipBearingIsOn == null) {
                        val torque: Vector3dc = computeRotationalForce(data, physShip as PhysShipImpl, null);
                        physShip.applyInvariantTorque(torque);
                    } else {
                        val torque: Vector3dc = computeRotationalForce(data, physShip as PhysShipImpl, physShipBearingIsOn as PhysShipImpl);
                        physShip.applyInvariantTorque(torque);
                        physShipBearingIsOn.applyInvariantTorque(torque.mul(-1.0, Vector3d()));
                    }
                }
            }
        }
    }

    private fun computeRotationalForce(data: MotorData, physShip: PhysShipImpl, otherPhysShip: PhysShipImpl?) : Vector3dc {
        return if (data.locked)
            computeLockedRotationalForce(data, physShip, otherPhysShip)
        else
            computeUnlockedRotationalForce(data, physShip, otherPhysShip)
    }

    private fun computeUnlockedRotationalForce(data : MotorData, physShip : PhysShipImpl, otherPhysShip : PhysShipImpl?) : Vector3dc {
        if (data.motorAxis == null)
            return Vector3d()

        val bearingAxisInGlobal : Vector3d = Vector3d(data.motorAxis)
        otherPhysShip?.transform?.shipToWorldRotation?.transform(bearingAxisInGlobal)

        val idealRelativeOmega : Vector3d = bearingAxisInGlobal.mul(data.motorRPM.toDouble(), Vector3d()).mul((2 * Math.PI) / 60)
        val actualRelativeOmega : Vector3d = if (!physShip.isStatic)
            Vector3d(physShip.poseVel.omega)
        else {
            // TODO: What about static bodies that are moving?
            Vector3d()
        }

        val torqueMassMultiplier : Double
        if (!physShip.isStatic)
            if (otherPhysShip != null && !otherPhysShip.isStatic) {
                torqueMassMultiplier = physShip.inertia.shipMass.coerceAtMost(otherPhysShip.inertia.shipMass)
                // Sub otherPhysShip angularVel from actualRelativeOmega if otherPhysShip is not static
                // TODO: What about static bodies that are moving?
                actualRelativeOmega.sub(otherPhysShip.poseVel.omega)
            } else
                torqueMassMultiplier = physShip.inertia.shipMass
        else if (otherPhysShip != null && !otherPhysShip.isStatic) {
            // Set it to be the mass of otherPhysShip
            torqueMassMultiplier = otherPhysShip.inertia.shipMass
            // Sub otherPhysShip angularVel from actualRelativeOmega if otherPhysShip is not static
            // TODO: What about static bodies that are moving?
            actualRelativeOmega.sub(otherPhysShip.poseVel.omega)
        } else
            return Vector3d()

        val angularVelError : Vector3dc = idealRelativeOmega.sub(actualRelativeOmega, Vector3d())
        val angularVelErrorAlongBearingAxis : Vector3dc = bearingAxisInGlobal.mul(bearingAxisInGlobal.dot(angularVelError), Vector3d())
        // Only apply torque on the bearing axis
        return angularVelErrorAlongBearingAxis.mul(torqueMassMultiplier * 10.0, Vector3d())
    }

    private fun computeLockedRotationalForce(data : MotorData, physShip : PhysShipImpl, otherPhysShip : PhysShipImpl?) : Vector3dc {
        if (data.motorAxis == null)
            return Vector3d()

        val bearingAxisInGlobal : Vector3d = Vector3d(data.motorAxis)
        otherPhysShip?.transform?.shipToWorldRotation?.transform(bearingAxisInGlobal)

        val actualRelativeOmega : Vector3d = if (!physShip.isStatic)
            Vector3d(physShip.poseVel.omega)
        else {
            // TODO: What about static bodies that are moving?
            Vector3d()
        }

        val torqueMassMultiplier : Double
        if (!physShip.isStatic) {
            if (otherPhysShip != null && !otherPhysShip.isStatic) {
                torqueMassMultiplier = Math.min(physShip.inertia.shipMass, otherPhysShip.inertia.shipMass)
                // Sub otherPhysShip angularVel from actualRelativeOmega if otherPhysShip is not static
                // TODO: What about static bodies that are moving?
                actualRelativeOmega.sub(otherPhysShip.poseVel.omega)
            } else {
                torqueMassMultiplier = physShip.inertia.shipMass
            }
        } else if (otherPhysShip != null && !otherPhysShip.isStatic){
            // Set it to be the mass of otherPhysShip
            torqueMassMultiplier = otherPhysShip.inertia.shipMass
            // Sub otherPhysShip angularVel from actualRelativeOmega if otherPhysShip is not static
            // TODO: What about static bodies that are moving?
            actualRelativeOmega.sub(otherPhysShip.poseVel.omega)
        } else {
            return Vector3d()
        }


        // Only apply torque on the bearing axis


        //Proportional

        val perpendicularAxis : Vector3dc = if (abs(data.motorAxis.x()) == 1.0)
            Vector3d(0.0, 1.0, 0.0)
        else if (abs(data.motorAxis.y()) == 1.0)
            Vector3d(1.0, 0.0, 0.0)
        else if (abs(data.motorAxis.z()) == 1.0)
            Vector3d(0.0, 1.0, 0.0)
        else
            throw RuntimeException("how the fuck did you mess this up g");

        var perpAfterRot : Vector3dc = perpendicularAxis.rotate(physShip.poseVel.rot, Vector3d())
        if (otherPhysShip != null)
            perpAfterRot = otherPhysShip.poseVel.rot.transformInverse(perpAfterRot, Vector3d())

        val perpAfterRotInPlane : Vector3dc = perpAfterRot.sub(data.motorAxis.mul(data.motorAxis.dot(perpAfterRot), Vector3d()), Vector3d())

        val angleBTShipInRadians : Double = perpAfterRotInPlane.angle(perpendicularAxis);

        val crossOfYourMother : Vector3dc = perpAfterRotInPlane.cross(perpendicularAxis, Vector3d());
        val angleWRespectToBearingAxis : Double = if (crossOfYourMother.lengthSquared() > 1e-12) {
            angleBTShipInRadians * sign(crossOfYourMother.dot(data.motorAxis)) * -1
            // bro what do you expect me to do :sus:
        } else
            0.0

        var angleErr : Double = Math.toRadians(data.motorAngle) - angleWRespectToBearingAxis

        while (angleErr > Math.PI) {
            angleErr -= 2 * Math.PI
        }

        while (angleErr < -Math.PI) {
            angleErr += 2 * Math.PI
        }

        //Derivative

        val relativeOmegaInPhysShip : Vector3dc = physShip.transform.worldToShip.transformDirection(actualRelativeOmega, Vector3d())

        val relativeOmegaInPhysShipParallelBearingAxis : Double = data.motorAxis.dot(relativeOmegaInPhysShip)

        val omegaErr : Double = data.motorRPM * ((2 * Math.PI) / 60) - relativeOmegaInPhysShipParallelBearingAxis

        val torque : Double = ((angleErr * torqueMassMultiplier * 50.0) + (omegaErr * torqueMassMultiplier * 50.0))

//        return angularVelErrorAlongBearingAxis.mul(torqueMassMultiplier * 10.0, new Vector3d());

        return bearingAxisInGlobal.mul(torque, Vector3d());
    }

    fun addPhysBearing(data : MotorCreateData) : Long {
        val id : Int = nextMotorID++
        createdMotors.add(Pair(id, data))
        return id.toLong()
    }

    fun removePhysBearing(id : Int) {
        removedMotors.add(id)
    }

    fun updatePhysBearing(id : Int, data : MotorUpdateData) {
        motorUpdateData[id] = data
    }

    fun <T> areQueuesEqual(left : Queue<T>, right : Queue<T>) : Boolean =
            arrayOf(left).contentEquals(arrayOf(right))

    override fun equals(other: Any?): Boolean =
            if (this === other)
                true
            else if (other !is MotorController)
                false
            else
                Objects.equals(motorData, other.motorData)
                        && Objects.equals(motorUpdateData, other.motorUpdateData)
                        && areQueuesEqual(createdMotors, other.createdMotors)
                        && areQueuesEqual(removedMotors, other.removedMotors)
                        && nextMotorID == other.nextMotorID

    fun canDisassemble() : Boolean = false

    fun setAligning(yn : Boolean, id : Int) {
        motorData[id]?.setAligning(yn)
    }

    companion object {
        fun getOrCreate(ship: ServerShip) : MotorController {
            if (ship.getAttachment(MotorController::class.java) == null)
                ship.saveAttachment(MotorController::class.java, MotorController())
            return ship.getAttachment(MotorController::class.java)!!
        }
    }
}