package io.prediction.engines.stock
import com.github.nscala_time.time.Imports.DateTime

import io.prediction.core.BaseEngine
import io.prediction.core.AbstractEngine
import io.prediction.DefaultServer
import io.prediction.DefaultCleanser

import io.prediction.workflow.EvaluationWorkflow

object StockEngineFactory {
  def get(): AbstractEngine = {
    new BaseEngine(
      classOf[DefaultCleanser[TrainingData]],
      Map("random" -> classOf[RandomAlgorithm],
        "regression" -> classOf[RegressionAlgorithm]),
      classOf[StockServer])
  }
}


object Run {
  //val tickerList = Seq("GOOG", "AAPL", "FB", "GOOGL", "MSFT")
  val tickerList = Seq("GOOG", "AAPL", "AMZN", "MSFT", "IBM",
    "HPQ", "INTC", "NTAP", "CSCO", "ORCL",
    "XRX", "YHOO", "AMAT", "QCOM", "TXN",
    "CRM", "INTU", "WDC", "SNDK")

  def main(args: Array[String]) {
    val evalParams = new EvaluationParams(
      baseDate = new DateTime(2006, 1, 1, 0, 0),
      fromIdx = 600,
      untilIdx = 630,
      //untilIdx = 1200,
      trainingWindowSize = 600,
      evaluationInterval = 20,
      marketTicker = "SPY",
      tickerList = tickerList)

    val algoParams = new RandomAlgoParams(seed = 1, scale = 0.01)
    val serverParams = new StockServerParams(i = 1)

    val engine = StockEngineFactory.get


    val evaluatorClass = classOf[StockEvaluator]

    val algoParamsSet = Seq(
      ("random", new RandomAlgoParams(seed = 1, scale = 0.01)),
      ("random", new RandomAlgoParams(seed = 2, drift = 1)),
      ("regression", null))

    /*
    if (false) {
      // Pass engine directly
      PIORunner(evalParams, algoParamsSet(1), serverParams, 
        engine, evaluator, preparator)
      PIORunner(evalParams, algoParamsSet(2), serverParams,
        engine, evaluator, preparator)
    }
    */

    /*
    if (false) {
      // Pass engine via serialized object
      val engineFilename = "/home/yipjustin/tmp/engine.obj"
      PIORunner.writeEngine(engine, engineFilename)
      PIORunner(evalParams, algoParamsSet(2), stockServerParams,
        engineFilename, evaluator, preparator)
    }
    */

    if (true) {
      val evalWorkflow = EvaluationWorkflow(
        "", evalParams, 
        null /* cleanserParams */, algoParamsSet, serverParams, 
        engine, 
        evaluatorClass)

      println("Start singlethread runner") 
      evalWorkflow.run
    }
  }
}
