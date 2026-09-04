import os

files = {
    "./app/src/main/res/layout/fragment_tuner.xml": """<?xml version="1.0" encoding="utf-8"?>
<androidx.core.widget.NestedScrollView 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/bg_light"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/tuner_title"
            android:textColor="@color/text_primary"
            android:textSize="20sp"
            android:textStyle="bold" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/tuner_header_desc"
            android:textColor="@color/text_secondary"
            android:textSize="13sp"
            android:layout_marginTop="2dp"
            android:layout_marginBottom="16dp" />

        <com.google.android.material.button.MaterialButtonToggleGroup
            android:id="@+id/toggleTierGroup"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:singleSelection="true"
            app:selectionRequired="true"
            android:layout_marginBottom="16dp">

            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnTierBasic"
                style="?attr/materialButtonOutlinedStyle"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="@string/tier_basic"
                android:textSize="12sp"
                android:textColor="@color/text_primary" />

            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnTierAdvanced"
                style="?attr/materialButtonOutlinedStyle"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="@string/tier_advanced"
                android:textSize="12sp"
                android:textColor="@color/text_primary" />

            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnTierPro"
                style="?attr/materialButtonOutlinedStyle"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="@string/tier_pro"
                android:textSize="12sp"
                android:textColor="@color/text_primary" />
        </com.google.android.material.button.MaterialButtonToggleGroup>

        <com.google.android.material.card.MaterialCardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:cardBackgroundColor="@color/surface_light"
            app:cardCornerRadius="14dp"
            app:strokeColor="@color/google_btn_stroke"
            app:strokeWidth="1dp"
            app:cardElevation="0dp"
            android:layout_marginBottom="16dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="16dp">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal"
                    android:gravity="center_vertical">

                    <TextView
                        android:id="@+id/tvTierName"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:text="Modo Básico"
                        android:textColor="@color/primary"
                        android:textSize="16sp"
                        android:textStyle="bold" />

                    <TextView
                        android:id="@+id/tvTierBadge"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Nivel 1 - Ahorro"
                        android:background="@drawable/bg_badge_secure"
                        android:textColor="@color/white"
                        android:textSize="10sp"
                        android:textStyle="bold"
                        android:paddingHorizontal="8dp"
                        android:paddingVertical="4dp" />
                </LinearLayout>

                <TextView
                    android:id="@+id/tvTierDescription"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Optimizado para uso diario, bajo consumo de batería y navegación fluida."
                    android:textColor="@color/text_secondary"
                    android:textSize="13sp"
                    android:layout_marginTop="6dp" />

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal"
                    android:layout_marginTop="10dp">

                    <TextView
                        android:id="@+id/tvTierLatency"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:text="Latencia: ~0.5ms"
                        android:textColor="@color/text_primary"
                        android:textSize="12sp"
                        android:textStyle="bold" />

                    <TextView
                        android:id="@+id/tvTierDNS"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="DNS: Cloudflare / Google"
                        android:textColor="@color/text_secondary"
                        android:textSize="11sp" />
                </LinearLayout>
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <LinearLayout
            android:id="@+id/layoutTierBasic"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:visibility="visible">

            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                app:cardBackgroundColor="@color/surface_light"
                app:cardCornerRadius="14dp"
                app:strokeColor="@color/google_btn_stroke"
                app:strokeWidth="1dp"
                app:cardElevation="0dp"
                android:layout_marginBottom="16dp">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="16dp">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Parámetros Básicos"
                        android:textColor="@color/text_primary"
                        android:textSize="15sp"
                        android:textStyle="bold" />

                    <TextView
                        android:id="@+id/tvBasicSensitivityLabel"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Sensibilidad de Monitoreo: 50%"
                        android:textColor="@color/text_secondary"
                        android:textSize="13sp"
                        android:layout_marginTop="12dp" />

                    <com.google.android.material.slider.Slider
                        android:id="@+id/sliderBasicSensitivity"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:valueFrom="10"
                        android:valueTo="100"
                        android:stepSize="5"
                        android:value="50"
                        app:thumbColor="@color/primary"
                        app:trackColorActive="@color/primary" />

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:gravity="center_vertical"
                        android:layout_marginTop="8dp">

                        <TextView
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:layout_weight="1"
                            android:text="Filtro DNS Seguro (Anti-Malware)"
                            android:textColor="@color/text_primary"
                            android:textSize="14sp" />

                        <com.google.android.material.materialswitch.MaterialSwitch
                            android:id="@+id/switchBasicDns"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:checked="true" />
                    </LinearLayout>

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:gravity="center_vertical"
                        android:layout_marginTop="8dp">

                        <TextView
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:layout_weight="1"
                            android:text="Modo Ecológico (Batería Óptima)"
                            android:textColor="@color/text_primary"
                            android:textSize="14sp" />

                        <com.google.android.material.materialswitch.MaterialSwitch
                            android:id="@+id/switchBasicEco"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:checked="true" />
                    </LinearLayout>

                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnApplyBasic"
                android:layout_width="match_parent"
                android:layout_height="48dp"
                android:text="@string/apply_tuner_config"
                android:textColor="@color/white"
                app:backgroundTint="@color/primary"
                app:cornerRadius="24dp" />
        </LinearLayout>

        <LinearLayout
            android:id="@+id/layoutTierAdvanced"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:visibility="gone">

            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                app:cardBackgroundColor="@color/surface_light"
                app:cardCornerRadius="14dp"
                app:strokeColor="@color/google_btn_stroke"
                app:strokeWidth="1dp"
                app:cardElevation="0dp"
                android:layout_marginBottom="16dp">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="16dp">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Parámetros Avanzados de Red"
                        android:textColor="@color/text_primary"
                        android:textSize="15sp"
                        android:textStyle="bold" />

                    <TextView
                        android:id="@+id/tvAdvBufferLabel"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Tamaño de Buffer de Socket: 32 KB"
                        android:textColor="@color/text_secondary"
                        android:textSize="13sp"
                        android:layout_marginTop="12dp" />

                    <com.google.android.material.slider.Slider
                        android:id="@+id/sliderAdvBuffer"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:valueFrom="16"
                        android:valueTo="64"
                        android:stepSize="8"
                        android:value="32"
                        app:thumbColor="#FBBC04"
                        app:trackColorActive="#FBBC04" />

                    <TextView
                        android:id="@+id/tvAdvMtuLabel"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="MTU Dinámico: 1420 bytes"
                        android:textColor="@color/text_secondary"
                        android:textSize="13sp"
                        android:layout_marginTop="8dp" />

                    <com.google.android.material.slider.Slider
                        android:id="@+id/sliderAdvMtu"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:valueFrom="1350"
                        android:valueTo="1500"
                        android:stepSize="10"
                        android:value="1420"
                        app:thumbColor="#FBBC04"
                        app:trackColorActive="#FBBC04" />

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:gravity="center_vertical"
                        android:layout_marginTop="8dp">

                        <TextView
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:layout_weight="1"
                            android:text="Inspección Profunda de Paquetes (DPI)"
                            android:textColor="@color/text_primary"
                            android:textSize="14sp" />

                        <com.google.android.material.materialswitch.MaterialSwitch
                            android:id="@+id/switchAdvDpi"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:checked="true" />
                    </LinearLayout>

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:gravity="center_vertical"
                        android:layout_marginTop="8dp">

                        <TextView
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:layout_weight="1"
                            android:text="DNS over HTTPS Seguro (Quad9)"
                            android:textColor="@color/text_primary"
                            android:textSize="14sp" />

                        <com.google.android.material.materialswitch.MaterialSwitch
                            android:id="@+id/switchAdvDoh"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:checked="true" />
                    </LinearLayout>

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:gravity="center_vertical"
                        android:layout_marginTop="8dp">

                        <TextView
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:layout_weight="1"
                            android:text="Detección de Escaneo de Puertos"
                            android:textColor="@color/text_primary"
                            android:textSize="14sp" />

                        <com.google.android.material.materialswitch.MaterialSwitch
                            android:id="@+id/switchAdvPortScan"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:checked="true" />
                    </LinearLayout>

                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnApplyAdvanced"
                android:layout_width="match_parent"
                android:layout_height="48dp"
                android:text="@string/apply_tuner_config"
                android:textColor="@color/white"
                app:backgroundTint="#FBBC04"
                app:cornerRadius="24dp" />
        </LinearLayout>

        <LinearLayout
            android:id="@+id/layoutTierPro"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:visibility="gone">

            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                app:cardBackgroundColor="@color/surface_light"
                app:cardCornerRadius="14dp"
                app:strokeColor="@color/google_btn_stroke"
                app:strokeWidth="1dp"
                app:cardElevation="0dp"
                android:layout_marginBottom="16dp">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="16dp">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Parámetros Pro / Kernel / Hardware"
                        android:textColor="@color/text_primary"
                        android:textSize="15sp"
                        android:textStyle="bold" />

                    <TextView
                        android:id="@+id/tvProBufferLabel"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Buffer Cero-Pérdida: 64 KB"
                        android:textColor="@color/text_secondary"
                        android:textSize="13sp"
                        android:layout_marginTop="12dp" />

                    <com.google.android.material.slider.Slider
                        android:id="@+id/sliderProBuffer"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:valueFrom="16"
                        android:valueTo="128"
                        android:stepSize="16"
                        android:value="64"
                        app:thumbColor="@color/secondary"
                        app:trackColorActive="@color/secondary" />

                    <TextView
                        android:id="@+id/tvProMtuLabel"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="MTU Manual Anti-Fragmentación: 1400 bytes"
                        android:textColor="@color/text_secondary"
                        android:textSize="13sp"
                        android:layout_marginTop="8dp" />

                    <com.google.android.material.slider.Slider
                        android:id="@+id/sliderProMtu"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:valueFrom="1280"
                        android:valueTo="1500"
                        android:stepSize="10"
                        android:value="1400"
                        app:thumbColor="@color/secondary"
                        app:trackColorActive="@color/secondary" />

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:gravity="center_vertical"
                        android:layout_marginTop="8dp">

                        <TextView
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:layout_weight="1"
                            android:text="Aceleración Hardware ARMv8 / AES-NI"
                            android:textColor="@color/text_primary"
                            android:textSize="14sp" />

                        <com.google.android.material.materialswitch.MaterialSwitch
                            android:id="@+id/switchProHwCrypto"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:checked="true" />
                    </LinearLayout>

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:gravity="center_vertical"
                        android:layout_marginTop="8dp">

                        <TextView
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:layout_weight="1"
                            android:text="Modo Stealth Tunnel (Ofuscación ISP)"
                            android:textColor="@color/text_primary"
                            android:textSize="14sp" />

                        <com.google.android.material.materialswitch.MaterialSwitch
                            android:id="@+id/switchProStealth"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:checked="true" />
                    </LinearLayout>

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:gravity="center_vertical"
                        android:layout_marginTop="8dp">

                        <TextView
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:layout_weight="1"
                            android:text="Defensa Anti-Root &amp; Anti-Hooking"
                            android:textColor="@color/text_primary"
                            android:textSize="14sp" />

                        <com.google.android.material.materialswitch.MaterialSwitch
                            android:id="@+id/switchProAntiRoot"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:checked="true" />
                    </LinearLayout>

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:gravity="center_vertical"
                        android:layout_marginTop="8dp">

                        <TextView
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:layout_weight="1"
                            android:text="Prioridad Cero-Latencia Apps Almacén"
                            android:textColor="@color/text_primary"
                            android:textSize="14sp" />

                        <com.google.android.material.materialswitch.MaterialSwitch
                            android:id="@+id/switchProPriorityApps"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:checked="true" />
                    </LinearLayout>

                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnApplyPro"
                android:layout_width="match_parent"
                android:layout_height="48dp"
                android:text="@string/apply_tuner_config"
                android:textColor="@color/white"
                app:backgroundTint="@color/secondary"
                app:cornerRadius="24dp" />
        </LinearLayout>

    </LinearLayout>
</androidx.core.widget.NestedScrollView>""",

    "./app/src/main/java/com/securitynav/security/ui/TunerFragment.kt": """package com.securitynav.security.ui

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
}"""
}

for path, content in files.items():
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        f.write(content)
