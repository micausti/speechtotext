package com.speechtotext

import cats.effect.IO

object TranslationComparison {
  def compare(googleTranslation: List[GoogleWord],
              subTranslation: List[SentenceFromSubtitleParser],
              trimSample: Int): IO[Double] = IO {
    val results = subTranslation
      .filter(stSentence => stSentence.endSeconds < trimSample)
      .flatMap { stSentence =>
        val filteredGoogleWordList = googleTranslation.filter(
          googleWord =>
            (stSentence.startSeconds - 1 to stSentence.endSeconds + 1).toList
              .contains(googleWord.startSeconds))
        stSentence.sentenceWords.map(stWord =>
          filteredGoogleWordList.exists(googleWord =>
            googleWord.word.toLowerCase == stWord.toLowerCase))
      }
    val trues = results.count(identity)
    val total = results.size

    println("number of matches " + trues)
    println("total number of words checked" + total)
    trues.toDouble / total.toDouble
  }

}
