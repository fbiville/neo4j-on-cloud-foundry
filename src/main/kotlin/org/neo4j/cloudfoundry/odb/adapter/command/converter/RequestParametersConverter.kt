package org.neo4j.cloudfoundry.odb.adapter.command.converter

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import org.neo4j.cloudfoundry.odb.adapter.App
import org.neo4j.cloudfoundry.odb.adapter.domain.RequestParameters
import picocli.CommandLine

class RequestParametersConverter : CommandLine.ITypeConverter<RequestParameters> {
    private val gson = Gson()

    override fun convert(value: String): RequestParameters {
        val type = (object : TypeToken<Map<String, String>>() {}).type
        try {
            return RequestParameters(gson.fromJson<Map<String, String>>(value, type))
        } catch (e: JsonParseException) {
            throw CommandLine.ParameterException(App.deserializationError("request-params"), e)
        }
    }

}