package com.example.laundryr.data.repository

import com.example.laundryr.data.model.AuthResponse
import com.example.laundryr.data.model.ComplaintRequest
import com.example.laundryr.data.model.CreateOrderRequest
import com.example.laundryr.data.model.LoginRequest
import com.example.laundryr.data.model.OrderDto
import com.example.laundryr.data.model.PaymentVerifyRequest
import com.example.laundryr.data.model.RegisterRequest
import com.example.laundryr.data.model.SpecialItemCategoryDto
import com.example.laundryr.data.model.StatusTransitionRequest
import com.example.laundryr.data.remote.ApiService

class LaundryRepository(private val api: ApiService) {
    suspend fun register(payload: RegisterRequest): AuthResponse = api.register(payload)
    suspend fun login(payload: LoginRequest): AuthResponse = api.login(payload)
    suspend fun me() = api.me()
    suspend fun logout() = api.logout()

    suspend fun orders(): List<OrderDto> = api.orders()
    suspend fun createOrder(payload: CreateOrderRequest): OrderDto = api.createOrder(payload)
    suspend fun invoice(invoice: String): OrderDto = api.invoice(invoice)
    suspend fun transition(id: Long, nextStatus: String) = api.transition(id, StatusTransitionRequest(nextStatus))
    suspend fun verifyPayment(id: Long) = api.verifyPayment(id, PaymentVerifyRequest())
    suspend fun categories(): List<SpecialItemCategoryDto> = api.categories()
    suspend fun createComplaint(payload: ComplaintRequest) = api.createComplaint(payload)
}
