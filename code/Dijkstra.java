/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code;

import java.util.HashMap;
import java.util.PriorityQueue;

/**
 *
 * @author korisnik
 */
public class Dijkstra {
    private HashMap<Integer, HashMap<Integer, Integer>> branches;
    private HashMap<Integer, Integer> optimal;
    private HashMap<Integer, Integer> prev;
    private static Dijkstra instance = null;
    private int source;
    
    
    public static class Pair{
        public int node, distance;
        
        public Pair(int node, int distance){
            this.distance = distance;
            this.node = node;
        }

        @Override
        public String toString() {
            return String.valueOf(node) + ", " + String.valueOf(distance);
        }
        
        
        
    }
    
    
    
    private Dijkstra(){
        branches = new HashMap<>();
        optimal = new HashMap<>();
        prev = new HashMap<>();
        source = -1;
    }
    
    public static Dijkstra getInstance(){
        if(instance == null)
            instance = new Dijkstra();
        return instance;
    }
    
    public void empty(){
        branches.clear();
        optimal.clear();
        prev.clear();
    }
    
    public void addBranch(int node1, int node2, int distance){
        if(! branches.containsKey(node1))
            branches.put(node1, new HashMap<>());
        if(! branches.containsKey(node2))
            branches.put(node2, new HashMap<>());
        branches.get(node1).put(node2, distance);
        branches.get(node2).put(node1, distance);
    }
    
    public int getOptimalDistance(int destNode){
        return optimal.get(destNode);
    }
    
    
    public HashMap<Integer, Integer> getOptimal(){
        return optimal;
    }
    
    public String getOptimalPath(int node){
        if(optimal.get(node) == Integer.MAX_VALUE)
            return null;
        
        StringBuilder sb = new StringBuilder();
        
        int cur = node;
        
        while(cur != source){
            sb.append(String.valueOf(cur)).append(',');
            cur = prev.get(cur);
        }
        sb.append(String.valueOf(cur));
        
        return sb.toString();
    }
    
    public void compute(int source){
        this.source = source;
        optimal.clear();
        prev.clear();
        
        for(int node : branches.keySet()){
            prev.put(node, -1);
            optimal.put(node, Integer.MAX_VALUE);
        }
        optimal.put(source, 0);
        PriorityQueue<Pair> pq = new PriorityQueue<Pair>((a, b)-> a.distance - b.distance);
        pq.add(new Pair(source, 0));
        
        while(!pq.isEmpty()){
            Pair cur = pq.poll();
            if (cur.distance > optimal.get(cur.node))
                    continue;
            for(int next : branches.get(cur.node).keySet()){
                if(cur.distance + branches.get(cur.node).get(next) < optimal.get(next)){
                    optimal.put(next, cur.distance + branches.get(cur.node).get(next));
                    pq.add(new Pair(next, cur.distance + branches.get(cur.node).get(next)));
                    prev.put(next, cur.node);
                }
            }
        }
        
        
    }
    
    
    public static void main(String[] args) {
        Dijkstra d = Dijkstra.getInstance();
        d.addBranch(1, 6, 20);
        d.addBranch(1, 7, 100);
        d.addBranch(1, 5, 30);
        d.addBranch(1, 2, 10);
        d.addBranch(2, 4, 90);
        d.addBranch(2, 3, 5);
        d.addBranch(3, 4, 5);
        d.compute(1);
        
        HashMap<Integer, Integer> mapa = d.getOptimal();
        
        for(int k : mapa.keySet())
            System.out.println(String.valueOf(k) + ": "+mapa.get(k));
        System.out.println(d.getOptimalPath(1));
        System.out.println(d.getOptimalPath(4));
    }
}
