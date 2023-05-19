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
import com.example.banlancegameex.databinding.FragmentGenderBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class GenderFragment : Fragment() {
    private lateinit var binding: FragmentGenderBinding
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gender, container, false)

        opt1 = arguments?.getString("opt1", "").toString()
        opt2 = arguments?.getString("opt2", "").toString()
        count = (arguments?.getSerializable("count") as? CountModel)?: CountModel()

        binding.option1.setText(opt1)
        binding.option2.setText(opt2)

        val barChart1 = binding.opt1Chart
        barChart1.setExtraOffsets(0f, 0f, 0f, 5f)
        barChart1.setPinchZoom(false)
        barChart1.setScaleEnabled(false)

        val gender1 = ArrayList<String>()
        gender1.add("남성")
        gender1.add("여성")

        val values1 = ArrayList<BarEntry>()
        values1.add(BarEntry(0f, count.man_opt1.toFloat()))
        values1.add(BarEntry(1f, count.woman_opt1.toFloat()))

        val values2 = ArrayList<BarEntry>()
        values2.add(BarEntry(0.6f, count.man_opt2.toFloat()))
        values2.add(BarEntry(1.6f, count.woman_opt2.toFloat()))

        val barDataSet1 = BarDataSet(values1, "Gender")
        barDataSet1.color = Color.rgb(251,81,96)
        barDataSet1.setDrawValues(false)

        val barDataSet2 = BarDataSet(values2, "Gender2")
        barDataSet2.color = Color.rgb(84,122,255)
        barDataSet2.setDrawValues(false)

        val data1 = BarData(barDataSet1, barDataSet2)
        data1.barWidth = 0.3f

        val xAxis = barChart1.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(gender1)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1.3f
        xAxis.isGranularityEnabled = true
        xAxis.textSize = 15f
        xAxis.setLabelCount(2)

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
        barChart1.groupBars(-0.6f, 0.65f, 0f)

        binding.ageTap.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("count", count)
                putString("opt1", opt1)
                putString("opt2", opt2)
            }

            (activity as GameInsideActivity).openFragment(1, bundle)
        }

        binding.locateTap.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("count", count)
                putString("opt1", opt1)
                putString("opt2", opt2)
            }

            (activity as GameInsideActivity).openFragment(3, bundle)
        }

        return binding.root
    }
}