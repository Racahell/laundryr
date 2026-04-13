package com.example.laundryr.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.laundryr.data.local.SessionManager
import com.example.laundryr.data.model.ComplaintRequest
import com.example.laundryr.data.model.CreateOrderRequest
import com.example.laundryr.data.model.CustomerPayload
import com.example.laundryr.data.model.LoginRequest
import com.example.laundryr.data.model.OrderDto
import com.example.laundryr.data.model.RegisterRequest
import com.example.laundryr.data.model.SpecialItemRequest
import com.example.laundryr.data.model.UserDto
import com.example.laundryr.data.repository.LaundryRepository
import kotlinx.coroutines.launch

data class MainUiState(
    val loading: Boolean = false,
    val user: UserDto? = null,
    val orders: List<OrderDto> = emptyList(),
    val selectedInvoice: OrderDto? = null,
    val message: String? = null,
)

class MainViewModel(
    private val sessionManager: SessionManager,
    private val repository: LaundryRepository,
) : ViewModel() {
    var state = androidx.compose.runtime.mutableStateOf(MainUiState())
        private set

    init {
        restoreSession()
    }

    private fun restoreSession() {
        if (sessionManager.getToken() == null) return
        viewModelScope.launch {
            runCatching {
                repository.me()
            }.onSuccess {
                state.value = state.value.copy(user = it)
                loadOrders()
            }.onFailure {
                sessionManager.clearToken()
            }
        }
    }

    fun clearMessage() {
        state.value = state.value.copy(message = null)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            state.value = state.value.copy(loading = true, message = null)
            runCatching {
                repository.login(LoginRequest(email, password))
            }.onSuccess {
                sessionManager.saveToken(it.token)
                state.value = state.value.copy(loading = false, user = it.user, message = "Login berhasil")
                loadOrders()
            }.onFailure {
                state.value = state.value.copy(loading = false, message = it.message ?: "Login gagal")
            }
        }
    }

    fun register(name: String, email: String, phone: String, address: String, password: String) {
        viewModelScope.launch {
            state.value = state.value.copy(loading = true, message = null)
            runCatching {
                repository.register(RegisterRequest(name, email, phone, address, password))
            }.onSuccess {
                sessionManager.saveToken(it.token)
                state.value = state.value.copy(loading = false, user = it.user, message = "Registrasi berhasil")
                loadOrders()
            }.onFailure {
                state.value = state.value.copy(loading = false, message = it.message ?: "Registrasi gagal")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            runCatching { repository.logout() }
            sessionManager.clearToken()
            state.value = MainUiState(message = "Logout berhasil")
        }
    }

    fun loadOrders() {
        viewModelScope.launch {
            runCatching { repository.orders() }
                .onSuccess { state.value = state.value.copy(orders = it) }
                .onFailure { state.value = state.value.copy(message = it.message ?: "Gagal memuat order") }
        }
    }

    fun lookupInvoice(invoice: String) {
        viewModelScope.launch {
            state.value = state.value.copy(loading = true)
            runCatching { repository.invoice(invoice) }
                .onSuccess {
                    state.value = state.value.copy(loading = false, selectedInvoice = it)
                }
                .onFailure {
                    state.value = state.value.copy(loading = false, message = it.message ?: "Invoice tidak ditemukan")
                }
        }
    }

    fun createSampleOrder(
        serviceTypeId: Long,
        weightKg: Double,
        customerName: String,
        customerPhone: String,
        customerAddress: String,
    ) {
        viewModelScope.launch {
            state.value = state.value.copy(loading = true)
            val payload = CreateOrderRequest(
                service_type_id = serviceTypeId,
                laundry_weight_kg = weightKg,
                notes = null,
                customer = CustomerPayload(customerName, customerPhone, customerAddress),
                special_items = listOf(SpecialItemRequest(1, 1))
            )
            runCatching { repository.createOrder(payload) }
                .onSuccess {
                    state.value = state.value.copy(loading = false, message = "Order berhasil dibuat")
                    loadOrders()
                }
                .onFailure {
                    state.value = state.value.copy(loading = false, message = it.message ?: "Gagal membuat order")
                }
        }
    }

    fun transition(orderId: Long, nextStatus: String) {
        viewModelScope.launch {
            runCatching { repository.transition(orderId, nextStatus) }
                .onSuccess {
                    state.value = state.value.copy(message = "Status diupdate ke $nextStatus")
                    loadOrders()
                }
                .onFailure { state.value = state.value.copy(message = it.message ?: "Gagal update status") }
        }
    }

    fun verifyPayment(orderId: Long) {
        viewModelScope.launch {
            runCatching { repository.verifyPayment(orderId) }
                .onSuccess {
                    state.value = state.value.copy(message = "Pembayaran diverifikasi")
                    loadOrders()
                }
                .onFailure { state.value = state.value.copy(message = it.message ?: "Gagal verifikasi pembayaran") }
        }
    }

    fun submitComplaint(invoice: String, itemName: String, damageType: String, description: String) {
        viewModelScope.launch {
            runCatching {
                repository.createComplaint(
                    ComplaintRequest(
                        invoice_number = invoice,
                        item_name = itemName,
                        damage_type = damageType,
                        description = description,
                    )
                )
            }.onSuccess {
                state.value = state.value.copy(message = "Komplain terkirim")
            }.onFailure {
                state.value = state.value.copy(message = it.message ?: "Gagal kirim komplain")
            }
        }
    }
}

class MainViewModelFactory(
    private val sessionManager: SessionManager,
    private val repository: LaundryRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(sessionManager, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
