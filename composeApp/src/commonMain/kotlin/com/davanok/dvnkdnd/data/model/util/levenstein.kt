package com.davanok.dvnkdnd.data.model.util

fun levenshtein(a: String, b: String): Int {
    if (a == b || a.isEmpty() || b.isEmpty()) return 0

    val dp = Array(a.length + 1) { IntArray(b.length + 1) }

    for (i in 0..a.length) dp[i][0] = i
    for (j in 0..b.length) dp[0][j] = j

    for (i in 1..a.length) {
        for (j in 1..b.length) {
            val cost = if (a[i - 1] == b[j - 1]) 0 else 1
            dp[i][j] = minOf(
                dp[i - 1][j] + 1,     // delete
                dp[i][j - 1] + 1,     // insert
                dp[i - 1][j - 1] + cost // replace
            )
        }
    }

    return dp[a.length][b.length]
}

fun normalizedLevenshtein(a: String, b: String) =
    levenshtein(a.lowercase(), b.lowercase())

fun wordInTextLevenshtein(word: String, text: String): Int {
    val normalizedWord = word.lowercase()
    val normalizedText = text.lowercase()

    val words = normalizedText.split(Regex("\\W+")).filter { it.isNotBlank() }

    return words.minOfOrNull { levenshtein(it, normalizedWord) } ?: Int.MAX_VALUE
}
