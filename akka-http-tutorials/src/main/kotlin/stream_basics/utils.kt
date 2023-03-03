package stream_basics

import java.util.*
import kotlin.math.abs
import kotlin.math.pow

fun getVehiclePosition(vehicleId: Int): VehiclePositionMessage {
    val r: Random = Random()
    try {
        Thread.sleep((1_000 * r.nextInt(5)).toLong())
    }
    catch (ex: InterruptedException) {
        println("Oops, something went wrong! $ex")
    }
    return VehiclePositionMessage(vehicleId, Date(), r.nextInt(100), r.nextInt(100))
}

fun calculateSpeed(position1: VehiclePositionMessage, position2: VehiclePositionMessage): VehicleSpeed {
    var speed = if (position1.longPosition == 0 && position2.longPosition == 0) {
        0.0
    } else {
        val longDistance = abs(position1.longPosition - position2.longPosition).toDouble()
        val latDistance = abs(position1.latPosition - position2.latPosition).toDouble()

        val distanceTravelled = (longDistance.pow(2.0) + latDistance.pow(2.0)).pow(0.5)

        val duration = ((position1.currentDateTime.time - position2.currentDateTime.time) / 1_000).toLong()

        distanceTravelled * 10 / duration
    }

    if (speed > 120) speed = 50.0

    return VehicleSpeed(position1.vehicleId, speed)
}
