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
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;


/**
 *
 * @author USUARIO
 */
public class PlayerID implements IPlayer, IAuto{

    private String name = "Hakimi";
    private int _depth;
    private long numNodes;
    private long numHash;
    private long incorrectos;
    private int _poda;
    private CellType BF;
    private CellType rival;
    private boolean RTO = false;
    private long[][][] zobristTable = new long[8][8][2];
    HashMap<Long, Integer> test;
    private int[][] tablaPosibilidades = {
        { 120, -20, 20,  5,  5, 20, -20, 120},
        { -20, -40, -5, -5, -5, -5, -40, -20},
        {  20,  -5, 15,  3,  3, 15,  -5,  20},
        {   5,  -5,  3,  3,  3,  3,  -5,   5},
        {   5,  -5,  3,  3,  3,  3,  -5,   5},
        {  20,  -5, 15,  3,  3, 15,  -5,  20},
        { -20, -40, -5, -5, -5, -5, -40, -20},
        { 120, -20, 20,  5,  5, 20, -20, 120}
    };
    /*
    private int[][] tablaPosibilidades = {
        {4, -3,  2,  2,  2,  2, -3,  4},
        {-3, -4, -1, -1, -1, -1, -4, -3},
        { 2, -1,  1,  0,  0,  1, -1,  2},
        { 2, -1,  0,  1,  1,  0, -1,  2},
        { 2, -1,  0,  1,  1,  0, -1,  2},
        { 2, -1,  1,  0,  0,  1, -1,  2},
        {-3, -4, -1, -1, -1, -1, -4, -3},
        { 4, -3,  2,  2,  2,  2, -3,  4}
    };*/
      
    public PlayerID(){
        _poda = 0;
        _depth = 0;
        numNodes = 0;
        numHash = 0;
        Random random = new Random();
        for (int i = 0; i < 8; i++){//Amplada tablero
            for (int j = 0; j < 8; j++){//Altura tablero
              for (int k = 0; k < 2; k++){//Fichas 
                zobristTable[i][j][k] = random.nextLong();
              }
            }
          }
        test = new HashMap<>();
    }
    
    
    @Override
    public Move move(GameStatus gs) {
       numNodes = 0;
       numHash = 0;
       BF = gs.getCurrentPlayer();
       rival = CellType.opposite(BF);
       RTO = false;
       int prof = 1;
       Move resultatMove = null;
       while(!RTO){ // PERROR. true
           _depth = prof;
           resultatMove = minimax(gs, prof, resultatMove);
           prof++;
       }
       System.out.println("numNodes " + numNodes);
       System.out.println("numHash " + numHash);
        System.out.println("incorrectos " + incorrectos);
       return resultatMove;
    }

    @Override
    public void timeout() {
        RTO = true;
    }

    @Override
    public String getName() {
        return name;
    }
    
    //////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////|- MINIMAX -|/////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    
    public Move minimax(GameStatus t, int depth, Move lastres){
        int valor = Integer.MIN_VALUE;
        ArrayList<Point> ap = t.getMoves();
        Move RES = new Move(ap.get(0), 0, 0, SearchType.MINIMAX); //Si no ponemos .get(0), en algun caso falla y NO DEBERÍA
        for (Point p : ap) {
            GameStatus newT = new GameStatus(t);
            newT.movePiece(p);
            //Vamos al nodo MIN             //alpha             beta
            Integer newV = MIN(newT, depth-1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if(newV == null){
                --_depth;
                return lastres;
            }
            if(newV > valor){
                valor = newV;
                RES = new Move(p , numNodes, _depth, SearchType.MINIMAX);
            }   
        }
        
        //Transformación minimax a tipo Move
        return RES;    
    }

    public Integer MAX(GameStatus t, int depth, int alpha, int beta){
        if(RTO) return null;
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
            if(test.containsKey(hashvalue)){
                numHash++;
                //int h = Heuristica(t);
                //if(h != test.get(hashvalue))    incorrectos++;
                return test.get(hashvalue);
            }else{
                int h = Heuristica(t);
                test.put(hashvalue, h);
                return h;
            }
            //return Heuristica(t);
        }
        
        ArrayList<Point> ap = t.getMoves();
        for (Point p : ap) {
            GameStatus newT = new GameStatus(t);
            newT.movePiece(p);
            //Vamos al MIN
            Integer value = MIN(newT, depth-1, alpha, beta);
            if(value == null) return null;
            alpha = Math.max(value, alpha);
            //Hacemos poda alpha-beta
            if(alpha >= beta){
                _poda++;
                break;
            }
        }
        return alpha;
    }

    public Integer MIN(GameStatus t, int depth, int alpha, int beta){
        if(RTO) return null;
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
            if(test.containsKey(hashvalue)){
                numHash++;
//                int h = Heuristica(t);
//                if(h != test.get(hashvalue))    incorrectos++;
                return test.get(hashvalue);
            }else{
                int h = Heuristica(t);
                test.put(hashvalue, h);
                return h;
            }
            //return Heuristica(t);
        }
        
        ArrayList<Point> ap = t.getMoves();
        for (Point p : ap) {
            GameStatus newT = new GameStatus(t);
            newT.movePiece(p);
            //Vamos al MAX
            Integer value = MAX(newT, depth-1, alpha, beta);
            if(value == null) return null;
            beta = Math.min(value, beta);
            //Hacemos poda alpha-beta
            if(alpha >= beta){
                _poda++;
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
