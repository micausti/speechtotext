package com.speechtotext

object Main extends App {

  import com.google.cloud.speech.v1.RecognitionAudio
  import com.google.cloud.speech.v1.RecognitionConfig
  import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding
  import com.google.cloud.speech.v1.RecognizeResponse
  import com.google.cloud.speech.v1.SpeechClient
  import com.google.cloud.speech.v1.SpeechRecognitionAlternative
  import com.google.cloud.speech.v1.SpeechRecognitionResult
  import com.google.protobuf.ByteString
  import java.nio.file.Files
  import java.nio.file.Path
  import java.nio.file.Paths
  import java.util
  // Imports the Google Cloud client library

    /**
     * Demonstrates using the Speech API to transcribe an audio file.
     */

  case class Word(word:String, startSeconds: Int)

      try {
        val speechClient = SpeechClient.create
        try { // The path to the audio file to transcribe
          val fileName = getClass.getResource("/cake-wars-audio-sample_1.wav").getFile
          print(fileName)
          // Reads the audio file into memory
          val path = Paths.get(fileName)
          val data = Files.readAllBytes(path)
          val audioBytes = ByteString.copyFrom(data)
          // Builds the sync recognize request
          //val config = RecognitionConfig.newBuilder.setEncoding(AudioEncoding.LINEAR16).setSampleRateHertz(16000).setLanguageCode("en-US").build
          val config = RecognitionConfig.newBuilder.setLanguageCode("en-US").setEnableWordTimeOffsets(true).build
          val audio = RecognitionAudio.newBuilder.setContent(audioBytes).build
          // Performs speech recognition on the audio file
          val response = speechClient.recognize(config, audio)
          val results = response.getResultsList
          import scala.collection.JavaConversions._
          import scala.collection.JavaConverters._
          for (result <- results) { // There can be several alternative transcripts for a given chunk of speech. Just use the
            // first (most likely) one here.
            val alternative = result.getAlternativesList.get(0)
            //val transcript = alternative.getTranscript.toSeq
            //println(transcript.groupBy(identity).mapValues(_.size))
            System.out.printf("Transcription: %s%n", alternative.getWordsList)
            val wordList = alternative.getWordsList.asScala.map(w=> Word(w.getWord, w.getStartTime.getSeconds.toInt))
            print(wordList)


          }
        } finally if (speechClient != null) speechClient.close()
      }
    }


