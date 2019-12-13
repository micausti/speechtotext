package com.speechtotext

import java.io.InputStream

import fr.noop.subtitle.stl.StlParser

import scala.collection.JavaConverters._

case class SentenceFromSubtitleParser(sentence: List[String], startSeconds:Int, endSeconds:Int )

class SubtitleFileParser {
  def parse(input: InputStream): List[SentenceFromSubtitleParser]= {
  val stlParser = new StlParser()
//  stlParser.parse(inputStream).getTtis.asScala.toList.foreach { stl =>
//    println(stl.getTci)
//    println(stl.getTco)
//    println(stl.getTf)

    stlParser.parse(input).getTtis.asScala.toList.map { w =>
      //val sentence = w.getTf.replaceAll("\\p{C}|\\s+|\\r$|\\\\t|\\\\n|\\\\r", "").map(_.trim.filter(_ >= ' ')).toList.map(i=>i.trim).filterNot(_.isEmpty)
        //.replaceAll("\\p{C}|\\s+|\\r$|\\\\t|\\\\n|\\\\r", "")
      val sentence = w.getTf.replaceAll("\\p{C}|\\s+|\\r$|\\\\t|\\\\n|\\\\r", " ").split(" ").toList.map(i=>i.trim).filterNot(_.isEmpty)
      val startSeconds = w.getTci.getSecond
      val endSeconds = w.getTco.getSecond
      SentenceFromSubtitleParser(sentence, startSeconds, endSeconds)
    }.toList
    }
}
