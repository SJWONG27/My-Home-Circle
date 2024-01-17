package com.example.madassignment


data class RegistrationData(val email: String, val name: String)
data class LoginData(val email: String, val password: String)
data class TokenResponse(val token: String?)
data class UserProfileResponse(
    val name: String,
    val email: String,
)
data class ServiceModel(
    val userName: String,
    val userPhotoUrl: String, // Replace String with the appropriate data type for the photo (e.g., URL)
    val rating: Float
)

data class CommunityRequest(val groupName: String, val residentialName: String)

data class CommunityResponse(
    val community_id: String
)

data class JoinGroupRequest(val communityCode: String)

data class JoinGroupResponse(
    val groupName: String,
    val communityCode: String
)

data class Company(
    val companyName: String = "",
    val representativeIC: String = "",
    val ssmID: String = "",
    val companyEmail: String = "",
    val rating: Float = 0.0f,
    val serviceId: Int = 0,
    val profilePhotoUrl: String = "",
    val supportDocumentsUrl: String = ""
)


data class CompanyServiceModel(
    val companyId: String,
    val companyName: String,
    val profilePhotoUrl: String,
    val rating: Float,
    val email: String?
)

data class Appointment(
    val serviceId: Int,
    val companyId: String,
    val companyName: String,
    val appointmentId: String,
    val date: String,
    val time: String
)
