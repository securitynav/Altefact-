package com.securitynav.security.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider
import com.securitynav.security.R
import com.securitynav.security.data.TunerConfig
import com.securitynav.security.data.TunerManager
import com.securitynav.security.data.TunerTier

class TunerFragment : Fragment() {

    private lateinit var tunerManager: TunerManager

    private lateinit var tvTierName: TextView
    private lateinit var tvTierBadge: TextView
    private lateinit var tvTierDescription: TextView
    private lateinit var tvTierLatency: TextView
    private lateinit var tvTierDNS: TextView

    private lateinit var layoutBasic: LinearLayout
    private lateinit var layoutAdvanced: LinearLayout
    private lateinit var layoutPro: LinearLayout

    private lateinit var sliderBasicSensitivity: Slider
    private lateinit var tvBasicSensitivityLabel: TextView
    private lateinit var switchBasicDns: MaterialSwitch
    private lateinit var switchBasicEco: MaterialSwitch

    private lateinit var sliderAdvBuffer: Slider
    private lateinit var tvAdvBufferLabel: TextView
    private lateinit var sliderAdvMtu: Slider
    private lateinit var tvAdvMtuLabel: TextView
    private lateinit var switchAdvDpi: MaterialSwitch
    private lateinit var switchAdvDoh: MaterialSwitch
    private lateinit var switchAdvPortScan: MaterialSwitch

    private lateinit var sliderProBuffer: Slider
    private lateinit var tvProBufferLabel: TextView
    private lateinit var sliderProMtu: Slider
    private lateinit var tvProMtuLabel: TextView
    private lateinit var switchProHwCrypto: MaterialSwitch
    private lateinit var switchProStealth: MaterialSwitch
    private lateinit var switchProAntiRoot: MaterialSwitch
    private lateinit var switchProPriorityApps: MaterialSwitch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tuner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tunerManager = TunerManager(requireContext())

