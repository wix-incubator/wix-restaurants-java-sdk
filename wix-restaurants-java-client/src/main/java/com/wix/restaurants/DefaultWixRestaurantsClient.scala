package com.wix.restaurants

import java.io.IOException
import java.lang.{Integer => JInteger}
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import java.util.{Date, List => JList, Map => JMap}

import akka.actor.ActorSystem
import akka.http.javadsl.model.headers.Authorization
import akka.http.scaladsl.client.RequestBuilding.{Delete, Get, Post, Put}
import com.fasterxml.jackson.core.`type`.TypeReference
import com.google.api.client.http.HttpRequestFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.openrest.v1_1._
import com.wix.rest.rfc7807.api.model.ErrorResponse
import com.wix.rest.rfc7807.client.AkkaRestClient
import com.wix.restaurants.authentication.model.{Namespaces, User => AuthenticationUser}
import com.wix.restaurants.authentication.{DefaultWixRestaurantsAuthenticationClient, WixRestaurantsAuthenticationClient}
import com.wix.restaurants.authorization.{AuthorizationClient, DefaultAuthorizationClient}
import com.wix.restaurants.exceptions._
import com.wix.restaurants.i18n.Locale
import com.wix.restaurants.json.Json
import com.wix.restaurants.orders.{Orders, Statuses => OrderStatuses}
import com.wix.restaurants.requests.{DeleteCustomerRequest, Request, SearchRequest}
import com.wix.restaurants.reservations.requests.QueryUnhandledReservationsRequest
import com.wix.restaurants.reservations.{Reservation, Reservations, ReservationsResponse, Statuses => ReservationStatuses}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success, Try}

