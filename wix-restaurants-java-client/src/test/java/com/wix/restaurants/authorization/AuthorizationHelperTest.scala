package com.wix.restaurants.authorization

import com.openrest.v1_1.{Chain, Distributor, Restaurant}
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

class AuthorizationHelperTest extends SpecWithJUnit {
  private val restaurantId = "some-restaurant-id"
  private val otherRestaurantId = "some-other-restaurant-id"
  private val chainId = "some-chain-id"
  private val otherChainId = "some-other-chain-id"
  private val distributorId = "some-distributor-id"
  private val otherDistributorId = "some-other-distributor-id"

  private val restaurantManagerRole = new Role(restaurantId, Restaurant.TYPE, Roles.manager)
  private val restaurantEmployeeRole = new Role(restaurantId, Restaurant.TYPE, Roles.employee)
  private val otherRestaurantManagerRole = new Role(otherRestaurantId, Restaurant.TYPE, Roles.manager)
  private val chainManagerRole = new Role(chainId, Chain.TYPE, Roles.manager)
  private val chainEmployeeRole = new Role(chainId, Chain.TYPE, Roles.employee)
  private val otherChainManagerRole = new Role(otherChainId, Chain.TYPE, Roles.manager)
  private val distributorRole = new Role(distributorId, Distributor.TYPE, Roles.distributor)
  private val otherDistributorRole = new Role(otherDistributorId, Distributor.TYPE, Roles.distributor)
  private val adminRole = new Role(null, null, Roles.admin)


  trait Ctx extends Scope {}

  "hasManagerPermission [restaurant]" should {
    val restaurant = new Restaurant
    restaurant.id = restaurantId
    restaurant.chainId = chainId
    restaurant.distributorId = distributorId

    "return true for admins" in new Ctx {
      AuthorizationHelper.hasManagerPermission(restaurant, adminRole) must beTrue
    }

    "return true for distributors of the restaurant" in new Ctx {
      AuthorizationHelper.hasManagerPermission(restaurant, distributorRole) must beTrue
    }

    "return false for unrelated distributors" in new Ctx {
      AuthorizationHelper.hasManagerPermission(restaurant, otherDistributorRole) must beFalse
    }

    "return true for chain managers of the restaurant" in new Ctx {
      AuthorizationHelper.hasManagerPermission(restaurant, chainManagerRole) must beTrue
    }

    "return false for chain employees of the restaurant" in new Ctx {
      AuthorizationHelper.hasManagerPermission(restaurant, chainEmployeeRole) must beFalse
    }

    "return false for unrelated chain managers" in new Ctx {
      AuthorizationHelper.hasManagerPermission(restaurant, otherChainManagerRole) must beFalse
    }

    "return true for managers of the restaurant" in new Ctx {
      AuthorizationHelper.hasManagerPermission(restaurant, restaurantManagerRole) must beTrue
    }

    "return false for employees of the restaurant" in new Ctx {
      AuthorizationHelper.hasManagerPermission(restaurant, restaurantEmployeeRole) must beFalse
    }

    "return false for unrelated restaurant managers" in new Ctx {
      AuthorizationHelper.hasManagerPermission(restaurant, otherRestaurantManagerRole) must beFalse
    }
  }

  "hasManagerPermission [chain]" should {
    val chain = new Chain
    chain.id = chainId
    chain.distributorId = distributorId

    "return true for admins" in new Ctx {
      AuthorizationHelper.hasManagerPermission(chain, adminRole) must beTrue
    }

    "return true for distributors of the chain" in new Ctx {
      AuthorizationHelper.hasManagerPermission(chain, distributorRole) must beTrue
    }

    "return false for unrelated distributors" in new Ctx {
      AuthorizationHelper.hasManagerPermission(chain, otherDistributorRole) must beFalse
    }

    "return true for managers of the chain" in new Ctx {
      AuthorizationHelper.hasManagerPermission(chain, chainManagerRole) must beTrue
    }

    "return false for employees of the chain" in new Ctx {
      AuthorizationHelper.hasManagerPermission(chain, chainEmployeeRole) must beFalse
    }

    "return false for unrelated chain managers" in new Ctx {
      AuthorizationHelper.hasManagerPermission(chain, otherChainManagerRole) must beFalse
    }

    "return false for managers of a restaurant in the chain" in new Ctx {
      AuthorizationHelper.hasManagerPermission(chain, restaurantManagerRole) must beFalse
    }

    "return false for employees of a restaurant in the chain" in new Ctx {
      AuthorizationHelper.hasManagerPermission(chain, restaurantEmployeeRole) must beFalse
    }

    "return false for unrelated restaurant managers" in new Ctx {
      AuthorizationHelper.hasManagerPermission(chain, otherRestaurantManagerRole) must beFalse
    }
  }

  "hasManagerPermission [distributor]" should {
    val distributor = new Distributor
    distributor.id = distributorId

    "return true for admins" in new Ctx {
      AuthorizationHelper.hasManagerPermission(distributor, adminRole) must beTrue
    }

    "return true for distributors" in new Ctx {
      AuthorizationHelper.hasManagerPermission(distributor, distributorRole) must beTrue
    }

    "return false for unrelated distributors" in new Ctx {
      AuthorizationHelper.hasManagerPermission(distributor, otherDistributorRole) must beFalse
    }

    "return false for managers of a chain under the distributor" in new Ctx {
      AuthorizationHelper.hasManagerPermission(distributor, chainManagerRole) must beFalse
    }

    "return false for employees of a chain under the distributor" in new Ctx {
      AuthorizationHelper.hasManagerPermission(distributor, chainEmployeeRole) must beFalse
    }

    "return false for unrelated chain managers" in new Ctx {
      AuthorizationHelper.hasManagerPermission(distributor, otherChainManagerRole) must beFalse
    }

    "return false for managers of a restaurant under the distributor" in new Ctx {
      AuthorizationHelper.hasManagerPermission(distributor, restaurantManagerRole) must beFalse
    }

    "return false for employees of a restaurant under the distributor" in new Ctx {
      AuthorizationHelper.hasManagerPermission(distributor, restaurantEmployeeRole) must beFalse
    }

    "return false for unrelated restaurant managers" in new Ctx {
      AuthorizationHelper.hasManagerPermission(distributor, otherRestaurantManagerRole) must beFalse
    }
  }

  "hasAdminPermission" should {
    "return true for admins" in new Ctx {
      AuthorizationHelper.hasAdminPermission(adminRole) must beTrue
    }

    "return false otherwise" in new Ctx {
      val nonAdminRole: Role = distributorRole // using distributor as arbitrary non-admin role
      AuthorizationHelper.hasAdminPermission(nonAdminRole) must beFalse
    }
  }
}
