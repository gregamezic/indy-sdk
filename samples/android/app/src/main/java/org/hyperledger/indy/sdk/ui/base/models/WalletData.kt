package org.hyperledger.indy.sdk.ui.base.models

import org.hyperledger.indy.sdk.wallet.Wallet

data class WalletData(
    val wallet: Wallet,
    val walletConfig: String,
    val walletCredentials: String
)
