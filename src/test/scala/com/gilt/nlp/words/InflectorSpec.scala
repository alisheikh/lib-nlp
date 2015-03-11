package com.gilt.nlp.words

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class InflectorTest extends FlatSpec with Matchers {
  "Inflector" should "correctly singularise 'blue dresses' and 'black shoes'" in {
    Inflector.singularize("blue dresses") should be ("blue dress")
    Inflector.singularize("black shoes") should be ("black shoe")
  }

  it should "correctly not change a non-plural 'blue dress'" in {
    Inflector.singularize("blue dress") should be ("blue dress")
  }

  it should "correctly not change 'yellow fish'" in {
    Inflector.singularize("yellow fish") should be ("yellow fish")
  }

  it should "correctly change some specifical plurals examples..." in {
    Inflector.singularize("lenses") should be ("lens")
    Inflector.singularize("sleeves") should be ("sleeve")
    Inflector.singularize("ties") should be ("tie")
  }

  val SingularToPlural =
    Map(
      "table" -> "tables",
      "blanket" -> "blankets",
      "criterion" -> "criteria",
      "lamp" -> "lamps",
      "flat" -> "flats",
      "rug" -> "rugs",
      "band" -> "bands",
      "tankini" -> "tankinis",
      "bikini" -> "bikinis",
      "necklace" -> "necklaces",
      "pearl" -> "pearls",
      "mini" -> "minis",
      "pan" -> "pans",
      "fireplace" -> "fireplaces",
      "leg" -> "legs",
      "sofa" -> "sofas",
      "turtleneck" -> "turtlenecks",
      "sweater" -> "sweaters",
      "pump" -> "pumps",
      "cabinet" -> "cabinets",
      "toe" -> "toes",
      "peep-toe" -> "peep-toes",
      "jane" -> "janes",
      "camisole" -> "camisoles",
      "neck" -> "necks",
      "v-neck" -> "v-necks",
      "waist" -> "waists",
      "dress" -> "dresses",
      "boot" -> "boots",
      "tie" -> "ties",
      "hive" -> "hives",
      "blouse" -> "blouses",
      "mouse" -> "mice",
      "safe" -> "safes",
      "polo" -> "polos",
      "cable" -> "cables",
      "headboard" -> "headboards",
      "bookcase" -> "bookcases",
      "neckline" -> "necklines",
      "vest" -> "vests",
      "slip" -> "slips",
      "solitaire" -> "solitaires",
      "chronograph" -> "chronographs",
      "shift" -> "shifts",
      "henley" -> "henleys",
      "gemstone" -> "gemstones",
      "center" -> "centers",
      "base" -> "bases",
      "ceiling" -> "ceilings",
      "peplum" -> "peplums",
      "sheath" -> "sheaths",
      "jersey" -> "jerseys",
      "zip" -> "zips",
      "suit" -> "suits",
      "halter" -> "halters",
      "round" -> "rounds",
      "platform" -> "platforms",
      "sole" -> "soles",
      "emerald" -> "emeralds",
      "waistband" -> "waistbands",
      "coat" -> "coats",
      "oval" -> "ovals",
      "disc" -> "discs",
      "coat" -> "coats",
      "sportcoat" -> "sportcoats",
      "waistband" -> "waistbands",
      "dome" -> "domes",
      "heart" -> "hearts",
      "ankle" -> "ankles",
      "jacquard" -> "jacquards",
      "teardrop" -> "teardrops",
      "lace-up" -> "lace-ups",
      "multi" -> "multis",
      "a-line" -> "a-lines",
      "loop" -> "loops",
      "wrap" -> "wraps",
      "drop" -> "drops",
      "ring" -> "rings",
      "trouser" -> "trousers",
      "hem" -> "hems",
      "clock" -> "clocks",
      "shoulder" -> "shoulders",
      "belt" -> "belts",
      "bag" -> "bags",
      "sharpener" -> "sharpeners",
      "ottoman" -> "ottomans",
      "wall" -> "walls",
      "stud" -> "studs",
      "book" -> "books",
      "holder" -> "holders",
      "curtain" -> "curtains",
      "octopus" -> "octopuses"
    )

  it should "correctly create the plural of several examples..." in {
    SingularToPlural foreach {
      case (singular, plural) => Inflector.pluralize(singular) should be (plural)
    }
  }

  it should "correctly create the singular of several examples..." in {
    SingularToPlural foreach {
      case (singular, plural) => Inflector.singularize(plural) should be (singular)
    }
  }

  it should "leave already plural words as plural when pluralising" in {
    Seq("dresses", "oxen", "agate", "lace", "matrices", "sleeves", "hives", "boots", "aliases", "viri", "shoes", "buses", "ties").foreach {
      word =>
        Inflector.pluralize(word) should be (word)
    }
  }

  it should "explicitly not change (either plural or singular) certain terms" in {
    Seq(
      "ipod touch", "chaise lounge", "coffee", "style", "satin", "wood", "cabochon",
      "denim", "cotton", "linen", "zirconia", "chiffon", "ballet", "turquoise",
      "agate", "chalcedony", "floor", "leather", "canvas", "garnet", "aluminum",
      "lace", "baby", "tourmaline", "quartz", "gold", "enamel", "vermeil", "construction", "date", "skin", "seating",
      "twill", "rhodium", "silver", "sterling", "swarovski", "steel", "citrine", "cowhide", "cut", "seating", "tweed", "stretch",
      "tweed", "pile", "mother of pearl", "pave", "jewelry").foreach {
      word =>
        Inflector.pluralize(word) should be (word)
    }
  }

  it should "correctly pluralise a full ngram phrase" in {
    Inflector.pluralize("black dress") should be ("black dresses")
  }
}
