package com.gilt.nlp


sealed abstract class PartOfSpeech private[nlp](val name: String)
object PartOfSpeech {
  case object Verb extends PartOfSpeech("verb")
  case object Noun extends PartOfSpeech("noun")
  case object Adjective extends PartOfSpeech("adjective")
  case object Adverb extends PartOfSpeech("adverb")
  case object Unknown extends PartOfSpeech("<unknown>")
}

/**
 * based on subset of Penn English Treebank Parts of Speech (POS) tags.
 *
 * [http://www.cis.upenn.edu/~treebank], more specifically [ftp://ftp.cis.upenn.edu/pub/treebank/doc/tagguide.ps.gz]
 */
sealed abstract class POS private[nlp](val tag: String, val summaryPartOfSpeech: Option[PartOfSpeech] = None)

object POS {
  case object CoordinatingConjunction extends POS("CC")
  case object CardinalNumber extends POS("CN")
  case object Determiner extends POS("DT")
  case object ExistentialThere extends POS("EX")
  case object ForeignWord extends POS("FW")
  case object Preposition extends POS("IN")

  case object Adjective extends POS("JJ", Some(PartOfSpeech.Adjective))
  case object ComparativeAdjective extends POS("JJR", Some(PartOfSpeech.Adjective))
  case object SupurlativeAdjective extends POS("JJS", Some(PartOfSpeech.Adjective))

  case object ListModalMarker extends POS("LS")
  case object Modal extends POS("MD")

  case object SingularNoun extends POS("NN", Some(PartOfSpeech.Noun))
  case object PluralNoun extends POS("NNS", Some(PartOfSpeech.Noun))
  case object SingularProperNoun extends POS("NNP", Some(PartOfSpeech.Noun))
  case object PluralProperNoun extends POS("NNPS", Some(PartOfSpeech.Noun))

  case object Predeterminer extends POS("PDT")
  case object PossessiveEnding extends POS("POS")
  case object PersonalPronoun extends POS("PRP")
  case object PossessivePronoun extends POS("PRP$")

  case object Adverb extends POS("RB", Some(PartOfSpeech.Adverb))
  case object ComparativeAdverb extends POS("RBR", Some(PartOfSpeech.Adverb))
  case object SuperlativeAdverb extends POS("RBS", Some(PartOfSpeech.Adverb))

  case object Particle extends POS("RP")
  case object Symbol extends POS("SYM")
  case object To extends POS("TO")
  case object Interjection extends POS("UH")

  case object Verb extends POS("VB", Some(PartOfSpeech.Verb))
  case object PastTenseVerb extends POS("VBD", Some(PartOfSpeech.Verb))
  case object PresentParticipleVerb extends POS("VBG", Some(PartOfSpeech.Verb))
  case object PastParticipleVerb extends POS("VBN", Some(PartOfSpeech.Verb))
  case object NonThirdPersonSingularPresentVerb extends POS("VBP", Some(PartOfSpeech.Verb))
  case object ThirdPersonSingularPresentVerb extends POS("VBZ", Some(PartOfSpeech.Verb))

  case object Whdeterminer extends POS("WDT")
  case object Whpronoun extends POS("WP")
  case object PossessiveWhpronoun extends POS("WP$")
  case object Whadverb extends POS("WRB")

  private val ByTag = Seq(
    CoordinatingConjunction, CardinalNumber, Determiner, ExistentialThere, ForeignWord, Preposition,
    Adjective, ComparativeAdjective, SupurlativeAdjective,
    ListModalMarker, Modal,
    SingularNoun, PluralNoun, SingularProperNoun, PluralProperNoun,
    Predeterminer, PossessiveEnding, PersonalPronoun, PossessivePronoun,
    Adverb, ComparativeAdverb, SuperlativeAdverb,
    Particle, Symbol, Interjection,
    Verb, PastTenseVerb, PresentParticipleVerb, PastParticipleVerb, NonThirdPersonSingularPresentVerb, ThirdPersonSingularPresentVerb,
    Whdeterminer, Whpronoun, PossessiveWhpronoun, Whadverb
  ).map(pos => pos.tag -> pos).toMap

  def apply(tag: String): POS = ByTag.getOrElse(tag, Unknown)

  case object Unknown extends POS("UNKNOWN")
}
