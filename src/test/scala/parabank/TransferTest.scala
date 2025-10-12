package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import parabank.Data._

class TransferTest extends Simulation {

  // 1️⃣ Configuración HTTP
  val httpConf = http
    .baseUrl(url)
    .acceptHeader("application/json")
    .check(status.in(200, 201))

  // 2️ Feeder: usamos el transfer_feeder.csv con cuentas válidas
  val feeder = csv("transfer_feeder.csv").circular

  // 3️ Escenario: realizar transferencias usando cuentas válidas
  val scn = scenario("Transferencias simultaneas válidas")
    .feed(feeder)
    .exec(
      http("Transferencia")
        .post("/transfer")
        .queryParam("fromAccountId", "${fromAccountId}")
        .queryParam("toAccountId", "${toAccountId}")
        .queryParam("amount", "${amount}")
        .check(status.is(200))
    )

  // 4️ Configuración de inyección (150 transacciones/segundo durante 60s)
  setUp(
    scn.inject(
      constantUsersPerSec(150).during(60.seconds).randomized
    )
  ).protocols(httpConf)
    .assertions(
      global.requestsPerSec.gte(150),            // al menos 150 req/s
      global.successfulRequests.percent.gte(95), // >= 95% éxito
      global.failedRequests.percent.lte(5.0)     // <= 5% fallos
    )
}
