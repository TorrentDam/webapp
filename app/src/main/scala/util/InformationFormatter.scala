package util

import squants.information.{Bytes, Gigabytes, Information, Kilobytes, Megabytes}

object InformationFormatter {

  private val units = List(Gigabytes, Megabytes, Kilobytes, Bytes)
  
  def inBestUnit(bytes: Information): Information =
    units.find(unit => bytes.to(unit) >= 1).map(bytes.in).getOrElse(bytes)
}
