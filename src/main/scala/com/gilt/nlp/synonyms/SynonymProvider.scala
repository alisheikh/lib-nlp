package com.gilt.nlp.synonyms

import java.io.File

import scala.collection.JavaConverters._

import com.gilt.nlp.PartOfSpeech
import edu.mit.jwi
import edu.mit.jwi.item.{POS => JwiPos}

trait SynonymProvider {
  /**
   * Get known synonyms for a given word when used in as a specific POS.
   *
   * @param word
   * @param partOfSpeech Which Part of Speech to match the word on.
   * @return
   */
  def getSynonyms(word: String, partOfSpeech: PartOfSpeech): Set[String]
}

class WordnetSynonymProvider(dictLocation: File, allowMultiWordResults: Boolean = false) extends SynonymProvider {

  private val disallowedDuetoMultiword = if (allowMultiWordResults) {
    _: String => false
  } else {
    s: String => s.contains(" ")
  }

  private val dictionary = {
    val d = new jwi.Dictionary(dictLocation)
    d.open()
    d
  }

  /**
   * Get known synonyms for a given word when used in as a specific POS.
   *
   * @param word
   * @param partOfSpeech Which Part of Speech to match the word on.
   * @return
   */
  override def getSynonyms(word: String, partOfSpeech: PartOfSpeech): Set[String] = {
    val optWords = for {
      pos <- convert(partOfSpeech)
      indexWord <- Option(dictionary.getIndexWord(word, pos))
      wordIds <- Option(indexWord.getWordIDs)
      synsets = wordIds.asScala.map(_.getSynsetID).map(dictionary.getSynset)
    } yield {
      synsets
        .flatMap(_.getWords.asScala)
        .map(_.getLemma.replaceAll("_", " "))
        .filterNot(_ == word)
        .filterNot(disallowedDuetoMultiword)
        .toSet
    }

    optWords.getOrElse(Set.empty)
  }

  private def convert(pos: PartOfSpeech): Option[JwiPos] = pos match {
    case PartOfSpeech.Verb => Some(JwiPos.VERB)
    case PartOfSpeech.Noun => Some(JwiPos.NOUN)
    case PartOfSpeech.Adjective => Some(JwiPos.ADJECTIVE)
    case PartOfSpeech.Adverb => Some(JwiPos.ADVERB)
    case PartOfSpeech.Unknown => None
  }
}
