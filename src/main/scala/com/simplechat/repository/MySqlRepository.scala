package com.simplechat.repository

import java.util.concurrent.Executors

import slick.jdbc.MySQLProfile.backend.Database

import scala.concurrent.ExecutionContext

trait MySqlRepository {

  object DatabaseExecutionContext {
    private val processors = Runtime.getRuntime.availableProcessors()
    val noOfThread: Int = processors * 2
  }

  val schema = "deebee"

  val db = Database.forConfig(schema)

  val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(DatabaseExecutionContext.noOfThread))

}
