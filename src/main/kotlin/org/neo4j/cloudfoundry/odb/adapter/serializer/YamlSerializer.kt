package org.neo4j.cloudfoundry.odb.adapter.serializer

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.introspector.Property
import org.yaml.snakeyaml.nodes.NodeTuple
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Representer
import java.util.Comparator
import java.util.TreeSet

class YamlSerializer {
    private val options: DumperOptions = DumperOptions()

    init {
        options.indent = 4
        options.lineBreak = DumperOptions.LineBreak.UNIX
    }

    fun <T>serialize(representer: Representer, obj: T): String {
        return Yaml(representer, options).dumpAsMap(obj)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T>deserialize(type: Class<T>, value: String): T {
        return Yaml(Constructor(type)).load(value) as T
    }
}

internal class ManifestRepresenter : Representer() {
    override fun getProperties(type: Class<out Any>): MutableSet<Property> {
        return sort(super.getProperties(type))
    }

    override fun representJavaBeanProperty(javaBean: Any, property: Property, propertyValue: Any?, customTag: Tag?): NodeTuple? {
        if (propertyValue == null) {
            return null
        }

        return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag)
    }

    private fun sort(properties: MutableSet<Property>): MutableSet<Property> {
        val result = TreeSet<Property>(AlphabeticalPropertyComparator())
        result.addAll(properties)
        return result
    }
}

private class AlphabeticalPropertyComparator : Comparator<Property> {
    override fun compare(o1: Property, o2: Property): Int {
        return o1.name.compareTo(o2.name)
    }
}