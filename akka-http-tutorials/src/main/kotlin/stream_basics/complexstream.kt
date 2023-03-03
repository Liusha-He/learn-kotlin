package stream_basics

import akka.Done
import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.stream.javadsl.Flow
import akka.stream.javadsl.RunnableGraph
import akka.stream.javadsl.Sink
import akka.stream.javadsl.Source
import java.math.BigInteger
import java.util.Random
import java.util.concurrent.CompletionStage

fun main() {
    val system: ActorSystem<RunnableGraph<NotUsed>> = ActorSystem
        .create(Behaviors.empty(), "complexStream")

    val source: Source<Int, NotUsed> = Source.range(1, 10)

    val bigIntFlow: Flow<Int, BigInteger, NotUsed> = Flow
        .of(Int::class.java)
        .map{ BigInteger(2_000, Random()) }

    val primeFlow: Flow<BigInteger, BigInteger, NotUsed> = Flow
        .of(BigInteger::class.java)
        .map { input ->
                val prime: BigInteger = input.nextProbablePrime()
                println("The prime is: $prime")
                prime
        }

    val listFlow: Flow<BigInteger, List<BigInteger>, NotUsed> = Flow
        .of(BigInteger::class.java)
        .grouped(10)
        .map { list ->list.sorted() }

    val printSink: Sink<List<BigInteger>, CompletionStage<Done>> = Sink
        .foreach {
            input -> println(input)
        }

    source
        .via(bigIntFlow)
        .via(primeFlow)
        .via(listFlow)
        .to(printSink)
        .run(system)
}
