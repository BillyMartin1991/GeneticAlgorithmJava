/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BioCompAssignment;

import java.util.Random;

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

    public Rule(int length) {
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

    public Rule mutate(double mutationRate) {

        Rule rule = new Rule(gene, output);
        Rule ruleCopy;
        Random mutant = new Random();

        for (int j = 0; j < rule.gene.length; j++) {
            // flip bits in array at preset probability (0.1)
            if (mutationRate > mutant.nextDouble()) {
                if (rule.gene[j] == 0) {
                    rule.gene[j] = 1;
                } else if (rule.gene[j] == 1) {
                    rule.gene[j] = 0;
                }
            }
        }
        ruleCopy = rule.clone();
        return ruleCopy;
    }
}
