package com.trinitymirror.networkmonitor

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import com.nhaarman.mockito_kotlin.verify
import com.trinitymirror.networkmonitor.mother.UsageListenerMother
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.junit.Before
import org.junit.Test

class UsageCallbackRegisterTest : BaseTest() {

    companion object {
        val SUBSCRIBER_ID = "subscriber-id"
    }

    private val statsManager = mock(NetworkStatsManager::class)
    private val libraryCallback = mock(UsageListener.Callback::class)

    private lateinit var usageCallbackRegister: UsageCallbackRegisterStub

    @Before
    override fun setUp() {
        usageCallbackRegister = UsageCallbackRegisterStub(context, statsManager)
    }

    @Test
    fun `register is called with correct params for mobile`() {
        val id = 1
        val listener = UsageListenerMother.create(id, UsageListener.NetworkType.MOBILE, libraryCallback)


        usageCallbackRegister.registerUsageCallback(listener)


        usageCallbackRegister.callbacksList().size() shouldBe 1
        verify(statsManager).registerUsageCallback(
                ConnectivityManager.TYPE_MOBILE,
                SUBSCRIBER_ID,
                listener.params.maxBytesSinceAppRestart,
                usageCallbackRegister.callbacksList()[id])
    }

    @Test
    fun `register is called with correct params for wifi`() {
        val id = 1
        val listener = UsageListenerMother.create(id, UsageListener.NetworkType.WIFI, libraryCallback)


        usageCallbackRegister.registerUsageCallback(listener)


        usageCallbackRegister.callbacksList().size() shouldBe 1
        verify(statsManager).registerUsageCallback(
                ConnectivityManager.TYPE_WIFI,
                "",
                listener.params.maxBytesSinceAppRestart,
                usageCallbackRegister.callbacksList()[id])
    }

    @Test
    fun `when unregister, trigger NetworkStatsManager and remove from SparseArray`() {
        val id = 1
        val listener = UsageListenerMother.create(id, UsageListener.NetworkType.WIFI, libraryCallback)
        usageCallbackRegister.registerUsageCallback(listener)
        val callback = usageCallbackRegister.callbacksList()[id]


        usageCallbackRegister.unregisterUsageCallback(listener)


        usageCallbackRegister.callbacksList().size() shouldBe 0
        verify(statsManager).unregisterUsageCallback(callback)
    }

    @Test
    fun `when android triggers the callback, then notify listener`() {
        val listener = UsageListenerMother.create(1, UsageListener.NetworkType.MOBILE, libraryCallback)
        usageCallbackRegister.onThresholdReached(listener)

        verify(libraryCallback).onMaxBytesReached(any())

        //TODO("verify that:")
        //  - listener is notified
        //  - callback is unregistered
        //  - store value in shared prefs ?
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    internal class UsageCallbackRegisterStub(context: Context, statsManager: NetworkStatsManager)
        : UsageCallbackRegister.Nougat(context, statsManager) {

        override fun getSubscriberId(): String {
            return SUBSCRIBER_ID
        }

        fun callbacksList() = usageCallbacksList
    }
}