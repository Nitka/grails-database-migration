dataSource {
	pooled = true
	driverClassName = 'org.h2.Driver'
	username = 'sa'
	password = ''
	url = 'jdbc:h2:mem:testDb'
	dialect = org.hibernate.dialect.H2Dialect
}

hibernate {
	cache.use_second_level_cache = false
	cache.use_query_cache = false
	cache.provider_class = 'org.hibernate.cache.EhCacheProvider'
}

environments {
	development {
		dataSource {
			url = 'jdbc:h2:tcp://localhost/./target/testdb/testdb'
		}

		dataSource_secondary {
			pooled = true
			driverClassName = 'org.h2.Driver'
			username = 'sa'
			password = ''
			url = 'jdbc:h2:tcp://localhost/./target/testdb/testdb-secondary'
			dialect = org.hibernate.dialect.H2Dialect
		}
	}
	test {
		dataSource {
			url = 'jdbc:h2:file:target/testdb'
		}

		dataSource_secondary {
			pooled = true
			driverClassName = 'org.h2.Driver'
			username = 'sa'
			password = ''
			url = 'jdbc:h2:file:target/testdb-secondary'
			dialect = org.hibernate.dialect.H2Dialect
		}
	}
	dbdiff {
		dataSource {
			url = 'jdbc:h2:file:target/dbdiff/dbdiff'
		}
	}
}
