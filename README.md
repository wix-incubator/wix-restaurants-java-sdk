# Wix Restaurants Java SDK
This client library is designed to support the **Wix Restaurants API**, which is the canonical way to interact with all Wix Restaurants products.

The Wix Restaurants API is exposed as a standard web service that communicates JSON over HTTPS. This Java library acts as the reference client implementation and implicitly defines all API objects and methods.

## Usage
See [wix-restaurants-java-examples](https://github.com/wix/wix-restaurants-java-sdk/tree/master/wix-restaurants-java-examples) for common use cases:
* Search for restaurants by location
* Retrieve and display a restaurant's menu
* Submit an order (takeout or delivery) to a restaurant
* Submit an order received via a 3rd-party portal, where the customer pays the portal and not the restaurant
* Retrieve all new orders for a restaurant, and mark them as accepted

## Installation
### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>com.wix.restaurants</groupId>
  <artifactId>wix-restaurants-java-client</artifactId>
  <version>1.5.0</version>
</dependency>
```

## Reporting Issues

Please use [the issue tracker](https://github.com/wix/wix-restaurants-java-sdk/issues) to report issues related to this library, or to the Wix Restaurants API in general.

## License
This library uses the Apache License, version 2.0.

## Additional information
We have an open attitude for working with partners, big or small. For inquiries, email dannyl@wix.com
