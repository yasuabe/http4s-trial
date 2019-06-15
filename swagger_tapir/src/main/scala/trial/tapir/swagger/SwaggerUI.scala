package trial.tapir.swagger

import cats.Monad
import cats.syntax.option._
import cats.effect.{ContextShift, Sync}
import org.http4s.{HttpRoutes, StaticFile, _}
import org.http4s.dsl.Http4sDsl
import org.webjars.WebJarAssetLocator
import tapir.openapi.OpenAPI
import tapir.openapi.circe.yaml._
import scala.concurrent.ExecutionContext

class SwaggerUI[F[_]: Monad: Sync](
  openApi: OpenAPI, ec: ExecutionContext
)(implicit cs: ContextShift[F]) extends Http4sDsl[F] {
  private val prefix = "swagger-ui"
  private val path   = Path(prefix)

  private val resources =
    Option(new WebJarAssetLocator().getWebJars.get(prefix))
    .fold { throw new RuntimeException(s"Could not detect swagger-ui webjar version") }
          { v => s"/META-INF/resources/webjars/swagger-ui/$v/" }

  private def static(name: String, req: Request[F]) =
    StaticFile.fromResource(name, ec, req.some) getOrElseF NotFound()

  val service: HttpRoutes[F] = org.http4s.HttpRoutes.of[F] {
    case _   @ GET -> Path("swagger.yaml")  => Ok(openApi.toYaml)
    case req @ GET -> `path` / "index.html" => static(s"/$prefix/index.html", req)
    case req @ GET -> `path` / file         => static(resources + file,       req)
  }
}
object SwaggerUI {
  def apply[F[_]: Sync](
    openApi: OpenAPI,
    ec:      ExecutionContext = ExecutionContext.global
  )(implicit cs: ContextShift[F]): HttpRoutes[F] = new SwaggerUI[F](openApi, ec).service
}
