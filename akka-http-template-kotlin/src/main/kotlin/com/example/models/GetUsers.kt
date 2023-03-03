package com.example.models

import akka.actor.typed.ActorRef

class GetUsers(val replyTo: ActorRef<Users>) : Command
