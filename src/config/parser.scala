package yakumo.config
import yakumo.*

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


def passwordEnabled(config: List[String]): Boolean =
  val passenabled = getSetting(config, "usepass=")
  val password = getSetting(config, "password=")

  if passenabled == "no" || passenabled == "false" || password == "" then
    false
  else
    true

def getPassword(config: List[String]): String =
  getSetting(config, "password=")

def getStorageDirectory(config: List[String]): String =
  val setting = getSetting(config, "directory=")
  if setting == "\"\"" || setting == "" then
    "./"
  else if setting(setting.length-1) != '/' then
     s"$setting/"
  else
    setting

def getFileLimit(config: List[String], mode: String): Int =
  val settingName =
    mode match
      case "perfile" => "maxperfile="
      case "total" => "maxtotal="
  val strnum = getSetting(config, settingName)
  try
    val num = strnum.toInt
    num
  catch
    case e: Exception => -1

def getPathLayout(config: List[String]): Int =
  val amt = getSetting(config, "pathsperline=")
  try
    val amt_i = amt.toInt
    if amt_i < 0 then
      0
    else
      amt_i
  catch
    case e: Exception => 2

//test this
private def getSetting(config: List[String], seek: String): String = {
  def findLine(i: Int = 0): String =
    if i >= config.length then
      ""
    else if config(i).contains(seek) == true then
      config(i)
    else
      findLine(i+1)

  def getLineSetting(line: String): String =
    var copy = false
    var setting = ""
    for chr <- line do
      if copy == true then
        setting += chr
      else if chr == '=' then
        copy = true
    setting

  val settingline = findLine()
  if settingline != "" then
    getLineSetting(settingline)
  else
    ""
}

// private def getLineSetting(line: String): String = {
//   var copy = false
//   var setting = ""
//   for chr <- line do {
//     if copy == true then
//       setting += chr
//     else if chr == '=' then
//       copy = true
//   }
//   setting
// }
//
// private def findLine(config: List[String], seekstr: String, i: Int = 0): String = {
//   if config(i).contains(seekstr) == true then
//     config(i)
//   else if i == config.length-1 then
//     ""
//   else
//     findLine(config, seekstr, i+1)
// }