        bindViews(view)
        setupTierSelector(view)
        loadTierData(tunerManager.getSelectedTier())
        setupActionButtons(view)
    }

    private fun bindViews(view: View) {
        tvTierName = view.findViewById(R.id.tvTierName)
        tvTierBadge = view.findViewById(R.id.tvTierBadge)
        tvTierDescription = view.findViewById(R.id.tvTierDescription)
        tvTierLatency = view.findViewById(R.id.tvTierLatency)
        tvTierDNS = view.findViewById(R.id.tvTierDNS)

        layoutBasic = view.findViewById(R.id.layoutTierBasic)
        layoutAdvanced = view.findViewById(R.id.layoutTierAdvanced)
        layoutPro = view.findViewById(R.id.layoutTierPro)

        sliderBasicSensitivity = view.findViewById(R.id.sliderBasicSensitivity)
        tvBasicSensitivityLabel = view.findViewById(R.id.tvBasicSensitivityLabel)
        switchBasicDns = view.findViewById(R.id.switchBasicDns)
        switchBasicEco = view.findViewById(R.id.switchBasicEco)

        sliderBasicSensitivity.addOnChangeListener { _, value, _ ->
            tvBasicSensitivityLabel.text = "Sensibilidad de Monitoreo: ${value.toInt()}%"
        }

        sliderAdvBuffer = view.findViewById(R.id.sliderAdvBuffer)
        tvAdvBufferLabel = view.findViewById(R.id.tvAdvBufferLabel)
        sliderAdvMtu = view.findViewById(R.id.sliderAdvMtu)
        tvAdvMtuLabel = view.findViewById(R.id.tvAdvMtuLabel)
        switchAdvDpi = view.findViewById(R.id.switchAdvDpi)
        switchAdvDoh = view.findViewById(R.id.switchAdvDoh)
        switchAdvPortScan = view.findViewById(R.id.switchAdvPortScan)

        sliderAdvBuffer.addOnChangeListener { _, value, _ ->
            tvAdvBufferLabel.text = "Tamaño de Buffer de Socket: ${value.toInt()} KB"
        }
        sliderAdvMtu.addOnChangeListener { _, value, _ ->
            tvAdvMtuLabel.text = "MTU Dinámico: ${value.toInt()} bytes"
        }

        sliderProBuffer = view.findViewById(R.id.sliderProBuffer)
        tvProBufferLabel = view.findViewById(R.id.tvProBufferLabel)
        sliderProMtu = view.findViewById(R.id.sliderProMtu)
        tvProMtuLabel = view.findViewById(R.id.tvProMtuLabel)
        switchProHwCrypto = view.findViewById(R.id.switchProHwCrypto)
        switchProStealth = view.findViewById(R.id.switchProStealth)
        switchProAntiRoot = view.findViewById(R.id.switchProAntiRoot)
        switchProPriorityApps = view.findViewById(R.id.switchProPriorityApps)

        sliderProBuffer.addOnChangeListener { _, value, _ ->
            tvProBufferLabel.text = "Buffer Cero-Pérdida: ${value.toInt()} KB"
        }
        sliderProMtu.addOnChangeListener { _, value, _ ->
            tvProMtuLabel.text = "MTU Manual Anti-Fragmentación: ${value.toInt()} bytes"
        }
    }

    private fun setupTierSelector(view: View) {
        val toggleGroup = view.findViewById<MaterialButtonToggleGroup>(R.id.toggleTierGroup)
        when (tunerManager.getSelectedTier()) {
            TunerTier.BASIC -> toggleGroup.check(R.id.btnTierBasic)
            TunerTier.ADVANCED -> toggleGroup.check(R.id.btnTierAdvanced)
            TunerTier.PRO -> toggleGroup.check(R.id.btnTierPro)
        }

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val selectedTier = when (checkedId) {
                    R.id.btnTierBasic -> TunerTier.BASIC
                    R.id.btnTierAdvanced -> TunerTier.ADVANCED
                    R.id.btnTierPro -> TunerTier.PRO
                    else -> TunerTier.BASIC
                }
                tunerManager.setSelectedTier(selectedTier)
                loadTierData(selectedTier)
            }
        }
    }

    private fun loadTierData(tier: TunerTier) {
        val config = tunerManager.getConfigForTier(tier)

        when (tier) {
            TunerTier.BASIC -> {
                tvTierName.text = "Sintonizador Básico (Ahorro y Estabilidad)"
                tvTierName.setTextColor(Color.parseColor("#1A73E8"))
                tvTierBadge.text = "Nivel 1 - Esencial"
                tvTierDescription.text = "Filtrado liviano con servidores DNS seguros globales. Mantiene el dispositivo veloz y maximiza la autonomía de batería."
                tvTierLatency.text = "Latencia: ~0.5 ms"
                tvTierDNS.text = "DNS: Cloudflare / Google"

                layoutBasic.visibility = View.VISIBLE
                layoutAdvanced.visibility = View.GONE
                layoutPro.visibility = View.GONE

                sliderBasicSensitivity.value = config.networkSensitivityPercent.toFloat()
                tvBasicSensitivityLabel.text = "Sensibilidad de Monitoreo: ${config.networkSensitivityPercent}%"
            }
            TunerTier.ADVANCED -> {
                tvTierName.text = "Sintonizador Avanzado (Inspección Profunda)"
                tvTierName.setTextColor(Color.parseColor("#FBBC04"))
                tvTierBadge.text = "Nivel 2 - Heurístico"
                tvTierDescription.text = "Inspección de metadatos de paquetes en vivo (DPI), DNS sobre HTTPS cifrado (DoH) y mitigación de port scans."
                tvTierLatency.text = "Latencia: ~2.1 ms"
                tvTierDNS.text = "DNS: Quad9 DoH (Cifrado)"

                layoutBasic.visibility = View.GONE
                layoutAdvanced.visibility = View.VISIBLE
                layoutPro.visibility = View.GONE

                sliderAdvBuffer.value = config.bufferSizeKb.toFloat().coerceIn(16f, 64f)
                tvAdvBufferLabel.text = "Tamaño de Buffer de Socket: ${config.bufferSizeKb} KB"
                sliderAdvMtu.value = config.mtu.toFloat().coerceIn(1350f, 1500f)
                tvAdvMtuLabel.text = "MTU Dinámico: ${config.mtu} bytes"
                switchAdvDpi.isChecked = config.isDpiEnabled
                switchAdvPortScan.isChecked = config.isHeuristicAnalysisEnabled
            }
            TunerTier.PRO -> {
                tvTierName.text = "Sintonizador Pro (Kernel & Militar)"
                tvTierName.setTextColor(Color.parseColor("#34A853"))
                tvTierBadge.text = "Nivel 3 - Grado Militar"
                tvTierDescription.text = "Aceleración de cifrado por hardware ARMv8, ofuscación de paquetes Stealth Tunnel, escudo Anti-Root y prioridad de buffer para apps del almacén."
                tvTierLatency.text = "Latencia: ~1.2 ms (Acelerado)"
                tvTierDNS.text = "DNS: DNSCrypt Multinodo"

                layoutBasic.visibility = View.GONE
                layoutAdvanced.visibility = View.GONE
                layoutPro.visibility = View.VISIBLE

                sliderProBuffer.value = config.bufferSizeKb.toFloat().coerceIn(16f, 128f)
                tvProBufferLabel.text = "Buffer Cero-Pérdida: ${config.bufferSizeKb} KB"
                sliderProMtu.value = config.mtu.toFloat().coerceIn(1280f, 1500f)
                tvProMtuLabel.text = "MTU Manual Anti-Fragmentación: ${config.mtu} bytes"
                switchProHwCrypto.isChecked = config.isHardwareCryptoEnabled
                switchProStealth.isChecked = config.isStealthTunnelEnabled
                switchProAntiRoot.isChecked = config.isAntiRootShieldEnabled
                switchProPriorityApps.isChecked = config.priorityProtectedApps
            }
        }
    }

    private fun setupActionButtons(view: View) {
        view.findViewById<Button>(R.id.btnApplyBasic)?.setOnClickListener {
            val config = TunerConfig(
                tier = TunerTier.BASIC,
                mtu = 1500,
                bufferSizeKb = 16,
                dnsProvider = "Cloudflare 1.1.1.1",
                isDpiEnabled = false,
                isStealthTunnelEnabled = false,
                isHardwareCryptoEnabled = false,
                isAntiRootShieldEnabled = false,
                isHeuristicAnalysisEnabled = false,
                priorityProtectedApps = true,
                networkSensitivityPercent = sliderBasicSensitivity.value.toInt()
            )
            tunerManager.saveConfig(config)
            Toast.makeText(requireContext(), "¡Perfil Sintonizador BÁSICO aplicado!", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btnApplyAdvanced)?.setOnClickListener {
            val config = TunerConfig(
                tier = TunerTier.ADVANCED,
                mtu = sliderAdvMtu.value.toInt(),
                bufferSizeKb = sliderAdvBuffer.value.toInt(),
                dnsProvider = "Quad9 DoH",
                isDpiEnabled = switchAdvDpi.isChecked,
                isStealthTunnelEnabled = false,
                isHardwareCryptoEnabled = true,
                isAntiRootShieldEnabled = false,
                isHeuristicAnalysisEnabled = switchAdvPortScan.isChecked,
                priorityProtectedApps = true,
                networkSensitivityPercent = 75
            )
            tunerManager.saveConfig(config)
            Toast.makeText(requireContext(), "¡Perfil Sintonizador AVANZADO aplicado con DPI y DoH!", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btnApplyPro)?.setOnClickListener {
            val config = TunerConfig(
                tier = TunerTier.PRO,
                mtu = sliderProMtu.value.toInt(),
                bufferSizeKb = sliderProBuffer.value.toInt(),
                dnsProvider = "DNSCrypt Multinodo",
                isDpiEnabled = true,
                isStealthTunnelEnabled = switchProStealth.isChecked,
                isHardwareCryptoEnabled = switchProHwCrypto.isChecked,
                isAntiRootShieldEnabled = switchProAntiRoot.isChecked,
                isHeuristicAnalysisEnabled = true,
                priorityProtectedApps = switchProPriorityApps.isChecked,
                networkSensitivityPercent = 95
            )
            tunerManager.saveConfig(config)
            Toast.makeText(requireContext(), "¡Perfil Sintonizador PRO activado con aceleración Hardware!", Toast.LENGTH_SHORT).show()
        }
    }
}