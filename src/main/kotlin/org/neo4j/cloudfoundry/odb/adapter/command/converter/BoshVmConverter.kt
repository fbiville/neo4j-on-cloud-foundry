package org.neo4j.cloudfoundry.odb.adapter.command.converter

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.neo4j.cloudfoundry.odb.adapter.App
import org.neo4j.cloudfoundry.odb.adapter.ErrorMessages
import org.neo4j.cloudfoundry.odb.adapter.domain.BoshVms
import picocli.CommandLine

class BoshVmConverter(private val gson: Gson,
                      private val mandatoryFieldsValidator: MandatoryFieldsValidator) : CommandLine.ITypeConverter<BoshVms> {

    private val parameterName = "bosh-VMs"

    override fun convert(value: String?): BoshVms {
        try {
            val result = gson.fromJson<BoshVms>(value, BoshVms::class.java)
            val missingFields = mandatoryFieldsValidator.validate(result)
            if (missingFields.isNotEmpty()) {
                throw CommandLine.ParameterException(ErrorMessages.missingMandatoryFields(parameterName, missingFields))
            }
            return result
        } catch (e: JsonSyntaxException) {
            throw CommandLine.ParameterException(ErrorMessages.deserializationFailed(parameterName), e)
        }
    }
}