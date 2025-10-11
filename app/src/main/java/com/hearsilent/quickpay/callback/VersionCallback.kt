package com.hearsilent.quickpay.callback

import com.hearsilent.quickpay.models.VersionModel

abstract class VersionCallback() {

    abstract fun onSuccess(current: VersionModel?, latest: VersionModel)
    abstract fun onFail()
}