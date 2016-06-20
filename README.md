# NEologdPictures

Tool to collect images from [MediaWiki API](https://www.mediawiki.org/wiki/API:Main_page) for MeCab dictionary

You will get too many images' URLs when you run for [NEologd](https://github.com/neologd/mecab-ipadic-neologd).

## Usage

1. Run `./dic/unique.sh` for the dictionary to remove duplicate words.
2. Run `@JvmStatic fun main(args: Array<String>)` with `./pom.xml` and the file created from `1.`.

## References

* [Maven &#x2013; Welcome to Apache Maven](https://maven.apache.org/)
* [Kotlin Programming Language](https://kotlinlang.org/)
* [MediaWiki API](https://www.mediawiki.org/wiki/API:Main_page)
* [NEologd](https://github.com/neologd/mecab-ipadic-neologd)

