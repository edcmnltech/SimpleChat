package com.simplechat.actor

object Message {
  def joined(username: String) = s"$username joined the chat"
  def left(username: String) = s"$username left the chat"
}
