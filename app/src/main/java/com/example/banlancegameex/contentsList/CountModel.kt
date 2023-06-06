package com.example.banlancegameex.contentsList

import java.io.Serializable

data class AgeStatistics(
    var teenager_opt1: Int = 0,
    var twenties_opt1: Int = 0,
    var thirties_opt1: Int = 0,
    var fourties_opt1: Int = 0,
    var fifties_opt1: Int = 0,

    var teenager_opt2: Int = 0,
    var twenties_opt2: Int = 0,
    var thirties_opt2: Int = 0,
    var fourties_opt2: Int = 0,
    var fifties_opt2: Int = 0
)

data class GenderStatistics(
    var man_opt1: Int = 0,
    var woman_opt1: Int = 0,

    var man_opt2: Int = 0,
    var woman_opt2: Int = 0
)

data class RegionStatistics(
    var gyeonggi_opt1: Int = 0,
    var gangwon_opt1: Int = 0,
    var chungcheong_opt1: Int = 0,
    var gyeongsang_opt1: Int = 0,
    var jeolla_opt1: Int = 0,
    var jeju_opt1: Int = 0,

    var gyeonggi_opt2: Int = 0,
    var gangwon_opt2: Int = 0,
    var chungcheong_opt2: Int = 0,
    var gyeongsang_opt2: Int = 0,
    var jeolla_opt2: Int = 0,
    var jeju_opt2: Int = 0
)

data class CountModel(
    var total_opt1 : Int = 0,
    var total_opt2 : Int = 0,
    var ageStatistics: AgeStatistics = AgeStatistics(),
    var genderStatistics: GenderStatistics = GenderStatistics(),
    var regionStatistics: RegionStatistics = RegionStatistics()
): Serializable