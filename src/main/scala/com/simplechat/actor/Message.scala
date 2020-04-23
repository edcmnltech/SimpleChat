package com.simplechat.actor

object Message {
  def joined(username: String) = s"$username joined the chat"
  def quit(username: String) = s"$username quit the chat"
}
