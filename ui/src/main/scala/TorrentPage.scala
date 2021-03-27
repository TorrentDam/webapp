package default

import com.raquo.laminar.api.L._


object TorrentPage {

  def apply(infoHash: String) = {

    div(s"torrent here: $infoHash")
  }
}
