package com.gilt.nlp.util

import scala.annotation.tailrec

import scala.collection.parallel.immutable.ParMap

private[nlp] object CollectionUtils {

  implicit class SeqExtensions[A](val elems: Seq[A]) extends AnyVal {
    /**
     * Get tailing subsequences of a sequence of things. Similar to Seq.tails, but this allows for skipping parts, etc.
     */
    def tailingSkipSequences: Seq[Seq[A]] = {
      @tailrec
      def recur(remainingElems: Seq[A], acc: Set[Seq[A]]): Set[Seq[A]] = {
        if (remainingElems.isEmpty) acc
        else {
          val (headElem, elemTails) = (remainingElems.head, remainingElems.tail)
          val tails = (elemTails.inits.toSet ++ elemTails.tails).filter(_.nonEmpty)
          recur(remainingElems.tail, (acc + Seq(headElem)) ++ tails.map(b => headElem +: b))
        }
      }
      recur(elems, Set.empty).toSeq
    }

    /**
     * gets all slices (including single element slices, and full-length) from the sequence.
     * @return
     */
    def slices: Seq[Seq[A]] = {
      1.to(elems.size).flatMap(elems.sliding)
    }
  }
}
