# lib-nlp
A Simple Natural Language Processing Library for Scala.

## Word Inflector

The `Inflector` can be used to generate plural or singular forms for input words.

```scala
val word = "world"
val plural = Inflector.pluralize(word)
val singular = Inflector.singularize(plural)
```

## Noun Phrase Extraction

Given some input english text, the `Extractor` generates relevant Noun Phrases. This is done by processing the input text using the [Apache OpenNLP](https://opennlp.apache.org) Utilties.

```scala
val extractor = new OpenNlpExtractor()

val inputText = "This is a piece of text. There are several sentences. There may be something about a cotton turquoise dress"

extractor.extractInterestingPhrases(inputText).foreach { phrase =>
  println(phrase)
}
```

## Synonyms

This library provides a very simple synonym generator based on [WordNet](http://wordnet.princeton.edu). This requires that the appropriate [Wordnet Dictionary files](http://wordnetcode.princeton.edu/3.0/WNdb-3.0.tar.gz) be downloaded, and installed separately.

Synonyms are generated for a given word, when used as a particular Part of Speech.

```scala
val synonymProvider = new WordnetSynonymProvider(pathToWordnetDictionaryFiles)
val synonyms = synonymProvider.getSynonyms("pullover", PartOfSpeech.Noun)
```

Building on this generator, and in conjunction with Noun Phrase Extraction, a class is provided to generate Synonym Phrases

```scala
val synonymPhraseGenerator = new OpenNlpSynonymPhraseGenerator(synonymProvider)
synonymPhraseGenerator.generateSynonyms("cotton summer dress")
```

## License

Copyright 2015 Gilt Groupe, Inc.

Licensed under the MIT License.
