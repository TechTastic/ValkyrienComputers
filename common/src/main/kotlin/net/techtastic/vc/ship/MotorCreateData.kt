package net.techtastic.vc.ship

import org.joml.Vector3dc
import org.valkyrienskies.core.apigame.constraints.VSConstraintAndId

data class MotorCreateData(val motorPos: Vector3dc, val motorAxis: Vector3dc, val motorAngle: Double, val motorRPM: Float, val locked: Boolean, val shiptraptionID: Long, val constraint: VSConstraintAndId?, val hingeConstraint: VSConstraintAndId?, val posDampConstraint: VSConstraintAndId?, val rotDampConstraint: VSConstraintAndId?)
