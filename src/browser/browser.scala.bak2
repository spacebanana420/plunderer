package yakumo.browser
import yakumo.*

import java.io.File
import scala.sys.process.*
import scala.io.StdIn.readLine
import scala.sys.exit

def browse(): String = {
//   def browser_getpath(paths: Array[Array[String]], answer: Int): String = {
//
//   }
  def browseLoop(basedir: String): String = {
    val paths = fileBrowser(basedir)
    val answer = readLine()
    if browserCommand(answer, basedir) == true then
      browseLoop(basedir)
    else if answer != "0" then
      try
        val answernum = answer.toInt
        if answernum == 1 then
          browseLoop(getParentPath(basedir))
        else if answernum - 2 <= paths(0).length-1 then //maybe just check if its a file or not
          browseLoop(s"${basedir}/${paths(0)(answernum - 2)}")
        else if answernum - 2 - paths(0).length <= paths(1).length-1 then
          s"${basedir}/${paths(1)(answernum - 2 - paths(0).length)}"
        else
          browseLoop(basedir)
      catch
        case e: Exception => browseLoop(basedir)
    else
      "!cancelled!" //replace with only exiting the download mode
  }

  val chosenfile = browseLoop(File("").getAbsolutePath())
  if chosenfile != "!cancelled!" then
    println(s"Selected ${chosenfile}")
  chosenfile
}


def fileBrowser(basedir: String): Array[Array[String]] = { //test
  val green = foreground("green")
  val red = foreground("red")
  val default = foreground("default")

  def addtoscreen(elements: Array[String], pathNum: Int, screen: String = "", i: Int = 0, added: Int = 0): String = {
    if i >= elements.length then
      screen
    else if added < 2 then
      val newstr = s"$green${pathNum}:$default ${elements(i)}       "
      addtoscreen(elements, pathNum + 1, screen + newstr, i+1, added+1)
    else
      val newstr = s"$green${pathNum}:$default ${elements(i)}\n"
      addtoscreen(elements, pathNum + 1, screen + newstr, i+1, 0)
  }

  val paths = getPaths(basedir)
  val dirs = paths.filter(x => File(s"${basedir}/${x}").isFile() == false)
  val files = paths.filter(x => File(s"${basedir}/${x}").isFile() == true)

  val browserScreen =
    s"$basedir\n\n${red}0:$default Exit   ${green}1:$default Go back\n\n---Directories---\n"
    + addtoscreen(dirs, 2)
    + "\n---Files---\n"
    + addtoscreen(files, 2 + dirs.length)
    + "\nPick a file to send or navigate through the filesystem\nType \"help\" to see the list of commands"

  clear()
  println(browserScreen)
  Array(dirs, files)
}

def browser_seek(basedir: String, seek: String) = { //test
  val green = foreground("green")
  val red = foreground("red")
  val default = foreground("default")

  def addtoscreen(elements: Array[String], pathNum: Int, screen: String = "", i: Int = 0, added: Int = 0): String = {
    if i >= elements.length then
      screen
    else if elements(i).contains(seek) == true then
      if added < 2 then
        val newstr = s"$green${pathNum}:$default ${elements(i)}       "
        addtoscreen(elements, pathNum + 1, screen + newstr, i+1, added+1)
      else
        val newstr = s"$green${pathNum}:$default ${elements(i)}\n"
        addtoscreen(elements, pathNum + 1, screen + newstr, i+1, 0)
    else
        addtoscreen(elements, pathNum + 1, screen, i+1, added)
  }

  val paths = getPaths(basedir)
  val dirs = paths.filter(x => File(s"${basedir}/${x}").isFile() == false)
  val files = paths.filter(x => File(s"${basedir}/${x}").isFile() == true)

  val browserScreen =
    s"$basedir\n\n---Directories---\n"
    + addtoscreen(dirs, 2)
    + "\n---Files---\n"
    + addtoscreen(files, 2 + dirs.length)
    + "\nThe following entries were found\nPress enter to continue"

  clear()
  readUserInput(browserScreen)
}
