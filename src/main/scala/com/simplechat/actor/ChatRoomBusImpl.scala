package com.simplechat.actor

import akka.actor.ActorRef
import akka.event.{EventBus, LookupClassification}
import com.simplechat.protocol.ChatProtocol.IncomingMessage

final case class IncomingMessageEnvelope(topic: String, payload: IncomingMessage)

class ChatRoomBusImpl extends EventBus with LookupClassification {
  type Event = IncomingMessageEnvelope
  type Classifier = String
  type Subscriber = ActorRef

  override protected def classify(event: Event): Classifier = event.topic

  override protected def publish(event: Event, subscriber: Subscriber): Unit = {
    subscriber ! event.payload
  }

  override protected def mapSize(): Int = 128

  override protected def compareSubscribers(a: ActorRef, b: ActorRef): Int = a.compareTo(b)
}
