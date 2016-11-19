/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BioCompAssignment;

/**
 *
 * @author Billy
 */
public class Rule {

    public int output;
    public int[] gene;

    public int[] getGene() {
        return gene;
    }

    public void setGene(int[] gene) {
        this.gene = gene;
    }

    /**
     * Get the value of fitness
     *
     * @return the value of fitness
     */
    public int getFitness() {
        return output;
    }

    /**
     * Set the value of fitness
     *
     * @param fitness new value of fitness
     */
    public void setFitness(int fitness) {
        this.output = fitness;
    }

    public Rule(int length, int fitness) {     // new individual takes in int length
        this.gene = new int[length];    // new array of integers of length "length"
        this.output = fitness;
    }
    
    public Rule(int length){
        this.gene = new int[length];
    }

    public Rule(int[] gene) {
        this.gene = gene;
        this.output = 0;
    }

    public Rule(int[] gene, int fitness) {
        this.gene = gene;
        this.output = fitness;
    }

    public Rule clone() {
        Rule clone = new Rule(gene.length, output);
        clone.gene = gene.clone();
        return clone;
    }

}
