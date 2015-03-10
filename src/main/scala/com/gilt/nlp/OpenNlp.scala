package com.gilt.nlp

import com.gilt.nlp.util.Closer._
import opennlp.tools.chunker.ChunkerModel
import opennlp.tools.postag.POSModel
import opennlp.tools.sentdetect.SentenceModel
import opennlp.tools.tokenize.TokenizerModel

object OpenNlp {
  private def model(path: String) = Option(getClass.getClassLoader.getResourceAsStream(path)).getOrElse(sys.error(s"Failed to get model for '$path'"))
  lazy val SentenceModel: SentenceModel = doWith(model("nlp-models/en-sent.bin"))(new SentenceModel(_))
  lazy val TokenizerModel: TokenizerModel = doWith(model("nlp-models/en-token.bin"))(new TokenizerModel(_))
  lazy val PosTaggerModel: POSModel = doWith(model("nlp-models/en-pos-maxent.bin"))(new POSModel(_))
  lazy val ChunkerModel: ChunkerModel = doWith(model("nlp-models/en-chunker.bin"))(new ChunkerModel(_))
}
