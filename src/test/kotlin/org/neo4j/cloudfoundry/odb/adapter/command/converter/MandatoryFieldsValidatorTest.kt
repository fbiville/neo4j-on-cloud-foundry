package org.neo4j.cloudfoundry.odb.adapter.command.converter

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.domain.Mandatory
import org.neo4j.cloudfoundry.odb.adapter.domain.NoArgConstructorPlease
import org.neo4j.cloudfoundry.odb.adapter.domain.update.Update
import java.lang.reflect.Constructor
import java.util.Arrays

class MandatoryFieldsValidatorTest {

    @Test
    fun `returns the name of missing mandatory parameters`() {
        val subject = MandatoryFieldsValidator()

        val result = subject.validate(noArgConstructor(Update::class.java).newInstance() as Update)

        assertThat(result)
                .containsOnlyOnce("update_watch_time", "canary_watch_time", "canaries", "max_in_flight")
    }

    @Test
    fun `returns the name of a nested missing mandatory parameters`() {
        val subject = MandatoryFieldsValidator()
        val nestedUpdate = noArgConstructor(NestedUpdate::class.java).newInstance() as NestedUpdate
        nestedUpdate.update = noArgConstructor(Update::class.java).newInstance() as Update

        val result = subject.validate(nestedUpdate)

        assertThat(result).containsOnlyOnce("other", "update.update_watch_time", "update.canary_watch_time", "update.canaries", "update.max_in_flight")
    }

    @Test
    fun `returns an empty list when no mandatory parameter is missing`() {
        val subject = MandatoryFieldsValidator()

        val result = subject.validate(Update(0, 0, "value", "value"))

        assertThat(result).isEmpty()
    }

    private fun <T> noArgConstructor(clazz: java.lang.Class<T>): Constructor<*> {
        return Arrays.stream(clazz.constructors).filter { it.parameters.isEmpty() }.findFirst().get()
    }
}

@NoArgConstructorPlease
data class NestedUpdate(@Mandatory var update: Update, @Mandatory var other: String)