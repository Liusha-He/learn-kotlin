package stream_basics

import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.stream.javadsl.Flow
import akka.stream.javadsl.Keep
import akka.stream.javadsl.RunnableGraph
import akka.stream.javadsl.Sink
import akka.stream.javadsl.Source
import java.util.concurrent.CompletionStage
import java.util.Random

fun main() {
    val system: ActorSystem<RunnableGraph<NotUsed>> = ActorSystem
        .create(Behaviors.empty(), "materialized_system")

    val source: Source<Int, NotUsed> = Source
        .range(1, 100)
        .map{ value -> Random().nextInt(1_000) + 1 }

    val filterFlow: Flow<Int, Int, NotUsed> = Flow
        .of(Int::class.java)
        .filter { value ->
            system.log().debug("Filter input $value")
            value > 200
        }

    val evenFlow: Flow<Int, Int, NotUsed> = Flow
        .of(Int::class.java)
        .filter { value -> value % 2 == 0 }

    val addFlow: Flow<Int, Int, NotUsed> = Flow
        .of(Int::class.java)
        .log("Flow input")
        .map {
            value -> value + 1
        }
        .log("flow output")

    val sink: Sink<Int, CompletionStage<Int>> = Sink
        .fold(0) { counter, value ->
            println(value)
            counter + 1
        }

    val result: CompletionStage<Int> = source
        .via(filterFlow)
        .via(addFlow)
        .viaMat(evenFlow, Keep.right())
        .toMat(sink, Keep.right())
        .run(system)

    result.whenComplete { value, throwable ->
        if (throwable == null) {
            println("The graph's materialized value is $value")
        } else {
            println("Oops, something went wrong - $throwable")
        }
        system.terminate()
    }
}
