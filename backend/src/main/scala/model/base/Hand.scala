package model.base

import exceptions.hand.{TooLittleCardsPlayedException, TooManyCardsException, TooManyCardsPlayedException, TooManyJokersException}
import model.joker.Joker

import scala.collection.mutable.ArrayBuffer


/**
 * Represents a collection of cards and jokers, typically forming a hand in a card game.
 *
 */
class Hand {
  
  private var _cards = List[Card]()
  private var _jokers = List[Joker]()

  val MAX_CARDS = 8
  private val MAX_JOKERS = 2
  private val MAX_PLAYED = 5
  private val MIN_PLAYED = 1
  
  /**
   * Returns an immutable list of cards currently in the hand.
   * The cards are derived from the underlying mutable collection, ensuring encapsulation.
   *
   * @return a list containing all `Card` objects in the hand
   */
  def cards: List[Card] = _cards
  
  /**
   * Returns an immutable list of jokers currently in the hand.
   * The jokers are derived from the underlying mutable collection, ensuring encapsulation.
   *
   * @return a list containing all `Joker` objects in the hand
   */
  def jokers: List[Joker] = _jokers

  /**
   * Adds a list of cards to the current hand.
   *
   * This method appends the provided cards to the existing collection of cards
   * in the hand. If the total number of cards exceeds the allowed maximum,
   * a `TooManyCardsException` is thrown.
   *
   * @param cards the list of `Card` objects to be added to the hand
   * @return Unit, as the method modifies the state of the `Hand` object
   * @throws TooManyCardsException if the total number of cards exceeds the maximum limit
   */
  def addCards(cards: List[Card]): Unit = {
    if (cards.length + _cards.length > MAX_CARDS) throw TooManyCardsException(cards.length + _cards.length)
    _cards = _cards ++ cards
  }

  /**
   * Adds a list of jokers to the current hand.
   *
   * This method appends the provided jokers to the existing collection of jokers
   * within the hand. If the total number of jokers exceeds the allowed maximum,
   * a `TooManyJokersException` is thrown.
   *
   * @param jokers the list of `Joker` objects to be added to the hand
   * @return Unit, as the method modifies the state of the `Hand` object
   * @throws TooManyJokersException if the total number of jokers exceeds the maximum limit
   */
  def addJokers(jokers: List[Joker]): Unit = {
    if (jokers.length + _jokers.length > MAX_JOKERS) throw TooManyJokersException(jokers.length + _jokers.length)
    _jokers = _jokers ++ jokers
  }
  
  /**
   * Removes cards from the hand at the specified indexes.
   *
   * This method updates the internal collection of cards by filtering
   * out the cards located at the given list of indexes. It modifies the
   * state of the hand in place.
   *
   * @param indexes the list of integer indexes corresponding to the cards to be removed
   * @return Unit, as the method performs an in-place modification of the hand
   * @throws IndexOutOfBoundsException if any index in the list is out of bounds
   */
  def removeCards(indexes: List[Int]): Unit = {
    indexes.foreach { index =>
      if (index >= _cards.length) throw IndexOutOfBoundsException(index)
    }
    _cards = _cards
      .zipWithIndex
      .filterNot { case (_, index) => indexes.contains(index) }
      .map(_._1)
  }

  /**
   * Removes jokers from the hand at the specified indexes.
   *
   * This method updates the internal collection of jokers by filtering
   * out the jokers located at the given list of indexes. It modifies the
   * state of the hand in place.
   *
   * @param indexes the list of integer indexes corresponding to the jokers to be removed
   * @return Unit, as the method performs an in-place modification of the hand
   * @throws IndexOutOfBoundsException if any index in the list is out of bounds
   */
  def removeJokers(indexes: List[Int]) : Unit = {
    indexes.foreach { index =>
      if (index >= _jokers.length) throw IndexOutOfBoundsException(index)
    }
    _jokers = _jokers
      .zipWithIndex
      .filterNot { case (_, index) => indexes.contains(index) }
      .map(_._1)
  }

  /**
   * Plays cards from the hand based on the given indexes.
   *
   * This method retrieves the cards at the specified indexes from the hand,
   * removes those cards from the internal collection, and returns them as a list.
   *
   * @param indexes the list of integer indexes corresponding to the cards
   *                to be played from the hand
   * @return a list of `Card` objects that were played
   */
  def play(indexes: List[Int]): List[Card] = {
    if (indexes.length < MIN_PLAYED) throw TooLittleCardsPlayedException(indexes.length)

    if (indexes.length > MAX_PLAYED) throw TooManyCardsPlayedException(indexes.length)

    indexes.foreach { index =>
      if (index >= _cards.length) throw IndexOutOfBoundsException(index)
    }

    val played = for
      i <- indexes
    yield _cards(i)

    played
  }

}
