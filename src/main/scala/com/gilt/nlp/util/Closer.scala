package com.gilt.nlp.util

import scala.language.reflectiveCalls

object Closer {
  def doWith[T <: { def close() }, R](toClose: T)(f: T => R): R = {
    try {
      f(toClose)
    } finally {
      toClose.close()
    }
  }
}



