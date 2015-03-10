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


## License

Copyright 2015 Gilt Groupe, Inc.

Licensed under the MIT License. 