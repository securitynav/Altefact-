#!/bin/bash
sed -i 's/net.zetetic.database.sqlcipher/net.sqlcipher.database/g' ./app/src/main/java/com/securitynav/security/data/db/SecurityDatabaseHelper.kt
