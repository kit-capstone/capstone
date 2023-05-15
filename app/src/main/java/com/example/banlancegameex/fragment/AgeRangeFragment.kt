package com.example.banlancegameex.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.banlancegameex.R
import com.example.banlancegameex.contentsList.CountModel
import com.example.banlancegameex.contentsList.GameInsideActivity
import com.example.banlancegameex.databinding.FragmentAgeRangeBinding
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

        binding.option1.setText(opt1)
        binding.option2.setText(opt2)

        val barChart1 = binding.opt1Chart
        val barChart2 = binding.opt2Chart

        val ages1 = ArrayList<String>()
        ages1.add("10대")
        ages1.add("20대")
        ages1.add("30대")
        ages1.add("40대")
        ages1.add("50대 이상")

        val ages2 = ArrayList<String>()
        ages2.add("10대")
        ages2.add("20대")
        ages2.add("30대")
        ages2.add("40대")
        ages2.add("50대 이상")


        val values = ArrayList<BarEntry>()
        values.add(BarEntry(0f, count.teenager_op1.toFloat()))
        values.add(BarEntry(1f, count.twenties_opt1.toFloat()))
        values.add(BarEntry(2f, count.thirties_opt1.toFloat()))
        values.add(BarEntry(3f, count.fourties_opt1.toFloat()))
        values.add(BarEntry(4f, count.fifties_opt1.toFloat()))

        val values2 = ArrayList<BarEntry>()
        values2.add(BarEntry(0f, count.teenager_op2.toFloat()))
        values2.add(BarEntry(1f, count.twenties_opt2.toFloat()))
        values2.add(BarEntry(2f, count.thirties_opt2.toFloat()))
        values2.add(BarEntry(3f, count.fourties_opt2.toFloat()))
        values2.add(BarEntry(4f, count.fifties_opt2.toFloat()))

        val barDataSet = BarDataSet(values, "Ages")
        barDataSet.color = R.color.tilte_blue

        val barDataSet2 = BarDataSet(values2, "Ages")
        barDataSet2.color = R.color.tilte_blue

        val data = BarData(barDataSet)
        data.barWidth = 0.9f

        val data2 = BarData(barDataSet2)
        data2.barWidth = 0.9f

        val xAxis = barChart1.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(ages1)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true

        val xAxis2 = barChart2.xAxis
        xAxis2.valueFormatter = IndexAxisValueFormatter(ages2)
        xAxis2.position = XAxis.XAxisPosition.BOTTOM
        xAxis2.setDrawGridLines(false)
        xAxis2.setDrawAxisLine(true)
        xAxis2.granularity = 1f
        xAxis2.isGranularityEnabled = true

        val leftAxis = barChart1.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.setDrawGridLines(true)

        val leftAxis2 = barChart2.axisLeft
        leftAxis2.axisMinimum = 0f
        leftAxis2.setDrawGridLines(true)

        val rightAxis = barChart1.axisRight
        rightAxis.setDrawLabels(false)
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawGridLines(false)

        val rightAxis2 = barChart2.axisRight
        rightAxis2.setDrawLabels(false)
        rightAxis2.setDrawAxisLine(false)
        rightAxis2.setDrawGridLines(false)

        barChart1.setFitBars(true)
        barChart1.data = data
        barChart1.animateY(1000)
        barChart1.description.isEnabled = false
        barChart1.legend.isEnabled = false
        barChart1.invalidate()

        barChart2.setFitBars(true)
        barChart2.data = data2
        barChart2.animateY(1000)
        barChart2.description.isEnabled = false
        barChart2.legend.isEnabled = false
        barChart2.invalidate()


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