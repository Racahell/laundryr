package com.example.laundryr.data.model

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String?,
    val role: String
)

data class AuthResponse(
    val token: String,
    val user: UserDto
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val address: String?,
    val password: String
)

data class CustomerPayload(
    val name: String,
    val phone: String,
    val address: String?
)

data class SpecialItemRequest(
    val special_item_category_id: Long,
    val quantity: Int
)

data class CreateOrderRequest(
    val service_type_id: Long,
    val laundry_weight_kg: Double,
    val notes: String?,
    val customer: CustomerPayload,
    val special_items: List<SpecialItemRequest>
)

data class CustomerDto(
    val id: Long,
    val name: String,
    val phone: String,
    val address: String?
)

data class ServiceTypeDto(
    val id: Long,
    val code: String,
    val name: String
)

data class OrderSpecialItemDto(
    val id: Long,
    val category_name_snapshot: String,
    val unit_snapshot: String,
    val price_per_item_snapshot: Int,
    val quantity: Int,
    val subtotal: Int
)

data class OrderDto(
    val id: Long,
    val invoice_number: String,
    val customer: CustomerDto,
    val service_type: ServiceTypeDto,
    val laundry_weight_kg: Double,
    val subtotal_kg: Int,
    val subtotal_special: Int,
    val grand_total: Int,
    val payment_status: String,
    val current_status: String,
    val overdue_unclaimed: Boolean,
    val special_items: List<OrderSpecialItemDto>
)

data class StatusTransitionRequest(val next_status: String)

data class PaymentVerifyRequest(val amount_paid: Int? = null, val notes: String? = null)

data class SpecialItemCategoryDto(
    val id: Long,
    val name: String,
    val unit: String,
    val price_per_item: Int,
    val is_active: Boolean
)

data class ComplaintRequest(
    val invoice_number: String,
    val item_name: String,
    val damage_type: String,
    val description: String?
)
