package com.speechtotext

import cats.effect.IO

object Main extends App {

  //val audioFile = getClass.getResource("/2-4350-0001-001-sample.wav").getFile
  //*5 min Ellen sample
  val audioFile = "gs://speech-to-text-ellen/2-4350-0001-001-sample.wav"
  //*60 sec Corrie sample
  val audioFileIncorrect =
    "gs://speech-to-text-ellen/1-0694-1817-001-sample-2.wav"
  val subtitleFile = getClass.getResourceAsStream("/2-4350-0001-001.stl")

  val application: IO[Unit] = for {
    googleWords <- SpeechToTextClient.work(audioFile)
    subtitleSentences <- SubtitleFileParser.parseFile(subtitleFile)
    _ = println(s"Subtitle sentences: $subtitleSentences ")
    result <- TranslationComparison.compare(googleWords, subtitleSentences, 300)
  } yield result

  println(application.unsafeRunSync())

}
