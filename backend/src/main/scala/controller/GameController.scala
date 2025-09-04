package controller

import api.types.cards.{CardAttribute, CardView, PileView}
import api.types.score.ScoreView
import api.types.GameView
import api.types.players.PlayerView
import controller.state.GameState
import controller.state.states.ChoiceState
import model.actions.Action
import model.base.{Card, Hand, Score}
import model.joker.jokers.EvenSteven
import model.rank.ranks.*
import model.suit.suits.*

import scala.util.Random

class GameController extends api.GameController {
  override def getGameState: GameView = GameView(
    players = List(
      PlayerView(
        id = "player1",
        name = "Player 1",
        hand =
          for
            card <- hand.cards
            if !_chosenCardsIndexes.contains(hand.cards.indexOf(card))
          yield CardView(
            id = card.toString,
            faceUp = true,
            name = card.toString,
            attributes = List(
              CardAttribute("rank", card.rank.toString),
              CardAttribute("suit", card.suit.toString)
            )
          ),
      )
    ),
    cardPiles = List(
      PileView(
        id = "jokers",
        ownerId = Some("player1"),
        cards =
          for card <- hand.jokers
          yield CardView(
            id = card.toString,
            faceUp = true,
            name = card.toString,
            attributes = List()
          )
      ),
      PileView(
        id = "choosen_cards",
        ownerId = Some("player1"),
        cards =
          for
            index <- _chosenCardsIndexes
            card = hand.cards(index)
          yield CardView(
            id = card.toString,
            faceUp = true,
            name = card.toString,
            attributes = List(
              CardAttribute("rank", card.rank.toString),
              CardAttribute("suit", card.suit.toString)
            )
          )
      )
    ),
    currentPlayerId = Some("player1"),
    scores = List(
      ScoreView("Total", totalScore.toString),
      ScoreView("Mult", score.multi.toString),
      ScoreView("Chips", score.chips.toString)
    ),
    messageToShow = Some(_state.messageToShow)
  )

  override def getAvailableActions: List[Action] = _state.availableActions

  private var _state: GameState = new ChoiceState
  def changeState(state: GameState): Unit = _state = state

  val hand: Hand = new Hand
  def drawFromDeck(n: Int): Unit = {
    if (_deck.nonEmpty) {
      hand.addCards(_deck.take(n))
      _deck = _deck.drop(n)
    }
  }
  def removeFromHand(indexes: List[Int]): Unit = {
    hand.removeCards(indexes)
  }

  private var _chosenCardsIndexes: List[Int] = List()
  def addToChosenCardsIndexes(index: Int): Unit =
    _chosenCardsIndexes = index :: _chosenCardsIndexes
  def removeChosenCardsIndexes(index: Int): Unit =
    _chosenCardsIndexes = _chosenCardsIndexes.filter(_ != index)
  def resetChosenCardsIndexes(): Unit =
    _chosenCardsIndexes = List()
  def getChosenCardsIndexes: List[Int] = _chosenCardsIndexes

  private var _score: Score = new Score(0, 0)
  def score: Score = _score
  def score_=(s: Score): Unit = _score = s

  private var _totalScore: Int = 0
  def totalScore: Int = _totalScore
  def totalScore_=(s: Int): Unit = _totalScore = s

  def choseCard(index: Int): Unit = {
    _state.chooseCard(this, index)
    println(this._chosenCardsIndexes)
  }

  def removeChoice(index: Int): Unit =
    _state.removeChoice(this, index)
    println(this._chosenCardsIndexes)

  def next(): Unit =
    _state.play(this)

  def discard(): Unit =
    _state.discard(this)
    println(this._chosenCardsIndexes)
    println(this.hand.cards)

  def play(): Unit =
    _state.play(this)
    println(this._chosenCardsIndexes)
    println(this.hand.cards)

  private var _deck: List[Card] = Random.shuffle(
    List(
      new Card(Ace, Spades),
      new Card(Ace, Clubs),
      new Card(Ace, Diamond),
      new Card(Ace, Hearts),
      new Card(King, Spades),
      new Card(King, Clubs),
      new Card(King, Diamond),
      new Card(King, Hearts),
      new Card(Queen, Spades),
      new Card(Queen, Clubs),
      new Card(Queen, Diamond),
      new Card(Queen, Hearts),
      new Card(Jack, Spades),
      new Card(Jack, Clubs),
      new Card(Jack, Diamond),
      new Card(Jack, Hearts),
      new Card(Ten, Spades),
      new Card(Ten, Clubs),
      new Card(Ten, Diamond),
      new Card(Ten, Hearts),
      new Card(Nine, Spades),
      new Card(Nine, Clubs),
      new Card(Nine, Diamond),
      new Card(Nine, Hearts),
      new Card(Eight, Spades),
      new Card(Eight, Clubs),
      new Card(Eight, Diamond),
      new Card(Eight, Hearts),
      new Card(Seven, Spades),
      new Card(Seven, Clubs),
      new Card(Seven, Diamond),
      new Card(Seven, Hearts),
      new Card(Six, Spades),
      new Card(Six, Clubs),
      new Card(Six, Diamond),
      new Card(Six, Hearts),
      new Card(Five, Spades),
      new Card(Five, Clubs),
      new Card(Five, Diamond),
      new Card(Five, Hearts),
      new Card(Four, Spades),
      new Card(Four, Clubs),
      new Card(Four, Diamond),
      new Card(Four, Hearts),
      new Card(Three, Spades),
      new Card(Three, Clubs),
      new Card(Three, Diamond),
      new Card(Three, Hearts),
      new Card(Two, Spades),
      new Card(Two, Clubs),
      new Card(Two, Diamond),
      new Card(Two, Hearts)
    )
  )

  drawFromDeck(8)
  hand.addJokers(List(new EvenSteven))
}

object GameController {
  private var _instance: GameController = new GameController
  def instance: GameController = _instance

  private def reset(): Unit = _instance = new GameController
}
