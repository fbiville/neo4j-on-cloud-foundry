package org.neo4j.cloudfoundry.odb.adapter.command

import com.google.gson.Gson
import org.neo4j.cloudfoundry.odb.adapter.CommandErrors
import org.neo4j.cloudfoundry.odb.adapter.command.error.CommandOutput
import org.neo4j.cloudfoundry.odb.adapter.command.persistence.CredentialsRepository
import org.neo4j.cloudfoundry.odb.adapter.command.persistence.PersistenceError
import org.neo4j.cloudfoundry.odb.adapter.domain.BoshVms
import org.neo4j.cloudfoundry.odb.adapter.domain.Either
import org.neo4j.cloudfoundry.odb.adapter.domain.RequestParameters
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Manifest
import org.neo4j.driver.v1.AuthTokens
import org.neo4j.driver.v1.Driver
import org.neo4j.driver.v1.GraphDatabase
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(name = "create-binding")
class CreateBindingCommand(private val credentialsRepository: CredentialsRepository,
                           private val gson: Gson) : ServiceAdapterCommand {

    @CommandLine.Parameters(index = "0", arity = "1")
    lateinit var bindingId: String

    @CommandLine.Parameters(index = "1", arity = "1")
    lateinit var boshVms: BoshVms

    @CommandLine.Parameters(index = "2", arity = "1")
    lateinit var manifest: Manifest

    @CommandLine.Parameters(index = "3", arity = "1")
    lateinit var requestParams: RequestParameters

    override fun execute(): CommandOutput {
        return try {
            val driver = getDriver(manifest)
            when (driver) {
                is Either.Left<CommandOutput.Error> -> driver.value
                is Either.Right<Driver> -> insertUser(driver)
            }
        } catch (e: PersistenceError) {
            val error = CommandErrors.userCreationError
            CommandOutput.Error(error.first, error.second(bindingId))
        } catch (e: ServiceUnavailableException) {
            val unreachableVmError = CommandErrors.unreachableVmError
            CommandOutput.Error(unreachableVmError.first, unreachableVmError.second(bindingId, e))
        }
    }

    private fun insertUser(driver: Either.Right<Driver>): CommandOutput {
        return if (credentialsRepository.exists(driver.value, bindingId)) {
            val error = CommandErrors.bindingAlreadyExistsError
            CommandOutput.Error(error.first, error.second(bindingId))
        } else CommandOutput.Standard(gson.toJson(credentialsRepository.save(driver.value, bindingId)))
    }

    private fun getDriver(manifest: Manifest): Either<CommandOutput.Error, Driver> {
        if (boshVms.neo4j?.isEmpty() != false) {
            return Either.Left(CommandOutput.Error(CommandErrors.noVmIpError.first, CommandErrors.noVmIpError.second(bindingId)))
        }
        if (manifest.properties?.admin_password == null) {
            val error = CommandErrors.noPasswordError
            return Either.Left(CommandOutput.Error(error.first, error.second(bindingId)))
        }
        return Either.Right(GraphDatabase.driver(
                "bolt://${boshVms.neo4j?.get(0)}:7687",
                AuthTokens.basic("neo4j", manifest.properties?.admin_password)
        ))
    }

}

