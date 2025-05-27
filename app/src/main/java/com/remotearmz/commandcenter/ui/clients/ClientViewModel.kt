package com.remotearmz.commandcenter.ui.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remotearmz.commandcenter.data.model.Client
import com.remotearmz.commandcenter.data.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val repository: ClientRepository
) : ViewModel() {

    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients: StateFlow<List<Client>> = _clients

    private val _selectedClient = MutableStateFlow<Client?>(null)
    val selectedClient: StateFlow<Client?> = _selectedClient

    init {
        loadClients()
    }

    private fun loadClients() {
        viewModelScope.launch {
            repository.getClients().collect { clients ->
                _clients.value = clients
            }
        }
    }

    fun addClient(client: Client) {
        viewModelScope.launch {
            repository.addClient(client)
        }
    }

    fun updateClient(client: Client) {
        viewModelScope.launch {
            repository.updateClient(client)
        }
    }

    fun deleteClient(client: Client) {
        viewModelScope.launch {
            repository.deleteClient(client)
        }
    }

    fun selectClient(client: Client) {
        _selectedClient.value = client
    }

    fun clearSelectedClient() {
        _selectedClient.value = null
    }
}
