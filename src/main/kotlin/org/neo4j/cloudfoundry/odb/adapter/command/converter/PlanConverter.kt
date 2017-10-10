package org.neo4j.cloudfoundry.odb.adapter.command.converter

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.neo4j.cloudfoundry.odb.adapter.App
import org.neo4j.cloudfoundry.odb.adapter.domain.plan.Plan
import picocli.CommandLine

class PlanConverter : CommandLine.ITypeConverter<Plan> {
    private val mandatoryFieldsValidator: MandatoryFieldsValidator

    private val gson: Gson
    constructor(): this(MandatoryFieldsValidator())

    internal constructor(mandatoryFieldsValidator: MandatoryFieldsValidator) {
        this.mandatoryFieldsValidator = mandatoryFieldsValidator
        gson = Gson()
    }

    override fun convert(value: String): Plan {
        try {
            val plan = gson.fromJson<Plan>(value, Plan::class.java)
            val missingMandatoryFields = mandatoryFieldsValidator.validate(plan)
            if (!missingMandatoryFields.isEmpty()) {
                throw CommandLine.ParameterException(App.missingMandatoryFieldsError("plan", missingMandatoryFields))
            }
            return plan
        } catch (e: JsonSyntaxException) {
            throw CommandLine.ParameterException(App.deserializationError("plan"), e)
        }
    }

}