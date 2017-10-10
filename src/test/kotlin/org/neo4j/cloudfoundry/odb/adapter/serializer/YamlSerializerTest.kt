package org.neo4j.cloudfoundry.odb.adapter.serializer

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class YamlSerializerTest {
    var subject = YamlSerializer()

    @Test
    fun `serializes a simple object with nested alphabetically-sorted properties`() {
        val nestedObject = NestedObject("value", SimpleObject("Jean Bon"))

        val result = subject.serialize(nestedObject)

        assertThat(result).isEqualTo("nestedProperty:\n    name: Jean Bon\n    number: 42\nproperty: value\n")
    }

    @Test
    fun `skips null properties when serializing a simple object`() {
        val nestedObject = NestedObject("another-value", null)

        val result = subject.serialize(nestedObject)

        assertThat(result).isEqualTo("property: another-value\n")
    }

    @Test
    fun `serializes an object with an enum`() {
        val enumObject = EnumObject(BetterBoolean.Zucchini)

        val result = subject.serialize(enumObject)

        assertThat(result).isEqualTo("enumProperty: Zucchini\n")
    }
}

data class NestedObject(var property: String, var nestedProperty: SimpleObject?)
data class SimpleObject(var name: String, var number: Int = 42)
data class EnumObject(var enumProperty: BetterBoolean)
enum class BetterBoolean { True, False, Zucchini }