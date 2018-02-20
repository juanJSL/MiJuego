package com.pmdm.migame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import java.awt.Button;

import static com.badlogic.gdx.math.MathUtils.clamp;

public class MiJuego extends ApplicationAdapter implements InputProcessor {
    //Objeto para el mapa
    private TiledMap mapa;
    //Objeto para pintar el mapa
    private TiledMapRenderer mapaRenderer;
    //Camara para la vista del juego
    private OrthographicCamera camara;
    //Objeto para cargar la imagen del mosquetero
    private Texture img;
    //Objeto sprite
    private Sprite sprite;
    //Objeto par dibujar imagenes 2d
    private SpriteBatch sb;

    // Constantes que indican el número de filas y columnas de la hoja de sprites.
    private static final int FRAME_COLS = 3;
    private static final int FRAME_ROWS = 4;
    // Animación que se muestra en el método render()
    private Animation jugador;
    // Animaciones para cada una de las direcciones de movimiento del personaje del jugador.
    private Animation jugadorArriba;
    private Animation jugadorDerecha;
    private Animation jugadorAbajo;
    private Animation jugadorIzquierda;
    // Posición en el eje de coordenadas actual del jugador.
    private float jugadorX, jugadorY;
    // Este atributo indica el tiempo en segundos transcurridos desde que se inicia la    animación,
    // servirá para determinar cual es el frame que se debe representar .
    private float stateTime;
    // Contendrá el frame que se va a mostrar en cada momento.
    private TextureRegion cuadroActual;



    // Tamaño del mapa de baldosas.
    private int anchoMapa, altoMapa;
    //Atributos que indican la anchura y la altura de un tile del mapa de baldosas
    int anchoCelda,altoCelda;




    //Obstaculos
    private boolean [][] obstaculo;
    TiledMapTileLayer capaObstaculos;



    //Atributos que indican la anchura y altura del sprite animado del jugador.
    int anchoJugador, altoJugador;

