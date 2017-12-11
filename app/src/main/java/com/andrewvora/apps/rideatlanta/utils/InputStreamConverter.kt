package com.andrewvora.apps.rideatlanta.utils

import java.io.InputStream

/**
 * Created on 12/10/2017.
 * @author Andrew Vorakrajangthiti
 */
class InputStreamConverter {
    fun getString(inputStream: InputStream) = inputStream.bufferedReader().use { it.readText() }
}
