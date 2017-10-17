package org.neo4j.cloudfoundry.odb.adapter.serializer

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.command.Fixtures
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Manifest

class YamlSerializerTest {
    var subject = YamlSerializer()

    @Test
    fun `serializes a simple object with nested alphabetically-sorted properties`() {
        val nestedObject = NestedObject("value", SimpleObject("Jean Bon"))

        val result = subject.serialize(ManifestRepresenter(), nestedObject)

        assertThat(result).isEqualTo("nestedProperty:\n    name: Jean Bon\n    number: 42\nproperty: value\n")
    }

    @Test
    fun `skips null properties when serializing a simple object`() {
        val nestedObject = NestedObject("another-value", null)

        val result = subject.serialize(ManifestRepresenter(), nestedObject)

        assertThat(result).isEqualTo("property: another-value\n")
    }

    @Test
    fun `serializes an object with an enum`() {
        val enumObject = EnumObject(BetterBoolean.Zucchini)

        val result = subject.serialize(ManifestRepresenter(), enumObject)

        assertThat(result).isEqualTo("enumProperty: Zucchini\n")
    }

    @Test
    fun `deserializes an object`() {
        val result = subject.deserialize(Manifest::class.java, Fixtures.manifestYaml)

        assertThat(result).isEqualTo(Fixtures.manifest)
    }
}

data class NestedObject(var property: String, var nestedProperty: SimpleObject?)
data class SimpleObject(var name: String, var number: Int = 42)
data class EnumObject(var enumProperty: BetterBoolean)
enum class BetterBoolean { True, False, Zucchini }