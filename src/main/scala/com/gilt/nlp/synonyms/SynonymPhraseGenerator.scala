package com.gilt.nlp.synonyms

import com.gilt.nlp.{OpenNlp, PartOfSpeech, POS}
import opennlp.tools.postag.{POSModel, POSTaggerME}
import opennlp.tools.tokenize.{TokenizerModel, TokenizerME}

import scala.concurrent.{Await, ExecutionContext, Future}

trait SynonymPhraseGenerator {
  def generateSynonyms(phrase: String): Set[String]
}

/**
 * @param synonymProvider
 * @param inputStopWords Words on the input to not be considered for synonym replacements.
 * @param inputStopPartsOfSpeech disallow input words of certain PartOfSpeech from consideration in synonym replacements.
 *                               e.g. disallow all Adjectives from being candidate synonym replacements.
 * @param synonymStopWords Words to be excluded from synonym replacement results.
 * @param phraseEndStopWords Words to be excluded from synonym replacement, but only when in the trailing word position
 *                           (i.e. the last word of a generated result phrase.)
 *
 * Note, stop word/POS functionality here does not take existing words in input phrase into consideration. Any word
 * in the input phrase can appear in the output phrase(s) regardless of the stop lists provided as arguments here.
 */
class OpenNlpSynonymPhraseGenerator(synonymProvider: SynonymProvider,
                                    inputStopWords: Set[String] = Set.empty,
                                    inputStopPartsOfSpeech: Set[PartOfSpeech] = Set.empty,
                                    synonymStopWords: Set[String] = Set.empty,
                                    phraseEndStopWords: Set[String] = Set.empty) extends SynonymPhraseGenerator {

  protected def tokenizerModel: TokenizerModel = OpenNlp.TokenizerModel
  protected def posModel: POSModel = OpenNlp.PosTaggerModel

  private lazy val tokenizer = new TokenizerME(tokenizerModel)
  private lazy val posTagger = new POSTaggerME(posModel)

  def generateSynonyms(phrase: String): Set[String] = {
    val tokens = tokenizer.tokenize(phrase)
    val partsOfSpeech = posTagger.tag(tokens).map(POS(_).summaryPartOfSpeech)

    def recur(remaining: Seq[(String, Option[PartOfSpeech])], acc: Set[String]): Set[String] = {
      if (remaining.isEmpty) {
        acc.filterNot(_ == phrase)
      } else {
        val (word, optPos) = remaining.head

        val unfilteredSynonyms =
          optPos.filterNot(inputStopPartsOfSpeech.contains)
            .filterNot(_ => inputStopWords.contains(word))
            .fold(Set.empty[String]) {
            pos =>
              synonymProvider.getSynonyms(word, pos)
          }

        val newacc = unfilteredSynonyms.filterNot(synonymStopWords.contains) match {
          case s if s.isEmpty =>
            acc match {
              case _ if acc.isEmpty => Set(word)
              case _ => acc.map(_ + " " + word)
            }
          case synonyms =>
            acc match {
              case _ if acc.isEmpty => synonyms + word
              case _ =>
                // Check if this is the tail of the phrase, if so, need to filter out end-stop-words.
                val alts = if (remaining.tail.isEmpty) {
                  synonyms.filterNot(phraseEndStopWords.contains)
                } else {
                  synonyms
                } + word
                
                acc.flatMap(phrase => alts.map(phrase + " " + _))
            }
        }
        recur(remaining.tail, newacc)
      }
    }

    recur(tokens.zip(partsOfSpeech).toSeq, Set.empty)
  }
}
