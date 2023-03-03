package com.example.models

import akka.actor.typed.ActorRef

class CreateUser(val user: User, val replyTo: ActorRef<ActionPerformed>) : Command
