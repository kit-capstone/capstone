package com.example.banlancegameex.utils

import com.example.banlancegameex.UserDataModel
import com.example.banlancegameex.contentsList.CountModel

class CountResult {
    companion object {
        val currentUser = FBAuth.getemail()
        var userdata = UserDataModel()

        fun delaycount(choose: String, count: CountModel) : CountModel {
            val locate = userdata.locate.split(" ")[0]
            if (choose == "A") {
                if(userdata.agerange == "10대") {
                    count.teenager_op1++
                }else if(userdata.agerange == "20대"){
                    count.twenties_opt1++
                }else if(userdata.agerange == "30대"){
                    count.thirties_opt1++
                }else if(userdata.agerange == "40대") {
                    count.fourties_opt1++
                }else if(userdata.agerange == "50대 이상"){
                    count.fifties_opt1++
                }

                if(userdata.gender == "남성"){
                    count.man_opt1++
                }else if(userdata.gender == "여성"){
                    count.woman_opt1++
                }

                if(locate == "경기도") {
                    count.gyeonggi_opt1++
                }else if(locate == "강원도") {
                    count.gangwon_opt1++
                }else if((locate == "충청북도") || (locate == "충청남도")) {
                    count.chungcheong_opt1++
                }else if((locate == "경상북도") || (locate == "경상남도")) {
                    count.gyeongsang_opt1++
                }else if((locate == "전라북도") || (locate == "전라남도")) {
                    count.jeolla_opt1++
                }else if(locate == "제주특별자치도") {
                    count.jeju_opt1++
                }
            }else if(choose == "B"){
                if(userdata.agerange == "10대") {
                    count.teenager_op2++
                }else if(userdata.agerange == "20대"){
                    count.twenties_opt2++
                }else if(userdata.agerange == "30대"){
                    count.thirties_opt2++
                }else if(userdata.agerange == "40대") {
                    count.fourties_opt2++
                }else if(userdata.agerange == "50대 이상"){
                    count.fifties_opt2++
                }

                if(userdata.gender == "남성"){
                    count.man_opt2++
                }else if(userdata.gender == "여성"){
                    count.woman_opt2++
                }

                if(locate == "경기도") {
                    count.gyeonggi_opt2++
                }else if(locate == "강원도") {
                    count.gangwon_opt2++
                }else if((locate == "충청북도") || (locate == "충청남도")) {
                    count.chungcheong_opt2++
                }else if((locate == "경상북도") || (locate == "경상남도")) {
                    count.gyeongsang_opt2++
                }else if((locate == "전라북도") || (locate == "전라남도")) {
                    count.jeolla_opt2++
                }else if(locate == "제주특별자치도") {
                    count.jeju_opt2++
                }
            }
            return count
        }
    }
}