    @Override
    public void create() {
        //Inicializar la camara del juego
        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();

        //Creamos la camara y la vinculamos con el lienzo del juego
        //Indicamos unos valores para que el juego se vea igual en todas las plataforamas
        camara = new OrthographicCamera(800, 400);

        //Posicionamos la camara para que el vertice inferior sea 0,0
        camara.position.set(camara.viewportWidth / 2f, camara.viewportHeight / 2f, 0);

        //Indicamos por quien seran procesados los eventos de entrada
        Gdx.input.setInputProcessor(this);

        camara.update();

        //Cargamos la imagen del mosquetero en el objeto Texture
        img = new Texture(Gdx.files.internal("mosquetero.png"));

        //Sacamos los frames de img en un array de TextureRegion
        TextureRegion[][] tmp = TextureRegion.split(img, img.getWidth() / FRAME_COLS, img.getHeight() / FRAME_ROWS);

        // Creamos las distintas animaciones, teniendo en cuenta que el tiempo de muestra de cada frame
        // será de 150 milisegundos, y que les pasamos las distintas filas de la matriz tmp a las mismas
        jugadorArriba = new Animation(0.150f, tmp[0]);
        jugadorDerecha = new Animation(0.150f, tmp[1]);
        jugadorAbajo = new Animation(0.150f, tmp[2]);
        jugadorIzquierda = new Animation(0.150f, tmp[3]);

        //Por defecto se usa la posicion de jugadorArriba
        jugador = jugadorArriba;

        //Indicamos la posicion del jugador
        jugadorX = 0;
        jugadorY = 0;

        // Ponemos a cero el atributo stateTime, que marca el tiempo e ejecución de la animación.
        stateTime = 0f;


        //Asignamos la imagen al objeto sprite para que pueda ser representado en la pantalla
        sprite = new Sprite(img);

        //Creamos el SpriteBatch que nos permite dibujar el msoquetero
        sb = new SpriteBatch();

        //Cargamos el mapa
        mapa = new TmxMapLoader().load("nuevoMapa.tmx");
        mapaRenderer = new OrthogonalTiledMapRenderer(mapa);




        //Determinamos el alto y ancho del mapa de baldosas. Para ello necesitamos extraer la capa
        //base del mapa y, a partir de ella, determinamos el número de celdas a lo ancho y alto,
        //así como el tamaño de la celda, que multiplicando por el número de celdas a lo alto y
        //ancho, da como resultado el alto y ancho en pixeles del mapa.
        TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(0);
        anchoCelda = (int) capa.getTileWidth();
        altoCelda = (int) capa.getTileHeight();
        anchoMapa = capa.getWidth() * anchoCelda;
        altoMapa = capa.getHeight() * altoCelda;





        /*
        OBSTACULOS
         */

        capaObstaculos = (TiledMapTileLayer) mapa.getLayers().get(2);//Cargamos la matriz de los obstáculos del mapa de baldosas.
        int anchoCapa = capaObstaculos.getWidth(), altoCapa = capaObstaculos.getHeight();
        obstaculo = new boolean[altoCapa][anchoCapa];
        for (int x = 0; x < anchoCapa; x++) {
            for (int y = 0; y < altoCapa; y++) {
                obstaculo[x][y] = (capaObstaculos.getCell(x, y) != null);
            }
        }


        //Cargamos en los atributos del ancho y alto del sprite sus valores
        cuadroActual = (TextureRegion) jugador.getKeyFrame(stateTime);
        anchoJugador = cuadroActual.getRegionHeight();
        altoJugador = cuadroActual.getRegionHeight();


    }
/*
    @Override
    public void render() {
        //Color de fondo
        Gdx.gl.glClearColor(1, 0, 0, 1);
        //Borramos la pantalla
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Actualizamos la camara del juego
        camara.update();
        //Vinculamos el objeto que dibuja el mapa con la camara
        mapaRenderer.setView(camara);
        //Dibujamos el mapa
        mapaRenderer.render();
        //Inicializamos el objeto SpriteBatch


        // extraemos el tiempo de la última actualización del sprite y la acumulamos stateTime.
        stateTime += Gdx . graphics . getDeltaTime ();
        // Extraemos el frame que debe ir asociado al momento actual.
        cuadroActual = (TextureRegion) jugador. getKeyFrame (stateTime); // 1
        // le indicamos al SpriteBatch que se muestre en el sistema de coordenadas
        // específicas de la cámara.
        sb. setProjectionMatrix (camara. combined );
        sb.begin();
        //Pintamos el Sprite con el objeto SpriteBatch
        sprite.draw(sb);
        //Finalizamos el SpriteBatch
        sb.end();


    }
*/


    @Override
    public void render() {
        //Ponemos el color del fondo a negro
        Gdx.gl.glClearColor(0, 0, 0, 1);
        //Borramos la pantalla
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Trasladamos la cámara para que se centre en el mosquetero.
        camara.position.set(jugadorX,jugadorY,0f);



        /*
        SIGUIENDO AL JUGADOR II
         */

        //Trasladamos la cámara para que se centre en el mosquetero.
        camara.position.set(jugadorX, jugadorY, 0f);
        //Comprobamos que la cámara no se salga de los límites del mapa de baldosas,
        //Verificamos, con el método clamp(), que el valor de la posición x de la cámara
        //esté entre la mitad de la anchura de la vista de la cámara y entre la diferencia entre
        //la anchura del mapa restando la mitad de la anchura de la vista de la cámara,
        camara.position.x = MathUtils.clamp(camara.position.x, camara.viewportWidth/2f, anchoMapa - camara.viewportWidth/2f);
        //Verificamos, con el método clamp(), que el valor de la posición y de la cámara
        //esté entre la mitad de la altura de la vista de la cámara y entre la diferencia entre
        //la altura del mapa restando la mitad de la altura de la vista de la cámara,
        camara.position.y = clamp(camara.position.y, camara.viewportHeight / 2f,altoMapa - camara.viewportHeight / 2f);








        //Actualizamos la cámara del juego
        camara.update();
        //Vinculamos el objeto de dibuja el TiledMap con la cámara del juego
        mapaRenderer.setView(camara);
        //Dibujamos el TiledMap
        mapaRenderer.render();
        // extraemos el tiempo de la última actualización del sprite y la acumulamos a        stateTime.
        stateTime += Gdx.graphics.getDeltaTime();
        // Extraemos el frame que debe ir asociado al momento actual.
        cuadroActual = (TextureRegion) jugador.getKeyFrame(stateTime); // 1
        // le indicamos al SpriteBatch que se muestre en el sistema de coordenadas
        // específicas de la cámara.
        sb.setProjectionMatrix(camara.combined);
        // Inicializamos el objeto SpriteBatch
        sb.begin();
        // Pintamos el objeto Sprite a través del objeto SpriteBatch
        sb.draw(cuadroActual, jugadorX, jugadorY); // 2
        // Finalizamos el objeto SpriteBatch
        sb.end();
    }


