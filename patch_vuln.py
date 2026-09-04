import re

with open("app/src/main/java/com/securitynav/security/ui/VulnerabilityDashboardFragment.kt", "r") as f:
    content = f.read()

# Change how Paywall is shown. We need to add state for it.
new_compose = """
@Composable
fun VulnerabilityDashboardScreen(onPaywallRequested: () -> Unit) {
    val isPro by SubscriptionManager.isProUser.collectAsState(initial = false)
    val leaks by DataLeakDetector.leaks.collectAsState(initial = emptyList())
    var showPaywall by remember { mutableStateOf(false) }

    if (showPaywall) {
        PaywallBottomSheet(
            onDismiss = { showPaywall = false },
            subscriptionManager = SubscriptionManager
        )
    }

    LazyColumn(
"""
content = re.sub(r'@Composable\s*fun VulnerabilityDashboardScreen.*?LazyColumn\(', new_compose.strip() + '(', content, flags=re.DOTALL)

# Also update the original call to onPaywallRequested if we aren't using it anymore
content = content.replace('onPaywallRequested()', 'showPaywall = true')
# We need to make sure the fragment call doesn't error
content = content.replace(
    'VulnerabilityDashboardScreen(\n                        onPaywallRequested = {\n                            PaywallBottomSheet().show(parentFragmentManager, "Paywall")\n                        }\n                    )',
    'VulnerabilityDashboardScreen(onPaywallRequested = {})'
)

with open("app/src/main/java/com/securitynav/security/ui/VulnerabilityDashboardFragment.kt", "w") as f:
    f.write(content)
