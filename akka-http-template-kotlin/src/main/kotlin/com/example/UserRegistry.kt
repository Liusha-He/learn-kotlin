package com.example

import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import com.example.models.*

class UserRegistry(context: ActorContext<Command>): AbstractBehavior<Command>(context)
{
    private val users = mutableListOf<User>()

    companion object {
        @JvmStatic
        fun create(): Behavior<Command> {
            return Behaviors.setup(::UserRegistry)
        }
    }

    override fun createReceive(): Receive<Command> {
        return newReceiveBuilder()
            .onMessage(GetUsers::class.java, ::onGetUsers)
            .onMessage(CreateUser::class.java, ::onCreateUser)
            .onMessage(GetUser::class.java, ::onGetUser)
            .onMessage(DeleteUser::class.java, ::onDeleteUser)
            .build()
    }

    private fun onGetUsers(command: GetUsers): Behavior<Command> {
        command.replyTo.tell(Users(this.users.toList()))
        return this
    }

    private fun onCreateUser(command: CreateUser): Behavior<Command> {
        users.add(command.user)
        command.replyTo.tell(ActionPerformed("User ${command.user.name} created."))
        return this
    }

    private fun onGetUser(command: GetUser): Behavior<Command> {
        val maybeUser = users.stream()
            .filter { it.name == command.name }
            .findFirst()
        command.replyTo.tell(GetUserResponse(maybeUser))
        return this
    }

    private fun onDeleteUser(command: DeleteUser): Behavior<Command> {
        users.removeIf{ it.name == command.name }
        command.replyTo.tell(ActionPerformed("User ${command.name} deleted."))
        return this
    }
}
