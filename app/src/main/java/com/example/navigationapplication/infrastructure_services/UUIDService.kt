package com.example.navigationapplication.infrastructure_services

import java.util.UUID

interface UUIDService {
    fun newUUID(): UUID
}

class UUIDServiceImpl: UUIDService {

    override fun newUUID(): UUID {
        return UUID.randomUUID()
    }

}