package com.example.banlancegameex.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.banlancegameex.R
import com.example.banlancegameex.contentsList.CountModel
import com.example.banlancegameex.contentsList.GameInsideActivity
import com.example.banlancegameex.databinding.FragmentLocateBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class LocateFragment : Fragment() {
    private lateinit var binding: FragmentLocateBinding
    private lateinit var count : CountModel
    private lateinit var opt1 : String
    private lateinit var opt2 : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_locate, container, false)

        opt1 = arguments?.getString("opt1", "").toString()
        opt2 = arguments?.getString("opt2", "").toString()
        count = (arguments?.getSerializable("count") as? CountModel)?: CountModel()

        binding.option1.setText(opt1)
        binding.option2.setText(opt2)

        val barChart1 = binding.opt1Chart
        barChart1.setExtraOffsets(0f, 0f, 0f, 5f)
        barChart1.setPinchZoom(false)
        barChart1.setScaleEnabled(false)

        val locate1 = ArrayList<String>()
        locate1.add("경기")
        locate1.add("강원")
        locate1.add("충청")
        locate1.add("경상")
        locate1.add("전라")
        locate1.add("제주")

        val values = ArrayList<BarEntry>()
        values.add(BarEntry(0f, count.gyeonggi_opt1.toFloat()))
        values.add(BarEntry(1f, count.gangwon_opt1.toFloat()))
        values.add(BarEntry(2f, count.chungcheong_opt1.toFloat()))
        values.add(BarEntry(3f, count.gyeongsang_opt1.toFloat()))
        values.add(BarEntry(4f, count.jeolla_opt1.toFloat()))
        values.add(BarEntry(5f, count.jeju_opt1.toFloat()))

        val values2 = ArrayList<BarEntry>()
        values2.add(BarEntry(0f, count.gyeonggi_opt2.toFloat()))
        values2.add(BarEntry(1f, count.gangwon_opt2.toFloat()))
        values2.add(BarEntry(2f, count.chungcheong_opt2.toFloat()))
        values2.add(BarEntry(3f, count.gyeongsang_opt2.toFloat()))
        values2.add(BarEntry(4f, count.jeolla_opt2.toFloat()))
        values2.add(BarEntry(5f, count.jeju_opt2.toFloat()))

        val barDataSet1 = BarDataSet(values, "Locate")
        barDataSet1.color = Color.rgb(251,81,96)
        barDataSet1.setDrawValues(false)

        val barDataSet2 = BarDataSet(values2, "Locate2")
        barDataSet2.color = Color.rgb(84,122,255)
        barDataSet2.setDrawValues(false)

        val data1 = BarData(barDataSet1, barDataSet2)
        data1.barWidth = 0.3f

        val xAxis = barChart1.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(locate1)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.textSize = 15f
        xAxis.setLabelCount(6)

        val leftAxis = barChart1.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.setDrawGridLines(true)

        val rightAxis = barChart1.axisRight
        rightAxis.setDrawLabels(false)
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawGridLines(false)

        barChart1.setFitBars(true)
        barChart1.data = data1
        barChart1.animateY(1000)
        barChart1.description.isEnabled = false
        barChart1.legend.isEnabled = false
        barChart1.invalidate()
        barChart1.groupBars(-0.5f, 0.4f, 0f)

        binding.ageTap.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("count", count)
                putString("opt1", opt1)
                putString("opt2", opt2)
            }

            (activity as GameInsideActivity).openFragment(1, bundle)
        }

        binding.genderTap.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("count", count)
                putString("opt1", opt1)
                putString("opt2", opt2)
            }

            (activity as GameInsideActivity).openFragment(2, bundle)
        }

        return binding.root
    }
}