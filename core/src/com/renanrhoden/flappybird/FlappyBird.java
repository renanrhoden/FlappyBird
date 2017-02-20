package com.renanrhoden.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;

    private Texture[] passaro;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture gameOver;

    //private ShapeRenderer shapeRenderer;

    private Circle passaroCirculo;
    private Rectangle canoTopoRetangulo;
    private Rectangle canoBaixoRetangulo;

    private BitmapFont fonte;

    private int larguraDispositivo;
    private int alturaDispositivo;
    private int pontuacao = 0;
    private int posicaoPassaroX = 120;
    private int estadoJogo = 0; // estado 0 = jogo não iniciado | estado 1 = jogo iniciado | estado 2 = Game Over
    private int posicaoGameOverX;
    private int posicaoGameOverY;

    private float variacaoImgPassaro = 0;
    private float velocidadeQueda = 0;
    private float posicaoPassaroY;
    private float posicaoMovimentoCanoHorizontal;
    private float espaçoEntreCanos;
    private float deltaTime;
    private float alturaEntreCanosRandomico;
    private float canoBaixoY;
    private float canoTopoY;
    private final float VIRTUAL_WIDTH = 768;
    private  final float VIRTUAL_HEIGHT = 1024;

    private Random nroRandomico;

    private boolean marcouPonto = false;

    private OrthographicCamera camera;

    private Viewport viewport;

    @Override
    public void create () {
        batch = new SpriteBatch();
        passaro = new Texture[3];
        passaro[0] = new Texture("passaro1.png");
        passaro[1] = new Texture("passaro2.png");
        passaro[2] = new Texture("passaro3.png");
        fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo.png");
        canoTopo = new Texture("cano_topo.png");
        nroRandomico = new Random();
        fonte = new BitmapFont();
        passaroCirculo = new Circle();
        canoBaixoRetangulo = new Rectangle();
        canoTopoRetangulo = new Rectangle();
        gameOver = new Texture("game_over.png");
        //shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        viewport = new StretchViewport( VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera );

        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);

        larguraDispositivo = (int) VIRTUAL_WIDTH;
        alturaDispositivo = (int) VIRTUAL_HEIGHT;

        posicaoPassaroY = alturaDispositivo / 2;
        posicaoMovimentoCanoHorizontal = larguraDispositivo - canoBaixo.getWidth();
        espaçoEntreCanos = 300;
        posicaoGameOverX = larguraDispositivo / 2 - gameOver.getWidth() / 2;
        posicaoGameOverY = alturaDispositivo / 2 - gameOver.getHeight() / 2;
    }

    @Override
    public void render () {

        camera.update();

        //Limpar frames
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        deltaTime = Gdx.graphics.getDeltaTime();
        variacaoImgPassaro += deltaTime * 5; //velocidade da animação do passaro
        canoBaixoY = alturaDispositivo / 2 - canoBaixo.getHeight() - espaçoEntreCanos / 2 + alturaEntreCanosRandomico;
        canoTopoY = alturaDispositivo / 2 + espaçoEntreCanos / 2 + alturaEntreCanosRandomico;

        batch.setProjectionMatrix( camera.combined );

        batch.begin();
        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, canoTopoY);
        batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, canoBaixoY);
        batch.draw(passaro[ (int) variacaoImgPassaro], posicaoPassaroX, posicaoPassaroY);
        fonte.draw(batch, String.valueOf( pontuacao ), larguraDispositivo / 2, alturaDispositivo - fonte.getScaleY()* 3);
        if ( estadoJogo == 2 ){
            batch.draw( gameOver, posicaoGameOverX, posicaoGameOverY );
        }
        batch.end();

        passaroCirculo.set(posicaoPassaroX + passaro[0].getWidth() / 2, posicaoPassaroY + passaro[0].getHeight() / 2, passaro[0].getWidth() / 2);
        canoTopoRetangulo.set(posicaoMovimentoCanoHorizontal, canoTopoY, canoTopo.getWidth(), canoTopo.getHeight());
        canoBaixoRetangulo.set(posicaoMovimentoCanoHorizontal, canoBaixoY, canoBaixo.getWidth(), canoBaixo.getHeight());

        /*
        shapeRenderer.begin( ShapeRenderer.ShapeType.Filled );
        shapeRenderer.rect(canoTopoRetangulo.x, canoTopoRetangulo.y, canoTopoRetangulo.width, canoTopoRetangulo.height);
        shapeRenderer.rect(canoBaixoRetangulo.x, canoBaixoRetangulo.y, canoBaixoRetangulo.width, canoBaixoRetangulo.height);
        shapeRenderer.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.end();
        */

        //teste colisão
        if (Intersector.overlaps(passaroCirculo, canoBaixoRetangulo) || Intersector.overlaps(passaroCirculo, canoTopoRetangulo)
                || posicaoPassaroY <= 0){
            estadoJogo = 2;
        }

        if (variacaoImgPassaro > 2) variacaoImgPassaro = 0; //reset da animaçao do passaro


        if ( estadoJogo  == 0 ) {
            if (Gdx.input.justTouched()){
                estadoJogo = 1;
            }
        } else {

            velocidadeQueda++;
            if (posicaoPassaroY > 0 || velocidadeQueda < 0) { // teste da queda do pássaro
                posicaoPassaroY -= velocidadeQueda;
            }


            if ( estadoJogo == 1 ) {

                posicaoMovimentoCanoHorizontal -= deltaTime * 200; //movimento dos canos para a esquerda

                //reset dos canos
                if (posicaoMovimentoCanoHorizontal < - canoBaixo.getWidth()){
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    alturaEntreCanosRandomico = nroRandomico.nextInt(400) - 200;
                    marcouPonto = false;
                }

                if ( Gdx.input.justTouched() ){ //pulo do passaro
                    velocidadeQueda = -15;
                }

                if (posicaoMovimentoCanoHorizontal < posicaoPassaroX && !marcouPonto){
                    pontuacao++;
                    marcouPonto = true;
                }
            }else{ //GAME OVER

                if (Gdx.input.justTouched()) {
                    estadoJogo = 0;
                    velocidadeQueda = 0;
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    pontuacao = 0;
                    posicaoPassaroY = alturaDispositivo / 2;
                }

            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
