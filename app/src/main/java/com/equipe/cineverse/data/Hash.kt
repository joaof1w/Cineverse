package com.equipe.cineverse.data

import java.security.MessageDigest

// Nunca salvamos a senha em texto puro: no banco fica só o hash SHA-256.
fun String.sha256(): String =
    MessageDigest.getInstance("SHA-256")
        .digest(toByteArray())
        .joinToString("") { "%02x".format(it) }
