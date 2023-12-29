package com.example.findgarbagedump

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.findgarbagedump.databinding.ActivityMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.MapFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    data class GarbageDetails(
        val lat: Double,
        val lon: Double,
        val prv: String,
        val cty: String,
        val addr: String,
        val trs_dt: String,
        val f_trs_dt: String,
        val re_trs_dt: String,
        val trs_tm_s: String,
        val trs_tm_e: String,
        val f_trs_tm_s: String,
        val f_trs_tm_e: String,
        val re_trs_tm_s: String,
        val re_trs_tm_e: String,
        val non_dt: String,
        val office: String,
        val office_num: String,
        val plc_det: String
    )

    private val LOCATION_PERMISSION_REQUEST_CODE = 5000

    private val PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private lateinit var binding: ActivityMapBinding
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            initMapView()
        }
    }

    private fun initMapView() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun hasPermission(): Boolean {
        for (permission in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.uiSettings.isLocationButtonEnabled = true
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        // 네트워크 요청과 마커 추가를 진행
        loadAndAddMarkers()
    }

    private fun loadAndAddMarkers() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val locations = fetchDataFromApi()
                launch(Dispatchers.Main) {
                    // 마커 추가를 호출
                    addMarkersToMap(locations)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 예외 처리를 여기에 추가하세요.
            }
        }
    }

    private suspend fun fetchDataFromApi(): List<GarbageDetails> {
        var site =
            "https://apis.data.go.kr/4180000/cctrashloc/getLocationList?serviceKey=eADbE4y3ReKdtP0m7ZSr0BH9sRESFIw0VqcJskmthXp6JhyYBqEYC8qW5e0ZBE14FuHNqy1vipDQM%2BGwZZwvUg%3D%3D&numOfRows=50&pageNo=1"
        var url = URL(site)
        var conn = url.openConnection()
        var input = conn.getInputStream()
        var isr = InputStreamReader(input)
        var br = BufferedReader(isr)

        var str: String? = null
        var buf = StringBuffer()

        do {
            str = br.readLine()

            if (str != null) {
                buf.append(str)
            }
        } while (str != null)

        var root = JSONObject(buf.toString())
        var stores = root.getJSONArray("items")

        val garbageDetailsList = mutableListOf<GarbageDetails>()
        for (i in 0 until stores.length()) {
            var obj2 = stores.getJSONObject(i)

            // 위도, 경도
            var lat: Double = obj2.getString("ps_lat").toDouble()
            var lon: Double = obj2.getString("ps_lng").toDouble()

            // 도, 시, 동
            var prv: String = obj2.getString("prv")
            var cty: String = obj2.getString("cty")
            var addr: String = obj2.getString("addr")

            // 쓰레기장 정보
            var plc_det: String = obj2.getString("plc_det")

            // 일반쓰레기 수거일, 음식물 쓰레기 수거일, 재활용 쓰레기 수거일
            var trs_dt: String = obj2.getString("trs_dt")
            var f_trs_dt: String = obj2.getString("f_trs_dt")
            var re_trs_dt: String = obj2.getString("re_trs_dt")

            // 일반쓰레기 수거시간, 음식물 쓰레기 수거시간, 재활용 쓰레기 수거시간
            var trs_tm_s: String = obj2.getString("trs_tm_s")
            var trs_tm_e: String = obj2.getString("trs_tm_e")

            var f_trs_tm_s: String = obj2.getString("f_trs_tm_s")
            var f_trs_tm_e: String = obj2.getString("f_trs_tm_e")

            var re_trs_tm_s: String = obj2.getString("re_trs_tm_s")
            var re_trs_tm_e: String = obj2.getString("re_trs_tm_e")

            // 쉬는날
            var non_dt: String = obj2.getString("non_dt")

            // 관리 부서, 전화번호
            var office: String = obj2.getString("office")
            var office_num: String = obj2.getString("office_num")

            val garbageDetails = GarbageDetails(
                lat, lon, prv, cty, addr, trs_dt, f_trs_dt, re_trs_dt,
                trs_tm_s, trs_tm_e, f_trs_tm_s, f_trs_tm_e, re_trs_tm_s, re_trs_tm_e,
                non_dt, office, office_num, plc_det
            )

            garbageDetailsList.add(garbageDetails)
        }

        return garbageDetailsList
    }

    private fun addMarkersToMap(garbageDetailsList: List<GarbageDetails>) {
        garbageDetailsList.forEach { garbageDetails ->
            val marker = Marker()
            marker.position = LatLng(garbageDetails.lat, garbageDetails.lon)
            marker.map = naverMap

            // 마커 클릭 이벤트 리스너 추가
            marker.setOnClickListener {
                // 클릭한 마커의 정보를 전달하면서 com.example.findgarbagedump.InfoActivity 시작
                val intent = Intent(this@MapActivity, InfoActivity::class.java)
                intent.putExtra("latitude", garbageDetails.lat)
                intent.putExtra("longitude", garbageDetails.lon)

                // 세부 정보들을 추가로 전달
                intent.putExtra("prv", garbageDetails.prv)
                intent.putExtra("cty", garbageDetails.cty)
                intent.putExtra("addr", garbageDetails.addr)
                intent.putExtra("trs_dt", garbageDetails.trs_dt)
                intent.putExtra("f_trs_dt", garbageDetails.f_trs_dt)
                intent.putExtra("re_trs_dt", garbageDetails.re_trs_dt)
                intent.putExtra("trs_tm_s", garbageDetails.trs_tm_s)
                intent.putExtra("trs_tm_e", garbageDetails.trs_tm_e)
                intent.putExtra("f_trs_tm_s", garbageDetails.f_trs_tm_s)
                intent.putExtra("f_trs_tm_e", garbageDetails.f_trs_tm_e)
                intent.putExtra("re_trs_tm_s", garbageDetails.re_trs_tm_s)
                intent.putExtra("re_trs_tm_e", garbageDetails.re_trs_tm_e)
                intent.putExtra("non_dt", garbageDetails.non_dt)
                intent.putExtra("office", garbageDetails.office)
                intent.putExtra("office_num", garbageDetails.office_num)
                intent.putExtra("plc_det", garbageDetails.plc_det)

                startActivity(intent)
                false  // 클릭 이벤트를 소비하지 않고 계속 전파하도록 false로 변경
            }
        }
    }
}
