with open("app/src/main/java/com/securitynav/security/engine/DataLeakDetector.kt", "r") as f:
    content = f.read()

content = content.replace("""    init {
        // Initialize with some mock data for the dashboard
        _leaks.value = listOf(
            LeakEvent("1", LeakVector.APP, "com.fake.app", "Contacts: John Doe (+123456)"),
            LeakEvent("2", LeakVector.ROGUE_TOWER, "Cell ID: 8392 (Spoofed)", "Downgrade 4G -> 2G"),
            LeakEvent("3", LeakVector.NETWORK_DNS, "DNS: 45.33.x.x", "Query: api.bank.com"),
            LeakEvent("4", LeakVector.MALWARE, "ScreenReader Accessibility", "Keylog: Password123")
        )
    }""", "")

with open("app/src/main/java/com/securitynav/security/engine/DataLeakDetector.kt", "w") as f:
    f.write(content)
