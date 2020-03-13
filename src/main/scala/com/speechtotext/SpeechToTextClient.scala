package com.speechtotext

import java.util.Calendar

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
        val startClock: Long = Calendar.getInstance().toInstant.toEpochMilli
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
          println(s"Transcription: %s%n ${alternative.getWordsList.asScala}")
          val endClock: Long = Calendar.getInstance().toInstant.toEpochMilli
          println(s"Run Time ${endClock.-(startClock)}")
          (alternative.getWordsList.asScala
            .map(wordInfo =>
              GoogleWord(wordInfo.getWord.replaceAll("\\p{Punct}", ""),
                         wordInfo.getStartTime.getSeconds.toInt))
            .toList)

        }
        results.toList

      }
    }
  }
}
