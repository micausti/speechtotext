package com.speechtotext

import cats.effect.{IO, Resource}
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding
import com.google.cloud.speech.v1.{
  RecognitionAudio,
  RecognitionConfig,
  SpeechClient
}

import scala.collection.JavaConverters._

case class GoogleWord(word: String, startSeconds: Int)

object SpeechToTextClient {

  private def clientResource: Resource[IO, SpeechClient] =
    Resource.make(IO(SpeechClient.create))(client => IO(client.shutdown()))

  def work(fileName: String): IO[List[GoogleWord]] = {

    clientResource.use { speechClient =>
      IO {
        val config = RecognitionConfig.newBuilder
          .setEncoding(AudioEncoding.LINEAR16)
          .setSampleRateHertz(44100)
          .setLanguageCode("en-US")
          .setEnableWordTimeOffsets(true)
          .build
        val audio = RecognitionAudio.newBuilder.setUri(fileName).build
        val response =
          speechClient.longRunningRecognizeAsync(config, audio).get()

        val results = response.getResultsList.asScala.flatMap { result =>
          val alternative = result.getAlternativesList.get(0)
          println("Transcription: %s%n", alternative.getWordsList.asScala)
          (alternative.getWordsList.asScala
            .map(wordInfo =>
              GoogleWord(wordInfo.getWord.replaceAll("\\p{Punct}", ""),
                         wordInfo.getStartTime.getSeconds.toInt))
            .toList)
        //TODO figure out how to print the updated list of good words with punctuation removed so can visually check for other anomalies
        }
        results.toList
      }
    }
  }
}
