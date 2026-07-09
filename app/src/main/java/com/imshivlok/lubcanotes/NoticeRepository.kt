package com.imshivlok.lubcanotes

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class UniversityNotice(
    val title: String,
    val date: String,
    val link: String,
    val size: String
)

object NoticeRepository {
    private const val LKO_UNI_URL = "https://www.lkouniv.ac.in/en/news?Newslistslug=en-notices&cd=MwAzADcA"
    private const val PREFS_NAME = "LUBCANotes_Prefs"
    private const val DOWNLOADED_LINKS_KEY = "downloaded_pdf_links"

    // 💾 Cache keys for notice elements persistence
    private const val CACHED_NOTICES_KEY = "cached_university_notices"

    suspend fun fetchLatestNotices(context: Context? = null): List<UniversityNotice> = withContext(Dispatchers.IO) {
        try {
            val url = URL(LKO_UNI_URL)
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val htmlContent = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val doc = Jsoup.parse(htmlContent, LKO_UNI_URL)
            val parsedNotices = mutableListOf<UniversityNotice>()
            val rows = doc.select("form#form1 tr, .news_row")

            for (row in rows) {
                val anchor = row.select("a").first() ?: continue
                val rawLink = anchor.attr("abs:href")

                if (rawLink.isEmpty() || rawLink.contains("javascript:") || rawLink.contains("Newslistslug=")) {
                    continue
                }

                val anchorText = anchor.text()
                val rowText = row.text()

                val sizeRegex = Regex("\\[([^\\]]*(?:KB|MB|kb|mb|Bytes|pdf|PDF)[^\\]]*)\\]")
                val matchResult = sizeRegex.find(anchorText) ?: sizeRegex.find(rowText)

                var fileSize = matchResult?.groupValues?.get(1)?.replace(Regex("pdf", RegexOption.IGNORE_CASE), "")?.trim() ?: ""
                if (fileSize.isEmpty() || fileSize.all { !it.isDigit() }) {
                    fileSize = ""
                }

                val titleText = anchorText
                    .replace(Regex("^\\d+\\.\\s*"), "")
                    .replace(Regex("pdf\\s*\\[.*$", RegexOption.IGNORE_CASE), "")
                    .trim()

                if (titleText.length <= 8 || titleText.contains("Search here", ignoreCase = true)) {
                    continue
                }

                if (parsedNotices.any { it.link == rawLink || it.title == titleText }) {
                    continue
                }

                var dateText = "Recent"
                val dateElement = row.select(".news_date, .date, span").first()
                if (dateElement != null && dateElement.text().isNotEmpty()) {
                    dateText = dateElement.text().trim()
                } else {
                    val cells = row.select("td")
                    if (cells.size >= 2) {
                        dateText = cells.last()?.text()?.trim() ?: "Recent"
                    }
                }

                parsedNotices.add(
                    UniversityNotice(titleText, dateText, rawLink, fileSize)
                )

                if (parsedNotices.size >= 5) break
            }

            if (parsedNotices.isEmpty()) {
                val emergencyAnchors = doc.select("a[href*=.pdf]")
                for (anchor in emergencyAnchors) {
                    val link = anchor.attr("abs:href")
                    val text = anchor.text().trim()

                    if (text.length > 8 && !parsedNotices.any { it.link == link }) {
                        val sizeMatch = Regex("\\[([^\\]]+)\\]").find(text)
                        val fileSize = sizeMatch?.groupValues?.get(1)?.replace(Regex("pdf", RegexOption.IGNORE_CASE), "")?.trim() ?: ""

                        parsedNotices.add(
                            UniversityNotice(
                                text.replace(Regex("^\\d+\\.\\s*"), "").replace(Regex("pdf\\s*\\[.*$", RegexOption.IGNORE_CASE), ""),
                                "Recent",
                                link,
                                if (fileSize.any { it.isDigit() }) fileSize else ""
                            )
                        )
                    }
                    if (parsedNotices.size >= 5) break
                }
            }

            // Save the live successful crawl list array right into shared memory cache
            if (context != null && parsedNotices.isNotEmpty()) {
                saveNoticesToCache(context, parsedNotices)
            }

            parsedNotices

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // 💾 Serializes the Notice structures into safe strings for disk caching
    fun saveNoticesToCache(context: Context, notices: List<UniversityNotice>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val serializedSet = notices.map { "${it.title}|||${it.date}|||${it.link}|||${it.size}" }.toSet()
        prefs.edit().putStringSet(CACHED_NOTICES_KEY, serializedSet).apply()
    }

    // 🔄 Instant Cache Retrieval Engine
    fun loadNoticesFromCache(context: Context): List<UniversityNotice> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedSet = prefs.getStringSet(CACHED_NOTICES_KEY, emptySet()) ?: emptySet()
        return savedSet.map { rawStr ->
            val tokens = rawStr.split("|||")
            UniversityNotice(
                title = tokens.getOrNull(0) ?: "Notice",
                date = tokens.getOrNull(1) ?: "Recent",
                link = tokens.getOrNull(2) ?: "",
                size = tokens.getOrNull(3) ?: ""
            )
        }
    }

    fun getTargetFolder(context: Context): File {
        val baseFolder = context.getExternalFilesDir(null) ?: context.filesDir
        val lubcaFolder = File(baseFolder, "LUBCANotes")
        if (!lubcaFolder.exists()) {
            lubcaFolder.mkdirs()
        }
        return lubcaFolder
    }

    fun getFileNameFromUrl(url: String): String {
        return url.substringAfterLast("/").substringBefore("?").ifEmpty { "Notice_${url.hashCode()}.pdf" }
    }

    suspend fun downloadPdfToFile(context: Context, urlStr: String): File? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val targetFile = File(getTargetFolder(context), getFileNameFromUrl(urlStr))
            val url = URL(urlStr)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.doInput = true

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        val buffer = ByteArray(4096)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                        }
                        output.flush()
                    }
                }
                targetFile
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            connection?.disconnect()
        }
    }

    fun saveDownloadedLinks(context: Context, links: List<String>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(DOWNLOADED_LINKS_KEY, links.toSet()).apply()
    }

    fun loadAndVerifyDownloadedLinks(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedLinks = prefs.getStringSet(DOWNLOADED_LINKS_KEY, emptySet()) ?: emptySet()

        val verifiedLinks = mutableListOf<String>()
        val lubcaFolder = getTargetFolder(context)

        for (link in savedLinks) {
            val localFile = File(lubcaFolder, getFileNameFromUrl(link))
            if (localFile.exists() && localFile.length() > 0) {
                verifiedLinks.add(link)
            }
        }

        if (verifiedLinks.size != savedLinks.size) {
            saveDownloadedLinks(context, verifiedLinks)
        }
        return verifiedLinks
    }
}