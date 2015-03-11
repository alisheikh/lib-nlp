package com.gilt.nlp.synonyms

import com.gilt.nlp.PartOfSpeech
import com.gilt.nlp.PartOfSpeech.{Noun, Adverb, Adjective}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, FlatSpec}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class OpenNlpSynonymPhraseGeneratorSpec extends FlatSpec with Matchers {
  class TestSynonymProvider(mappings: Map[(String, PartOfSpeech), Set[String]]) extends SynonymProvider {
    override def getSynonyms(word: String, partOfSpeech: PartOfSpeech): Set[String] =
      mappings.getOrElse((word, partOfSpeech), Set.empty)
  }

  "SynonymPhraseGenerator" should "generate synonym phrases where there are acceptable synonyms for words in phrase" in {
    val iut = new OpenNlpSynonymPhraseGenerator(
      new TestSynonymProvider(
        Map(
          ("little", Adjective) -> Set("small", "tiny"),
          ("little", Adverb) -> Set("12345"),
          ("little", Noun) -> Set("98765")
        )
      ))

    val phrases = iut.generateSynonyms("little cotton dress")
    phrases.contains("small cotton dress") should be (true)
    phrases.contains("tiny cotton dress") should be (true)
    phrases.contains("little cotton dress") should be (false)
    phrases.contains("12345 cotton dress") should be (false)
    phrases.contains("98765 cotton dress") should be (false)
  }

  it should "generate synonym phrases taking care not to map stopped input parts-of-speech" in {
    val iut = new OpenNlpSynonymPhraseGenerator(
      new TestSynonymProvider(
        Map(
          ("little", Adjective) -> Set("small", "tiny"),
          ("pullover", Noun) -> Set("jumper", "sweater"),
          ("little", Adverb) -> Set("12345"),
          ("little", Noun) -> Set("98765")
        )
      ),
      inputStopPartsOfSpeech = Set(PartOfSpeech.Adjective)
    )

    val phrases = iut.generateSynonyms("little cotton pullover")
    phrases.contains("small cotton pullover") should be (false)
    phrases.contains("tiny cotton pullover") should be (false)
    phrases.contains("little cotton pullover") should be (false)
    phrases.contains("little cotton jumper") should be (true)
    phrases.contains("little cotton sweater") should be (true)
  }

  it should "generate synonym phrases taking care not to map stopped input words" in {
    val iut = new OpenNlpSynonymPhraseGenerator(
      new TestSynonymProvider(
        Map(
          ("little", Adjective) -> Set("small", "tiny"),
          ("pullover", Noun) -> Set("jumper", "sweater"),
          ("little", Adverb) -> Set("12345"),
          ("little", Noun) -> Set("98765")
        )
      ),
      inputStopWords = Set("little", "jumper")
    )

    val phrases = iut.generateSynonyms("little cotton pullover")
    phrases.contains("small cotton pullover") should be (false)
    phrases.contains("tiny cotton pullover") should be (false)
    phrases.contains("little cotton pullover") should be (false)
    phrases.contains("little cotton jumper") should be (true)
    phrases.contains("little cotton sweater") should be (true)
  }

  it should "generate synonym phrases taking care not to map to stopped synonym words" in {
    val iut = new OpenNlpSynonymPhraseGenerator(
      new TestSynonymProvider(
        Map(
          ("little", Adjective) -> Set("jumper"),
          ("pullover", Noun) -> Set("jumper", "sweater"),
          ("little", Adverb) -> Set("12345"),
          ("little", Noun) -> Set("98765")
        )
      ),
      synonymStopWords = Set("jumper")
    )

    val phrases = iut.generateSynonyms("little cotton pullover")
    phrases.contains("little cotton pullover") should be (false) // Source phrase not included by definition.
    phrases.contains("little cotton jumper") should be (false)
    phrases.contains("little cotton sweater") should be (true)
    phrases.contains("jumper cotton sweater") should be (false)
  }

  it should "generate synonym phrases taking care not to map to stopped synonym tail position words" in {
    val iut = new OpenNlpSynonymPhraseGenerator(
      new TestSynonymProvider(
        Map(
          ("little", Adjective) -> Set("jumper"),
          ("pullover", Noun) -> Set("jumper", "sweater"),
          ("little", Adverb) -> Set("12345"),
          ("little", Noun) -> Set("98765")
        )
      ),
      phraseEndStopWords = Set("jumper")
    )

    val phrases = iut.generateSynonyms("little cotton pullover")
    phrases.contains("little cotton jumper") should be (false)
    phrases.contains("little cotton sweater") should be (true)
    phrases.contains("jumper cotton sweater") should be (true)
  }

  it should "not return the input phrase when there are no synonyms" in {
    val iut = new OpenNlpSynonymPhraseGenerator(new TestSynonymProvider(Map.empty))

    val phrases = iut.generateSynonyms("little cotton dress")
    phrases.isEmpty should be (true)
  }
}
