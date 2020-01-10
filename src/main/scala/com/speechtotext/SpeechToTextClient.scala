package com.speechtotext

import java.nio.file.{Files, Paths}

import cats.effect.{IO, Resource}
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding
import com.google.cloud.speech.v1.{RecognitionAudio, RecognitionConfig, SpeechClient}
import com.google.protobuf.ByteString
import com.speechtotext.Main.getClass

import scala.collection.JavaConverters._

case class GoogleWord(word:String, startSeconds: Int)

object SpeechToTextClient {

  private def clientResource = Resource.make(IO(SpeechClient.create))(client => IO(client.shutdown()))

  def work(fileName: String): IO[List[GoogleWord]] = {

    clientResource.use { speechClient =>
      IO {
//        val path = Paths.get(fileName)
//        val data = Files.readAllBytes(path)
//        val audioBytes = ByteString.copyFrom(data)
        // Builds the sync recognize request
        val config = RecognitionConfig.newBuilder.setEncoding(AudioEncoding.LINEAR16).setSampleRateHertz(44100).setLanguageCode("en-US").setEnableWordTimeOffsets(true).build
        //val config = RecognitionConfig.newBuilder.setLanguageCode("en-GB").setEnableWordTimeOffsets(true).build
        val audio = RecognitionAudio.newBuilder.setUri(fileName).build
        // Performs speech recognition on the audio file
        val response = speechClient.longRunningRecognizeAsync(config, audio).get()
        //val response = speechClient.recognize(config, audio)
        println("size", response.getResultsList.asScala.size)
        val results = response.getResultsList.asScala.flatMap { result =>
          val alternative = result.getAlternativesList.get(0)
          println("Transcription: %s%n", alternative.getWordsList.asScala)
          alternative.getWordsList.asScala.map(w => GoogleWord(w.getWord, w.getStartTime.getSeconds.toInt)).toList
        }
        results.toList
      }
    }
  }
}
