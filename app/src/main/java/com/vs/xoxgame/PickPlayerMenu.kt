package com.vs.xoxgame

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vs.xoxgame.databinding.ActivityPickPlayerMenuBinding

class PickPlayerMenu : AppCompatActivity() {

    private lateinit var binding: ActivityPickPlayerMenuBinding

    private var startPlayerSecond :Boolean? = null
    private var secondPlayerName: String? = null
    private var botLevel: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPickPlayerMenuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        secondPlayerName = intent.getStringExtra("Second_Player_Name")
        botLevel = intent.getStringExtra("Bot_Level")
        binding.firstPlayerButton.text=intent.getStringExtra("First_Player_Name")

        if(secondPlayerName != null)
        {
            binding.SecondPlayerButton.text=intent.getStringExtra("Second_Player_Name")
        }
        else
        {
            binding.SecondPlayerButton.text="Bot"
        }


    }

    fun firstPlayerClick(view: View)
    {
        startPlayerSecond=true
        butonIslemleri(startPlayerSecond)
    }
    fun secondPlayerClick(view: View)
    {
        startPlayerSecond=false
        butonIslemleri(startPlayerSecond)
    }
    fun randomPlayerClick(view: View)
    {
        val randomBoolean = (0..1).random() == 1
        butonIslemleri(randomBoolean)
    }

    private fun butonIslemleri(deger: Boolean?=null)
    {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("Selected_Player", deger)
        intent.putExtra("First_Player_Name", binding.firstPlayerButton.text.toString())
        intent.putExtra("Second_Player_Name", binding.SecondPlayerButton.text.toString())
        intent.putExtra("Bot_Level", botLevel.toString())


        startActivity(intent)
        finish()
    }
}