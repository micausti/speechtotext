package com.speechtotext

object Main extends App {

  /**
   * Demonstrates using the Speech API to transcribe an audio file.
   */

 //val audioFile = getClass.getResource("/2-4350-0001-001-sample.wav").getFile
  //val audioFile = "gs://speech-to-text-ellen/2-4350-0001-001-sample.wav"
  val audioFileIncorrect = "gs://speech-to-text-ellen/1-0694-1817-001-sample-2.wav"
  val subtitleFile = getClass.getResourceAsStream("/2-4350-0001-001.stl")



  println("Application Starting")

  val application = for {
    googleWords <- SpeechToTextClient.work(audioFileIncorrect)
    subtitleSentences <- SubtitleFileParser.parse(subtitleFile)
    _ = println("Subtitle sentences: " + subtitleSentences)
    result <- TranslationComparison.compare(googleWords, subtitleSentences, 60)
  } yield result

  println(application.unsafeRunSync())

}

