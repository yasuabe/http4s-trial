package trial1

import cats.data.Kleisli
import cats.syntax.functor._
import cats.syntax.either._
import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.dsl.impl.Root
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.dsl.io._
import org.http4s.implicits._
import tapir._
import tapir.server.http4s._

object Main extends IOApp {

  val helloWorldService0 = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")
  }.orNotFound

  val helloWorldEP =
    endpoint.get.in("hello" / path[String]("name")).out(stringBody)

  def hello(name: String): IO[Either[Unit, String]] = IO {
    s"Hello, $name.".asRight[Unit]
  }
  val helloWorldService: Kleisli[IO, Request[IO], Response[IO]] =
    (helloWorldEP toRoutes hello) orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(helloWorldService)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
