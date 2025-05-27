package com.remotearmz.commandcenter.data.repository

import com.remotearmz.commandcenter.data.model.Client
import kotlinx.coroutines.flow.Flow

interface ClientRepository {
    fun getClients(): Flow<List<Client>>
    suspend fun addClient(client: Client)
    suspend fun updateClient(client: Client)
    suspend fun deleteClient(client: Client)
}

class DefaultClientRepository : ClientRepository {
    private val clients = mutableListOf(
        Client(
            id = "1",
            name = "TechCorp Solutions",
            whatsapp = "+1234567890",
            email = "contact@techcorp.com",
            instagram = "@techcorp",
            monthlyCharge = 5000.0,
            deliverables = listOf("Social Media Management", "Content Creation", "SEO"),
            paymentDate = "2024-01-15",
            status = ClientStatus.ACTIVE
        ),
        Client(
            id = "2",
            name = "StartupXYZ",
            whatsapp = "+0987654321",
            email = "hello@startupxyz.com",
            instagram = "@startupxyz",
            monthlyCharge = 3500.0,
            deliverables = listOf("Website Development", "Brand Design"),
            paymentDate = "2024-01-20",
            status = ClientStatus.ACTIVE
        )
    )

    override fun getClients(): Flow<List<Client>> = flow { emit(clients) }

    override suspend fun addClient(client: Client) {
        clients.add(client)
    }

    override suspend fun updateClient(client: Client) {
        val index = clients.indexOfFirst { it.id == client.id }
        if (index != -1) {
            clients[index] = client
        }
    }

    override suspend fun deleteClient(client: Client) {
        clients.remove(client)
    }
}
