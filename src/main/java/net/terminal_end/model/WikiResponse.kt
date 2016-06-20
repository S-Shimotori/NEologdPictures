package net.terminal_end.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

/**
 * Created by S-Shimotori on 6/16/16.
 */

class WikiResponse(val batchcomplete: String?, val `continue`: Continue?, val query: Query) {
    class Continue(val iistart: String, val `continue`: String) {
    }
    class Query(val pages: List<Page>) {
        class Page(val pageid: Int, val ns: Int, val title: String,
                   val contentmodel: String?, val pagelanguage: String?, val pagelanguagehtmlcode: String?, val pagelanguagedir: String?, val touched: String?, val lastrayid: Int?, val length: Int?,
                   val revisions: List<Revisions>?,
                   val imagerepository: String?, val imageinfo: List<ImageInfo>?) {
            class Revisions(val contentformat: String, val contentmodel: String, val body: String) {
                class Deserializer: JsonDeserializer<Revisions> {
                    override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): Revisions? {
                        if (p0 == null || p2 == null) {
                            return null
                        }
                        val jsonObject = p0 as JsonObject
                        val contentFormat = jsonObject.getAsJsonPrimitive("contentformat").asString
                        val contentModel = jsonObject.getAsJsonPrimitive("contentmodel").asString
                        val body = jsonObject.getAsJsonPrimitive("*").asString
                        return Revisions(contentFormat, contentModel, body)
                    }
                }
            }
            class ImageInfo(val url: String, val descriptionurl: String, val descriptionshorturl: String) {
            }
        }

        class Deserializer: JsonDeserializer<Query> {
            override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): Query? {
                if (p0 == null || p2 == null) {
                    return null
                }
                val jsonObject = p0 as JsonObject
                return try {
                    Query(jsonObject.getAsJsonObject("pages").entrySet().map {
                        p2.deserialize<Page>(it.value, Page::class.java)
                    })
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}