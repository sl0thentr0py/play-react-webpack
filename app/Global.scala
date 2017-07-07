import java.util.concurrent.Executors

import org.slf4j.LoggerFactory
import play.api.Application
import play.api.mvc.{Result, RequestHeader, Filter, WithFilters}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, ExecutionContext}

object Global extends WithFilters(LoggingFilter) {

  override def onStart(app: Application)
  {
    super.onStart(app)
  }

  override def onStop(app: Application): Unit =  {
    super.onStop(app)
  }

}

object LoggingFilter extends Filter {

  lazy private val logger = LoggerFactory.getLogger("access")

  // 4 threads for logging
  lazy private val loggingExecutionContext = new ExecutionContext {
    val threadPool = Executors.newFixedThreadPool(4)
    def execute(runnable: Runnable) { threadPool.submit(runnable) }
    def reportFailure(t: Throwable) {}
  }

  def apply(nextFilter: (RequestHeader) => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    if (requestHeader.uri.startsWith("/assets") ||
      requestHeader.uri.startsWith("/versioned") ||
      requestHeader.uri.startsWith("/content")) {
      nextFilter(requestHeader)
    } else {
      val startTime = System.currentTimeMillis
      nextFilter(requestHeader).map { result =>
        val endTime = System.currentTimeMillis
        val requestTime = endTime - startTime
        //val ipAddress = requestHeader.remoteAddress.split(", ").head
        val ipAddress = requestHeader.headers.get("X-Real-IP")

        // logger waits for cloud logging response. run in separate thread pool
        Future {
          logger.info(s"${requestHeader.method} ${requestHeader.uri} - ${ipAddress} - ${requestTime}ms - status ${result.header.status}")
        }(loggingExecutionContext)

        result
      }
    }

  }
}
