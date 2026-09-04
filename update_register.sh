#!/bin/bash
sed -i 's/tvPromoStatus.text = "Verifying..."/tvPromoStatus.text = getString(R.string.promo_verifying)/g' ./app/src/main/java/com/securitynav/security/ui/RegisterActivity.kt
sed -i 's/tvPromoStatus.text = "Success: \${body.message} (-\${body.discountValue}%)"/tvPromoStatus.text = getString(R.string.promo_success, body.message, body.discountValue)/g' ./app/src/main/java/com/securitynav/security/ui/RegisterActivity.kt
sed -i 's/tvPromoStatus.text = "Invalid Code: \${body.message}"/tvPromoStatus.text = getString(R.string.promo_invalid, body.message)/g' ./app/src/main/java/com/securitynav/security/ui/RegisterActivity.kt
sed -i 's/tvPromoStatus.text = "Server Error: Could not validate code"/tvPromoStatus.text = getString(R.string.promo_server_error)/g' ./app/src/main/java/com/securitynav/security/ui/RegisterActivity.kt
sed -i 's/tvPromoStatus.text = "Promo applied successfully! (Mock)"/tvPromoStatus.text = getString(R.string.promo_mock_success)/g' ./app/src/main/java/com/securitynav/security/ui/RegisterActivity.kt
