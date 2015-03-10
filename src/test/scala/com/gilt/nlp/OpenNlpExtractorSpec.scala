package com.gilt.nlp

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import scala.concurrent.ExecutionContext.Implicits.global

class OpenNlpExtractorSpec extends FlatSpec with Matchers {
  "Extractor" should "extract appropriate only phrases from 'woven black dress'" in {
    val extractor = new OpenNlpExtractor(Set.empty, Set.empty)
    val phrases = extractor.extractInterestingPhrases("woven black dress").toSeq
    phrases.nonEmpty should be (true)
    phrases.contains("black dress") should be (true)
    phrases.contains("woven dress") should be (true)
    phrases.contains("woven black dress") should be (true)
  }

  it should "correctly skip stop words" in {

    val extractor1 = new OpenNlpExtractor(Set.empty, Set.empty)
    extractor1.extractInterestingPhrases("woven black dress").toSeq.contains("black dress") should be (true)

    val extractor2 = new OpenNlpExtractor(Set("black"), Set.empty)
    extractor2.extractInterestingPhrases("woven black dress").toSeq.contains("black dress") should be (false)
  }

  it should "correctly skip trailing stop words when in trailing position only" in {
    val extractor1 = new OpenNlpExtractor(Set.empty, Set.empty)
    val phrases1 = extractor1.extractInterestingPhrases("woven black shirt dress").toSeq

    phrases1.contains("black shirt") should be (true)
    phrases1.contains("black shirt dress") should be (true)
    phrases1.contains("black dress") should be (true)

    val extractor2 = new OpenNlpExtractor(Set.empty, Set("dress"))
    val phrases2 = extractor2.extractInterestingPhrases("woven black shirt dress").toSeq

    phrases2.contains("black shirt") should be (true)
    phrases2.contains("black shirt dress") should be (false)
    phrases2.contains("black dress") should be (false)
  }

  it should "extract appropriate phrases from 'summer silk flutter dress'" in {
    val extractor = new OpenNlpExtractor(Set.empty, Set.empty)
    val phrases = extractor.extractInterestingPhrases("summer silk flutter dress").toSeq
    phrases.nonEmpty should be (true)
    phrases.contains("summer dress") should be (true)
    phrases.contains("silk dress") should be (true)
    phrases.contains("flutter dress") should be (true)
    phrases.contains("summer silk dress") should be (true)
    phrases.contains("summer flutter dress") should be (true)
  }

  it should "extract appropriate phrases from 'summer silk flutter dress' when not allowing skip-phrases" in {
    val extractor = new OpenNlpExtractor(Set.empty, Set.empty)
    val phrases = extractor.extractInterestingPhrases("summer silk flutter dress", allowSkipPhrases = false).toSeq
    phrases.nonEmpty should be (true)
    phrases.contains("summer dress") should be (false)
    phrases.contains("silk dress") should be (false)
    phrases.contains("flutter dress") should be (true)
    phrases.contains("summer silk dress") should be (false)
    phrases.contains("summer flutter dress") should be (false)
    phrases.contains("silk flutter dress") should be (true)
  }

  it should "extract appropriate phrases from 'woven silk dress'" in {
    val extractor = new OpenNlpExtractor(Set.empty, Set.empty)
    val phrases = extractor.extractInterestingPhrases("woven silk dress").toSeq
    phrases.nonEmpty should be (true)
    phrases.contains("woven dress") should be (true)
    phrases.contains("silk dress") should be (true)
    phrases.contains("woven silk dress") should be (true)
  }

  it should "not have phrases repeated even when they are found in different sentences" in {
    val extractor = new OpenNlpExtractor(Set.empty, Set.empty)
    val phrases = extractor.extractInterestingPhrases("a nice black dress. a very nice black dress").toSeq
    phrases.nonEmpty should be (true)
    phrases.contains("black dress") should be (true)
    phrases.groupBy(identity).get("black dress").map(_.size) should be (Some(1))
  }

  it should "remove non-valid characters" in {
    val sourceBody = String.valueOf(Array[Char]('b', 'l', 'a', 'c', 0x19, 'k', ' ', 'd', 'r', 'e', 's', 's'))
    val extractor = new OpenNlpExtractor(Set.empty, Set.empty)
    val phrases = extractor.extractInterestingPhrases(sourceBody).toSeq
    phrases.nonEmpty should be (true)
    phrases.contains("black dress") should be (true)
    phrases.groupBy(identity).get("black dress").map(_.size) should be (Some(1))
  }

  it should "not extract across obviously different sentences" in {
    val sourceBody = "Egyptian cotton. 200 thread count percale. Features a double hemstitch"
    val extractor = new OpenNlpExtractor(Set.empty, Set.empty)
    val phrases = extractor.extractInterestingPhrases(sourceBody).toSeq
    phrases.contains("Egyptian cotton thread count") should be (false)
  }

  it should "correctly clean trailing chars" in {
    val extractor = new OpenNlpExtractor(Set.empty, Set.empty)
    extractor.cleanTrailing("colon:") should be ("colon")
    extractor.cleanTrailing("semicolon;") should be ("semicolon")
    extractor.cleanTrailing("period.") should be ("period")
    extractor.cleanTrailing("comma,") should be ("comma")
    extractor.cleanTrailing("forwardslash/") should be ("forwardslash")
    extractor.cleanTrailing("nothing") should be ("nothing")
  }

  it should "correctly extract and count nouns" in {
    val extractor = new OpenNlpExtractor(Set.empty, Set.empty)
    val counted = extractor.extractCountedNounPhrases("The lazy brown dog who, was a dog named Dog, jumped over the quick fox, Michael Fox")
    counted.toSet should be (Set(("michael", 1), ("brown dog", 1), ("lazy brown dog", 1), ("named dog", 1), ("fox", 2), ("quick fox", 1), ("michael fox",1), ("dog", 3)))
  }
}
