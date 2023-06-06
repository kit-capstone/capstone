package com.abtwin.banlancegameex.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.abtwin.banlancegameex.databinding.FragmentAgeRangeBinding
import com.abtwin.banlancegameex.R
import com.abtwin.banlancegameex.contentsList.CountModel
import com.abtwin.banlancegameex.contentsList.GameInsideActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class AgeRangeFragment : Fragment() {
    private lateinit var binding: FragmentAgeRangeBinding
    private lateinit var count : CountModel
    private lateinit var opt1 : String
    private lateinit var opt2 : String
    var title : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_age_range, container, false)

        opt1 = arguments?.getString("opt1", "").toString()
        opt2 = arguments?.getString("opt2", "").toString()
        count = (arguments?.getSerializable("count") as? CountModel)?: CountModel()
        val ageStatistics = count.ageStatistics

        binding.option1.setText(opt1)
        binding.option2.setText(opt2)

        val barChart1 = binding.opt1Chart
        barChart1.setExtraOffsets(0f, 0f, 0f, 5f)
        barChart1.setPinchZoom(false)
        barChart1.setScaleEnabled(false)

        val ages1 = ArrayList<String>()
        ages1.add("10대")
        ages1.add("20대")
        ages1.add("30대")
        ages1.add("40대")
        ages1.add("50대 이상")

        val values = ArrayList<BarEntry>()
        values.add(BarEntry(0f, ageStatistics.teenager_opt1.toFloat()))
        values.add(BarEntry(1f, ageStatistics.twenties_opt1.toFloat()))
        values.add(BarEntry(2f, ageStatistics.thirties_opt1.toFloat()))
        values.add(BarEntry(3f, ageStatistics.fourties_opt1.toFloat()))
        values.add(BarEntry(4f, ageStatistics.fifties_opt1.toFloat()))

        val values2 = ArrayList<BarEntry>()
        values2.add(BarEntry(0.3f, ageStatistics.teenager_opt2.toFloat()))
        values2.add(BarEntry(1.3f, ageStatistics.twenties_opt2.toFloat()))
        values2.add(BarEntry(2.3f, ageStatistics.thirties_opt2.toFloat()))
        values2.add(BarEntry(3.3f, ageStatistics.fourties_opt2.toFloat()))
        values2.add(BarEntry(4.3f, ageStatistics.fifties_opt2.toFloat()))

        val barDataSet = BarDataSet(values, "Ages")
        barDataSet.color = Color.rgb(251,81,96)
        barDataSet.setDrawValues(false)

        val barDataSet2 = BarDataSet(values2, "Ages2")
        barDataSet2.color = Color.rgb(84,122,255)
        barDataSet2.setDrawValues(false)

        val data = BarData(barDataSet, barDataSet2)
        data.barWidth = 0.3f

        val xAxis = barChart1.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(ages1)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.textSize = 15f
        xAxis.setLabelCount(5)

        val leftAxis = barChart1.axisLeft
        leftAxis.axisMinimum = 0.3f
        leftAxis.setDrawGridLines(true)

        val rightAxis = barChart1.axisRight
        rightAxis.setDrawLabels(false)
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawGridLines(false)

        barChart1.setFitBars(true)
        barChart1.data = data
        barChart1.animateY(1000)
        barChart1.description.isEnabled = false
        barChart1.legend.isEnabled = false
        barChart1.invalidate()
        barChart1.groupBars(-0.5f, 0.4f, 0f)


        binding.genderTap.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("count", count)
                putString("opt1", opt1)
                putString("opt2", opt2)
            }

            (activity as GameInsideActivity).openFragment(2, bundle)
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