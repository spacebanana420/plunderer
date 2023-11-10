package yakumo

import java.io.File
import scala.sys.process.*
import scala.io.StdIn.readLine
import scala.sys.exit

// def separatePaths(paths: Array[String], mode: String, i: Int = 0, finalpaths: List[String] = List[String]()): List[String] = {
//   if i < paths.length-1 then
//     if (File(paths(i)).isFile == true && mode == "file") || (File(paths(i)).isFile == false && mode == "dir") then
//       separatePaths(paths, mode, i+1, finalpaths :+ paths(i))
//     else
//       separatePaths(paths, mode, i+1, finalpaths)
//   else
//     finalpaths
// }

def getParentPath(path: String): String = {
  val parent = File(path).getParent()
  if parent != null then
    parent
  else
    path
}

def getRelativePath(fullpath: String): String = {
  var relativepath = ""
  fullpath.foreach(i => if i == '/' || i == '\\' then relativepath = "" else relativepath += i)
  relativepath
}

// def indexFromList(paths: List[String], answer: Int, i: Int = 0): Int = {
//   if answer != i + 2 then
//     indexFromList(paths, answer, i+1)
// }
