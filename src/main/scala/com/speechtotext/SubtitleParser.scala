package com.speechtotext

import java.io.InputStream

import cats.effect.IO
import fr.noop.subtitle.stl.StlParser

import scala.collection.JavaConverters._

case class SentenceFromSubtitleParser(sentence: List[String], startSeconds: Int, endSeconds: Int)

object SubtitleFileParser {
  def parse(input: InputStream): IO[List[SentenceFromSubtitleParser]] = IO {
    val stlParser = new StlParser()
    stlParser.parse(input).getTtis.asScala.toList.map { w =>
      val sentence = w.getTf.replaceAll("\\p{Punct}", "").toList.map(c => if (c.isLetterOrDigit) c else " ").mkString.split(" ").filterNot(_.trim.isEmpty).toList
      val startMinutes = w.getTci.getMinute
      val startSeconds = w.getTci.getSecond
      val endMinutes = w.getTco.getMinute
      val endSeconds = w.getTco.getSecond
      SentenceFromSubtitleParser(sentence, (startMinutes * 60) + startSeconds,(endMinutes * 60) + endSeconds)
    }.filterNot(s => s.sentence.isEmpty)
  }
}
