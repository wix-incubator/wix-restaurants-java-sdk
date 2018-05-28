package com.wix.restaurants.authorization

import akka.actor.ActorSystem
import akka.http.javadsl.model.headers.Authorization
import akka.http.scaladsl.client.RequestBuilding.Get
import com.openrest.v1_1.Organization
import com.wix.rest.rfc7807.client.AkkaRestClient
import com.wix.restaurants.ExceptionTranslator

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

class DefaultAuthorizationClient(apiUrl: String = "https://api.wixrestaurants.com/v2") extends AuthorizationClient {
  private implicit val system: ActorSystem = ActorSystem("akka-wix-restaurants-authorization-client-system")
  private implicit val executionContext: ExecutionContext = system.dispatcher
  private val client: AkkaRestClient = new AkkaRestClient(errorResponseAsException = ExceptionTranslator.asException)


  def getRoles(accessToken: String): Future[Seq[Role]] = {
    val request = Get(s"$apiUrl/me/roles")
      .addHeader(Authorization.oauth2(accessToken))
    val response: Future[RolesResponse] = client.execute(request) withResult[RolesResponse]()
    response.map { _.roles.asScala }
  }

  override def hasAdminPermission(accessToken: String): Future[Boolean] = {
    getRoles(accessToken).map { _.exists { AuthorizationHelper.hasAdminPermission } }
  }

  override def hasManagerPermission(accessToken: String, organizationId: String): Future[Boolean] = {
    getRolesAndOrganization(accessToken, organizationId).map { case (roles, organization) =>
      hasManagerPermission(roles, organization)
    }
  }

  private def hasManagerPermission(roles: Seq[Role], organization: Organization): Boolean = {
    roles.exists { AuthorizationHelper.hasManagerPermission(organization, _) }
  }

  private def getRolesAndOrganization(accessToken: String, organizationId: String): Future[(Seq[Role], Organization)] = {
    val futureRoles = getRoles(accessToken)
    val futureOrganization = getOrganizaton(organizationId)

    futureRoles.flatMap { roles =>
      futureOrganization.map { organization =>
        (roles, organization)
      }
    }
  }

  private def getOrganizaton(organizationId: String): Future[Organization] = {
    val request = Get(s"$apiUrl/organizations/$organizationId")
    client.execute(request) withResult[Organization]()
  }
}
