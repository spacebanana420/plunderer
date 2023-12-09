package yakumo.browser
import yakumo.*

//This code is not implemented yet, but it will make the client able to upload multiple handpicked files

def parseChoices(answer: String, dirqtd: Int): List[Int] = {
  def append(s: String, l: List[Int]): List[Int] =
    try
      if s != "" then //is this needed
        l :+ s.toInt
      else
        l
    catch case e: Exception => l
  def getChoice(i: Int = 0, s: String = "", choices: List[Int] = List()): List[Int] =
    if i >= answer.length then
      append(s, choices)
    else if answer(i) == ' ' then
      getChoice(i+1, "", append(s, choices))
    else
      getChoice(i+1, s + answer(i), choices)

  getChoice().filter(x => x < dirqtd+2 && x > 1)
}
