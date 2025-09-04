package api

import api.types.GameView
import model.actions.Action

trait GameController {

  def getGameState: GameView

  def getAvailableActions: List[Action]
}
