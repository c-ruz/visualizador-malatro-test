package api

import api.types.*
import api.types.cards.{CardAttribute, CardView, PileView}
import api.types.players.PlayerView
import api.types.score.ScoreView
import api.util.ReflectJson.*
import controller.GameController
import model.actions.Action
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.apache.pekko.http.scaladsl.server.Directives
import org.apache.pekko.http.cors.scaladsl.CorsDirectives.*
import spray.json.RootJsonFormat
import spray.json.*

import scala.io.StdIn
import scala.concurrent.*

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val scoreViewFormat: RootJsonFormat[ScoreView] = jsonFormat2(
    ScoreView.apply
  )
  implicit val cardAttributeFormat: RootJsonFormat[CardAttribute] = jsonFormat2(
    CardAttribute.apply
  )
  implicit val orderFormat: RootJsonFormat[CardView] = jsonFormat4(
    CardView.apply
  )
  implicit val pileFormat: RootJsonFormat[PileView] = jsonFormat3(
    PileView.apply
  )
  implicit val playerFormat: RootJsonFormat[PlayerView] = jsonFormat3(
    PlayerView.apply
  )
  implicit val gameStateFormat: RootJsonFormat[GameView] = jsonFormat5(
    GameView.apply
  )
}

object GameApi extends Directives with JsonSupport {

  implicit val system: ActorSystem[_] = ActorSystem(Behaviors.empty, "GameApi")
  implicit val executionContext: ExecutionContextExecutor =
    system.executionContext

  def main(args: Array[String]): Unit = {
    val route = cors() {
      path("state") {
        get {
          try {
            GameController.instance.synchronized {
              complete(GameController.instance.getGameState)
            }
          } catch {
            case e: Exception =>
              complete(
                Map("error" -> e.getMessage).toJson
              )
          }

        }
      } ~
        path("action") {
          get {
            complete(toJson(GameController.instance.getAvailableActions))
          } ~
            post {
              entity(as[String]) { jsonString =>
                try {
                  val action = createInstanceFromJson(jsonString)
                    .getOrElse(
                      throw new IllegalArgumentException("Invalid action JSON")
                    )

                  val message = action
                    .asInstanceOf[Action]
                    .doAction(GameController.instance)
                  complete(
                    Map("message" -> message).toJson
                  )
                } catch {
                  case e: Exception =>
                    complete(Map("error" -> e.getMessage).toJson)
                }
              }
            }
        }
    }

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(
      s"Server now online. Please navigate to http://localhost:8080/\nPress RETURN to stop..."
    )
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
