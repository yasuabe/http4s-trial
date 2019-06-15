package trial.tapir.swagger

import cats.syntax.functor._
import cats.syntax.either._
import cats.syntax.semigroupk._
import cats.effect.{ExitCode, IO, IOApp, Resource, SyncIO}
import org.http4s.HttpRoutes
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import tapir._
import tapir.openapi.OpenAPI
import tapir.server.http4s._

import scala.concurrent.ExecutionContext

object Main extends IOApp.WithContext {

  val hiEP: Endpoint[String, Unit, String, Nothing] =
    endpoint.get
    .in("hi" / path[String]("name"))
    .out(stringBody)

  val byeEP: Endpoint[String, Unit, String, Nothing] =
    endpoint.get
    .in("bye" / path[String]("name"))
    .out(stringBody)

  import tapir.docs.openapi._
  val api: OpenAPI = List(hiEP, byeEP).toOpenAPI("The Tapir Library", "1.0")

  val mainService: HttpRoutes[IO] = {
    hiEP.toRoutes(name => IO(s"Hi, $name".asRight[Unit])) combineK
    byeEP.toRoutes(name => IO(s"Bye, $name".asRight[Unit]))
  }
  protected def executionContextResource: Resource[SyncIO, ExecutionContext] =
    Resource.pure(ExecutionContext.Implicits.global)

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(mainService combineK SwaggerUI[IO](api) orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}

