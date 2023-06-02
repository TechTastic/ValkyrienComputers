package net.techtastic.vc.ship

import org.valkyrienskies.core.apigame.constraints.VSFixedOrientationConstraint
import org.valkyrienskies.core.apigame.constraints.VSHingeOrientationConstraint

data class MotorUpdateData(var motorAngle: Double, var motorRPM: Float, var locked: Boolean, var hingeConstraint: VSHingeOrientationConstraint?, var angleConstraint: VSFixedOrientationConstraint?)
