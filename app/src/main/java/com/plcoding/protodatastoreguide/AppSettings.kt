package com.plcoding.protodatastoreguide

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val language: Language = Language.ENGLISH,

    @Serializable(with = MyPersistentListSerializer::class)   //Register the serializer explicitly
    val knownLocations: PersistentList<Location> = persistentListOf(),

    @Serializable(with = PersistentMapSerializer::class)
    val themeSettingsMap: PersistentMap<String, PersistentMapSettings> = persistentMapOf(),
)

@Serializable
data class PersistentMapSettings(
    val id : String,
    val title : String,
    val isEnabled : Boolean
)

@Serializable
data class Location(
    val lat: Double,
    val lng: Double
)

enum class Language {
    ENGLISH, GERMAN, SPANISH
}
