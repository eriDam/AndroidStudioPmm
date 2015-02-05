package com.examples.eri.proyecto5menuentrada;
/**
        * @author erika_000
        *
        * PROYECTO 5 - MENÚ DE ENTRADA - Splash Screen - Reproducciendo música
        * con MediaPlayer - TableLayout - ToggleB desactivar sonido - Exit
        *
        *
        * 2.	Modificaremos las líneas necesarias para sustituir SoundPool por MediaPlayer
        *
        * La principal diferencia entre SoundPool y mediaPlayer es que MediaPlayer consume muchos mas recursos
        * que SoundPool, MediaPlayer también reproduce archivos mas grandes e incluso de video
        * */
import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;
import static android.media.MediaPlayer.OnPreparedListener;

public class Music_activity extends Activity implements CompoundButton.OnCheckedChangeListener{
    /**
     * Guardar preferencias de usuario: Para que cuando se inicie de nuevo, recuerde estos datos
     * Capturamos un bolean introducidos por el usuario al activar el boton.
     *
     *  Las preferencias no se almacenan en ficheros binarios como las bases de datos SQLite,
     *  sino en ficheros XML. Estos ficheros XML se almacenan en una ruta que sigue el siguiente patrón:
     *  /data/data/paquete.java/shared_prefs/nombre_coleccion.xml
     * Creamos  las preferencias de usuario
     * */
    private SharedPreferences pref;
    private boolean audioActivado;
    public static final String PREFS_NAME = "AmorOdioSettings";

    //Control de volumen
    private int mVolume = 6, mVolumeMax = 10, mVolumeMin = 0;
    //Control sonando música
    private int sonando=0; //0=cancion no comenzada; 1=comenzada; 2=pause;



    //Definimos los elementos que utilizaremos
    ToggleButton tButtonMusica;
    //También vamos a manipular el text view
    TextView tView;

