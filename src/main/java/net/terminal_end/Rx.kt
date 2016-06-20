package net.terminal_end

import com.google.gson.GsonBuilder
import com.squareup.okhttp.*
import net.terminal_end.model.WikiResponse
import org.apache.http.client.utils.URIBuilder
import rx.Observable
import java.io.IOException

/**
 * Created by S-Shimotori on 6/16/16.
 */

fun createPageIdObservable(titles: List<String>): Observable<List<Int>> {
    return Observable.create {
        val uriBuilder = URIBuilder("https://ja.wikipedia.org/w/api.php")
        uriBuilder.addParameter("format", "json")
        uriBuilder.addParameter("action", "query")
        uriBuilder.addParameter("prop", "info")
        uriBuilder.addParameter("titles", titles.reduce { result, title -> result + "|" + title })
        val url = uriBuilder.build().toURL()

        try {
            var request = Request.Builder()
                    .url(url)
                    .get()
                    .build()
            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(p0: Request?, p1: IOException?) {
                    it.onError(p1)
                }

                override fun onResponse(p0: Response?) {
                    if (p0 == null) {
                        it.onError(NullPointerException())
                        return
                    }
                    val body = p0.body()
                    val string = body.string()
                    try {
                        val wikiResponse = GsonBuilder()
                                .registerTypeAdapter(WikiResponse.Query::class.java, WikiResponse.Query.Deserializer())
                                .registerTypeAdapter(WikiResponse.Query.Page.Revisions::class.java, WikiResponse.Query.Page.Revisions.Deserializer())
                                .create()
                                .fromJson(string, WikiResponse::class.java)
                        val pageIds = wikiResponse.query.pages.filter { it.pageid > 0 }.map { it.pageid }
                        if (pageIds.size > 0) {
                            try {
                                Thread.sleep(500)
                            } catch (e: InterruptedException) {
                            }
                            it.onNext(pageIds)
                        }
                    } catch (e: Exception) {
                    }
                    it.onCompleted()
                    body.close()
                }
            })
        } catch(e: Exception) {
            it.onError(e)
        }
    }
}

fun createRevisionsObservable(pageIds: List<Int>): Observable<List<WikiResponse.Query.Page>> {
    return Observable.create {
        val uriBuilder = URIBuilder("https://ja.wikipedia.org/w/api.php")
        uriBuilder.addParameter("format", "json")
        uriBuilder.addParameter("action", "query")
        uriBuilder.addParameter("prop", "revisions")
        uriBuilder.addParameter("pageids", pageIds.map { it.toString() }.reduce { result, pageId -> result + "|" + pageId })
        uriBuilder.addParameter("rvprop", "content")
        val url = uriBuilder.build().toURL()

        try {
            var request = Request.Builder()
                    .url(url)
                    .get()
                    .build()
            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(p0: Request?, p1: IOException?) {
                    it.onError(p1)
                }

                override fun onResponse(p0: Response?) {
                    if (p0 == null) {
                        it.onError(NullPointerException())
                        return
                    }
                    val body = p0.body()
                    val string = body.string()
                    try {
                        val wikiResponse = GsonBuilder()
                                .registerTypeAdapter(WikiResponse.Query::class.java, WikiResponse.Query.Deserializer())
                                .registerTypeAdapter(WikiResponse.Query.Page.Revisions::class.java, WikiResponse.Query.Page.Revisions.Deserializer())
                                .create()
                                .fromJson(string, WikiResponse::class.java)
                        val pages = wikiResponse.query.pages.filter { it.pageid > 0 }
                        if (pages.size > 0) {
                            it.onNext(pages)
                        }
                    } catch (e: Exception) {
                    }
                    it.onCompleted()
                    body.close()
                }
            })
        } catch(e: Exception) {
            it.onError(e)
        }
    }
}

fun createImageInfoObservable(page: WikiResponse.Query.Page, imageTitle: String): Observable<Pair<String, String>>{
    return Observable.create {
        val uriBuilder = URIBuilder("https://commons.wikimedia.org/w/api.php")
        uriBuilder.addParameter("format", "json")
        uriBuilder.addParameter("action", "query")
        uriBuilder.addParameter("titles", imageTitle)
        uriBuilder.addParameter("prop", "imageinfo")
        uriBuilder.addParameter("iiprop", "url")
        val url = uriBuilder.build().toURL()

        try {
            var request = Request.Builder()
                    .url(url)
                    .get()
                    .build()
            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(p0: Request?, p1: IOException?) {
                    it.onError(p1)
                }

                override fun onResponse(p0: Response?) {
                    if (p0 == null) {
                        it.onError(NullPointerException())
                        return
                    }
                    val body = p0.body()
                    val string = body.string()
                    try {
                        val wikiResponse = GsonBuilder()
                                .registerTypeAdapter(WikiResponse.Query::class.java, WikiResponse.Query.Deserializer())
                                .registerTypeAdapter(WikiResponse.Query.Page.Revisions::class.java, WikiResponse.Query.Page.Revisions.Deserializer())
                                .create()
                                .fromJson(string, WikiResponse::class.java)
                        val pages = wikiResponse.query.pages
                        if (pages.size > 0 && pages[0].imageinfo != null && pages[0].imageinfo!!.size > 0) {
                            it.onNext(Pair(page.title, pages[0].imageinfo!![0].url))
                        }
                    } catch (e: Exception) {
                    }
                    it.onCompleted()
                    body.close()
                }
            })
        } catch (e: Exception) {
            it.onError(e)
        }
    }
}
