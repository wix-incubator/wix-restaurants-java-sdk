package com.wix.restaurants

import com.wix.pay.smaug.client.model.CreditCardToken

case class SaveCardsRequest(cardTokens: Seq[CreditCardToken])
