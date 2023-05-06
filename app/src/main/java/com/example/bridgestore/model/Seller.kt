package com.example.bridgestore.model

data class Seller (
    val id: String?,
    val fullName: String,
    val phone: String,
    val address: String,
    val email: String,
    val businessName: String,
    val businessRegistrationNumber: String,
    val description: String,

){
    fun updateSeller(
        fullName: String? = null,
        phone: String? = null,
        address: String? = null,
        email: String? = null,
        businessName: String? = null,
        businessRegistrationNumber: String? = null,
        description: String? = null
    ): Seller {
        return this.copy(
            fullName = fullName ?: this.fullName,
            phone = phone ?: this.phone,
            address = address ?: this.address,
            email = email ?: this.email,
            businessName = businessName ?: this.businessName,
            businessRegistrationNumber = businessRegistrationNumber
                ?: this.businessRegistrationNumber,
            description = description ?: this.description
        )
    }
}