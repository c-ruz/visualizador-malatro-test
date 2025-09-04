package exceptions.state

class InvalidTransitionState(from: String, transition: String) extends Exception(s"Invalid transition from state $from using $transition")
