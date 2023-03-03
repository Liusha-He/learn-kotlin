package com.example.models

import akka.actor.typed.ActorRef

class GetUser(val name: String, val replyTo: ActorRef<GetUserResponse>): Command