class DefaultWixRestaurantsClient(api2Url: String = "https://api.wixrestaurants.com/v2",
                                  api1Url: String = "https://api.wixrestaurants.com/v1.1",
                                  authApiUrl: String = "https://auth.wixrestaurants.com/v1",
                                  connectTimeout: Option[Duration] = None,
                                  readTimeout: Option[Duration] = None,
                                  numberOfRetries: Integer = 0) extends WixRestaurantsClient {
  private val actualReadTimeout = readTimeout.getOrElse(Duration.Inf)

  private val requestFactory: HttpRequestFactory = new NetHttpTransport().createRequestFactory
  private val apiV1Client = new OpenrestClient(
    requestFactory,
    connectTimeout.map { _.toMillis.toInt }.map { JInteger.valueOf }.orNull,
    readTimeout.map { _.toMillis.toInt }.map { JInteger.valueOf }.orNull,
    numberOfRetries,
    api1Url)
  private val authClient: WixRestaurantsAuthenticationClient = new DefaultWixRestaurantsAuthenticationClient(
    requestFactory,
    connectTimeout.map { _.toMillis.toInt }.map { JInteger.valueOf }.orNull,
    readTimeout.map { _.toMillis.toInt }.map { JInteger.valueOf }.orNull,
    numberOfRetries,
    authApiUrl)
  private val authorizationClient: AuthorizationClient = new DefaultAuthorizationClient(
    apiUrl = api2Url)

  private implicit val system: ActorSystem = ActorSystem("akka-wix-restaurants-client-system")
  private implicit val executionContext: ExecutionContext = system.dispatcher
  private val client: AkkaRestClient = new AkkaRestClient(errorResponseAsException = ExceptionTranslator.asException)


  override def getAuthenticationClient: WixRestaurantsAuthenticationClient = authClient

  override def getAuthorizationClient: AuthorizationClient = authorizationClient

  override def retrieveRestaurantInfo(restaurantId: String): RestaurantFullInfo = {
    val request = Get(s"$api2Url/organizations/$restaurantId/full")
    Await.result(client.execute(request) withResult[RestaurantFullInfo](), actualReadTimeout)
  }

  override def retrieveOrganization(organizationId: String): Organization = {
    val request = Get(s"$api2Url/organizations/$organizationId")
    Await.result(client.execute(request) withResult[Organization](), actualReadTimeout)
  }

  override def setOrganization(accessToken: String, organization: Organization): Organization = {
    val request = Put(s"$api2Url/organizations/${organization.id}", Json.stringify(organization))
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Organization](), actualReadTimeout)
  }

  override def search(filter: Filter, limit: Int): JList[SearchResult] = {
    val searchRequest = new SearchRequest
    searchRequest.filter = filter
    searchRequest.limit = limit
    val searchResponse = apiV1Request(searchRequest, new TypeReference[Response[SearchResponse]]() {})
    searchResponse.results
  }

  override def changeOrganizationLocale(accessToken: String, organizationId: String, locale: Locale): Unit = {
    val request = Post(s"$api2Url/organizations/$organizationId/change_locale?locale=$locale")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withoutResult(), actualReadTimeout)
  }

  override def getNotifications(accessToken: String , organizationId: String): Notifications = {
    val request = Get(s"$api2Url/organizations/$organizationId/notifications")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Notifications](), actualReadTimeout)
  }

  override def setNotifications(accessToken: String, organizationId: String, notifications: Notifications): Notifications = {
    val request = Put(s"$api2Url/organizations/$organizationId/notifications", Json.stringify(notifications))
        .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Notifications](), actualReadTimeout)
  }

  override def getSecrets(accessToken: String , organizationId: String): Secrets = {
    val request = Get(s"$api2Url/organizations/$organizationId/secrets")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Secrets](), actualReadTimeout)
  }

  override def setSecrets(accessToken: String, organizationId: String, secrets: Secrets): Secrets = {
    val request = Put(s"$api2Url/organizations/$organizationId/secrets", Json.stringify(secrets))
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Secrets](), actualReadTimeout)
  }

  override def getMenu(accessToken: String , restaurantId: String): Menu = {
    val request = Get(s"$api2Url/organizations/$restaurantId/menu")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Menu](), actualReadTimeout)
  }

  override def setMenu(accessToken: String, restaurantId: String, menu: Menu): Menu = {
    val request = Put(s"$api2Url/organizations/$restaurantId/menu", Json.stringify(menu))
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Menu](), actualReadTimeout)
  }

  override def submitOrder(accessToken: String, order: Order): Order = {
    val request = Post(s"$api2Url/organizations/${order.restaurantId}/orders", Json.stringify(order))
    Option(accessToken).foreach { theAccessToken => request.addHeader(Authorization.oauth2(theAccessToken)) }
    Await.result(client.execute(request) withResult[Order](), actualReadTimeout)
  }

  override def retrieveOrderAsRestaurant(accessToken: String, restaurantId: String, orderId: String): Order = {
    val request = Get(s"$api2Url/organizations/$restaurantId/orders/$orderId?viewMode=${Actors.restaurant}")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Order](), actualReadTimeout)
  }

  override def retrieveOrderAsOwner(accessToken: String, restaurantId: String, orderId: String): Order = {
    val request = Get(s"$api2Url/organizations/$restaurantId/orders/$orderId?viewMode=${Actors.customer}")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Order](), actualReadTimeout)
  }

  override def retrieveNewOrders(accessToken: String, restaurantId: String): JList[Order] = {
    val request = Get(s"$api2Url/organizations/$restaurantId/orders?viewMode=${Actors.restaurant}&status=${OrderStatuses.new_}")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result[Orders](client.execute(request) withResult[Orders](), actualReadTimeout).results
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
    val request = Get(s"$api2Url/organizations/$organizationId/orders?viewMode=${Actors.restaurant}&user=${user.ns}:${URLEncoder.encode(user.id, "UTF-8")}&modified=gte:$modifiedSinceTimestamp&limit=$actualLimit")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result[Orders](client.execute(request) withResult[Orders](), actualReadTimeout).results
  }

  override def acceptOrder(accessToken: String, restaurantId: String, orderId: String, externalIds: JMap[String, String]): Order = {
    val request = Post(s"$api2Url/organizations/$restaurantId/orders/$orderId/accept", Json.stringify(AnyRef))
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Order](), actualReadTimeout)
  }

  override def rejectOrder(accessToken: String, restaurantId: String, orderId: String, comment: String): Order = {
    val request = Post(s"$api2Url/organizations/$restaurantId/orders/$orderId/cancel", Json.stringify(AnyRef))
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Order](), actualReadTimeout)
  }

  override def submitReservation(accessToken: String, reservation: Reservation): Reservation = {
    val request = Post(s"$api2Url/organizations/${reservation.restaurantId}/reservations", Json.stringify(reservation))
    Option(accessToken).foreach { theAccessToken => request.addHeader(Authorization.oauth2(theAccessToken)) }
    Await.result(client.execute(request) withResult[Reservation](), actualReadTimeout)
  }

  override def retrieveReservationAsOwner(accessToken: String, restaurantId: String, reservationId: String): Reservation = {
    val request = Get(s"$api2Url/organizations/$restaurantId/reservations/$reservationId?viewMode=${Actors.customer}")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Reservation](), actualReadTimeout)
  }

  override def retrieveReservationAsRestaurant(accessToken: String, restaurantId: String, reservationId: String): Reservation = {
    val request = Get(s"$api2Url/organizations/$restaurantId/reservations/$reservationId?viewMode=${Actors.restaurant}")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withResult[Reservation](), actualReadTimeout)
  }

  override def retrieveUnhandledReservations(accessToken: String, restaurantId: String): JList[Reservation] = {
    val queryUnhandledReservationsRequest = new QueryUnhandledReservationsRequest
    queryUnhandledReservationsRequest.accessToken = accessToken
    queryUnhandledReservationsRequest.organizationId = restaurantId
    queryUnhandledReservationsRequest.viewMode = Actors.restaurant

    val queryUnhandledReservationsResponse = apiV1Request(queryUnhandledReservationsRequest, new TypeReference[Response[ReservationsResponse]]() {})

    queryUnhandledReservationsResponse.results
  }

  override def setReservationStatusAsRestaurant(accessToken: String, restaurantId: String, reservationId: String, status: String, comment: String): Reservation = {
    status match {
      case ReservationStatuses.accepted =>
        val request = Post(s"$api2Url/organizations/$restaurantId/reservations/$reservationId/accept?as=${Actors.restaurant}")
          .addHeader(Authorization.oauth2(accessToken))
        Await.result(client.execute(request) withResult[Reservation](), actualReadTimeout)

      case ReservationStatuses.canceled =>
        val request = Post(s"$api2Url/organizations/$restaurantId/reservations/$reservationId/cancel?as=${Actors.restaurant}",
          Json.stringify(UpdateStatusRequest(comment = Option(comment))))
          .addHeader(Authorization.oauth2(accessToken))
        Await.result(client.execute(request) withResult[Reservation](), actualReadTimeout)
    }
  }

  override def setReservationStatusAsOwner(accessToken: String, restaurantId: String, reservationId: String, status: String, comment: String): Reservation = {
    status match {
      case ReservationStatuses.canceled =>
        val request = Post(s"$api2Url/organizations/$restaurantId/reservations/$reservationId/cancel?as=${Actors.customer}",
          Json.stringify(UpdateStatusRequest(comment = Option(comment))))
          .addHeader(Authorization.oauth2(accessToken))
        Await.result(client.execute(request) withResult[Reservation](), actualReadTimeout)
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
    val request = Get(s"$api2Url/organizations/$organizationId/reservations?viewMode=${Actors.restaurant}&user=${user.ns}:${URLEncoder.encode(user.id, "UTF-8")}&modified=gte:$modifiedSinceTimestamp&limit=$actualLimit")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result[Reservations](client.execute(request) withResult[Reservations](), actualReadTimeout).results
  }

  override def deleteOrganization(accessToken: String, organizationId: String): Unit = {
    val request = Delete(s"$api2Url/organizations/$organizationId")
      .addHeader(Authorization.oauth2(accessToken))
    Await.result(client.execute(request) withoutResult(), actualReadTimeout)
  }

  override def deleteCustomerByPhone(accessToken: String, organizationId: String, phone: String): Unit = {
    val deleteCustomerRequest = new DeleteCustomerRequest
    deleteCustomerRequest.accessToken = accessToken
    deleteCustomerRequest.organizationId = organizationId
    deleteCustomerRequest.customerId = customerByPhone(phone)

    apiV1Request(deleteCustomerRequest, new TypeReference[Response[AnyRef]]() {})
  }

  override def deleteCustomerByEmail(accessToken: String, organizationId: String, email: String): Unit = {
    val deleteCustomerRequest = new DeleteCustomerRequest
    deleteCustomerRequest.accessToken = accessToken
    deleteCustomerRequest.organizationId = organizationId
    deleteCustomerRequest.customerId = customerByEmail(email)

    apiV1Request(deleteCustomerRequest, new TypeReference[Response[AnyRef]]() {})
  }

  private def customerByPhone(phone: String) = new ClientId(ClientNamespaces.phone, phone, null, false)

  private def customerByEmail(email: String) = new ClientId(ClientNamespaces.email, email, null, false)

  private def apiV1Request[T](request: Request, responseType: TypeReference[Response[T]]) = {
    Try {
      apiV1Client.request(request, responseType)
    } match {
      case Success(value) => value
      case Failure(e: IOException) => throw new CommunicationException(e.getMessage, e)
      case Failure(e: OpenrestException) => throw translateException(e)
      case Failure(e) => throw e
    }
  }

  private def translateException(e: OpenrestException): RestaurantsException = {
    e.error match {
      case Error.ERROR_NO_PERMISSION => new NoPermissionException(e.errorMessage, e)
      case Error.ERROR_INVALID_DATA => new InvalidDataException(e.errorMessage, e)
      case Error.ERROR_INTERNAL => new InternalException(e.errorMessage, e)
      case Error.ERROR_NOT_FOUND => new NotFoundException(e.errorMessage, e)
      case _ => new RestaurantsException(e.error + "|" + e.errorMessage, e)
    }
  }
}

