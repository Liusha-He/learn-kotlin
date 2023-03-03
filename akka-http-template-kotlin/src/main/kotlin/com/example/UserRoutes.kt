package com.example

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Scheduler
import akka.actor.typed.javadsl.AskPattern
import akka.http.javadsl.marshallers.jackson.Jackson
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.server.Directives.*
import akka.http.javadsl.server.PathMatchers
import akka.http.javadsl.server.Route
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.CompletionStage

import com.example.models.*

class UserRoutes(system: ActorSystem<Void>, private val userRegistryActor: ActorRef<Command>)
{
    private val log = LoggerFactory.getLogger(UserRoutes::class.java)

    private val objectMapper = ObjectMapper()
        .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
        .findAndRegisterModules()

    private val scheduler: Scheduler = system.scheduler()
    private val askTimeout: Duration = system.settings().config().getDuration("my-app.routes.ask-timeout")

    private fun getUser(name: String): CompletionStage<GetUserResponse> {
        return AskPattern.ask(
            userRegistryActor,
            { GetUser(name, it) },
            askTimeout,
            scheduler
        )
    }

    private fun deleteUser(name: String): CompletionStage<ActionPerformed> {
        return AskPattern.ask(
            userRegistryActor,
            { DeleteUser(name, it) },
            askTimeout,
            scheduler
        )
    }

    private fun getUsers(): CompletionStage<Users> {
        return AskPattern.ask(
            userRegistryActor,
            { GetUsers(it) },
            askTimeout,
            scheduler
        )
    }

    private fun createUser(user: User): CompletionStage<ActionPerformed> {
        return AskPattern.ask(
            userRegistryActor,
            { CreateUser(user, it) },
            askTimeout,
            scheduler
        )
    }

    fun userRoutes(): Route {
        return pathPrefix("users") {
            concat(
                pathEnd {
                    concat(
                        get {
                            onSuccess(getUsers()) {
                                complete(
                                    StatusCodes.OK,
                                    it,
                                    Jackson.marshaller(objectMapper)
                                )
                            }},
                        post {
                            entity(Jackson.unmarshaller(
                                objectMapper,
                                User::class.java)
                            ) { user ->
                                onSuccess(createUser(user)) { performed ->
                                    log.info("Create result: {}", performed.description)
                                    complete(
                                        StatusCodes.CREATED,
                                        performed,
                                        Jackson.marshaller(objectMapper)
                                    )
                                }
                            }
                        }
                    )
                },
                path(PathMatchers.segment()) {name: String ->
                    concat (
                        get {
                            onSuccess(getUser(name)) { performed ->
                                // TODO figure out why - rejectEmptyResponse did not work properly
                                if (performed.maybeUser.isPresent) {
                                    complete(
                                        StatusCodes.OK,
                                        performed.maybeUser,
                                        Jackson.marshaller(objectMapper)
                                    )
                                } else {
                                    complete(StatusCodes.NOT_FOUND)
                                }
                            }
                        },
                        delete {
                            onSuccess(deleteUser(name)) { performed ->
                                log.info("Delete result: {}", performed.description)
                                complete(
                                    StatusCodes.OK,
                                    performed, Jackson.marshaller(objectMapper)
                                )
                            }
                        }
                    )
                }
            )
        }
    }
}
