/*
 *  BioComputation Assignment
 */
package BioCompAssignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Billy
 */
public class BioComputingMain {

    private static final int GENOMES = 50;
    private static final int POPULATION_SIZE = 50;
    private static final int GENERATIONS = 50;
    private static final double MUTATION_RATE = 0.01;
    private static final double MUTATION_PROBABILITY = 0.5;
    private static final double CROSSOVER_RATE = 1;
    private static boolean FINISHED = false;

    public static void main(String[] args) throws FileNotFoundException {

        PrintWriter pw = new PrintWriter(new File("filedump/test.csv"));
        StringBuilder sb = new StringBuilder();

        Individual[] population = new Individual[POPULATION_SIZE];  // new Array of Individuals
        Individual[] offspring = new Individual[POPULATION_SIZE];

        generatePopulation(population, GENOMES);
        fitness(population);
        populationFitness(population);

        sb.append("Gene");
        sb.append(',');
        sb.append("Fitness");
        sb.append(',');
        sb.append("Average Fitness");
        sb.append(',');
        sb.append("Generation");
        sb.append('\n');

        for (int i = 0; i < GENERATIONS; i++) {

            selection(population, offspring);
            populationFitness(offspring);
            crossover(offspring, population, CROSSOVER_RATE);
            mutation(population, offspring, MUTATION_RATE, MUTATION_PROBABILITY);
            populationFitness(offspring);
            System.out.println("gen " + i);
            keepBest(offspring, population);

            writeToFile(population, sb, i);

            if (FINISHED) {
                break;
            }
        }

        pw.write(sb.toString());
        pw.close();
        System.out.println("done!");

    }

    public static void generatePopulation(Individual[] population, int genomes) {

        Random random = new Random();
        for (int i = 0; i < population.length; i++) {
            Individual indv = new Individual(genomes);   // generate new individual with length 10 (gene size) for each iteration, which is of population size
            for (int j = 0; j < indv.gene.length; j++) {
                indv.gene[j] = random.nextInt(2);   // for each genome in the new individual's gene array generate a random number between 0-1
            }
            population[i] = indv;
            System.out.println("population gene " + i + "= " + Arrays.toString(population[i].gene));
        }
    }

    public static void fitness(Individual[] indy) {

        for (int i = 0; i < indy.length; i++) {// generate new individual with length 10 (gene size) for each iteration, which is of population size
            indy[i].fitness = 0;
        }
        for (int i = 0; i < indy.length; i++) {
            for (int j = 0; j < indy[i].gene.length; j++) {
                if ((indy[i].gene[j]) == 1) {
                    indy[i].fitness++;
                }
            }
        }
    }

    public static void selection(Individual[] population, Individual[] offspring) {

        Random rand = new Random();
        int parent1;
        int parent2;

        for (int i = 0; i < population.length; i++) {
            parent1 = rand.nextInt((population.length) - 1);
            parent2 = rand.nextInt((population.length) - 1);

            if (population[parent1].fitness >= population[parent2].fitness) {
                offspring[i] = new Individual(population[parent1].gene, population[parent1].fitness);
            } else {
                offspring[i] = new Individual(population[parent2].gene, population[parent2].fitness);;
            }

            // fitness
            fitness(population);
        }
        fitness(offspring);
    }

    public static void populationFitness(Individual[] population) {

        // average fitness
        int j;
        int k = 0;
        for (int i = 0; i < population.length; i++) {
            j = population[i].fitness;
            k = k + j;
        }
        double averageFitness = (double) k / population.length;

        // Fittest in population
        int fittestIndex = 0;
        for (int i = 0; i < population.length; i++) {
            if (population[i].fitness >= population[fittestIndex].fitness) {
                fittestIndex = i;
            }
        }
        System.out.println("\nfittest =" + (population[fittestIndex].fitness));
        System.out.println("population avg. fitness = " + averageFitness);
    }

