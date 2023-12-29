package com.example.findgarbagedump

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.findgarbagedump.databinding.ActivityDataBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class DataActivity : AppCompatActivity() {
        private lateinit var binding: ActivityDataBinding

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                binding = ActivityDataBinding.inflate(layoutInflater)
                setContentView(binding.root)

                binding.textView.text = ""

                // 버튼을 누르면 쓰레드 동작
                binding.button.setOnClickListener {
                        // 쓰레드 생성
                        var thread = NetworkThread()
                        thread.start()
                }
        }

        // 네트워크를 이용할 때는 쓰레드를 사용해서 접근해야 함
        inner class NetworkThread : Thread() {
                override fun run() {
                        // 접속할 페이지 주소: Site
                        var site = "https://apis.data.go.kr/4180000/cctrashloc/getLocationList?serviceKey=eADbE4y3ReKdtP0m7ZSr0BH9sRESFIw0VqcJskmthXp6JhyYBqEYC8qW5e0ZBE14FuHNqy1vipDQM%2BGwZZwvUg%3D%3D&numOfRows=5&pageNo=1"
                        var url = URL(site)
                        var conn = url.openConnection()
                        var input = conn.getInputStream()
                        var isr = InputStreamReader(input)
                        // br: 라인 단위로 데이터를 읽어오기 위해서 만듦
                        var br = BufferedReader(isr)

                        // Json 문서는 일단 문자열로 데이터를 모두 읽어온 후, Json에 관련된 객체를 만들어서 데이터를 가져옴
                        var str: String? = null
                        var buf = StringBuffer()

                        do {
                                str = br.readLine()

                                if (str != null) {
                                        buf.append(str)
                                }
                        } while (str != null)

                        // 전체가 객체로 묶여있기 때문에 객체형태로 가져옴
                        var root = JSONObject(buf.toString())

                        // 화면에 출력
                        runOnUiThread {
                                // 객체 안에 있는 items라는 이름의 리스트를 가져옴
                                var stores = root.getJSONArray("items")

                                // 리스트에 있는 데이터를 읽어옴
                                for (i in 0 until stores.length()) {
                                        var obj2 = stores.getJSONObject(i)

                                        var lat: String = obj2.getString("ps_lat")
                                        var lon: String = obj2.getString("ps_lng")

                                        // 화면에 출력
                                        runOnUiThread {
                                                binding.textView.append("위도: ${lat}\n")
                                                binding.textView.append("경도: ${lon}\n\n")
                                        }
                                }
                        }
                }
        }
}
