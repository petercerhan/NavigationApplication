package com.example.navigationapplication.infrastructure_services

import android.os.SystemClock

/*
    Elapsed Realtime is an Android system API which gives milliseconds since device boot
    Guaranteed to be monotonically increasing
 */

interface ElapsedRealtimeService {
    fun elapsedRealtime(): Long
}

class ElapsedRealtimeServiceImpl: ElapsedRealtimeService {

    override fun elapsedRealtime(): Long {
        return SystemClock.elapsedRealtime()
    }

}
