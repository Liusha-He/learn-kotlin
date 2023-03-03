package stream_basics

import java.util.Date

data class VehiclePositionMessage(
    val vehicleId: Int,
    val currentDateTime: Date,
    val longPosition: Int,
    val latPosition: Int
)
