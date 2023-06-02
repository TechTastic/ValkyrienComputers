package net.techtastic.vc.ship

import org.joml.Vector3dc
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.constraints.VSConstraintAndId
import org.valkyrienskies.core.apigame.constraints.VSFixedOrientationConstraint
import org.valkyrienskies.core.apigame.constraints.VSHingeOrientationConstraint

class MotorData(val motorPosition: Vector3dc, val motorAxis: Vector3dc, var motorAngle: Double, var motorRPM: Float, var locked: Boolean, var shiptraptionID: Long, constraintAndId: VSConstraintAndId, hingeConstraintAndId: VSConstraintAndId, posDampConstraintAndId: VSConstraintAndId?, rotDampConstraintAndId: VSConstraintAndId?) {
    var aligning : Boolean

    var attachConstraint : VSAttachmentConstraint
    var attachID : Int

    var hingeConstraint : VSHingeOrientationConstraint
    var angleConstraint : VSFixedOrientationConstraint?
    var hingeID : Int

    init {
        this.attachConstraint = constraintAndId.vsConstraint as VSAttachmentConstraint
        this.attachID = constraintAndId.constraintId
        this.hingeConstraint = hingeConstraintAndId.vsConstraint as VSHingeOrientationConstraint
        this.hingeID = hingeConstraintAndId.constraintId
        this.aligning = false
        this.angleConstraint = null
    }

    fun setAligning(yn : Boolean) {
        this.aligning = yn;
    }
}