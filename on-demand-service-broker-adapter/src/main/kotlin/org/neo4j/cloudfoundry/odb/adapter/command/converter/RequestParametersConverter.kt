package org.neo4j.cloudfoundry.odb.adapter.command.converter

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import org.neo4j.cloudfoundry.odb.adapter.App
import org.neo4j.cloudfoundry.odb.adapter.ErrorMessages
import org.neo4j.cloudfoundry.odb.adapter.domain.RequestParameters
import picocli.CommandLine

class RequestParametersConverter(private val gson: Gson) : CommandLine.ITypeConverter<RequestParameters> {

    private val parameterName = "request-params"

    override fun convert(value: String): RequestParameters {
        val type = (object : TypeToken<Map<String, String>>() {}).type
        try {
            return RequestParameters(gson.fromJson<Map<String, String>>(value, type))
        } catch (e: JsonParseException) {
            throw CommandLine.ParameterException(ErrorMessages.deserializationFailed(parameterName), e)
        }
    }

}