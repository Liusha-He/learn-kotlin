package com.example.models

import akka.actor.typed.ActorRef

class DeleteUser(val name: String, val replyTo: ActorRef<ActionPerformed>): Command
