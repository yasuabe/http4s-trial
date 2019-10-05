package trial.tapir.swagger

import cats.Semigroup
import cats.data.NonEmptyList
import cats.syntax.functor._
import cats.syntax.either._
import cats.syntax.semigroupk._
import cats.effect.{Blocker, ExitCode, IO, IOApp}
import fs2.Stream
import org.http4s.HttpRoutes
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import tapir.EndpointInput.PathCapture
import tapir._
import tapir.openapi.OpenAPI
import tapir.server.http4s._
import tapir.docs.openapi._

object Main extends IOApp {
  val nameParam: PathCapture[String] =
    path[String]("name").description("名前").example("World")

  val greetEP: Endpoint[Unit, Unit, String, Nothing] =
    endpoint.get.out(stringBody.example("Hello, World!"))

  val helloEP: Endpoint[String, Unit, String, Nothing] =
    greetEP.in("hello" / nameParam)

  val hiEP: Endpoint[String, Unit, String, Nothing] =
    greetEP.in("hi" / nameParam)

  val byeEP: Endpoint[String, Unit, String, Nothing] =
    greetEP.in("bye" / nameParam)

  def hello(name: String): IO[Either[Unit, String]] = IO {
    s"Hello, $name!".asRight[Unit]
  }
  def hi(name: String): IO[Either[Unit, String]] = IO {
    s"Hi, $name!".asRight[Unit]
  }
  def goodBye(name: String): IO[Either[Unit, String]] = IO {
    s"Bye, $name!".asRight[Unit]
  }
  val api: OpenAPI = List(helloEP, hiEP, byeEP).toOpenAPI("http4s × tapir × Swagger", "1.0")

  implicit val routesSemigroup: Semigroup[HttpRoutes[IO]] = _ combineK _

  val greetingService: HttpRoutes[IO] = NonEmptyList.of(
    helloEP toRoutes hello,
    hiEP    toRoutes hi,
    byeEP   toRoutes goodBye
  ).reduce

  def run(args: List[String]): IO[ExitCode] =
    Stream.resource(Blocker[IO]).flatMap { bl: Blocker =>
      BlazeServerBuilder[IO]
        .bindHttp(8080, "localhost")
        .withHttpApp(greetingService combineK SwaggerUI[IO](api, bl) orNotFound)
        .serve
    } .compile
      .drain
      .as(ExitCode.Success)
}

