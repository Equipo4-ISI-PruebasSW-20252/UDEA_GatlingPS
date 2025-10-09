package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import parabank.Data._

class AccountStatusTest extends Simulation{

  // 1 Http Conf
  val httpConf = http.baseUrl(url)
    .acceptHeader("application/json")
    //Verificar de forma general para todas las solicitudes
    .check(status.is(200))

  val scn = scenario("Consultar transacciones con 200 usuarios simultáneos").
    exec(http("transactions")
      .get(s"/accounts/$accountId/transactions")
      .check(status.is(200))
    )

  setUp(
    scn.inject(atOnceUsers(200)).protocols(httpConf)
  ).assertions(
    global.responseTime.mean.lt(3001),
    global.successfulRequests.percent.gte(99)
  )

}

