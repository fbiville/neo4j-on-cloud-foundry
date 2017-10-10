package org.neo4j.cloudfoundry.odb.adapter.command.converter

import com.google.gson.JsonSyntaxException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.data.MapEntry
import org.junit.Test
import picocli.CommandLine.ParameterException

class RequestParametersConverterTest {
    val subject = RequestParametersConverter()

    @Test
    fun `fails with invalid Map{String, String} payload`() {
        assertThatExceptionOfType(ParameterException::class.java)
                .isThrownBy { subject.convert("""{"key": ["value", "hey it's an array!"]}""") }
                .withMessage("Parameter 'request-params' cannot be deserialized")
                .withCauseInstanceOf(JsonSyntaxException::class.java)
    }

    @Test
    fun `converts a valid Map{String, String} payload`() {
        val result = subject.convert("""{"key": "value"}""")
        assertThat(result.map).containsExactly(MapEntry.entry("key", "value"))
    }
}

