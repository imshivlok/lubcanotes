package com.imshivlok.lubcanotes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

data class UniversityNotice(
    val title: String,
    val date: String,
    val link: String
)

object NoticeRepository {
    private const val LKO_UNI_URL = "https://www.lkouniv.ac.in/en/page/news-and-announcement"

    suspend fun fetchLatestNotices(): List<UniversityNotice> = withContext(Dispatchers.IO) {
        try {
            val doc = Jsoup.connect(LKO_UNI_URL)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .timeout(10000)
                .get()

            // Target the news list elements from Lucknow University's layout structure
            val noticeElements = doc.select(".news-list li, .announcement-list li, a[href*=.pdf]")

            noticeElements.take(5).map { element ->
                val anchor = element.select("a").first()
                val titleText = anchor?.text() ?: element.text()
                val rawLink = anchor?.attr("abs:href") ?: ""
                val dateText = element.select(".date, .time, span").first()?.text() ?: "Recent"

                UniversityNotice(
                    title = titleText.trim(),
                    date = dateText.trim(),
                    link = rawLink
                )
            }.filter { it.title.isNotEmpty() }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback content to keep layout unbroken if user is completely offline
            listOf(
                UniversityNotice("Unable to synchronize with Lucknow University noticeboard.", "Offline", ""),
                UniversityNotice("Check your internet network parameters and try again.", "Alert", "")
            )
        }
    }
}