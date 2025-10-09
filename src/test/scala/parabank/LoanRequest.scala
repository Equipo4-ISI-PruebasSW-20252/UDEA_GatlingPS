package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import parabank.Data._
import scala.concurrent.duration._

class LoanRequest extends Simulation{
    
    val httpConf = http
    .baseUrl(url)
    .acceptHeader("application/json") // Expect JSON now
    .check(status.is(200))

    val scn = scenario("Solicitud de prestamo Loan")
    .exec(
      http("Request Loan")
        .post("/requestLoan")
        .queryParam("customerId", s"$customerId")
        .queryParam("amount", s"$loanAmount")
        .queryParam("downPayment", s"$loanDownPayment")
        .queryParam("fromAccountId", s"$fromAccountId")
        .check(status.is(200)) 
    )

    setUp(
    scn.inject(
      constantConcurrentUsers(150).during(5.seconds) // # users y duración de la concurrencia
    )
  )
    .protocols(httpConf)
    .assertions(
      global.responseTime.mean.lte(5000),
      global.successfulRequests.percent.gte(98)   //porcentaje
    )
}