    //Creamos el MediaPlayer
    private MediaPlayer mPlayer;
    private int mSoundId;
    private AudioManager mAudioManager;
    private boolean mCanPlayAudio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_music);

        //Los localizamos mediante su id
        tButtonMusica=(ToggleButton)findViewById(R.id.toggleButtonMusica);
        tView=(TextView)findViewById(R.id.tView);

        /**Asignarle a tButtonMusica el setOnCheckedChangeListener,
         * le decimos que escuche a traves de esta interfaz y ya se puede
         * manipular dentro del método onCheckedChanged línea 58*/

        tButtonMusica.setOnCheckedChangeListener(this);





        // Capturamos/llamamos a el AudioManager, es 1 servicio de android que nos proporciona
        // el control del volumen y los tonos del telefono
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Volumen actual programado
        final TextView tv = (TextView) findViewById(R.id.textViewCount);
        tv.setText(String.valueOf(mVolume));

        // Subir volumen
        final Button upButton = (Button) findViewById(R.id.buttonS);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // El AudioManager puede realizar efectos, como hacer sonar el efecto del click
                mAudioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, mVolume);

                if (mVolume < mVolumeMax) {
                    mVolume += 2;
                    tv.setText(String.valueOf(mVolume));
                    //Cuando le demos a subir volumen, Le pasamos el volumen izqd y dcho, 2 canales y cada uno con un volumen
                    mPlayer.setVolume((float) mVolume / mVolumeMax, (float) mVolume / mVolumeMax);
                }
            }
        });

        // Bajar Volumen de la canción
        final Button downButton = (Button) findViewById(R.id.buttonB);
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Hacer sonar el efecto de click
                mAudioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, mVolume);

                if (mVolume > mVolumeMin) {
                    mVolume -= 2;
                    tv.setText(String.valueOf(mVolume));
                }
                mPlayer.setVolume((float) mVolume / mVolumeMax,
                        (float) mVolume / mVolumeMax);
            }
        });

        // Desactivamos el toggleButton
        //final Button playButton = (Button) findViewById(R.id.buttonPlay);
        //playButton.setEnabled(false);
        tButtonMusica.setEnabled(false);

         /*CARGAMOS LA CANCIÓN NUEVA FORMA - NO HACE FALTA GENERAR UN NUEVO SOUNDPOOL
        * Con el método estático MediaPlayer create, le pasamos this  para indicarle este activity
        * y dónde está el Id de la canción. Una vez que lo cree me devolverá un indentificador mPlayer como objeto MediaPlayer*/
        mPlayer = MediaPlayer.create(this, R.raw.miraclelong);
        /**
         * Hemos sustituido el setOnLoadCompleteListener por setOnPreparedListener,
         * que lo que implementa es un Listener de tipo onPrepare*/

        mPlayer.setOnPreparedListener(new OnPreparedListener() {
          @Override
          public void onPrepared(MediaPlayer mp) {
          Log.d("AUDIO", "Cargada la cancion");
          //ponemos el tButtonMusica activo
          tButtonMusica.setEnabled(true);
                                          }
                                      }
        );
        // Suena la cancion
        tButtonMusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Capturamos las preferencias de usuario
                 * Con la clase SharedPreferences realizaremos 2 partes, lectura y escritura
                 *
                 * Lectura: cogeremos las preferencias de este activity en
                 * modo privado MODE_PRIVATE(solo serán utilizadas para esta app) creando un objeto de tipo
                 * SharedPreferences
                 *
                 */

                pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                audioActivado = pref.getBoolean("Audio", tButtonMusica.isChecked());//Si no hay preferencias será true por defecto
                Log.d("AUDIO SET", String.valueOf(audioActivado));



                /**En el evento del botón:
                 * Para escribir, cogeremos el Editor y obtendremos los
                 * datos del toggleB directamente*/
                SharedPreferences.Editor prefEd = pref.edit();


                if (sonando == 0)
                {
                    // playButton.setText("||");
                    tButtonMusica.setChecked(true);
                    sonando = 1;
                    if (mCanPlayAudio)
                        mPlayer.setVolume((float) mVolume / mVolumeMax,
                                (float) mVolume / mVolumeMax);
                    mPlayer.start();

                    //escribimos preferencias
                    prefEd.putBoolean("Activado",true);
                    prefEd.commit();//Actualizamos las pref
                    MessageBox("Guardadas preferencias");

                    //mSoundPool.play(mSoundId, (float) mVolume / mVolumeMax,
                    //        (float) mVolume / mVolumeMax, 1, 0, 1.0f);

                } else if (sonando == 1)
                {
                    tButtonMusica.setChecked(false);
                    sonando = 2;
                    mPlayer.setVolume((float) mVolume / mVolumeMax,
                            (float) mVolume / mVolumeMax);
                    mPlayer.pause();
                    //mSoundPool.pause(mSoundId);
                    //escribimos preferencias
                    prefEd.putBoolean("Activado",false);
                    prefEd.commit();//Actualizamos las pref
                    MessageBox("Guardadas preferencias");
                }
                else
                {
                    //tButtonMusica.setText("Activada");
                    sonando=1;

                    mPlayer.start();
                    //escribimos preferencias
                    prefEd.putBoolean("Activado",true);
                    prefEd.commit();//Actualizamos las pref
                    MessageBox("Guardadas preferencias");
                }
                /**
                 * Una vez que lo tenemos definido, realizaremos un commit y con esto queda guardado,
                 * mostramos un mensaje emergente informando al usuario.
                 * */

                MessageBox("Guardadas preferencias"); }

        });


        // Request audio focus
        int result = mAudioManager.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        mCanPlayAudio = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == result;


    }


    // Preparado para reproducir la música
    @Override
    protected void onResume() {
        Log.d("AUDIO", "VOLVIENDO A TOCAR");
        super.onResume();
        mAudioManager.setSpeakerphoneOn(true);
        mAudioManager.loadSoundEffects();
    }

    // Es importante hacer un release, si en algún momento nos vamos de la aplicación
    //liberará los recursos del sistema, ya que consume bastante
    @Override
    protected void onPause() {
        Log.d("AUDIO", "EN PAUSA");
        if (null != mAudioManager) {
            mPlayer.release();
            //mSoundPool.unload(mSoundId);
            //mSoundPool.release();
            //mSoundPool = null;
        }
        mAudioManager.setSpeakerphoneOn(false);
        mAudioManager.unloadSoundEffects();
        super.onPause();
    }
    //Método para si se sale de la aplicacion que se destruya


    @Override
    protected void onDestroy() {
        Log.d("AUDIO", "TERMINADO");
        if (null != mAudioManager) {
            super.onDestroy();
        }
    }

    // Listen for Audio focus changes
    OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                mAudioManager.abandonAudioFocus(afChangeListener);
                mCanPlayAudio = false;
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Al implementar la clase OnCheckedChangeListener tenemos que implementar
    // este metodo onCheckedChanged para el toggleButton
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //Con el condicional le decimos que si está checked (si es true) inserta un texto
        if (tButtonMusica.isChecked()){
            tView.setText("Sonido Activado");
        }else{
            tView.setText("Sonido desactivado");
        }

        }//Fin onCheckedChanged

    //Metodo para usar el messaje Box
    public void MessageBox(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    }

