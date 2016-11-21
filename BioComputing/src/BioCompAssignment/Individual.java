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
public class Individual {

    public int[] gene;
    public int fitness;

    public int[] getGene() {
        return gene;
    }

    public int getFitness() {
        return fitness;
    }

    public void setGene(int[] gene) {
        this.gene = gene;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public Individual(int length) {     // new individual takes in int length
        this.gene = new int[length];    // new array of integers of length "length"
        this.fitness = 0;
    }

    public Individual(int[] gene, int fitness) {
        this.gene = gene;
        this.fitness = fitness;
    }

    public Individual(int[] gene) {
        this.gene = gene;
    }

    public Individual copy() {
        Individual individual = new Individual(gene, fitness);
        individual.gene = gene;
        return individual;
    }

    public Individual clone() {
        Individual clone = new Individual(gene.length);
        clone.gene = gene.clone();
        clone.fitness = fitness;
        return clone;
    }

    public Individual mutate(double mutationRate) {

        Individual individual = new Individual(gene, fitness);
        Individual individualCopy;
        Random mutant = new Random();

        for (int j = 0; j < individual.gene.length; j++) {
            // flip bits in array at preset probability (0.1)
            if (mutationRate > mutant.nextDouble()) {
                if (individual.gene[j] == 0) {
                    individual.gene[j] = 1;
                } else if (individual.gene[j] == 1) {
                    individual.gene[j] = 0;
                }
            }
        }
        individualCopy = individual.clone();
        return individualCopy;
    }
}
