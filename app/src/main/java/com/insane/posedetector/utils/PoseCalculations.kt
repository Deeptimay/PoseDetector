package com.insane.posedetector.utils

import com.google.mediapipe.tasks.components.containers.Landmark
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.abs
import kotlin.math.atan2

object PoseCalculations {

    private var bicepCurlCount = 0
    private var bicepCurlState = "up"

    private var squatCount = 0
    private var squatState = "up"

    private var pushUpCount = 0
    private var pushUpState = "up"


    fun isSquatComplete(landmarks: List<Landmark>): Int {
        // Assuming LEFT_HIP, LEFT_KNEE, and LEFT_ANKLE are available in `landmarks`
        val leftHip = landmarks[PoseLandmark.LEFT_HIP]
        val leftKnee = landmarks[PoseLandmark.LEFT_KNEE]
        val leftAnkle = landmarks[PoseLandmark.LEFT_ANKLE]

        // Calculate the angle between the hip, knee, and ankle
        val kneeAngle = calculateAngle(leftHip, leftKnee, leftAnkle)

        // Thresholds for detecting a squat
        val squatDownThreshold = 70.0
        val squatUpThreshold = 160.0

        // Track state changes and count repetitions
        if (kneeAngle < squatDownThreshold && squatState == "up") {
            squatState = "down"
        } else if (kneeAngle > squatUpThreshold && squatState == "down") {
            squatState = "up"
            squatCount++
        }

        return squatCount
    }

    fun isPushUpComplete(landmarkList: List<Landmark>): Int {
        val leftShoulder = landmarkList[PoseLandmark.LEFT_SHOULDER]
        val leftElbow = landmarkList[PoseLandmark.LEFT_ELBOW]
        val leftWrist = landmarkList[PoseLandmark.LEFT_WRIST]
        // Implement logic to check if the pose meets the criteria for a complete push-up
        // For example, check elbow angles and joint positions
        // Calculate the angle between the hip, knee, and ankle
        val kneeAngle = calculateAngle(leftShoulder, leftElbow, leftWrist)

        // Thresholds for detecting a squat
        val squatDownThreshold = 70.0
        val squatUpThreshold = 160.0

        // Track state changes and count repetitions
        if (kneeAngle < squatDownThreshold && pushUpState == "up") {
            pushUpState = "down"
        } else if (kneeAngle > squatUpThreshold && pushUpState == "down") {
            pushUpState = "up"
            pushUpCount++
        }
        return pushUpCount
    }

    fun isBicepCurlComplete(landmarks: List<Landmark>): Int {
        // Get landmarks for shoulder, elbow, and wrist
        val leftShoulder = landmarks[PoseLandmark.LEFT_SHOULDER]
        val leftElbow = landmarks[PoseLandmark.LEFT_ELBOW]
        val leftWrist = landmarks[PoseLandmark.LEFT_WRIST]

        // Calculate the elbow angle for the left arm
        val elbowAngle = calculateAngle(leftShoulder, leftElbow, leftWrist)

        // Define thresholds for bicep curl detection
        val curlThreshold = 40.0   // When the elbow is bent (curl up)
        val extensionThreshold = 160.0  // When the elbow is straight (curl down)


        if (elbowAngle < curlThreshold && bicepCurlState == "up") {
            bicepCurlState = "down"
        } else if (elbowAngle > extensionThreshold && bicepCurlState == "down") {
            bicepCurlState = "up"
            bicepCurlCount++
        }

        return bicepCurlCount
    }


    private fun calculateAngle(pointA: Landmark, pointB: Landmark, pointC: Landmark): Double {
        val radians = atan2(pointC.y() - pointB.y(), pointC.x() - pointB.x()) -
                atan2(pointA.y() - pointB.y(), pointA.x() - pointB.x())
        var angle = abs(radians * 180.0 / Math.PI)

        if (angle > 180.0) {
            angle = 360.0 - angle
        }

        return angle
    }
}