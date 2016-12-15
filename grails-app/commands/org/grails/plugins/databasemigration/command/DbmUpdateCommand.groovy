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
import liquibase.Liquibase
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class DbmUpdateCommand implements ApplicationCommand, ApplicationContextDatabaseMigrationCommand {
    private static final Logger log = LoggerFactory.getLogger(this);

    final String description = 'Updates a database to the current version'

    @Override
    void handle() {
        withLiquibase { Liquibase liquibase ->
            withTransaction {
                log.debug("Start update command (context: $contexts)...")
                liquibase.update(contexts)
                log.debug("Finish update command...")
            }
        }
    }
}
