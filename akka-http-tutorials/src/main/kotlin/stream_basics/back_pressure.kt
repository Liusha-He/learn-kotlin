package stream_basics

import akka.Done
import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.stream.Attributes
import akka.stream.OverflowStrategy
import akka.stream.javadsl.*
import java.math.BigInteger
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

fun main() {
    val start: Long = System.currentTimeMillis()
    val parallelism = 4

    val system: ActorSystem<RunnableGraph<NotUsed>> = ActorSystem
        .create(Behaviors.empty(), "complexStream")

    val source: Source<Int, NotUsed> = Source.range(1, 100)

    val bigIntFlow: Flow<Int, BigInteger, NotUsed> = Flow
        .of(Int::class.java)
        .map{
            val result = BigInteger(3_000, Random())
            println("The Big Integer is - $result")
            result
        }

    val primeFlow: Flow<BigInteger, BigInteger, NotUsed> = Flow
        .of(BigInteger::class.java)
        .mapAsync(parallelism) { input ->
            val futurePrime = CompletableFuture<BigInteger>()
            futurePrime.completeAsync {
                val prime: BigInteger = input.nextProbablePrime()
                println("The prime is: $prime")
                prime
            }
            futurePrime
        }

    val listFlow: Flow<BigInteger, List<BigInteger>, NotUsed> = Flow
        .of(BigInteger::class.java)
        .grouped(10)
        .map { list ->list.sorted() }

    val printSink: Sink<List<BigInteger>, CompletionStage<Done>> = Sink
        .foreach {
                input -> println(input)
        }

    val result: CompletionStage<Done> = source
        .via(bigIntFlow)
//        .buffer(16, OverflowStrategy.backpressure())
        .async()
        .via(primeFlow)
        .async()
        .via(listFlow)
        .toMat(printSink, Keep.right())
        .run(system)

    result.whenComplete{
        value, throwable ->
        println("Application run in : ${(System.currentTimeMillis() - start)/1_000} sec.")
        system.terminate()
    }
}
