package org.neo4j.cloudfoundry.odb.adapter.command.converter

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.command.Fixtures
import org.neo4j.cloudfoundry.odb.adapter.domain.plan.Plan
import picocli.CommandLine

class PlanConverterTest {

    private val gson = Gson()
    private val subject = PlanConverter(gson, MandatoryFieldsValidator())

    @Test
    fun `fails when the Plan payload is not valid`() {
        assertThatExceptionOfType(CommandLine.ParameterException::class.java)
                .isThrownBy { subject.convert("""\salut\""") }
                .withMessage("Parameter 'plan' cannot be deserialized")
                .withCauseInstanceOf(JsonSyntaxException::class.java)
    }

    @Test
    fun `fails when the Plan payload is incomplete`() {
        val mandatoryFieldsValidator = mock<MandatoryFieldsValidator>()
        whenever(mandatoryFieldsValidator.validate(any<Plan>(), eq("")))
                .thenReturn(listOf("jean", "bonneau"))
        val subject = PlanConverter(gson, mandatoryFieldsValidator)

        assertThatExceptionOfType(CommandLine.ParameterException::class.java)
                .isThrownBy { subject.convert("{}") }
                .withMessage("Parameter 'plan' is missing mandatory parameters: jean, bonneau")
    }

    @Test
    fun `converts a valid Plan payload`() {
        assertThat(subject.convert(Fixtures.planJson)).isEqualTo(Fixtures.plan)
    }
}

