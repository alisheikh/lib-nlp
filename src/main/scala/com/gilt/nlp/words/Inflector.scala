package com.gilt.nlp.words

object Inflector {

  /**
   * The lowercase words that are to be excluded and not processed.
   */
  private val Uncountables = Set("jewelry", "equipment", "information", "rice", "money", "species", "series", "fish", "sheep", "ipod touch")

  private val SingularizeRules = Seq(
    InflectorRule("(base)s$", "$1"),
    InflectorRule("(quiz)zes$", "$1"),
    InflectorRule("(matr)ices$", "$1ix"),
    InflectorRule("(vert|ind)ices$", "$1ex"),
    InflectorRule("^(ox)en", "$1"),
    InflectorRule("(alias|status)$", "$1"),
    InflectorRule("(alias|status)es$", "$1"),
    InflectorRule("(octop|vir)us$", "$1us"),
    InflectorRule("(octop|vir)i$", "$1us"),
    InflectorRule("(cris|ax|test)es$", "$1is"),
    InflectorRule("(cris|ax|test)is$", "$1is"),
    InflectorRule("(shoe|toe|tie)s$", "$1"),
    InflectorRule("(o|bus|lens)es$", "$1"),
    InflectorRule("([m|l])ice$", "$1ouse"),
    InflectorRule("(x|ch|ss|sh)es$", "$1"),
    InflectorRule("(m)ovies$", "$1ovie"),
    InflectorRule("(s)eries$", "$1eries"),
    InflectorRule("(sleeve)s$", "$1"),
    InflectorRule("([^aeiouy]|qu)ies$", "$1y"),
    InflectorRule("([lr])ves$", "$1f"),
    InflectorRule("(tive)s$", "$1"),
    InflectorRule("(hive)s$", "$1"),
    InflectorRule("([^f])ves$", "$1fe"),
    InflectorRule("(^analy)s[ie]s$", "$1sis"),
    InflectorRule("((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "$1$2sis"),
    InflectorRule("([ti])a$", "$1um"),
    InflectorRule("(n)ews$", "$1ews"),
    InflectorRule("(s|si|u)s$", "$1s"),
    InflectorRule("s$", "")
  )

  private val PluralizeRules = Seq(
    InflectorRule("(chalcedony|baby|rhodium|stretch|mother of pearl|pave)$", "$1"),
    InflectorRule("(qui)(z|zes)$", "$1zes"),
    InflectorRule("(matr)(ix|ices)$", "$1ices"),
    InflectorRule("(vert|ind)(ex|ices)$", "$1ices"),
    InflectorRule("^(ox)(en)?$", "$1en"),
    InflectorRule("(alias|status)(es)?$", "$1es"),
    InflectorRule("(octop|vir)(us|i)$", "$1i"),
    InflectorRule("(cris|ax|test)(is|es)$", "$1es"),
    InflectorRule("(shoe|tie|sleeve)(s)?$", "$1s"),
    InflectorRule("(polo)(s)?$", "$1s"),
    InflectorRule("(o|bus|lens)(es)?$", "$1es"),
    InflectorRule("(m)(ouse|ice)$", "$1ice"),
    InflectorRule("(x|ch|ss|sh)(es)?$", "$1es"),
    InflectorRule("(m)ovie(s)$", "$1ovies"),
    InflectorRule("(s)eries$", "$1eries"),
    InflectorRule("([^aeiouy]|qu)(y|ies)$", "$1ies"),
    InflectorRule("([lr])(f|ves)$", "$1ves"),
    InflectorRule("(tive)(s)?$", "$1s"),
    InflectorRule("(hive)(s)?$", "$1s"),
    InflectorRule("(safe)(s)?$", "$1s"),
    InflectorRule("([^f])(fe|ves)$", "$1ves"),
    InflectorRule("(^analy)(sis|ses)$", "$1ses"),
    InflectorRule("((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)(sis|ses)$", "$1$2ses"),
    InflectorRule("([ti])(um|a)$", "$1a"),
    InflectorRule("(n)ews$", "$1ews"),
    InflectorRule("(s|si|u)s$", "$1s"),
    // Explicit set of matches rather than globally allowed "$1s" - this is safer...
    InflectorRule("(boot)(s)?$", "$1s"),
    InflectorRule("(silk)(s)?$", "$1s"),
    InflectorRule("(tee)(s)?$", "$1s"),
    InflectorRule("(bodice)(s)?$", "$1s"),
    InflectorRule("(bikini)(s)?$", "$1s"),
    InflectorRule("(movement)(s)?$", "$1s"),
    InflectorRule("(overlay)(s)?$", "$1s"),
    InflectorRule("(swimsuit)(s)?$", "$1s"),
    InflectorRule("(t-shirt)(s)?$", "$1s"),
    InflectorRule("(set)(s)?$", "$1s"),
    InflectorRule("(blender)(s)?$", "$1s"),
    InflectorRule("(winder)(s)?$", "$1s"),
    InflectorRule("(shirt)(s)?$", "$1s"),
    InflectorRule("(duvet)(s)?$", "$1s"),
    InflectorRule("(trim)(s)?$", "$1s"),
    InflectorRule("(necklace)(s)?$", "$1s"),
    InflectorRule("(dial)(s)?$", "$1s"),
    InflectorRule("(maxi)(s)?$", "$1s"),
    InflectorRule("(runner)(s)?$", "$1s"),
    InflectorRule("(blazer)(s)?$", "$1s"),
    InflectorRule("(sweatshirt)(s)?$", "$1s"),
    InflectorRule("(skirt)(s)?$", "$1s"),
    InflectorRule("(collar)(s)?$", "$1s"),
    InflectorRule("(pullover)(s)?$", "$1s"),
    InflectorRule("(hoodie)(s)?$", "$1s"),
    InflectorRule("(sapphire)(s)?$", "$1s"),
    InflectorRule("(chain)(s)?$", "$1s"),
    InflectorRule("(tunic)(s)?$", "$1s"),
    InflectorRule("(wallet)(s)?$", "$1s"),
    InflectorRule("(hat)(s)?$", "$1s"),
    InflectorRule("(cardigan)(s)?$", "$1s"),
    InflectorRule("(gown)(s)?$", "$1s"),
    InflectorRule("(jumpsuit)(s)?$", "$1s"),
    InflectorRule("(bra)(s)?$", "$1s"),
    InflectorRule("(jacket)(s)?$", "$1s"),
    InflectorRule("(bezel)(s)?$", "$1s"),
    InflectorRule("(wedge)(s)?$", "$1s"),
    InflectorRule("(tote)(s)?$", "$1s"),
    InflectorRule("(maker)(s)?$", "$1s"),
    InflectorRule("(satchel|bag)(s)?$", "$1s"),
    InflectorRule("(sharpener)(s)?$", "$1s"),
    InflectorRule("(sweater|disc|teardrop|neck|pump|cabinet|toe|jane|camisole|waist|boot|tie|hive|slip)(s)?$", "$1s"),
    InflectorRule("(sheath|sole|oval|cabinet|waist|blouse|safe|polo|cable|headboard|bookcase|neckline|vest)(s)?$", "$1s"),
    InflectorRule("(solitaire|chronograph|shift|henley|gemstone|center|base|zip|ceiling|waistband|ankle|jacquard|teardrop|lace-up|multi|a-line|coat|suit)(s)?$", "$1s"),
    InflectorRule("(sofa|peplum|dome|emerald|halter|round|sole|oval|disc|waistband|heart|ankle|platform|jersey)(s)?$", "$1s"),
    InflectorRule("(mini|pan|fireplace|leg|belt|a-line|loop|wrap|drop|ring|trouser|hem|clock|shoulder|belt|lamp|flat|rug|band|tankini|bikini|pearl)(s)?$", "$1s"),
    InflectorRule("(ottoman|stud|bow|wall|book|holder|curtain)(s)?$", "$1s")
  )


  /**
   * Returns the singular form of the word in the string.
   *
   * @param word the word that is to be pluralized - this is required to be correctly trimmed, etc., prior to the call here.
   * @return the singularized form of the word, or the word itself if it could not be singularized
   */
  def singularize(word: String): String = {
    if (word.isEmpty) word
    else if (Uncountables.contains(word)) word
    else {
      // mutable/procedural approach here as this is called deep within inner loops by applications...
      var result: String = word
      SingularizeRules.exists { rule =>
        val finder = rule.finder(word)
        val found = finder.find
        if (found) result = rule.usafeApply(finder)
        found
      }
      result
    }
  }

  /**
   * Perform the opposite of singularize - i.e. create the plural form of a given word.
   * @param word
   * @return
   */
  def pluralize(word: String): String = {
    if (word.isEmpty) word
    else if (Uncountables.contains(word)) word
    else {
      // mutable/procedural approach here as this is called deep within inner loops by application...
      var result: String = word
      PluralizeRules.exists { rule =>
        val finder = rule.finder(word)
        val found = finder.find
        if (found) result = rule.usafeApply(finder)
        found
      }
      result
    }
  }
}
