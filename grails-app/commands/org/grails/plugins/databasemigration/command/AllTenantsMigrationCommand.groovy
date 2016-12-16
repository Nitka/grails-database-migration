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
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Path

@CompileStatic
trait AllTenantsMigrationCommand implements ApplicationCommand, ApplicationContextDatabaseMigrationCommand {
    private static final Logger log = LoggerFactory.getLogger(this);

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
        List<String> dsKeys = dataSources.keySet().sort()

        def field = config.getProperty("${configPrefix}.filterField", String)
        if (hasOption('include')) {
            List<String> include = optionValue('include').split(",").toList()
            log.debug("Include only the following tenants: ${include.join(", ")}")
            if (field) {
                log.debug("Include tenants by $field")
                dsKeys.removeAll {
                    String fieldValue = dataSources[it][field]
                    !fieldValue || !(fieldValue in include)
                }
            } else {
                dsKeys.removeAll { !(it in include) }
            }
        } else if (hasOption('exclude')) {
            List<String> exclude = optionValue('exclude').split(",").toList()
            log.debug("Exclude the following tenants: ${exclude.join(", ")}")
            if (field) {
                log.debug("Exclude tenants by $field")
                dsKeys.removeAll {
                    String fieldValue = dataSources[it][field]
                    fieldValue && (fieldValue in exclude)
                }
            } else {
                dsKeys.removeAll { it in exclude }
            }
        }

        List<String> firstDataSources = config.getProperty("${configPrefix}.firstDataSources", List)
        if (firstDataSources) {
            log.debug("First dataSources to run against: ${firstDataSources.join(", ")}")
            firstDataSources.eachWithIndex { String name, int index ->
                if (dsKeys.remove(name)) {
                    dsKeys.add(index, name)
                }
            }
        }

        int total = dsKeys.size()
        dsKeys.eachWithIndex { String key, int index ->
            String fieldValue = field ? (dataSources[key][field] ?: "") : ""
            log.debug("Start executing command for tenant #$key $fieldValue (${index + 1}/$total)")
            withDatabase(dataSources[key], closure)
            log.debug("Finish executing command for tenant #$key $fieldValue (${index + 1}/$total)" as String)
        }
    }

    @Override
    String getConfigPrefix() {
        "grails.plugin.databasemigration.tenants"
    }
}
