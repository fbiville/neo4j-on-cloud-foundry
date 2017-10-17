package org.neo4j.cloudfoundry.odb.adapter.command

import com.google.gson.Gson
import org.neo4j.cloudfoundry.odb.adapter.CommandErrors
import org.neo4j.cloudfoundry.odb.adapter.command.error.CommandOutput
import org.neo4j.cloudfoundry.odb.adapter.command.persistence.CredentialsRepository
import org.neo4j.cloudfoundry.odb.adapter.command.persistence.PersistenceError
import org.neo4j.cloudfoundry.odb.adapter.command.supplier.AdminPasswordSupplier
import org.neo4j.cloudfoundry.odb.adapter.command.supplier.BoltUriSupplier
import org.neo4j.cloudfoundry.odb.adapter.command.supplier.DriverSupplier
import org.neo4j.cloudfoundry.odb.adapter.domain.BoshVms
import org.neo4j.cloudfoundry.odb.adapter.domain.Either
import org.neo4j.cloudfoundry.odb.adapter.domain.RequestParameters
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Manifest
import org.neo4j.driver.v1.Driver
import org.neo4j.driver.v1.exceptions.Neo4jException
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(name = "create-binding")
class CreateBindingCommand(private val credentialsRepository: CredentialsRepository,
                           private val gson: Gson,
                           private val driverSupplier: DriverSupplier,
                           private val boltUriSupplier: BoltUriSupplier,
                           private val adminPasswordSupplier: AdminPasswordSupplier) : ServiceAdapterCommand {

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
            val driver = instantiateDriver()
            when (driver) {
                is Either.Left<CommandOutput.Error> -> driver.value
                is Either.Right<Driver> -> insertUser(driver)
            }
        } catch (e: PersistenceError) {
            val error = CommandErrors.userCreationError
            CommandOutput.Error(error.first, error.second(bindingId, e))
        } catch (e: Neo4jException) {
            val unreachableVmError = CommandErrors.unreachableVmError
            CommandOutput.Error(unreachableVmError.first, unreachableVmError.second(bindingId, e))
        }
    }

    private fun instantiateDriver(): Either<CommandOutput.Error, Driver> {
        val boltUri = boltUriSupplier.getBoltUri(boshVms)
        if (boltUri == null) {
            val error = CommandErrors.noVmIpError
            return Either.Left(CommandOutput.Error(error.first, error.second(bindingId)))
        }
        val adminPassword = adminPasswordSupplier.getAdminPassword(manifest)
        if (adminPassword == null) {
            val error = CommandErrors.noPasswordError
            return Either.Left(CommandOutput.Error(error.first, error.second(bindingId)))
        }
        return Either.Right(driverSupplier.getDriver(boltUri, adminPassword))
    }

    private fun insertUser(driver: Either.Right<Driver>): CommandOutput {
        return if (credentialsRepository.exists(driver.value, bindingId)) {
            val error = CommandErrors.bindingAlreadyExistsError
            CommandOutput.Error(error.first, error.second(bindingId))
        } else CommandOutput.Standard(gson.toJson(credentialsRepository.save(driver.value, bindingId)))
    }

}


