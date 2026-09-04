import re

with open("app/src/main/java/com/securitynav/security/billing/PaywallBottomSheet.kt", "r") as f:
    content = f.read()

content = content.replace("subscriptionManager: SubscriptionManager", "subscriptionManager: SubscriptionManager = SubscriptionManager")

with open("app/src/main/java/com/securitynav/security/billing/PaywallBottomSheet.kt", "w") as f:
    f.write(content)
