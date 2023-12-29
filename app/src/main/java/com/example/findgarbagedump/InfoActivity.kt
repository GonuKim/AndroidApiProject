package com.example.findgarbagedump

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.findgarbagedump.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)


        val prv = intent.getStringExtra("prv") ?: ""
        val cty = intent.getStringExtra("cty") ?: ""
        val addr = intent.getStringExtra("addr") ?: ""
        val trsDt = intent.getStringExtra("trs_dt") ?: ""
        val fTrsDt = intent.getStringExtra("f_trs_dt") ?: ""
        val reTrsDt = intent.getStringExtra("re_trs_dt") ?: ""
        val trsTmS = intent.getStringExtra("trs_tm_s") ?: ""
        val trsTmE = intent.getStringExtra("trs_tm_e") ?: ""
        val fTrsTmS = intent.getStringExtra("f_trs_tm_s") ?: ""
        val fTrsTmE = intent.getStringExtra("f_trs_tm_e") ?: ""
        val reTrsTmS = intent.getStringExtra("re_trs_tm_s") ?: ""
        val reTrsTmE = intent.getStringExtra("re_trs_tm_e") ?: ""
        val nonDt = intent.getStringExtra("non_dt") ?: ""
        val office = intent.getStringExtra("office") ?: ""
        val officeNum = intent.getStringExtra("office_num") ?: ""
        val plcDet = intent.getStringExtra("plc_det") ?: ""



        binding.textViewInfo.append("위도: $latitude\n")
        binding.textViewInfo.append("경도: $longitude\n")

        binding.textViewInfo.append("쓰레기장 이름: $plcDet\n")

        binding.textViewInfo.append("주소: $prv $cty $addr\n")
        binding.textViewInfo.append("일반쓰레기 버리는 날: $trsDt\n")
        binding.textViewInfo.append("$trsTmS 부터 $trsTmE 까지\n")
        binding.textViewInfo.append("음식물쓰레기 버리는 날: $fTrsDt\n")
        binding.textViewInfo.append("$fTrsTmS 부터 $fTrsTmE 까지\n")
        binding.textViewInfo.append("재활용쓰레기 버리는 날: $reTrsDt\n")
        binding.textViewInfo.append("$reTrsTmS 부터 $reTrsTmE 까지\n")
        binding.textViewInfo.append("쉬는날 $nonDt\n")
        binding.textViewInfo.append("관리부서 $office $officeNum\n")


    }
}
