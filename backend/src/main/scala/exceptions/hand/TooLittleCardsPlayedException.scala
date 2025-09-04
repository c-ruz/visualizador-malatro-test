package exceptions.hand

class TooLittleCardsPlayedException(n:Int) extends Exception(s"$n cards were played but the minimum is 1")
