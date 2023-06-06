package com.example.banlancegameex.utils

import com.example.banlancegameex.UserDataModel
import com.example.banlancegameex.contentsList.CountModel

class CountResult {
    companion object {
        val currentUser = FBAuth.getemail()
        var userdata = UserDataModel()

        fun delaycount(choose: String, count: CountModel) : CountModel {
            val locate = userdata.locate.split(" ")[0]
            val ageStatistics = count.ageStatistics
            val genderStatistics = count.genderStatistics
            val regionStatistics = count.regionStatistics

            if (choose == "A") {
                if(userdata.agerange == "10대") {
                    ageStatistics.teenager_opt1++
                }else if(userdata.agerange == "20대"){
                    ageStatistics.twenties_opt1++
                }else if(userdata.agerange == "30대"){
                    ageStatistics.thirties_opt1++
                }else if(userdata.agerange == "40대") {
                    ageStatistics.fourties_opt1++
                }else if(userdata.agerange == "50대 이상"){
                    ageStatistics.fifties_opt1++
                }

                if(userdata.gender == "남성"){
                    genderStatistics.man_opt1++
                }else if(userdata.gender == "여성"){
                    genderStatistics.woman_opt1++
                }

                if(locate == "경기도") {
                    regionStatistics.gyeonggi_opt1++
                }else if(locate == "강원도") {
                    regionStatistics.gangwon_opt1++
                }else if((locate == "충청북도") || (locate == "충청남도")) {
                    regionStatistics.chungcheong_opt1++
                }else if((locate == "경상북도") || (locate == "경상남도")) {
                    regionStatistics.gyeongsang_opt1++
                }else if((locate == "전라북도") || (locate == "전라남도")) {
                    regionStatistics.jeolla_opt1++
                }else if(locate == "제주특별자치도") {
                    regionStatistics.jeju_opt1++
                }
            }else if(choose == "B"){
                if(userdata.agerange == "10대") {
                    ageStatistics.teenager_opt2++
                }else if(userdata.agerange == "20대"){
                    ageStatistics.twenties_opt2++
                }else if(userdata.agerange == "30대"){
                    ageStatistics.thirties_opt2++
                }else if(userdata.agerange == "40대") {
                    ageStatistics.fourties_opt2++
                }else if(userdata.agerange == "50대 이상"){
                    ageStatistics.fifties_opt2++
                }

                if(userdata.gender == "남성"){
                    genderStatistics.man_opt2++
                }else if(userdata.gender == "여성"){
                    genderStatistics.woman_opt2++
                }

                if(locate == "경기도") {
                    regionStatistics.gyeonggi_opt2++
                }else if(locate == "강원도") {
                    regionStatistics.gangwon_opt2++
                }else if((locate == "충청북도") || (locate == "충청남도")) {
                    regionStatistics.chungcheong_opt2++
                }else if((locate == "경상북도") || (locate == "경상남도")) {
                    regionStatistics.gyeongsang_opt2++
                }else if((locate == "전라북도") || (locate == "전라남도")) {
                    regionStatistics.jeolla_opt2++
                }else if(locate == "제주특별자치도") {
                    regionStatistics.jeju_opt2++
                }
            }
            return count
        }
    }
}