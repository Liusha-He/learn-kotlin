package server_basics

import akka.Done
import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.http.javadsl.Http
import akka.http.javadsl.IncomingConnection
import akka.http.javadsl.ServerBinding
import akka.http.javadsl.model.HttpRequest
import akka.http.javadsl.model.HttpResponse
import akka.http.javadsl.model.StatusCodes
import akka.stream.Materializer
import akka.stream.javadsl.Flow
import akka.stream.javadsl.RunnableGraph
import akka.stream.javadsl.Sink
import akka.stream.javadsl.Source
import java.util.concurrent.CompletionStage
import akka.japi.function.Function

fun main() {
    val server = SimpleSynchronousSystem()
    server.run()
}

class SimpleSynchronousSystem {
    val system: ActorSystem<RunnableGraph<NotUsed>> = ActorSystem.create(Behaviors.empty(), "simpleServer")

    val synchronizedHandler: Function<HttpRequest, HttpResponse> = Function { httpRequest: HttpRequest ->
        println("The headers are: ")
        httpRequest.getHeaders().forEach { header ->
            println("${header.name()} : ${header.value()}")
        }
        println("The URI is - ${httpRequest.uri.pathString}")
        if (httpRequest.uri.rawQueryString().isPresent) {
            println("the query string was ${httpRequest.uri.rawQueryString().get()}")
        }
        httpRequest.discardEntityBytes(system)
        HttpResponse
            .create()
            .withStatus(StatusCodes.OK)
            .withEntity("The data was received...\n")
    }

    fun run() {
        val source: Source<IncomingConnection, CompletionStage<ServerBinding>> = Http
            .get(system)
            .newServerAt("localhost", 8080)
            .connectionSource()

        val flow: Flow<IncomingConnection, IncomingConnection, NotUsed> = Flow
            .of(IncomingConnection::class.java)
            .map { connection ->
                println("Incoming Connection from ${connection.remoteAddress()}")
                connection.handleWithSyncHandler(
                    synchronizedHandler,
                    Materializer.createMaterializer(system)
                )
                connection
            }

        val sink: Sink<IncomingConnection, CompletionStage<Done>> = Sink.ignore()

        val server: CompletionStage<ServerBinding> = source.via(flow).to(sink).run(system)

        server.whenComplete { binding, throwable ->
            if (throwable != null) {
                println("Oops, something went wrong - $throwable")
            } else {
                println("The server is running at ${binding.localAddress()}")
            }
        }
    }
}
