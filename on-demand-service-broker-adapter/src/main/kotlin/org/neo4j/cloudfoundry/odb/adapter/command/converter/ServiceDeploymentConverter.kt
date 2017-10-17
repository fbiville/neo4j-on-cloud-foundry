package org.neo4j.cloudfoundry.odb.adapter.command.converter

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.neo4j.cloudfoundry.odb.adapter.ErrorMessages
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceDeployment
import picocli.CommandLine

class ServiceDeploymentConverter(private val gson: Gson,
                                 private val mandatoryFieldsValidator: MandatoryFieldsValidator) : CommandLine.ITypeConverter<ServiceDeployment> {

    private val parameterName = "service-deployment"

    override fun convert(value: String): ServiceDeployment {
        try {
            val serviceDeployment = gson.fromJson<ServiceDeployment>(value, ServiceDeployment::class.java)
            val missingMandatoryFields = mandatoryFieldsValidator.validate(serviceDeployment)
            if (missingMandatoryFields.isNotEmpty()) {
                throw CommandLine.ParameterException(ErrorMessages.missingMandatoryFields(parameterName, missingMandatoryFields))
            }
            return serviceDeployment
        } catch (e: JsonSyntaxException) {
            throw CommandLine.ParameterException(ErrorMessages.deserializationFailed(parameterName), e)
        }
    }
}