package com.trackhub.core_hub.domain.enums

enum class InvitationStatus(val value: String) {
    ROLE_UPDATED("role_updated"),
    INVITATION_UPDATED("invitation_updated"),
    INVITATION_RESENT("invitation_resent"),
    INVITATION_SENT("invitation_sent"),
    ERROR("error");

    companion object {
        fun fromValue(value: String): InvitationStatus {
            return entries.find { it.value == value } ?: ERROR
        }
    }
}