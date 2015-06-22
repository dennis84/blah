package blah.core

import com.datastax.driver.core.{Row, BoundStatement, ResultSet, ResultSetFuture}
import scala.concurrent.{CanAwait, Future, ExecutionContext}
import scala.util.{Success, Try}
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

private[core] trait CassandraResultSetOperations {
  private case class ExecutionContextExecutor(ec: ExecutionContext) extends java.util.concurrent.Executor {
    def execute(cmd: Runnable) = ec.execute(cmd)
  }

  protected class RichResultSetFuture(fut: ResultSetFuture) extends Future[ResultSet] {
    @throws(classOf[InterruptedException])
    @throws(classOf[scala.concurrent.TimeoutException])
    def ready(atMost: Duration)(implicit permit: CanAwait): this.type = {
      fut.get(atMost.toMillis, TimeUnit.MILLISECONDS)
      this
    }

    @throws(classOf[Exception])
    def result(atMost: Duration)(implicit permit: CanAwait): ResultSet = {
      fut.get(atMost.toMillis, TimeUnit.MILLISECONDS)
    }

    def onComplete[U](func: (Try[ResultSet]) => U)(implicit ec: ExecutionContext): Unit = {
      if (fut.isDone) {
        func(Success(fut.getUninterruptibly))
      } else {
        fut.addListener(new Runnable {
          def run() {
            func(Try(fut.get()))
          }
        }, ExecutionContextExecutor(ec))
      }
    }

    def isCompleted: Boolean = fut.isDone

    def value: Option[Try[ResultSet]] =
      if(fut.isDone) Some(Try(fut.get()))
      else None
  }

  implicit def toFuture(fut: ResultSetFuture): Future[ResultSet] =
    new RichResultSetFuture(fut)
}

trait Binder[-A] {
  def bind(value: A, boundStatement: BoundStatement): Unit
}

trait BoundStatementOperations {

  implicit class RichBoundStatement[A : Binder](boundStatement: BoundStatement) {
    val binder = implicitly[Binder[A]]

    def bindFrom(value: A): BoundStatement = {
      binder.bind(value, boundStatement)
      boundStatement
    }
  }
}

trait CassandraTweaks
  extends CassandraResultSetOperations