    public static void printArray(Individual[] population) {

        for (int i = 0; i < population.length; i++) {
            System.out.println("offspring " + i + Arrays.toString(population[i].gene) + population[i].fitness);
        }
        System.out.println("\n");
    }

    public static void crossover(Individual[] population, Individual[] offspring, double crossoverRate) {

        Random r = new Random();
        int crossover = r.nextInt(population.length + 1);
        int halfGene = (population.length / 2);

        for (int i = 0; i < halfGene; i++) {
            if (crossoverRate > r.nextDouble()) {
                int ind1 = i * 2;
                int ind2 = (i * 2) + 1;

                for (int l = crossover; l < population[i].gene.length; l++) {

                    int tempBit = population[ind1].gene[l];
                    population[ind1].gene[l] = population[ind2].gene[l];
                    population[ind2].gene[l] = tempBit;
                }
            }
        }

        // copy population to offspring
        for (int i = 0; i < population.length; i++) {
            offspring[i] = population[i].clone();
        }
        fitness(offspring);
    }

    public static void mutation(Individual[] population, Individual[] mutatedOffspring, double mutationRate, double mutationProbability) {

        // Iterate through gene, randomly select whether
        // or not to change the value of the genome
        //
//        System.out.println("\nMUTATION\n");
        Individual[] offspring = new Individual[POPULATION_SIZE];
        Random mutant = new Random();

        for (int i = 0; i < population.length; i++) {
            if (mutationProbability > mutant.nextDouble()) {
                offspring[i] = population[i].clone();
            } else {
                offspring[i] = population[i].mutate(mutationRate);
            }

        }

        fitness(population);
        fitness(offspring);

        // deep copy using .clone
        for (int i = 0; i < offspring.length; i++) {
            mutatedOffspring[i] = offspring[i].clone();
        }

        fitness(mutatedOffspring);
//        System.out.println("mutated offspring");

//        // print population
//        printArray(mutatedOffspring);
    }

    public static void keepBest(Individual[] population, Individual[] offspring) {

        int weakestIndex = 0;
        int fittestIndex = 0;
        for (int i = 0; i < population.length; i++) {
            if (population[i].fitness >= population[fittestIndex].fitness) {
                fittestIndex = i;
            } else if (population[i].fitness <= population[weakestIndex].fitness) {
                weakestIndex = i;
            }
        }
//        System.out.println("weakest " + Arrays.toString(population[weakestIndex].gene));
//        System.out.println("fittest " + Arrays.toString(population[fittestIndex].gene));

        // overwrite weakest individual with fittest
        population[weakestIndex] = new Individual(population[fittestIndex].gene, population[fittestIndex].fitness);

        // Copy offspring array back to population
        for (int i = 0; i < population.length; i++) {
            offspring[i] = new Individual(population[i].gene, population[i].fitness);
        }

        for (int i = 0; i < offspring.length; i++) {
            if (offspring[i].fitness == GENOMES) {
                FINISHED = true;
            }
        }
        if (FINISHED) {
            System.out.println("fittest " + Arrays.toString(population[fittestIndex].gene));
        }
    }

    public static void writeToFile(Individual [] population, StringBuilder sb, int generations) {
        
        int fittestIndex = 0;
            for (int j = 0; j < population.length; j++) {
                if (population[j].fitness >= population[fittestIndex].fitness) {
                    fittestIndex = j;
                }
            }
            int j;
            int k = 0;
            for (int l = 0; l < population.length; l++) {
                j = population[l].fitness;
                k = k + j;
            }
            double averageFitness = (double) k / population.length;

            for (int l = 0; l < population[fittestIndex].gene.length; l++) {
                sb.append(population[fittestIndex].gene[l]);
            }
            sb.append(',');
            sb.append(population[fittestIndex].fitness);
            sb.append(',');
            sb.append(averageFitness);
            sb.append(',');
            sb.append(generations);
            sb.append('\n');
            
    }
}
