package stream_basics

import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.stream.javadsl.Flow
import akka.stream.javadsl.Keep
import akka.stream.javadsl.RunnableGraph
import akka.stream.javadsl.Sink
import akka.stream.javadsl.Source
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

fun main() {
    val system: ActorSystem<RunnableGraph<VehicleSpeed>> = ActorSystem.create(
        Behaviors.empty(), "simpleTracker"
    )

    val vehicleTrackingMap = HashMap<Int, VehiclePositionMessage>()
    for (i in 1..8) {
        vehicleTrackingMap[i] = VehiclePositionMessage(
            1, Date(), 0, 0
        )
    }

    val source: Source<String, NotUsed> = Source.repeat("go")
        .throttle(1, Duration.ofSeconds(10))

    val vehicleIds: Flow<String, Int, NotUsed> = Flow
        .of(String::class.java)
        .mapConcat{ value -> (1..8).toList() }

    val vehiclePositions: Flow<Int, VehiclePositionMessage, NotUsed> = Flow
        .of(Int::class.java)
        .mapAsyncUnordered(8) {
            vehicleId ->
            println("Requesting position for vehicle $vehicleId")
            val future: CompletableFuture<VehiclePositionMessage> = CompletableFuture()
            future.completeAsync { getVehiclePosition(vehicleId) }
            future
        }

    val vehicleSpeeds: Flow<VehiclePositionMessage, VehicleSpeed, NotUsed> = Flow
        .of(VehiclePositionMessage::class.java)
        .map {
            vpm ->
            val previous = vehicleTrackingMap.get(vpm.vehicleId)
            if (previous != null) {
                val speed = calculateSpeed(vpm, previous)
                vehicleTrackingMap.put(vpm.vehicleId, vpm)
                println("Vehicle ${vpm.vehicleId} is travelling at ${speed.speed}")
                speed
            } else {
                println("something went wrong...")
                null
            }
        }

    val speedFilter: Flow<VehicleSpeed, VehicleSpeed, NotUsed> = Flow
        .of(VehicleSpeed::class.java)
        .filter { speed -> speed.speed > 95 }

    val sink: Sink<VehicleSpeed, CompletionStage<VehicleSpeed>> = Sink.head()

    val result: CompletionStage<VehicleSpeed> = source
        .via(vehicleIds)
        .async()
        .via(vehiclePositions)
        .async()
        .via(vehicleSpeeds)
        .via(speedFilter)
        .toMat(sink, Keep.right())
        .run(system)

    result.whenComplete {
        value, throwable ->
        if (throwable != null) {
            println("Oops. something went wrong!")
        } else {
            println("Vehicle ${value.vehicleId} was going at speed ${value.speed}")
        }
        system.terminate()
    }
}