    @Override
    public void dispose() {
    }


    public boolean keyDown(int keycode) {
        return false;
    }

    public boolean keyUp(int keycode) {
        return false;
    }

    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        System.out.println(button);

        // Vector en tres dimensiones que recoge las coordenadas donde se ha hecho click
        // o toque de la pantalla.
        Vector3 clickCoordinates = new Vector3(screenX, screenY, 0);
        // Transformamos las coordenadas del vector a coordenadas de nuestra cámara.
        Vector3 posicion = camara.unproject(clickCoordinates);
        // Se pone a cero el atributo que marca el tiempo de ejecución de la animación,
        // provocando que la misma se reinicie.
        stateTime = 0;


        //​ Guardamos la posición anterior del jugador por si al desplazarlo se topa
        //​ con un obstáculo y podamos volverlo a la posición anterior.
        float jugadorAnteriorX = jugadorX;
        float jugadorAnteriorY = jugadorY;


        // Si se ha pulsado por encima de la animación, se sube esta 5 píxeles y se reproduce la
        // animación del jugador desplazándose hacia arriba.
        if ((jugadorY+altoJugador) < posicion.y) {
            jugadorY += 5;
            jugador = jugadorArriba;
        // Si se ha pulsado por debajo de la animación, se baja esta 5 píxeles y se reproduce
        // la animación del jugador desplazándose hacia abajo.
        } else if (jugadorY > posicion.y) {
            jugadorY -= 5;
            jugador = jugadorAbajo;
        }
        // Si se ha pulsado a la derecha de la animación, se mueve esta 5 píxeles a la derecha y
        // se reproduce la animación del jugador desplazándose hacia la derecha.
        if ((jugadorX+anchoJugador/2) < posicion.x) {
            jugadorX += 5;
            jugador = jugadorDerecha;
        // Si se ha pulsado a la izquierda de la animación, se mueve esta 5 píxeles a la
        // izquierda y se reproduce la animación del jugador desplazándose hacia la izquierda.
        } else if ((jugadorX-anchoJugador/2) > posicion.x) {
            jugadorX -= 5;
            jugador = jugadorIzquierda;
        }


        //Comprobar la colision con el obstaculo
        //Al chocar con un obstáculo el jugador vuelve a su posición inicial
        if ((obstaculo[(int) ((jugadorX + anchoJugador/4) / anchoCelda)][((int) (jugadorY)
                / altoCelda)])
                || (obstaculo[(int) ((jugadorX + 3*anchoJugador/4) / anchoCelda)][((int)
                (jugadorY) / altoCelda)])) {
            jugadorX = jugadorAnteriorX;
            jugadorY = jugadorAnteriorY;
        }



        return true;
    }

    public boolean touchUp(int x, int y, int pointer, int button) {
        return false;
    }

    public boolean touchDragged(int x, int y, int pointer) {
        return false;
    }

    public boolean mouseMoved(int x, int y) {
        return false;
    }

    public boolean scrolled(int amount) {
        return false;
    }

}
