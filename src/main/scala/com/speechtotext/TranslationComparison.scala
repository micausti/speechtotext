package com.speechtotext

import cats.effect.IO

object TranslationComparison {
  def compare(googleTranslation: List[GoogleWord], subTranslation: List[SentenceFromSubtitleParser], trimSample: Int): IO[Double] = IO {
    val results = subTranslation.filter(t => t.endSeconds < trimSample).flatMap{s =>
      val between = googleTranslation.filter(w => (s.startSeconds - 1 to s.endSeconds + 1).toList.contains(w.startSeconds))
      println("between", between)
      println("sentence", s)
      s.sentence.map(w=> between.exists(q => q.word.toLowerCase == w.toLowerCase))}
    println("results", results)
    val trues = results.count(identity)
    val total = results.size
    trues.toDouble / total.toDouble
  }

}

