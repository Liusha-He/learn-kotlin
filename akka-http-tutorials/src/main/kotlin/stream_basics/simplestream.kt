package stream_basics

import akka.Done
import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.stream.javadsl.Flow
import akka.stream.javadsl.RunnableGraph
import akka.stream.javadsl.Sink
import akka.stream.javadsl.Source
import java.util.concurrent.CompletionStage

fun main() {
    val system: ActorSystem<RunnableGraph<NotUsed>> = ActorSystem.create(
        Behaviors.empty(), "simpleSystem"
    )

    val source: Source<Int, NotUsed> = Source.range(1, 100)

    val flow: Flow<Int, String, NotUsed> = Flow.of(Int::class.java).map{
        value -> "The next number is $value"
    }

    val sink: Sink<String, CompletionStage<Done>> = Sink.foreach{
        value -> println(value)
    }

    val graph: RunnableGraph<NotUsed> = source.via(flow).to(sink)
    graph.run(system)
}
