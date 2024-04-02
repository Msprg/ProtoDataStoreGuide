package com.plcoding.protodatastoreguide

import androidx.datastore.core.Serializer
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object AppSettingsSerializer : Serializer<AppSettings> {

    override val defaultValue: AppSettings
        get() = AppSettings()

    override suspend fun readFrom(input: InputStream): AppSettings {
        return try {
            Json.decodeFromString(
                deserializer = AppSettings.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppSettings, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = AppSettings.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}

//See: https://programmerofpersia.medium.com/how-to-serialize-deserialize-a-persistentlist-persistentmap-with-kotlinx-serialization-72a11a226e56
@OptIn(ExperimentalSerializationApi::class)
//@Serializer(forClass = PersistentList::class) //Hasn't worked for me
//@kotlinx.serialization.Serializer(forClass = PersistentList::class)   //Works, but warns as redundant
class MyPersistentListSerializer(
    private val serializer: KSerializer<String>,
) : KSerializer<PersistentList<String>> {

    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<List<String>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<String>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<String> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }
}

@OptIn(ExperimentalSerializationApi::class)
//@Serializer(forClass = PersistentMap::class)
//@kotlinx.serialization.Serializer(forClass = PersistentMap::class)
class PersistentMapSerializer(
    private val keySerializer: KSerializer<String>,
    private val valueSerializer: KSerializer<PersistentMapSettings>
) : KSerializer<PersistentMap<String, PersistentMapSettings>> {

    private class PersistentMapDescriptor :
        SerialDescriptor by serialDescriptor<Map<String, PersistentMapSettings>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentMap"
    }

    override val descriptor: SerialDescriptor = PersistentMapDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentMap<String, PersistentMapSettings>) {
        return MapSerializer(keySerializer, valueSerializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentMap<String, PersistentMapSettings> {
        return MapSerializer(keySerializer, valueSerializer).deserialize(decoder).toPersistentMap()
    }

}