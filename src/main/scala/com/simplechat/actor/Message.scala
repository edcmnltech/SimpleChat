package com.simplechat.actor

import com.simplechat.protocol.ChatProtocol.InfoMessage

object Message {
  def joined(username: String) = InfoMessage(s"$username joined the chat")
  def quit(username: String) = InfoMessage(s"$username quit the chat")
  def reconnected(username: String) = InfoMessage(s"$username reconnected the chat")
}
