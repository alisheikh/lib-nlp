package com.gilt.nlp

import com.gilt.nlp.POS._
import com.gilt.nlp.util.CollectionUtils._
import com.gilt.gfc.logging.Loggable

import opennlp.tools.chunker.{ChunkerME, ChunkerModel}
import opennlp.tools.postag.{POSTaggerME, POSModel}
import opennlp.tools.sentdetect.{SentenceDetectorME, SentenceModel}
import opennlp.tools.tokenize.{TokenizerME, TokenizerModel}
import opennlp.tools.util.Span

trait Extractor {
  def extractInterestingPhrases(body: String, allowSkipPhrases: Boolean = true): Iterable[String]
  def extractCountedNounPhrases(body: String): Iterable[(String, Int)]
}

/**
 * This class is known to be not threadsafe.
 */
class OpenNlpExtractor(stopWords: Set[String] = Set.empty,
                       phraseEndStopWords: Set[String] = Set.empty,
                       postfixesToStrip: Set[String] = Set(":", ";", ".", ",", "/"),
                       distanceThreshold: Int = 4) extends Extractor with Loggable {

  protected def sentenceModel: SentenceModel = OpenNlp.SentenceModel
  protected def tokenizerModel: TokenizerModel = OpenNlp.TokenizerModel
  protected def posModel: POSModel = OpenNlp.PosTaggerModel
  protected def chunkerModel: ChunkerModel = OpenNlp.ChunkerModel

  private lazy val sentenceDetector = new SentenceDetectorME(sentenceModel)
  private lazy val tokenizer = new TokenizerME(tokenizerModel)
  private lazy val posTagger = new POSTaggerME(posModel)
  private lazy val chunker = new ChunkerME(chunkerModel)

  /**
   * Extract noun phrases and their counts from a given body of text.
   * @param body
   * @return
   */
  override def extractCountedNounPhrases(body: String): Iterable[(String, Int)] = {
    val tokens = tokenizer.tokenize(scrub(body)).map(tok => cleanTrailing(tok.toLowerCase))
    val posTags = posTagger.tag(tokens).map(POS(_))

    val singleWordPhrases: Array[String] = tokens.zip(posTags).filter {
      case (tok, pos) => tok.nonEmpty && !isStop(tok) && isNoun(pos)
    }.map(_._1)

    val multiWordPhrases = extractInterestingPhrases(tokens, posTags, false)

    val phrases = singleWordPhrases ++ multiWordPhrases

    phrases.groupBy(identity).map {
      case (tok, insts) => tok -> insts.size
    }
  }

  /**
   * Inspired by [[http://dragon.ischool.drexel.edu/xtract.asp]]
   *
   * Using POS tags as [[http://blog.dpdearing.com/2011/12/opennlp-part-of-speech-pos-tags-penn-english-treebank described here]]
   *
   * This function implements something akin to the xTract description, by doing the following:
   *
   * $ - Sentence detection (to only pull phrases from individual sentences)
   * $ - POS ("Part of Speech") Tagging, to allow processing only certain parts of speech (Nouns, etc.)
   * $ - "Chunking" - to restrict phrase extraction to appropriate sub-sentence structures
   * $ - Filtering chunks to "noun phrases" only
   * $ - Extraction of words based on some simple rules on POS:
   * $   - First word can be either Noun or Adjective
   * $   - Select other Adjectives/Nouns within a threshold of the "first word"
   * $   - From this selection, prepare n-grams such that the last word of N-Gram must be a Noun.
   */
  override def extractInterestingPhrases(body: String, allowSkipPhrases: Boolean = true): Iterable[String] = {
    sentenceDetector.sentDetect(body).flatMap { sentence =>
      val tokens = tokenizer.tokenize(scrub(sentence)).map(tok => cleanTrailing(tok.toLowerCase))
      val posTags = posTagger.tag(tokens).map(POS(_))
      extractInterestingPhrases(tokens, posTags, allowSkipPhrases)
    }.toSet
  }

  private[nlp] def cleanTrailing(str: String): String = {
    def stripTailing(target: String, postfix: String): String = {
      if (target.endsWith(postfix)) {
        target.substring(0, target.length - postfix.length)
      } else {
        target
      }
    }

    postfixesToStrip.foldLeft(str) {
      case (accStr, postfix) => stripTailing(accStr, postfix)
    }
  }

  protected def isStop(word: String): Boolean = {
    word.length < 2 || word.contains('.') || word.contains(';') || word.contains(':') || word.contains('"') || stopWords.contains(word)
  }

  /**
   * @param str
   * @return
   */
  protected def scrub(str: String): String = scrubInvalidXmlCharacters(str)

  private def scrubInvalidXmlCharacters(str: String): String = {
    def isGoodCharacter(ch: Char): Boolean = {
      // Ref: http://blog.mark-mclaren.info/2007/02/invalid-xml-characters-when-valid-utf8_5873.html
      ch == 0x9 || ch == 0xA || ch == 0xD || (ch >= 0x20 && ch <= 0xD7FF) || (ch >= 0xE000 && ch <= 0xFFFD) || (ch >= 0x10000 && ch <= 0x10FFFF)
    }

    if (!str.forall(isGoodCharacter)) {
      str.filter(isGoodCharacter)
    } else {
      str
    }
  }

  /**
   * see http://blog.dpdearing.com/2011/12/opennlp-part-of-speech-pos-tags-penn-english-treebank/ for list of POS tags.
   */
  private def isNoun(pos: POS) = pos match {
    case SingularNoun => true
    case PluralNoun => true
    case SingularProperNoun => true
    case PluralProperNoun => true
    case _ => false
  }

  private def isAdjective(pos: POS) = {
    pos match {
      case Adjective => true
      case ComparativeAdjective => true
      case SupurlativeAdjective => true
      case _ => false
    }
  }

  private def isVerb(pos: POS) = pos match {
    // FIXME - only accepting certain ones for now, let's be restrictive rather than permissive for now...
//    case Verb => true
//    case ThirdPersonSingularPresentVerb => true
    case NonThirdPersonSingularPresentVerb => true
    case PastParticipleVerb => true
    case PastTenseVerb => true
    case PresentParticipleVerb => true
    case _ => false
  }

  private def hasAcceptableEnding(terms: Seq[(String, POS)]): Boolean = {
    terms.lastOption.exists {
      case (tok, pos) =>
        isNoun(pos) && !phraseEndStopWords.contains(tok)
    }
  }

  private def extractInterestingPhrases(tokens: Array[String], posTags: Array[POS], allowSkipPhrases: Boolean): Iterable[String] = {
    val allTaggedTokens = tokens.zip(posTags)

    def acceptableChunkType(span: Span): Boolean = span.getType match {
      case "NP" => true
      case "VP" => true
      case _ => false
    }

    // Extract terms from chunked pieces... allows for more accuracy (ensures words pulled together are actually in the same phrase)
    val chunkedTaggedTokens = chunker.chunkAsSpans(tokens, posTags.map(_.tag)).filter(acceptableChunkType).map { span =>
      // spans are inclusive ending.
      allTaggedTokens.slice(span.getStart, span.getEnd + 1)
    }

    // Process all chunks to get all phrases.
    chunkedTaggedTokens.flatMap { taggedTokens =>
      // Find all "first" words in this chunk.
      val firstWords = taggedTokens.zipWithIndex.filter {
        case ((tok, pos), idx) =>
          !isStop(tok) && (isNoun(pos) || isAdjective(pos) || isVerb(pos))
      }

      firstWords.flatMap { case ((firstWord, firstWordPos), idx) =>
        // get the tagged tokens that are ending words for this "firstWord"
        val taggedEndTokens: Array[(String, POS)] = taggedTokens.slice(idx + 1, idx + distanceThreshold + 1).filter {
          case (endWord, endPos) => firstWord != endWord && (isNoun(endPos) || isAdjective(endPos)) && !isStop(endWord)
        }

        // Just keep the unique items, retaining correct order (TreeSet does not work here)
        val uniqueTaggedTokens = taggedEndTokens.foldLeft(IndexedSeq.empty[(String, POS)]) {
          case (acc, v) => if (acc.contains(v)) acc else acc :+ v
        }

        val subsequences = if (allowSkipPhrases) uniqueTaggedTokens.tailingSkipSequences else uniqueTaggedTokens.inits

        // build N-grams, for bi-grams and up, depending on the number of acceptable terms in the span from the "first"
        subsequences.filter(terms => terms.nonEmpty && hasAcceptableEnding(terms)).map { s =>
          s"$firstWord ${s.map(_._1).mkString(" ")}"
        }
      }
    }
  }
}
