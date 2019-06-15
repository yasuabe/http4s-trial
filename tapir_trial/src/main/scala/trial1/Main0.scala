package trial1

import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.either._
import cats.syntax.functor._
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.{HttpApp, HttpRoutes}
import tapir._
import tapir.server.http4s._

object Main0 extends IOApp {

  val helloWorldServiceOrg: HttpApp[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "hello" / name =>
        Ok(s"Hello, $name.")
    }.orNotFound

  val helloWorldEP: Endpoint[String, Unit, String, Nothing] =
    endpoint.get.in("hello" / path[String]("name")).out(stringBody)

  def helloLogic(name: String): IO[Either[Unit, String]] = IO {
    s"Hello, $name.".asRight[Unit]
  }
  val helloWorldRoute: HttpRoutes[IO] =
    helloWorldEP toRoutes helloLogic

  val helloWorldService: HttpApp[IO] =
    helloWorldRoute orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(helloWorldService)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
