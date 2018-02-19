package com.pmdm.migame;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.pmdm.migame.MiJuego;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		//Deshabilitar el uso del acelerometro y la brujula
		config.useAccelerometer = false;
		config.useCompass = false;

		//Lanzar el juego
		initialize(new MiJuego(), config);
	}
}
