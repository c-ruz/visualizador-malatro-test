package exceptions.hand

class TooManyJokersException(n: Int) extends Exception(s"Hand can have at most 2 jokers, but had $n")
