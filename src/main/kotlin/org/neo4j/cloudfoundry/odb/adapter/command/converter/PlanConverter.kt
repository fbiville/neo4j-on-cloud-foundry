package org.neo4j.cloudfoundry.odb.adapter.command.converter

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.neo4j.cloudfoundry.odb.adapter.App
import org.neo4j.cloudfoundry.odb.adapter.ErrorMessages
import org.neo4j.cloudfoundry.odb.adapter.domain.plan.Plan
import picocli.CommandLine

class PlanConverter(private val gson: Gson,
                    private val mandatoryFieldsValidator: MandatoryFieldsValidator) : CommandLine.ITypeConverter<Plan> {

    private val parameterName = "plan"

    override fun convert(value: String): Plan {
        try {
            val plan = gson.fromJson<Plan>(value, Plan::class.java)
            val missingMandatoryFields = mandatoryFieldsValidator.validate(plan)
            if (!missingMandatoryFields.isEmpty()) {
                throw CommandLine.ParameterException(ErrorMessages.missingMandatoryFields(parameterName, missingMandatoryFields))
            }
            return plan
        } catch (e: JsonSyntaxException) {
            throw CommandLine.ParameterException(ErrorMessages.deserializationFailed(parameterName), e)
        }
    }

}