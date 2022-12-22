/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package edu.upc.epsevg.prop.othello.players.BombardeenFrancia;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author BOMBARDEENFRANCIA
 */
public class PlayerMiniMax implements IPlayer, IAuto{

    private long[][][] zobristTable = new long[8][8][2];
    private String name = "Hakimi";
    private boolean RTO = false;
    HashMap<Long, Integer> Hash;
    private CellType BF, rival;
    private long numNodes;
    private int _depth; 
    
    /*private int[][] tablaPosibilidades = {
        { 120, -20, 20,  5,  5, 20, -20, 120},
        { -20, -40, -5, -5, -5, -5, -40, -20},
        {  20,  -5, 15,  3,  3, 15,  -5,  20},
        {   5,  -5,  3,  3,  3,  3,  -5,   5},
        {   5,  -5,  3,  3,  3,  3,  -5,   5},
        {  20,  -5, 15,  3,  3, 15,  -5,  20},
        { -20, -40, -5, -5, -5, -5, -40, -20},
        { 120, -20, 20,  5,  5, 20, -20, 120}
    };*/ 
    
    private int[][] tablaPosibilidades = {
        {4, -3,  2,  2,  2,  2, -3,  4},
        {-3, -4, -1, -1, -1, -1, -4, -3},
        { 2, -1,  1,  0,  0,  1, -1,  2},
        { 2, -1,  0,  1,  1,  0, -1,  2},
        { 2, -1,  0,  1,  1,  0, -1,  2},
        { 2, -1,  1,  0,  0,  1, -1,  2},
        {-3, -4, -1, -1, -1, -1, -4, -3},
        { 4, -3,  2,  2,  2,  2, -3,  4}
    };
    
      
    public PlayerMiniMax(int x){
        numNodes = 0;
        _depth = x;
        Random random = new Random();
        for(int i = 0; i < 8; i++){//Amplada tablero
            for(int j = 0; j < 8; j++){//Altura tablero
              for(int k = 0; k < 2; k++){//Fichas 
                zobristTable[i][j][k] = random.nextLong();
              }
            }
          }
        Hash = new HashMap<>();
    }
    
    
    @Override
    public Move move(GameStatus gs) {
       BF = gs.getCurrentPlayer();
       rival = CellType.opposite(BF);
       numNodes = 0;
       Move resultatMove = minimax(gs, _depth);
       return resultatMove;
    }

    @Override
    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-) 😎
    }

    @Override
    public String getName() {
        return name;
    }
    
    //////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////|- MINIMAX -|/////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    
    public Move minimax(GameStatus t, int depth){
        int valor = Integer.MIN_VALUE;
        ArrayList<Point> ap = t.getMoves();
        Move RES = new Move(ap.get(0), 0, 0, SearchType.MINIMAX); //Si no ponemos .get(0), en algun caso falla y NO DEBERÍA
        for (Point p : ap){
            GameStatus newT = new GameStatus(t);
            newT.movePiece(p);
            //Vamos al nodo MIN             //alpha             beta
            int newV = MIN(newT, depth-1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            //Mejor movimiento
            if(newV > valor){
                valor = newV;
                RES = new Move(p , numNodes, _depth, SearchType.MINIMAX);
            }
        }
        return RES;
    }

    public int MAX(GameStatus t, int depth, int alpha, int beta){
        //Se acaba la partida
        if(t.checkGameOver()){
            CellType win = t.GetWinner();
            if(win == BF){
                return 1000;
            }else if(win == rival){
                return -1000;
            }
        }
        //Caso base
        if (depth == 0 || t.getEmptyCellsCount() == 0){
            numNodes = numNodes + 1;
            long hashvalue = getZobristHash(t);
            if(Hash.containsKey(hashvalue)){    //Tablero repetido, return Hash
                return Hash.get(hashvalue);
            }else{                              //Nuevo tablero, return heuristica
                int h = Heuristica(t);
                Hash.put(hashvalue, h);
                return h;
            }
        }
        
        ArrayList<Point> ap = t.getMoves();
        for (Point p : ap) {
            GameStatus newT = new GameStatus(t);
            newT.movePiece(p);
            //Vamos al MIN
            int value = MIN(newT, depth-1, alpha, beta);
            alpha = Math.max(value, alpha);
            //Hacemos poda alpha-beta
            if(alpha >= beta){
                break;
            }
        }
        return alpha;
    }


    public int MIN(GameStatus t, int depth, int alpha, int beta){
        //Se acaba la partida
        if(t.checkGameOver()){
            CellType win = t.GetWinner();
            if(win == BF){
                return 1000;
            }else if(win == rival){
                return -1000;
            }
        }
        //Caso base
        if (depth == 0 || t.getEmptyCellsCount() == 0){
            numNodes = numNodes + 1;
            long hashvalue = getZobristHash(t);
            if(Hash.containsKey(hashvalue)){
                return Hash.get(hashvalue);
            }else{
                int h = Heuristica(t);
                Hash.put(hashvalue, h);
                return h;
            }
        }
        //Lista de movimientos
        ArrayList<Point> ap = t.getMoves();
        for (Point p : ap) {
            GameStatus newT = new GameStatus(t);
            newT.movePiece(p);
            //Vamos al MAX
            int value = MAX(newT, depth-1, alpha, beta);
            beta = Math.min(value, beta);
            //Hacemos poda alpha-beta
            if(alpha >= beta){
                break;
            }
        }
        return beta;
    }
    
    public int Heuristica(GameStatus t){
        int valorHeur = 0;
        for (int i = 0; i < t.getSize(); i++) {
            for (int j = 0; j < t.getSize(); j++) {
                if (t.getPos(i, j) == BF) {
                    valorHeur += tablaPosibilidades[i][j];
                } else if (t.getPos(i, j) == rival) {
                    valorHeur -= tablaPosibilidades[i][j];
                }
            }
        }
        return valorHeur;
    }
    
    public long getZobristHash(GameStatus t) {
        long hash = 0;
        for(int i = 0; i < t.getSize(); i++){
            for(int j = 0; j < t.getSize(); j++){
                CellType piece = t.getPos(i, j);
                int k;
                if(piece == CellType.PLAYER1){
                    k = 0;
                }else if(piece == CellType.PLAYER2){
                    k = 1;
                }else{
                    k = -1;
                }
                if(k != -1) hash ^= zobristTable[i][j][k];
            }
        }
        return hash;
    }
}
