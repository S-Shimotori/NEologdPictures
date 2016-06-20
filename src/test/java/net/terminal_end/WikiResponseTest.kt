package net.terminal_end

import com.google.gson.GsonBuilder
import junit.framework.Test
import junit.framework.TestCase
import junit.framework.TestSuite
import net.terminal_end.model.WikiResponse

/**
 * Created by S-Shimotori on 6/17/16.
 */

class WikiResponseTest(testName: String): TestCase(testName) {
    companion object {
        fun suite(): Test {
            return TestSuite(AppTest::class.java)
        }
    }

    fun testApp() {
        val string = """
        |{
        |   "batchcomplete":"",
        |   "query": {
        |       "pages": {
        |           "1279": {
        |               "pageid": 1279,
        |               "ns": 0,
        |               "title": "Java",
        |               "contentmodel": "wikitext",
        |               "pagelanguage": "ja",
        |               "pagelanguagehtmlcode": "ja",
        |               "pagelanguagedir": "ltr",
        |               "touched": "2016-06-12T03:55:18Z",
        |               "lastrevid": 59415404,
        |               "length": 166738
        |           }
        |       }
        |   }
        |}
        """.trimMargin()
        val wikiResponse = GsonBuilder()
                .registerTypeAdapter(WikiResponse.Query::class.java, WikiResponse.Query.Deserializer())
                .registerTypeAdapter(WikiResponse.Query.Page.Revisions::class.java, WikiResponse.Query.Page.Revisions.Deserializer())
                .create()
                .fromJson(string, WikiResponse::class.java)
        assertTrue(wikiResponse.batchcomplete == "")
        assertTrue(wikiResponse.query.pages[0].pageid == 1279)
    }
}