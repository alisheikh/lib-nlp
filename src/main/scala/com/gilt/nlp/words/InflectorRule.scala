package com.gilt.nlp.words

import java.util.regex.{Matcher, Pattern}

case class InflectorRule(expression: String, replacement: String = "") {
  // named Finder to impress the use of "find" method, opposed to "matches" method.
  type InflectorFinder = Matcher

  private val ExpressionPattern = Pattern.compile(this.expression, Pattern.CASE_INSENSITIVE)

  def finder(input: String): InflectorFinder = ExpressionPattern.matcher(input)

  /**
   * Apply the rule defined for a given finder.
   *
   * This MUST only be called when the finder is shown to match (i.e. finder.find() == true)
   *
   * @param finder
   * @return
   */
  def usafeApply(finder: InflectorFinder): String = {
    finder.replaceAll(replacement)
  }

  override def hashCode: Int = expression.hashCode

  override def equals(obj: Any): Boolean = {
    if (obj == this) return true
    if (obj != null && obj.getClass == this.getClass) {
      val that: InflectorRule = obj.asInstanceOf[InflectorRule]
      if (this.expression.equalsIgnoreCase(that.expression)) true
      else false
    } else false
  }
}
