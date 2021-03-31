package com.wix.restaurants.authorization

import com.openrest.v1_1.Organization

import scala.concurrent.Future

trait AuthorizationClient {
  def getRoles(accessToken: String): Future[Seq[Role]]

  def hasAdminPermission(accessToken: String): Future[Boolean]

  def hasManagerPermission(accessToken: String, organizationId: String): Future[Boolean]
  def hasManagerPermission(accessToken: String, organization: Organization): Future[Boolean]
}
