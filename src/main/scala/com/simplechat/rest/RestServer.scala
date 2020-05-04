package com.simplechat.rest

import akka.actor.{ActorSystem, InvalidActorNameException}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, ValidationRejection}
import akka.stream.Materializer
import com.simplechat.netty.Network
import com.simplechat.repository._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

class RestServer(system: ActorSystem)(implicit val mat: Materializer) extends MySqlRepository
  with ChatRoomRepository
  with ChatUserRepository
  with CORSHandler {

  implicit val sys: ActorSystem = system
  implicit val _ec: ExecutionContext = ec

  def start: Http.ServerBinding = {
    val host = Network.host.getHostAddress
    val port = Network.restPort
    val binding = Await.result(Http().bindAndHandle(route, host, port), 3.seconds)
    println("started...")
    binding
  }

  private lazy val route: Route =
    corsHandler(path("chatrooms"){
      get {
        onComplete(selectAll) {
          case Success(value)     => complete(value.asJson)
          case Failure(exception) => reject(ValidationRejection(s"Error in retrieving chatrooms: ${exception.getMessage}"))
        }
      } ~
      post {
        entity(as[ChatRoom]) { room =>
          val validations: Future[Int] = for {
            validUser       <- validateChatUser(room.creator)
            addedChatRoomId <- insertRoom(room)
          } yield addedChatRoomId

          validations.recoverWith { case a => Future.failed(a) }

          onComplete(validations) {
            case Success(roomId)    => complete(s"Chat room: $roomId record")
            case Failure(exception) => logAndReject(exception)
          }
        }
      }
    } ~
    path("auth") {
      entity(as[VerifyChatRoomCreator]) { verify =>
        post {
          onComplete(checkIfValidUser(verify.userName, verify.roomName, Option(verify.password))) {
            case Success(_)           => complete("Auth success.")
            case Failure(exception)   => logAndReject(exception)
          }
        }
      }
    })

  def validateChatUser(username: ChatUsername): Future[ChatUser] = selectByUsername(username)

  def logAndReject: Throwable => Route = { exception =>
    println(s"ERROR: $exception")
    exception match {
      case _: InvalidActorNameException => reject;
      case _ => reject(ValidationRejection(exception.getMessage))
    }
  }

}
