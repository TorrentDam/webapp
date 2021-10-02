package default

import io.circe.parser
import io.circe.Codec
import io.circe.Decoder
import io.circe.generic.semiauto.{deriveDecoder, deriveCodec}
import io.circe.Error
import com.github.lavrov.bittorrent.InfoHash
import io.circe.Encoder


enum WindowMessage {
  import WindowMessage.SearchResults

  case SearchResults(query: String, entries: List[SearchResults.Entry])
}

object WindowMessage {

  object SearchResults {

    case class Entry(name: String, infoHash: InfoHash, size: Long, ext: List[String])

    given Codec[InfoHash] = 
      Codec.from(
        Decoder.decodeString.emap {
          case InfoHash.fromString(infoHash) => Right(infoHash)
          case _ => Left("Invalid infohash")
        },
        Encoder.encodeString.contramap[InfoHash](_.toString)
      )

    given Codec[Entry] = deriveCodec
  }

  given Codec[WindowMessage] = deriveCodec
}