object DefaultWixRestaurantsClient {
  class Builder {
    private var connectTimeout = 0
    private var readTimeout = 0
    private var numberOfRetries = 0
    private var authApiUrl = com.wix.restaurants.authentication.Endpoints.PRODUCTION
    private var apiUrl = Endpoints.production
    private var api2Url = "https://api.wixrestaurants.com/v2"

    def setConnectTimeout(connectTimeout: Int): Builder = {
      this.connectTimeout = connectTimeout
      this
    }

    def setReadTimeout(readTimeout: Int): Builder = {
      this.readTimeout = readTimeout
      this
    }

    def setNumberOfRetries(numberOfRetries: Int): Builder = {
      this.numberOfRetries = numberOfRetries
      this
    }

    def setAuthApiUrl(authApiUrl: String): Builder = {
      this.authApiUrl = authApiUrl
      this
    }

    def setApiUrl(apiUrl: String): Builder = {
      this.apiUrl = apiUrl
      this
    }

    def setApi2Url(api2Url: String): Builder = {
      this.api2Url = api2Url
      this
    }

    def build: DefaultWixRestaurantsClient = {
      new DefaultWixRestaurantsClient(
        api2Url = api2Url,
        api1Url = apiUrl,
        authApiUrl = authApiUrl,
        connectTimeout = Some(Duration(connectTimeout, TimeUnit.MILLISECONDS)),
        readTimeout = Some(Duration(readTimeout, TimeUnit.MILLISECONDS)),
        numberOfRetries = numberOfRetries)
    }
  }

}

private object ExceptionTranslator {
  def asException(errorResponse: ErrorResponse): RuntimeException = {
    errorResponse match {
      case errRes if errRes.`type` == "https://www.wixrestaurants.com/errors/not_found" => new NotFoundException(errorResponse.detail.orNull)
      case _ => new RuntimeException(s"Received an error response from the server with type: '${errorResponse.`type`}'${errorResponse.detail.map(detail => s", Detail: '$detail'").getOrElse("")}")
    }
  }
}

private case class UpdateStatusRequest(comment: Option[String])
