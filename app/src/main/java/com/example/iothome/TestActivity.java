package com.example.iothome;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.utils.RoundKnobButton;
import com.example.utils.Singleton;

import org.w3c.dom.Text;

public class TestActivity extends AppCompatActivity {

    Singleton m_Inst = Singleton.getInstance();
    RelativeLayout layout;
    RoundKnobButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_Inst.InitGUIFrame(this);

        //RelativeLayout panel = new RelativeLayout(this);
        setContentView(R.layout.activity_test);
        RelativeLayout panel = (RelativeLayout) findViewById(R.id.mainLayout);

        RoundKnobButton knobButton = new RoundKnobButton(this, R.drawable.stator, R.drawable.rotoroff, R.drawable.rotor_def, m_Inst.Scale(250), m_Inst.Scale(250));

        //RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        panel.addView(knobButton, lp);

        knobButton.setRotorPercentage(0);
        knobButton.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            @Override
            public void onStateChange(boolean newstate) {
            }

            @Override
            public void onRotate(int percentage) {

                Log.d("TAG", percentage + "");
            }
        });

    }

}
