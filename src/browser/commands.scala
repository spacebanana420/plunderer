package yakumo

import java.io.File

def browserCommand(cmd: String, basedir: String): Boolean = {
  val cmds = List("search", "find", "help")
  def findCommand(i: Int = 0): String = {
    if i >= cmds.length then
      ""
    else if cmd.contains(cmds(i)) == true then
      cmds(i)
    else
      findCommand(i+1)
  }
  findCommand() match
    case "search" | "find" =>
      cmd_search(basedir)
      true
    case "help" =>
      cmd_help()
      true
    case _ =>
      false
}

def cmd_search(basedir: String) = {
  val substr = readUserInput("Type the name to look for")
  browser_seek(basedir, substr)
}

def cmd_help() = {
  readUserInput("Available commands:\n   * search/find\n   * help\n\nPress enter to continue")
}
