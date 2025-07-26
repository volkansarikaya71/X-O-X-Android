package com.vs.xoxgame

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.vs.xoxgame.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var twoPlayerCheck: Boolean? = null
    private var botLevelCheck: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        resetGameInfo()


        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        screenView(view)
        Glide.with(this)
            .asGif()
            .load(R.drawable.xox) // xox.gif drawable içine yerleştirilmeli
            .into(binding.GameGif)
    }



    fun onePlayerClick(view: View)
    {
        binding.SelectedPlayerCancel.visibility = View.VISIBLE
        binding.TwoPlayer.visibility = View.GONE

        binding.SelectedGameSetting.visibility = View.VISIBLE
        binding.SelectedGameSettingBot.visibility = View.VISIBLE

        binding.StartButtonMenu.visibility = View.VISIBLE

        twoPlayerCheck=false
    }
    fun twoPlayerClick(view: View)
    {

        binding.SelectedPlayerCancel.visibility = View.VISIBLE
        binding.OnePlayer.visibility = View.GONE


        binding.SelectedGameSetting.visibility = View.VISIBLE
        binding.SelectedGameSettingTwoName.visibility = View.VISIBLE

        binding.StartButtonMenu.visibility = View.VISIBLE

        twoPlayerCheck=true
        botLevelCheck=null
    }
    fun botEasy(view: View)
    {

        binding.BotSettingBack.visibility = View.VISIBLE
        binding.BotSettingHard.visibility = View.GONE

        botLevelCheck="1"

    }
    fun botHard(view: View)
    {

        binding.BotSettingBack.visibility = View.VISIBLE
        binding.BotSettingEasy.visibility = View.GONE

        botLevelCheck="2"

    }
    fun selectedBack(view: View)
    {
        binding.SelectedPlayerCancel.visibility = View.GONE
        binding.OnePlayer.visibility = View.VISIBLE
        binding.TwoPlayer.visibility = View.VISIBLE

        binding.SelectedGameSetting.visibility = View.GONE

        binding.SelectedGameSettingBot.visibility = View.GONE
        binding.SelectedGameSettingTwoName.visibility = View.GONE
        binding.StartButtonMenu.visibility = View.GONE

        twoPlayerCheck=null



    }
    fun botSelectedBack(view: View)
    {
        binding.BotSettingBack.visibility = View.GONE
        binding.BotSettingEasy.visibility = View.VISIBLE
        binding.BotSettingHard.visibility = View.VISIBLE

        botLevelCheck=null
    }

    fun startGameClick(view: View)
    {
        if (twoPlayerCheck == false)
        {
            val firstPlayerName = binding.PlayerOneName.text.toString()

            if (firstPlayerName.isEmpty()) {
                Toast.makeText(this, "Lütfen adınızı giriniz!", Toast.LENGTH_SHORT).show()
                return
            }

            if (botLevelCheck == null) {
                Toast.makeText(this, "Lütfen yapay zeka seviyesini seçiniz!", Toast.LENGTH_SHORT).show()
                return
            }


            val intent = Intent(this, PickPlayerMenu::class.java)
            intent.putExtra("First_Player_Name", firstPlayerName)
            intent.putExtra("Bot_Level", botLevelCheck)
            startActivity(intent)

            finish()
        }


        if (twoPlayerCheck == true)
        {
            val firstPlayerName = binding.PlayerOneName.text.toString()
            val secondPlayerName = binding.PlayerTwoName.text.toString()

            if (firstPlayerName.isEmpty()) {
                Toast.makeText(this, "Lütfen  birinci oyuncunun adını giriniz!", Toast.LENGTH_SHORT).show()
                return
            }
            if (secondPlayerName.isEmpty()) {
                Toast.makeText(this, "Lütfen ikinci oyuncunun adını giriniz!", Toast.LENGTH_SHORT).show()
                return
            }

            val intent = Intent(this, PickPlayerMenu::class.java)
            intent.putExtra("First_Player_Name", firstPlayerName)
            intent.putExtra("Second_Player_Name", secondPlayerName)

            botLevelCheck="3"
            intent.putExtra("Bot_Level", botLevelCheck)

            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
    }
    // oyun açıkken yeniden açmaya calısınca hafızadaki verileri sıfırlamak için
    fun resetGameInfo() {
        GameInfo.firstPlayerSkor = 0
        GameInfo.secondPlayerSkor = 0
        GameInfo.startPlayerSecond = null
        GameInfo.checkwinorloseordraw = 0
        GameInfo.buttonStates.clear()
    }

}