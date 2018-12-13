package dev.mad.ussd4etecsa.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import dev.mad.ussd4etecsa.Nav_Principal;
import dev.mad.ussd4etecsa.R;

/**
 * Created by Daymel on 22/9/2018.
 */

public class SwipeSplash extends AppIntro {
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("ussdPreferences", Context.MODE_PRIVATE);
        addSlide(AppIntroFragment.newInstance("Consultar saldo", "Consulta tu saldo, planes y bonos de manera sencilla.", R.drawable.saldo, Color.parseColor("#009688")));
        addSlide(AppIntroFragment.newInstance("Tranferir", "Transfiere y recarga tu saldo de fácil manera.", R.drawable.tranferir, Color.parseColor("#009688")));
        addSlide(AppIntroFragment.newInstance("Planes", "Contrata y actualiza tus planes de manera sencilla.", R.drawable.planes, Color.parseColor("#009688")));
        showSkipButton(true);
        setDoneText("Listo");
        setSkipText("Omitir");
    }

    @Override
    public void onDonePressed() {
        super.onDonePressed();
        init();
    }

    private void init() {
        preferences.edit().putBoolean("intro", true).apply();
        Intent intent = new Intent(SwipeSplash.this, Nav_Principal.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        init();
    }
}
