with open('app/src/main/java/com/securitynav/security/ui/VpnFragment.kt', 'r') as f:
    content = f.read()

content = content.replace('android.graphics.Color.parseColor("#00FFC2")', 'androidx.core.content.ContextCompat.getColor(requireContext(), com.securitynav.security.R.color.primary)')
content = content.replace('android.graphics.Color.parseColor("#FF0055")', 'androidx.core.content.ContextCompat.getColor(requireContext(), com.securitynav.security.R.color.error)')
content = content.replace('android.graphics.Color.parseColor("#B3B3B3")', 'androidx.core.content.ContextCompat.getColor(requireContext(), com.securitynav.security.R.color.text_secondary)')
content = content.replace('android.graphics.Color.parseColor("#39FF14")', 'androidx.core.content.ContextCompat.getColor(requireContext(), com.securitynav.security.R.color.success)')

with open('app/src/main/java/com/securitynav/security/ui/VpnFragment.kt', 'w') as f:
    f.write(content)
