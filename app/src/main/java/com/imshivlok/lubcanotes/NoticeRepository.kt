package com.imshivlok.lubcanotes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL

data class UniversityNotice(
    val title: String,
    val date: String,
    val link: String
)

object NoticeRepository {
    private const val LKO_UNI_URL = "https://www.lkouniv.ac.in/en/news?Newslistslug=en-notices&cd=MwAzADcA"

    suspend fun fetchLatestNotices(): List<UniversityNotice> = withContext(Dispatchers.IO) {
        try {
            val url = URL(LKO_UNI_URL)
            val connection = url.openConnection() as HttpURLConnection

            // Inject browser profile headers natively into the connection stream
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            // Idiomatic Kotlin reading method (Removes manual loops entirely)
            val htmlContent = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            // Parse unblocked source content text straight into Jsoup DOM tree
            val doc = Jsoup.parse(htmlContent, LKO_UNI_URL)
            val parsedNotices = mutableListOf<UniversityNotice>()

            // Scope targeting down directly to the core ASP content form row lines
            val rows = doc.select("form#form1 tr, .news_row")

            for (row in rows) {
                val anchor = row.select("a").first() ?: continue
                val rawLink = anchor.attr("abs:href")

                if (rawLink.isEmpty() || rawLink.contains("javascript:") || rawLink.contains("Newslistslug=")) {
                    continue
                }

                val titleText = anchor.text()
                    .replace(Regex("^\\d+\\.\\s*"), "")
                    .replace(Regex("pdf\\s*\\[.*\\]", RegexOption.IGNORE_CASE), "")
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
                    UniversityNotice(
                        title = titleText,
                        date = dateText,
                        link = rawLink
                    )
                )

                if (parsedNotices.size >= 5) break
            }

            // Secondary backup stream if the table structure renders blank
            if (parsedNotices.isEmpty()) {
                val emergencyAnchors = doc.select("a[href*=.pdf]")
                for (anchor in emergencyAnchors) {
                    val link = anchor.attr("abs:href")
                    val text = anchor.text().trim()

                    if (text.length > 8 && !parsedNotices.any { it.link == link }) {
                        parsedNotices.add(
                            UniversityNotice(
                                title = text.replace(Regex("^\\d+\\.\\s*"), ""),
                                date = "Recent",
                                link = link
                            )
                        )
                    }
                    if (parsedNotices.size >= 5) break
                }
            }

            parsedNotices

        } catch (e: Exception) {
            e.printStackTrace()
            listOf(
                UniversityNotice("Unable to synchronize with Lucknow University noticeboard.", "Offline", ""),
                UniversityNotice("Check your internet parameters and try again.", "Alert", "")
            )
        }
    }
}