package org.neo4j.cloudfoundry.odb.adapter.command.converter

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.neo4j.cloudfoundry.odb.adapter.App
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceDeployment
import picocli.CommandLine

class ServiceDeploymentConverter : CommandLine.ITypeConverter<ServiceDeployment> {
    private val gson = Gson()

    private val mandatoryFieldsValidator: MandatoryFieldsValidator
    constructor(): this(MandatoryFieldsValidator())

    internal constructor(mandatoryFieldsValidator: MandatoryFieldsValidator) {
        this.mandatoryFieldsValidator = mandatoryFieldsValidator
    }

    override fun convert(value: String): ServiceDeployment {
        try {
            val serviceDeployment = gson.fromJson<ServiceDeployment>(value, ServiceDeployment::class.java)
            val missingMandatoryFields = mandatoryFieldsValidator.validate(serviceDeployment)
            if (missingMandatoryFields.isNotEmpty()) {
                throw CommandLine.ParameterException(App.missingMandatoryFieldsError("service-deployment", missingMandatoryFields))
            }
            return serviceDeployment
        } catch (e: JsonSyntaxException) {
            throw CommandLine.ParameterException(App.deserializationError("service-deployment"), e)
        }
    }
}