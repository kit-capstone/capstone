package com.abtwin.banlancegameex.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.abtwin.banlancegameex.R
import com.abtwin.banlancegameex.contentsList.CountModel
import com.abtwin.banlancegameex.contentsList.GameInsideActivity
import com.abtwin.banlancegameex.databinding.FragmentLocateBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
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
        val regionStatistics = count.regionStatistics

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
        values.add(BarEntry(0f, regionStatistics.gyeonggi_opt1.toFloat()))
        values.add(BarEntry(1f, regionStatistics.gangwon_opt1.toFloat()))
        values.add(BarEntry(2f, regionStatistics.chungcheong_opt1.toFloat()))
        values.add(BarEntry(3f, regionStatistics.gyeongsang_opt1.toFloat()))
        values.add(BarEntry(4f, regionStatistics.jeolla_opt1.toFloat()))
        values.add(BarEntry(5f, regionStatistics.jeju_opt1.toFloat()))

        val values2 = ArrayList<BarEntry>()
        values2.add(BarEntry(0f, regionStatistics.gyeonggi_opt2.toFloat()))
        values2.add(BarEntry(1f, regionStatistics.gangwon_opt2.toFloat()))
        values2.add(BarEntry(2f, regionStatistics.chungcheong_opt2.toFloat()))
        values2.add(BarEntry(3f, regionStatistics.gyeongsang_opt2.toFloat()))
        values2.add(BarEntry(4f, regionStatistics.jeolla_opt2.toFloat()))
        values2.add(BarEntry(5f, regionStatistics.jeju_opt2.toFloat()))

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

        val gyeonggiEntries = mutableListOf<PieEntry>()
        gyeonggiEntries.add(PieEntry(regionStatistics.gyeonggi_opt1.toFloat(), ""))
        gyeonggiEntries.add(PieEntry(regionStatistics.gyeonggi_opt2.toFloat(), ""))

        val gangwonEntries = mutableListOf<PieEntry>()
        gangwonEntries.add(PieEntry(regionStatistics.gangwon_opt1.toFloat(), ""))
        gangwonEntries.add(PieEntry(regionStatistics.gangwon_opt2.toFloat(), ""))

        val chungcheongEntries = mutableListOf<PieEntry>()
        chungcheongEntries.add(PieEntry(regionStatistics.chungcheong_opt1.toFloat(), ""))
        chungcheongEntries.add(PieEntry(regionStatistics.chungcheong_opt2.toFloat(), ""))

        val gyeongsangEntries = mutableListOf<PieEntry>()
        gyeongsangEntries.add(PieEntry(regionStatistics.gyeongsang_opt1.toFloat(), ""))
        gyeongsangEntries.add(PieEntry(regionStatistics.gyeongsang_opt2.toFloat(), ""))

        val jeollaEntries = mutableListOf<PieEntry>()
        jeollaEntries.add(PieEntry(regionStatistics.jeolla_opt1.toFloat(), ""))
        jeollaEntries.add(PieEntry(regionStatistics.jeolla_opt2.toFloat(), ""))

        val jejuEntries = mutableListOf<PieEntry>()
        jejuEntries.add(PieEntry(regionStatistics.jeju_opt1.toFloat(), ""))
        jejuEntries.add(PieEntry(regionStatistics.jeju_opt2.toFloat(), ""))

        val gyeonggidataSet = PieDataSet(gyeonggiEntries, "경기")
        gyeonggidataSet.colors = listOf(Color.rgb(251,81,96), Color.rgb(84,122,255))

        val gangwondataSet = PieDataSet(gangwonEntries, "강원")
        gangwondataSet.colors = listOf(Color.rgb(251,81,96), Color.rgb(84,122,255))

        val chungcheongdataSet = PieDataSet(chungcheongEntries, "충청")
        chungcheongdataSet.colors = listOf(Color.rgb(251,81,96), Color.rgb(84,122,255))

        val gyeongsangdataSet = PieDataSet(gyeongsangEntries, "경상")
        gyeongsangdataSet.colors = listOf(Color.rgb(251,81,96), Color.rgb(84,122,255))

        val jeolladataSet = PieDataSet(jeollaEntries, "전라")
        jeolladataSet.colors = listOf(Color.rgb(251,81,96), Color.rgb(84,122,255))

        val jejudataSet = PieDataSet(jejuEntries, "제주")
        jejudataSet.colors = listOf(Color.rgb(251,81,96), Color.rgb(84,122,255))

        val gyeonggiPiedata = PieData(gyeonggidataSet)
        val gangwonPiedata = PieData(gangwondataSet)
        val chungcheongPiedata = PieData(chungcheongdataSet)
        val gyeongsangPiedata = PieData(gyeongsangdataSet)
        val jeollaPiedata = PieData(jeolladataSet)
        val jejuPiedata = PieData(jejudataSet)

        binding.gyeonggiChart.data = gyeonggiPiedata
        binding.gyeonggiChart.legend.isEnabled = false
        binding.gyeonggiChart.description.isEnabled = false
        binding.gyeonggiChart.setHoleColor(Color.GRAY)
        binding.gyeonggiChart.invalidate()

        binding.gangwonChart.data = gangwonPiedata
        binding.gangwonChart.legend.isEnabled = false
        binding.gangwonChart.description.isEnabled = false
        binding.gangwonChart.setHoleColor(Color.GRAY)
        binding.gangwonChart.invalidate()

        binding.chungcheongChart.data = chungcheongPiedata
        binding.chungcheongChart.legend.isEnabled = false
        binding.chungcheongChart.description.isEnabled = false
        binding.chungcheongChart.setHoleColor(Color.GRAY)
        binding.chungcheongChart.invalidate()

        binding.gyeongsangChart.data = gyeongsangPiedata
        binding.gyeongsangChart.legend.isEnabled = false
        binding.gyeongsangChart.description.isEnabled = false
        binding.gyeongsangChart.setHoleColor(Color.GRAY)
        binding.gyeongsangChart.invalidate()

        binding.jeollaChart.data = jeollaPiedata
        binding.jeollaChart.legend.isEnabled = false
        binding.jeollaChart.description.isEnabled = false
        binding.jeollaChart.setHoleColor(Color.GRAY)
        binding.jeollaChart.invalidate()

        binding.jejuChart.data = jejuPiedata
        binding.jejuChart.legend.isEnabled = false
        binding.jejuChart.description.isEnabled = false
        binding.jejuChart.setHoleColor(Color.GRAY)
        binding.jejuChart.invalidate()

        return binding.root
    }
}