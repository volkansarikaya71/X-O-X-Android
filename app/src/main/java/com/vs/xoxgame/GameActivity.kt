package com.vs.xoxgame

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.vs.xoxgame.databinding.ActivityGameBinding



class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private var botLevel:String?=null

    private var firstPlayerName:String?=null
    private var secondPlayerName:String?=null



    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        restoreButtonStates()

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firstPlayerName=intent.getStringExtra("First_Player_Name")
        secondPlayerName=intent.getStringExtra("Second_Player_Name")



        GameInfo.startPlayerSecond = GameInfo.startPlayerSecond ?: intent.getBooleanExtra("Selected_Player", false)

        botLevel=intent.getStringExtra("Bot_Level")

        binding.PlayerOneNameText.text=firstPlayerName.toString()
        binding.PlayerTwoNameText.text=secondPlayerName.toString()

        binding.playerOneSkorText.text = "Skor: ${GameInfo.firstPlayerSkor}"
        binding.playerTwoSkorText.text = "Skor: ${GameInfo.secondPlayerSkor}"

        turnimage()
        if (GameInfo.checkwinorloseordraw == 2)
        {
            disableAllButtons(false)
            showReplayDialog(GameInfo.winnername.toString())
            return
        }

        checkDraw()


        if (botLevel == "1" && GameInfo.startPlayerSecond == false) {
            disableAllButtons(false)
            Handler(Looper.getMainLooper()).postDelayed({
                disableAllButtons(true)
                easyBotMove()
            }, 600)
        }
        if (botLevel == "2" && GameInfo.startPlayerSecond == false) {
            disableAllButtons(false)
            Handler(Looper.getMainLooper()).postDelayed({
                disableAllButtons(true)
                hardBotMove()
            }, 600)
        }

    }
    fun buttonSelect(view: View)
    {

        selectplayer(view)

    }


    //x-o-x secme ve oyuncu secme kısmı

    private fun selectplayer(view: View)
    {
        val imageButton = view as ImageView

        // Butonun id ve tag değerini alıyoruz
        val buttonId = imageButton.id
        val buttonTag = if (GameInfo.startPlayerSecond == true) "X" else "O"

        // Butonun durumu global listeye kaydediliyor (varsa güncelleniyor)
        val existingIndex = GameInfo.buttonStates.indexOfFirst { it.buttonId == buttonId }
        if (existingIndex >= 0)
        {
            GameInfo.buttonStates[existingIndex] = ButtonState(buttonId, buttonTag)
        } else
        {
            GameInfo.buttonStates.add(ButtonState(buttonId, buttonTag))
        }



        if(GameInfo.startPlayerSecond==true)
        {
            imageButton.tag = "X"

            Glide.with(this)
                .asGif()
                .load(R.drawable.selectxbox)
                .into(object : com.bumptech.glide.request.target.ImageViewTarget<GifDrawable>(imageButton) {
                    override fun setResource(resource: GifDrawable?) {
                        resource?.setLoopCount(1) // Sadece 1 kere oynat
                        view.setImageDrawable(resource)

                        resource?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                            override fun onAnimationEnd(drawable: Drawable?) {
                                // GIF oynatımı bitince statik görsel yükle
                                imageButton.setImageResource(R.drawable.xbox)
                            }
                        })

                        resource?.start()
                    }
                })
            val control=checkWinner()
            if (!control) {
                val drawControl = checkDraw()
                if (!drawControl)
                {
                    imageButton.isEnabled = false
                    GameInfo.startPlayerSecond = false
                    turnimage()

                }
            }

            if (botLevel == "1" && GameInfo.startPlayerSecond == false) {
                disableAllButtons(false)
                Handler(Looper.getMainLooper()).postDelayed({
                    disableAllButtons(true)
                    easyBotMove()
                }, 600)
            }
            if (botLevel == "2" && GameInfo.startPlayerSecond == false) {
                disableAllButtons(false)
                Handler(Looper.getMainLooper()).postDelayed({
                    disableAllButtons(true)
                    hardBotMove()
                }, 600)
            }
            return
        }
        else
        {

            imageButton.tag = "O"

            Glide.with(this)
                .asGif()
                .load(R.drawable.selectobox)
                .into(object : com.bumptech.glide.request.target.ImageViewTarget<GifDrawable>(imageButton) {
                    override fun setResource(resource: GifDrawable?) {
                        resource?.setLoopCount(1) // Sadece 1 kere oynat
                        view.setImageDrawable(resource)

                        resource?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                            override fun onAnimationEnd(drawable: Drawable?) {
                                // GIF oynatımı bitince statik görsel yükle
                                imageButton.setImageResource(R.drawable.obox)
                            }
                        })

                        resource?.start()
                    }
                })
            val control = checkWinner()
            if (!control) {
                val drawControl = checkDraw()
                if (!drawControl)
                {
                    imageButton.isEnabled = false
                    GameInfo.startPlayerSecond = true
                    turnimage()

                }
            }
            return
        }
    }


    // sıra kimde gif  degistirme
    private fun turnimage()
    {
        val gifResource=if(GameInfo.startPlayerSecond==true)
        {
            R.drawable.selectleftarrow
        }
        else
        {
            R.drawable.selectrightarrow
        }

        Glide.with(this)
            .asGif()
            .load(gifResource)
            .into(binding.PickPlayer)
    }


    // oyun kazanılma durumunu kontrol eder
    @SuppressLint("SetTextI18n")
    private fun checkWinner(): Boolean {

        val winPositions = listOf(
            listOf(binding.buttonOne, binding.buttonTwo, binding.buttonThree),
            listOf(binding.buttonFour, binding.buttonFive, binding.buttonSix),
            listOf(binding.buttonSeven, binding.buttonEight, binding.buttonNine),
            listOf(binding.buttonOne, binding.buttonFour, binding.buttonSeven),
            listOf(binding.buttonTwo, binding.buttonFive, binding.buttonEight),
            listOf(binding.buttonThree, binding.buttonSix, binding.buttonNine),
            listOf(binding.buttonOne, binding.buttonFive, binding.buttonNine),
            listOf(binding.buttonThree, binding.buttonFive, binding.buttonSeven)
        )

        for (line in winPositions) {
            val first = line[0].tag
            val second = line[1].tag
            val third = line[2].tag

            if (first != null && first == second && second == third) {
                Toast.makeText(this, "$first kazandı!", Toast.LENGTH_SHORT).show()
                GameInfo.checkwinorloseordraw=2
                if(first.toString()=="X")
                {
                    GameInfo.winnername=firstPlayerName.toString()
                    showReplayDialog(firstPlayerName.toString())
                    GameInfo.firstPlayerSkor++
                }
                else
                {
                    GameInfo.winnername=secondPlayerName.toString()
                    showReplayDialog(secondPlayerName.toString())
                    GameInfo.secondPlayerSkor++
                }
                binding.playerOneSkorText.text = "Skor: ${GameInfo.firstPlayerSkor}"
                binding.playerTwoSkorText.text = "Skor: ${GameInfo.secondPlayerSkor}"
                disableAllButtons(false)
                return true
            }
        }
        return false
    }


    //oyun bittiginde cıkacak olan kısım
    private fun showReplayDialog(winner: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)

        //berabere
        if(GameInfo.checkwinorloseordraw==1)
        {
            builder.setTitle("Oyun Berabere bitti")
            builder.setMessage("Yeniden oynamak ister misiniz?")
        }

        //kazanma
        if(GameInfo.checkwinorloseordraw==2)
        {
            builder.setTitle("Oyunu $winner Kazandı")
            builder.setMessage("Yeniden oynamak ister misiniz?")
        }

        builder.setPositiveButton("Evet") { _, _ ->
            resetGame()
        }

        builder.setNegativeButton("Hayır") { dialog, _ ->
            dialog.dismiss()
            finish()

        }

        builder.setCancelable(false)
        builder.show()
    }

    //yeniden oynamak için
    private fun resetGame() {
        val buttons = listOf(
            binding.buttonOne, binding.buttonTwo, binding.buttonThree,
            binding.buttonFour, binding.buttonFive, binding.buttonSix,
            binding.buttonSeven, binding.buttonEight, binding.buttonNine
        )

        buttons.forEach {
            it.setImageResource(R.drawable.box)
            it.tag = null
            it.isEnabled = true
        }
        GameInfo.startPlayerSecond = !(GameInfo.startPlayerSecond ?: false)
        GameInfo.checkwinorloseordraw = 0
        turnimage()
        if (botLevel == "1" && GameInfo.startPlayerSecond == false) {
            disableAllButtons(false)
            Handler(Looper.getMainLooper()).postDelayed({
                disableAllButtons(true)
                easyBotMove()
            }, 600)
        }
        if (botLevel == "2" && GameInfo.startPlayerSecond == false) {
            disableAllButtons(false)
            Handler(Looper.getMainLooper()).postDelayed({
                disableAllButtons(true)
                hardBotMove()
            }, 600)
        }
    }

    //kazanma durumda butonları kilitleme
    private fun disableAllButtons(enabled: Boolean)
    {
        val buttons = listOf(
            binding.buttonOne, binding.buttonTwo, binding.buttonThree,
            binding.buttonFour, binding.buttonFive, binding.buttonSix,
            binding.buttonSeven, binding.buttonEight, binding.buttonNine
        )
        for (button in buttons) {
            button.isEnabled = enabled
        }
    }

    //beraberlik  durumu kontrolu
    private fun checkDraw(): Boolean {
        val buttons = listOf(
            binding.buttonOne, binding.buttonTwo, binding.buttonThree,
            binding.buttonFour, binding.buttonFive, binding.buttonSix,
            binding.buttonSeven, binding.buttonEight, binding.buttonNine
        )

        // Bütün butonların tag'ları null değil mi kontrol et
        val allFilled = buttons.all { it.tag != null }

        if (allFilled) {
            GameInfo.checkwinorloseordraw=1
            showReplayDialog("Berabere")
            return true
        }

        return false
    }

    //easy bot

    private fun easyBotMove() {
        if(botWin())
        {
            return
        }
        if(botWiningBlock())
        {
            return
        }
        val buttons = listOf(
            binding.buttonFive,  // önce ortayı dene
            binding.buttonOne, binding.buttonTwo, binding.buttonThree,
            binding.buttonFour, binding.buttonSix,
            binding.buttonSeven, binding.buttonEight, binding.buttonNine
        )

        for (button in buttons) {
            if (button.tag == null && button.isEnabled) {
                buttonSelect(button)
                break
            }
        }
    }

    private fun hardBotMove() {
        if(botWin())
        {
            return
        }
        if(botWiningBlock())
        {
            return
        }
        val buttons = listOf(
            binding.buttonFive,  // önce ortayı dene
            binding.buttonEight, binding.buttonThree, binding.buttonOne,
            binding.buttonSeven, binding.buttonTwo,
            binding.buttonFour, binding.buttonSix, binding.buttonEight
        )

        for (button in buttons) {
            if (button.tag == null && button.isEnabled) {
                buttonSelect(button)
                break
            }
        }
    }

    //ekran donunce butonları geri getirmek icin
    private fun restoreButtonStates() {
        for (state in GameInfo.buttonStates) {
            val button = findViewById<ImageView>(state.buttonId)
            if (button != null) {
                button.tag = state.tag
                when(state.tag) {
                    "X" -> button.setImageResource(R.drawable.xbox)
                    "O" -> button.setImageResource(R.drawable.obox)
                    else -> button.setImageResource(R.drawable.box)
                }
                button.isEnabled = state.tag == null
            }
        }
    }

    // bot kazanma durumun hamle yapıp kazancak
    private fun botWin(): Boolean
    {
        val lines = listOf(
            listOf(binding.buttonOne, binding.buttonTwo, binding.buttonThree),
            listOf(binding.buttonFour, binding.buttonFive, binding.buttonSix),
            listOf(binding.buttonSeven, binding.buttonEight, binding.buttonNine),
            listOf(binding.buttonOne, binding.buttonFour, binding.buttonSeven),
            listOf(binding.buttonTwo, binding.buttonFive, binding.buttonEight),
            listOf(binding.buttonThree, binding.buttonSix, binding.buttonNine),
            listOf(binding.buttonOne, binding.buttonFive, binding.buttonNine),
            listOf(binding.buttonThree, binding.buttonFive, binding.buttonSeven)
        )
        for (line in lines) {
            val tags = line.map { it.tag }
            if (tags.count { it == "O" } == 2 && tags.count { it == null } == 1) {
                val targetButton = line[tags.indexOf(null)]
                buttonSelect(targetButton)
                return true
            }
        }
        return false
    }

    // oyuncu kazancaksa engelleme yapcak
    private fun botWiningBlock(): Boolean
    {
        val lines = listOf(
            listOf(binding.buttonOne, binding.buttonTwo, binding.buttonThree),
            listOf(binding.buttonFour, binding.buttonFive, binding.buttonSix),
            listOf(binding.buttonSeven, binding.buttonEight, binding.buttonNine),
            listOf(binding.buttonOne, binding.buttonFour, binding.buttonSeven),
            listOf(binding.buttonTwo, binding.buttonFive, binding.buttonEight),
            listOf(binding.buttonThree, binding.buttonSix, binding.buttonNine),
            listOf(binding.buttonOne, binding.buttonFive, binding.buttonNine),
            listOf(binding.buttonThree, binding.buttonFive, binding.buttonSeven)
        )
        for (line in lines) {
            val tags = line.map { it.tag }
            if (tags.count { it == "X" } == 2 && tags.count { it == null } == 1) {
                val targetButton = line[tags.indexOf(null)]
                buttonSelect(targetButton)
                return true
            }
        }
        return false
    }











}