package exceptions.hand

class TooManyCardsException(n: Int) extends Exception(s"A hand can have at most 8 cards, but had: $n")
