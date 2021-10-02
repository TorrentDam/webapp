package default

import io.circe.parser
import io.circe.Codec
import io.circe.Decoder
import io.circe.generic.semiauto.{deriveDecoder, deriveCodec}
import io.circe.Error
import com.github.lavrov.bittorrent.InfoHash
import io.circe.Encoder

enum ServiceWorkerMessage {

  case Query(query: String)
}

object ServiceWorkerMessage {

  given Codec[ServiceWorkerMessage] = deriveCodec
}