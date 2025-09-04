package exceptions.hand

class TooManyCardsPlayedException(n:Int) extends Exception(s"$n cards were played but the maximum is 5") {

}
