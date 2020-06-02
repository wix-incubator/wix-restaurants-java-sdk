package com.wix.restaurants

import java.net.URLEncoder
import java.util.{Date, List => JList, Map => JMap}

import akka.actor.ActorSystem
import akka.http.javadsl.model.headers.Authorization
import akka.http.scaladsl.client.RequestBuilding.{Delete, Get, Post, Put}
import com.openrest.v1_1._
import com.wix.pay.smaug.client.model.CreditCardToken
import com.wix.rest.rfc7807.api.model.ErrorResponse
import com.wix.rest.rfc7807.client.AkkaRestClient
import com.wix.restaurants.authentication.model.{Namespaces, User => AuthenticationUser}
import com.wix.restaurants.authentication.{DefaultWixRestaurantsAuthenticationClient, WixRestaurantsAuthenticationClient}
import com.wix.restaurants.authorization.{AuthorizationClient, DefaultAuthorizationClient}
import com.wix.restaurants.exceptions._
import com.wix.restaurants.i18n.Locale
import com.wix.restaurants.json.Json
import com.wix.restaurants.orders.{Orders, Statuses => OrderStatuses}
import com.wix.restaurants.reservations.{Reservation, Reservations, Statuses => ReservationStatuses}

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

class DefaultWixRestaurantsClient(apiUrl: String = "https://api.wixrestaurants.com/v2",
                                  authApiUrl: String = "https://auth.wixrestaurants.com/v2",
                                  readTimeout: Duration = Duration.Inf) extends WixRestaurantsClient {

  private val authenticationClient: WixRestaurantsAuthenticationClient = new DefaultWixRestaurantsAuthenticationClient(
    authApiUrl, Some(readTimeout))
  private val authorizationClient: AuthorizationClient = new DefaultAuthorizationClient(
    apiUrl = apiUrl)

  private implicit val system: ActorSystem = ActorSystem("akka-wix-restaurants-client-system")
  private implicit val executionContext: ExecutionContext = system.dispatcher
  private val client: AkkaRestClient = new AkkaRestClient(errorResponseAsException = ExceptionTranslator.asException)


  override def getAuthenticationClient: WixRestaurantsAuthenticationClient = authenticationClient

  override def getAuthorizationClient: AuthorizationClient = authorizationClient

  override def retrieveRestaurantInfo(restaurantId: String): RestaurantFullInfo = {
    val request = Get(s"$apiUrl/organizations/$restaurantId/full")
    Await.result(client.execute(request) withResult[RestaurantFullInfo](), readTimeout)
  }

  override def retrieveOrganization(organizationId: String): Organization = {
    val request = Get(s"$apiUrl/organizations/$organizationId")
    Await.result(client.execute(request) withResult[Organization](), readTimeout)
  }

  override def setOrganization(accessToken: String, organization: Organization): Organization = {
    val request = Put(s"$apiUrl/organizations/${organization.id}", Json.stringify(organization))
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Organization](), readTimeout)
  }

  override def changeOrganizationLocale(accessToken: String, organizationId: String, locale: Locale): Unit = {
    val request = Post(s"$apiUrl/organizations/$organizationId/change_locale?locale=$locale")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withoutResult(), readTimeout)
  }

  override def getNotifications(accessToken: String , organizationId: String): Notifications = {
    val request = Get(s"$apiUrl/organizations/$organizationId/notifications")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Notifications](), readTimeout)
  }

  override def setNotifications(accessToken: String, organizationId: String, notifications: Notifications): Notifications = {
    val request = Put(s"$apiUrl/organizations/$organizationId/notifications", Json.stringify(notifications))
        .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Notifications](), readTimeout)
  }

  override def getSecrets(accessToken: String , organizationId: String): Secrets = {
    val request = Get(s"$apiUrl/organizations/$organizationId/secrets")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Secrets](), readTimeout)
  }

  override def setSecrets(accessToken: String, organizationId: String, secrets: Secrets): Secrets = {
    val request = Put(s"$apiUrl/organizations/$organizationId/secrets", Json.stringify(secrets))
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Secrets](), readTimeout)
  }

  override def getMenu(accessToken: String , restaurantId: String): Menu = {
    val request = Get(s"$apiUrl/organizations/$restaurantId/menu")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Menu](), readTimeout)
  }

  override def setMenu(accessToken: String, restaurantId: String, menu: Menu): Menu = {
    val request = Put(s"$apiUrl/organizations/$restaurantId/menu", Json.stringify(menu))
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Menu](), readTimeout)
  }

  override def submitOrder(accessToken: String, order: Order): Order = {
    val anonymousRequest = Post(s"$apiUrl/organizations/${order.restaurantId}/orders", Json.stringify(order))
    val request = Option(accessToken) match {
      case Some(at) => anonymousRequest.addHeader(Authorization.oauth2(at))
      case None => anonymousRequest
    }
    Await.result(client.execute(request) withResult[Order](), readTimeout)
  }

  override def retrieveOrderAsRestaurant(accessToken: String, restaurantId: String, orderId: String): Order = {
    val request = Get(s"$apiUrl/organizations/$restaurantId/orders/$orderId?viewMode=${Actors.restaurant}")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Order](), readTimeout)
  }

  override def retrieveOrderAsOwner(accessToken: String, restaurantId: String, orderId: String): Order = {
    val request = Get(s"$apiUrl/organizations/$restaurantId/orders/$orderId?viewMode=${Actors.customer}")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Order](), readTimeout)
  }

  override def retrieveNewOrders(accessToken: String, restaurantId: String): JList[Order] = {
    retrieveOrdersAsRestaurant(accessToken, restaurantId, OrderStatuses.new_, 10000)
  }

  override def retrieveOrdersAsRestaurant(accessToken: String, restaurantId: String, status: String, limit: Int): JList[Order] = {
    val statusPart = Option(status).map { theStatus => s"&status=$theStatus" }.getOrElse("")
    val request = Get(s"$apiUrl/organizations/$restaurantId/orders?viewMode=${Actors.restaurant}$statusPart&limit=$limit")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result[Orders](client.execute(request) withResult[Orders](), readTimeout).results
  }

  override def retrieveOrdersByPhone(accessToken: String, organizationId: String, phone: String, modifiedSince: Date, limit: Integer): JList[Order] = {
    retrieveUserOrders(accessToken, organizationId, new AuthenticationUser(Namespaces.phone, phone), modifiedSince, limit)
  }

  override def retrieveOrdersByEmail(accessToken: String, organizationId: String, email: String, modifiedSince: Date, limit: Integer): JList[Order] = {
    retrieveUserOrders(accessToken, organizationId, new AuthenticationUser(Namespaces.email, email), modifiedSince, limit)
  }

  private def retrieveUserOrders(accessToken: String, organizationId: String, user: AuthenticationUser, modifiedSince: Date, limit: Integer): JList[Order] = {
    val modifiedSinceTimestamp = Option(modifiedSince).map { _.getTime }.getOrElse(0L)
    val actualLimit = Option(limit).map { _.toInt }.getOrElse(1000000)
    val request = Get(s"$apiUrl/organizations/$organizationId/orders?viewMode=${Actors.restaurant}&user=${user.ns}:${URLEncoder.encode(user.id, "UTF-8")}&modified=gte:$modifiedSinceTimestamp&limit=$actualLimit")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result[Orders](client.execute(request) withResult[Orders](), readTimeout).results
  }

  override def acceptOrder(accessToken: String, restaurantId: String, orderId: String, externalIds: JMap[String, String]): Order = {
    val request = Post(s"$apiUrl/organizations/$restaurantId/orders/$orderId/accept?as=${Actors.restaurant}", Json.stringify(Comment(None)))
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Order](), readTimeout)
  }

  override def rejectOrder(accessToken: String, restaurantId: String, orderId: String, comment: String): Order = {
    val request = Post(s"$apiUrl/organizations/$restaurantId/orders/$orderId/cancel?as=${Actors.restaurant}", Json.stringify(Comment(Option(comment))))
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Order](), readTimeout)
  }

  override def setOrderProperties(accessToken: String, restaurantId: String, orderId: String, properties: JMap[String, String]): Order = {
    val request = Put(s"$apiUrl/organizations/$restaurantId/orders/$orderId/properties", Json.stringify(properties))
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Order](), readTimeout)
  }

  override def confirmOrderCashier(organizationId: String, orderId: String): Order = {
    val request = Post(s"$apiUrl/organizations/$organizationId/orders/$orderId/confirmCashier?as=${Actors.restaurant}")
    Await.result(client.execute(request) withResult[Order](), readTimeout)
  }

  override def submitReservation(accessToken: String, reservation: Reservation): Reservation = {
    val request = Post(s"$apiUrl/organizations/${reservation.restaurantId}/reservations", Json.stringify(reservation))
    Option(accessToken).foreach { theAccessToken => request.addHeader(Authorization.oauth2(theAccessToken)) }
    Await.result(client.execute(request) withResult[Reservation](), readTimeout)
  }

  override def retrieveReservationAsOwner(accessToken: String, restaurantId: String, reservationId: String): Reservation = {
    val request = Get(s"$apiUrl/organizations/$restaurantId/reservations/$reservationId?viewMode=${Actors.customer}")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Reservation](), readTimeout)
  }

  override def retrieveReservationAsRestaurant(accessToken: String, restaurantId: String, reservationId: String): Reservation = {
    val request = Get(s"$apiUrl/organizations/$restaurantId/reservations/$reservationId?viewMode=${Actors.restaurant}")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Reservation](), readTimeout)
  }

  override def retrieveUnhandledReservations(accessToken: String, restaurantId: String): JList[Reservation] = {
    val request = Get(s"$apiUrl/organizations/$restaurantId/reservations?viewMode=${Actors.restaurant}&unhandled=true")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result[Reservations](client.execute(request) withResult[Reservations](), readTimeout).results
  }

  override def setReservationStatusAsRestaurant(accessToken: String, restaurantId: String, reservationId: String, status: String, comment: String): Reservation = {
    status match {
      case ReservationStatuses.accepted =>
        val request = Post(s"$apiUrl/organizations/$restaurantId/reservations/$reservationId/accept?as=${Actors.restaurant}",
          Json.stringify(Comment(comment = Option(comment))))
          .addHeader(Authorization.oauth2(accessToken))
        Await.result(client.execute(request) withResult[Reservation](), readTimeout)

      case ReservationStatuses.canceled =>
        val request = Post(s"$apiUrl/organizations/$restaurantId/reservations/$reservationId/cancel?as=${Actors.restaurant}",
          Json.stringify(Comment(comment = Option(comment))))
          .addHeader(Authorization.oauth2(accessToken))
        Await.result(client.execute(request) withResult[Reservation](), readTimeout)
    }
  }

  override def setReservationStatusAsOwner(accessToken: String, restaurantId: String, reservationId: String, status: String, comment: String): Reservation = {
    status match {
      case ReservationStatuses.canceled =>
        val request = Post(s"$apiUrl/organizations/$restaurantId/reservations/$reservationId/cancel?as=${Actors.customer}",
          Json.stringify(Comment(comment = Option(comment))))
          .addHeader(Authorization.oauth2(accessToken))
        Await.result(client.execute(request) withResult[Reservation](), readTimeout)
    }
  }

  override def retrieveReservationsByPhone(accessToken: String, organizationId: String, phone: String, modifiedSince: Date, limit: Integer): JList[Reservation] = {
    retrieveUserReservations(accessToken, organizationId, new AuthenticationUser(Namespaces.phone, phone), modifiedSince, limit)
  }

  override def retrieveReservationsByEmail(accessToken: String, organizationId: String, email: String, modifiedSince: Date, limit: Integer): JList[Reservation] = {
    retrieveUserReservations(accessToken, organizationId, new AuthenticationUser(Namespaces.email, email), modifiedSince, limit)
  }

  private def retrieveUserReservations(accessToken: String, organizationId: String, user: AuthenticationUser, modifiedSince: Date, limit: Integer): JList[Reservation] = {
    val modifiedSinceTimestamp = Option(modifiedSince).map { _.getTime }.getOrElse(0L)
    val actualLimit = Option(limit).map { _.toInt }.getOrElse(1000000)
    val request = Get(s"$apiUrl/organizations/$organizationId/reservations?viewMode=${Actors.restaurant}&user=${user.ns}:${URLEncoder.encode(user.id, "UTF-8")}&modified=gte:$modifiedSinceTimestamp&limit=$actualLimit")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result[Reservations](client.execute(request) withResult[Reservations](), readTimeout).results
  }

  override def deleteOrganization(accessToken: String, organizationId: String): Unit = {
    val request = Delete(s"$apiUrl/organizations/$organizationId")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withoutResult(), readTimeout)
  }

  override def deleteCustomerByPhone(accessToken: String, organizationId: String, phone: String): Unit = {
    deleteCustomer(accessToken, organizationId, new AuthenticationUser(Namespaces.phone, phone))
  }

  override def deleteCustomerByEmail(accessToken: String, organizationId: String, email: String): Unit = {
    deleteCustomer(accessToken, organizationId, new AuthenticationUser(Namespaces.email, email))
  }

  override def getMyAccount(accessToken: String): ClientInfo = {
    val request = Get(s"$apiUrl/me/account")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[ClientInfo](), readTimeout)
  }

  override def setMyAccount(accessToken: String, account: ClientInfo): ClientInfo = {
    val request = Put(s"$apiUrl/me/account", Json.stringify(account))
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[ClientInfo](), readTimeout)
  }

  override def addMyAccountCards(accessToken: String, cardTokens: JList[CreditCardToken]): ClientInfo = {
    val request = Post(s"$apiUrl/me/account/cards", Json.stringify(CardTokens(cardTokens.asScala)))
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[ClientInfo](), readTimeout)
  }

  private def deleteCustomer(accessToken: String, organizationId: String, customer: AuthenticationUser): Unit = {
    val request = Post(s"$apiUrl/organizations/$organizationId/delete_customer", Json.stringify(customer))
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withoutResult(), readTimeout)
  }
}


private object ExceptionTranslator {
  def asException(errorResponse: ErrorResponse): RuntimeException = {
    errorResponse.`type` match {
      case Errors.InvalidData => new InvalidDataException(errorResponse.detail.orNull)
      case Errors.Forbidden => new NoPermissionException(errorResponse.detail.orNull)
      case Errors.Conflict => new ConflictException(errorResponse.detail.orNull)
      case Errors.NotFound => new NotFoundException(errorResponse.detail.orNull)
      case Errors.Internal => new InternalException(errorResponse.detail.orNull)
      case Errors.TemporarilyUnavailable => new TemporarilyUnavailableException(errorResponse.detail.orNull)
      case _ => new RestaurantsException(s"Type: '${errorResponse.`type`}'${errorResponse.detail.map(detail => s", Detail: '$detail'").getOrElse("")}")
    }
  }
}
