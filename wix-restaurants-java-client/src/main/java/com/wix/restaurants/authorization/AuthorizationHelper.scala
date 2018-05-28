package com.wix.restaurants.authorization

import com.openrest.v1_1.{Chain, Distributor, Organization, Restaurant}

object AuthorizationHelper {
  private val adminRole = new Role(null, null, Roles.admin)

  def hasManagerPermission(organization: Organization, role: Role): Boolean = {
    hasAdminPermission(role) || (organization match {
      case restaurant: Restaurant =>
        role.organizationType == Distributor.TYPE && role.role == Roles.distributor && role.organizationId == restaurant.distributorId ||
          role.organizationType == Chain.TYPE && role.role == Roles.manager && role.organizationId == restaurant.chainId ||
          role.organizationType == Restaurant.TYPE && role.role == Roles.manager && role.organizationId == restaurant.id

      case chain: Chain =>
        role.organizationType == Distributor.TYPE && role.role == Roles.distributor && role.organizationId == chain.distributorId ||
          role.organizationType == Chain.TYPE && role.role == Roles.manager && role.organizationId == chain.id

      case distributor: Distributor =>
        role.organizationType == Distributor.TYPE && role.role == Roles.distributor && role.organizationId == distributor.id

      case _ =>
        false // Fail closed
    })
  }

  def hasAdminPermission(role: Role): Boolean = {
    role == adminRole
  }
}
