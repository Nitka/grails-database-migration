/*
 * Copyright 2015 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.plugins.databasemigration.command

import grails.dev.commands.ApplicationCommand
import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import liquibase.Liquibase
import liquibase.database.Database

import java.nio.file.Path

@CompileStatic
trait AllTenantsMigrationCommand implements ApplicationCommand, ApplicationContextDatabaseMigrationCommand {

    void withLiquibaseForAllTenants(@ClosureParams(value = SimpleType, options = 'liquibase.Liquibase') Closure closure) {
        def resourceAccessor = createResourceAccessor()
        Path changeLogLocationPath = changeLogLocation.toPath()
        Path changeLogFilePath = changeLogFile.toPath()
        String relativePath = changeLogLocationPath.relativize(changeLogFilePath).toString()
        withDatabases { Database database ->
            def liquibase = new Liquibase(relativePath, resourceAccessor, database)
            closure.call(liquibase)
        }
    }

    void withDatabases(@ClosureParams(value = SimpleType, options = 'liquibase.database.Database') Closure closure) {
        Map<String, Map> dataSources = config.getProperty('dataSources', Map) ?: [:]
        List<String> dsKeys = dataSources.keySet().sort() //todo add better sorting
        dsKeys.each {
            withDatabase(dataSources[it], closure)
        }
    }

    @Override
    String getConfigPrefix() {
        "grails.plugin.databasemigration.tenants"
    }
}
