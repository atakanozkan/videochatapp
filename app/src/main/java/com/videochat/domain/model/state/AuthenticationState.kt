package com.videochat.domain.model.state

enum class AuthenticationState {
    Authenticated,
    Unauthenticated,
    Authenticating,
    Failed
}