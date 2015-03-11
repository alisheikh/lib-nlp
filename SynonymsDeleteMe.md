# Potential Synonym API Providers

## [wordsapi](https://www.wordsapi.com)

JSON API.

Free for 2500 daily request count, scaling licence costs (per month) for higher and higher daily counts ($10/month = 25K/day, $49/month = 250K/day, $89/month = 500K/month)

Needs signup/token - actually, needs credit card details even for free it looks like - as it goes to $0.004 per request over the free allocation... humm...

## [abbreviations.com](http://www.abbreviations.com/synonyms_api.php)

XML API

Free for 1000 daily. 

Needs signup/token

## [altavista.org](http://thesaurus.altervista.org/service)

XML and JSON API.

Appears to be rate limited, but no immediately obvious detail on allowed daily/frequency thoughput etc.

Needs signup/token

## [bighugelabs](http://words.bighugelabs.com/api.php)

XML and JSON API

Implies usage limits apply (there's a response code for it) but no details.

Looks to be fully free, needs signup for key though..

## [mashape webknox](https://www.mashape.com/webknox/words-1)

has a synonym endoint that returns JSON

**doesn't include PartOfSpeech...**

Needs a key...


## [wikisynonyms](https://www.mashape.com/ipeirotis/wikisynonyms)

JSON results

Needs a key.

**doesn't include PartOfSpeech...**

## [otherwords](http://otherwords.org)

This is not an API, but rather a lib wrapping openoffice extracted therasaurus, and other, files.

These files could be useful, if we port this lib. to Scala... (could be more work than its worth however).

## [Bing Synonyms API](https://datamarket.azure.com/dataset/bing/synonyms)

5000 transactions PER MONTH free.

JSON results possible.

Seems to be more than just word synonym, may do full phrase, but still, only 5K per month!!!

(also, some indications on comments on other sites that this is poor quality!)

## [Macmillan Dictionary](http://www.macmillandictionary.com/tools/aboutapi.html)

commercial use kicks in at > 10K calls PER MONTH - no details on costs, need to contact, and negotiate terms/conditions on a case by case basis!

No details on the form of the actual API....

## [Wordnet](http://wordnet.princeton.edu/wordnet/)

Not an API, but rather a downloadable set of database files, and tools around words - that are represented by synonymy relationships - may be well worth considering too...

there's a few [Java libraries/tools around this](http://wordnet.princeton.edu/wordnet/related-projects/#Java) that may be of use...

## [Wordreference](http://www.wordreference.com/docs/api.aspx)

No new users being accepted (due to abuse)
