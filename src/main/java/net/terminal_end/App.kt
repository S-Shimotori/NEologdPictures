package net.terminal_end
import rx.Observable
import rx.lang.kotlin.onError
import java.io.File

/**
 * Hello world!

 */
object App {
    @JvmStatic fun main(args: Array<String>) {
        val n = 35
        val words = readWordsFromMeCabDic()
        val times = Math.ceil((words.size / n.toFloat()).toDouble()).toInt()
        val groups = (0 .. times - 1).map { words.slice(it * n .. Math.min((it + 1) * n - 1, words.size - 1)) }
        getDataFromWeb(groups)
    }

    fun readWordsFromMeCabDic(): List<String> {
        val file = File("dic/output1-1-1-1-1-1-1-1.csv")
        val words = file.readLines().map {
            it.split(",")[10]
        }
        return words
    }

    fun getDataFromWeb(groups: List<List<String>>) {
        Observable.from(groups)
        .concatMap {
            // search article
            createPageIdObservable(it).retry()
        }.onBackpressureBuffer(1000)
        .onError {
            it.printStackTrace()
        }.concatMap {
            // get article
            createRevisionsObservable(it)
        }.flatMap {
            Observable.from(it).map { it }
        }.map {
            // search image file
            val body = it.revisions!![0].body
            val fileRegex = Regex("""(?<=\[\[)(F|f)ile:.*?\.(jpg|png|gif)""")
            val matchResult = fileRegex.find(body)
            Pair(it, matchResult?.value)
        }.filter {
            // filter article with image
            it.second != null
        }.concatMap {
            // get image url
            createImageInfoObservable(it.first, it.second!!).retry()
        }.onError {
            it.printStackTrace()
        }
        .subscribe({
            // record word and image
            File("dic/result/errortest/hoge.csv").appendText(it.first + "\t" + it.second + "\n")
        }, {
            it.printStackTrace()
        }, {
            println("finish.")
        })
    }
}
