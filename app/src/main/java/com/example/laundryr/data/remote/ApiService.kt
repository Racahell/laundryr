package com.example.laundryr.data.remote

import com.example.laundryr.data.model.AuthResponse
import com.example.laundryr.data.model.ComplaintRequest
import com.example.laundryr.data.model.CreateOrderRequest
import com.example.laundryr.data.model.LoginRequest
import com.example.laundryr.data.model.OrderDto
import com.example.laundryr.data.model.PaymentVerifyRequest
import com.example.laundryr.data.model.RegisterRequest
import com.example.laundryr.data.model.SpecialItemCategoryDto
import com.example.laundryr.data.model.StatusTransitionRequest
import com.example.laundryr.data.model.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/v1/auth/logout")
    suspend fun logout()

    @GET("api/v1/me")
    suspend fun me(): UserDto

    @GET("api/v1/orders")
    suspend fun orders(): List<OrderDto>

    @POST("api/v1/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): OrderDto

    @GET("api/v1/customer/invoices/{invoice}")
    suspend fun invoice(@Path("invoice") invoice: String): OrderDto

    @POST("api/v1/orders/{id}/status-transition")
    suspend fun transition(@Path("id") id: Long, @Body request: StatusTransitionRequest)

    @POST("api/v1/orders/{id}/payment-verify")
    suspend fun verifyPayment(@Path("id") id: Long, @Body request: PaymentVerifyRequest)

    @GET("api/v1/special-item-categories")
    suspend fun categories(): List<SpecialItemCategoryDto>

    @POST("api/v1/complaints")
    suspend fun createComplaint(@Body request: ComplaintRequest)
}
