package com.imshivlok.lubcanotes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
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

    suspend fun fetchLatestNotices(): List<UniversityNotice> = withContext(Dispatchers.IO) {
        try {
            val url = URL(LKO_UNI_URL)
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

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

                // 🎯 Bulletproof Size Extractor: Scan both anchor and parent row text for any brackets containing file metrics
                val sizeRegex = Regex("\\[([^\\]]*(?:KB|MB|kb|mb|Bytes|pdf|PDF)[^\\]]*)\\]")
                val matchResult = sizeRegex.find(anchorText) ?: sizeRegex.find(rowText)

                var fileSize = matchResult?.groupValues?.get(1)?.replace(Regex("pdf", RegexOption.IGNORE_CASE), "")?.trim() ?: ""
                if (fileSize.isEmpty() || fileSize.all { !it.isDigit() }) {
                    fileSize = "" // Clear it out if it doesn't contain actual data size numbers
                }

                // Clean the title description line fully
                val titleText = anchorText
                    .replace(Regex("^\\d+\\.\\s*"), "") // Strip serial numbers
                    .replace(Regex("pdf\\s*\\[.*$", RegexOption.IGNORE_CASE), "") // Strip trailing file size noise
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
                        link = rawLink,
                        size = fileSize
                    )
                )

                if (parsedNotices.size >= 5) break
            }

            // Fallback emergency scanner if the table matrix shifts layout values
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
                                title = text.replace(Regex("^\\d+\\.\\s*"), "").replace(Regex("pdf\\s*\\[.*$", RegexOption.IGNORE_CASE), ""),
                                date = "Recent",
                                link = link,
                                size = if (fileSize.any { it.isDigit() }) fileSize else ""
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
                UniversityNotice("Unable to synchronize with Lucknow University noticeboard.", "Offline", "", ""),
                UniversityNotice("Check your internet parameters and try again.", "Alert", "", "")
            )
        }
    }
}