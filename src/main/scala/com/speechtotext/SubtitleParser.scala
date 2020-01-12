package com.speechtotext

import java.io.InputStream

import cats.effect.IO
import fr.noop.subtitle.stl.StlParser

import scala.collection.JavaConverters._
import scala.collection.immutable

case class SentenceFromSubtitleParser(sentenceWords: List[String],
                                      startSeconds: Int,
                                      endSeconds: Int)

object SubtitleFileParser {
  def parseFile(input: InputStream): IO[List[SentenceFromSubtitleParser]] = IO {
    val stlParser = new StlParser()
    stlParser
      .parse(input)
      .getTtis
      .asScala
      .toList
      .map { stlTti =>
        val sentence: List[String] = stlTti.getTf
          .replaceAll("\\p{Punct}", "")
          .toList
          .map(char => if (char.isLetterOrDigit) char else " ")
          .mkString
          .split(" ")
          .filterNot(_.trim.isEmpty)
          .toList
        val startMinutes = stlTti.getTci.getMinute
        val startSeconds = stlTti.getTci.getSecond
        val endMinutes = stlTti.getTco.getMinute
        val endSeconds = stlTti.getTco.getSecond
        SentenceFromSubtitleParser(sentence,
                                   (startMinutes * 60) + startSeconds,
                                   (endMinutes * 60) + endSeconds)
      }
      .filterNot(sentence => sentence.sentenceWords.isEmpty)
      .filterNot(sentence => sentence.sentenceWords.contains("CHEERING"))
      .filterNot(sentence => sentence.sentenceWords.contains("APPLAUSE"))
      .filterNot(sentence => sentence.sentenceWords.contains("SQUEALING"))
      .filterNot(sentence => sentence.sentenceWords.contains("LAUGHTER"))
      .filterNot(sentence => sentence.sentenceWords.contains("Subtitles"))
  }
}
//TODO things to add in to improve accuracy
//reconcile US vs UK spelling ...also look into the GB language API - does it give UK or US spellings?
//see if it's possible to figure out 'music' from st file and remove OR be able to add this in from the google api
//there/their/theyre
//
