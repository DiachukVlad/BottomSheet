package vladiachuk.com

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.frame.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import vladiachuk.com.bottomsheet.BottomSheetController


class MainActivity : AppCompatActivity() {

    val firstFragment = FirstFragment()
    val secondFragment = SecondFragment()
    var isFirstState = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomSheet.controller = BottomSheetController(bottomSheet)

        firstState()

        bottom_navigation.setOnNavigationItemSelectedListener(this::onBottomNavigationClick)

        btn.setOnClickListener { nextState() }

        /*bottomSheet.post {
            bottomSheet.controller!!.run {
                setStateAnim(HALF_EXPANDED_STATE, 1000)
            }
            bottomSheet.onPositionChangedListener = {
                println(it)
            }
            bottomSheet.controller!!.onStateChangedListener = {
                println("onStateChangedListener $it")
            }
            bottomSheet.controller!!.onStateStartChangingListener = {
                println("onStateStartChangingListener $it")
            }
            bottomSheet.controller!!.onStateChangingCanceledListener = {
                println("onStateChangingCanceledListener")
            }
        }*/

        bottomSheet.reactToOrientationEvent()
    }

    override fun onBackPressed() {
        bottomSheet.controller?.run {
            if (state != COLLAPSED_STATE)
                setStateAnim(COLLAPSED_STATE)
            else
                super.onBackPressed()
        }
    }


    private fun nextState() {
        bottomSheet.controller?.run { setStateAnim(nextState) }
    }

    private fun onBottomNavigationClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.first_action -> {
                if (isFirstState)
                    nextState()
                else
                    GlobalScope.launch(Dispatchers.Main) {
                        bottomSheet.controller?.run { setStateAnimSuspend(COLLAPSED_STATE) }
                        firstState()
                        bottomSheet.controller?.run { setStateAnim(HALF_EXPANDED_STATE) }
                    }
            }
            R.id.second_action -> {
                if (!isFirstState)
                    nextState()
                else
                    GlobalScope.launch(Dispatchers.Main) {
                        bottomSheet.controller?.run { setStateAnimSuspend(COLLAPSED_STATE) }
                        secondState()
                        bottomSheet.controller?.run { setStateAnim(HALF_EXPANDED_STATE) }
                    }
            }
        }
        return false
    }

    private fun firstState() {
        supportFragmentManager.beginTransaction().replace(R.id.frame, firstFragment).commit()
        bottomSheet.controller?.run {
            possibleStates = arrayListOf(COLLAPSED_STATE, HALF_EXPANDED_STATE, EXPANDED_STATE)
            statesGraph = arrayListOf(
                arrayOf(COLLAPSED_STATE, HALF_EXPANDED_STATE),
                arrayOf(HALF_EXPANDED_STATE, COLLAPSED_STATE),
                arrayOf(EXPANDED_STATE, COLLAPSED_STATE)
            )
        }
        isFirstState = true
    }

    private fun secondState() {
        supportFragmentManager.beginTransaction().replace(R.id.frame, secondFragment).commit()
        bottomSheet.controller?.run {
            possibleStates = arrayListOf(COLLAPSED_STATE, HALF_EXPANDED_STATE, EXPANDED_STATE)
            statesGraph = arrayListOf(
                arrayOf(COLLAPSED_STATE, HALF_EXPANDED_STATE),
                arrayOf(HALF_EXPANDED_STATE, COLLAPSED_STATE),
                arrayOf(EXPANDED_STATE, COLLAPSED_STATE)
            )
        }
        isFirstState = false
    }
}
