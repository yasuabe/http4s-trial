package trial1

import cats.data.Kleisli
import cats.effect._
import cats.syntax.either._
import cats.syntax.functor._
import cats.syntax.semigroupk._
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import tapir._
import tapir.server.http4s._

object Main1 extends IOApp {

  val helloEP: Endpoint[String, Unit, String, Nothing] =
    endpoint.get
      .in("hello" / path[String]("name"))
      .out(stringBody)

  val hiEP: Endpoint[String, Unit, String, Nothing] =
    endpoint.get
      .in("hi" / path[String]("name"))
      .out(stringBody)

  val byeEP: Endpoint[String, Unit, String, Nothing] =
    endpoint.get
      .in("good-bye" / path[String]("name"))
      .out(stringBody)

  def hello(name: String): IO[Either[Unit, String]] = IO {
    s"Hello, $name".asRight[Unit]
  }
  def hi(name: String): IO[Either[Unit, String]] = IO {
    s"Hi, $name".asRight[Unit]
  }
  def goodBye(name: String): IO[Either[Unit, String]] = IO {
    s"Good-bye, $name".asRight[Unit]
  }
  val helloRoute:   HttpRoutes[IO] = helloEP toRoutes hello
  val hiRoute:      HttpRoutes[IO] = hiEP    toRoutes hi
  val goodByeRoute: HttpRoutes[IO] = byeEP   toRoutes goodBye

  val greetingService: Kleisli[IO, Request[IO], Response[IO]] =
    helloRoute   combineK
    hiRoute      combineK
    goodByeRoute orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(greetingService)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}