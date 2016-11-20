/*
 *  BioComputation Assignment
 */
package BioCompAssignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Billy
 */
public class BioComputingMain {

    private static final int RULE_GENOMES = 5;
    private static final int RULETRAINING_POPULATION_SIZE = 32;
    private static final int RULESET_POPULATION_SIZE = 10;
    private static final int GENOMES = 60;
    private static final int POPULATION_SIZE = 10;
    private static final int GENERATIONS = 50;
    private static final double MUTATION_RATE = 0.01;
    private static final double MUTATION_PROBABILITY = 0.5;
    private static final double CROSSOVER_RATE = 1;
    private static boolean FINISHED = false;
    private static final String DATA_SET_1 = "/Resource/data1.txt";
    private static final String DATA_SET_2 = "/Resource/data2.txt";
    private static final String DATA_SET_3 = "/Resource/data3.txt";

    public static void main(String[] args) throws FileNotFoundException {

//        runOriginalGA();
        Individual[] IndividualPopulation = new Individual[POPULATION_SIZE];
        Rule[] ruleTrainingPopulation = new Rule[RULETRAINING_POPULATION_SIZE];
        Rule[] ruleOffspring = new Rule[RULESET_POPULATION_SIZE];

        generateRulePopulation(ruleTrainingPopulation, DATA_SET_1);
        generateIndividualPopulation(IndividualPopulation, GENOMES);

        // split into candidate rules
        System.out.println("\nRules");
        splitPopulation(IndividualPopulation, ruleOffspring);
        System.out.println("\nFitness");
        fitness(ruleOffspring, ruleTrainingPopulation);
        populationFitness(ruleOffspring);

    }

    public static void generateRulePopulation(Rule[] rulePopulation, String File) {

        // Get Gonome Length from file
        Scanner sc = new Scanner(BioComputingMain.class.getResourceAsStream(File));
        sc.nextLine();
        int genomes;
        String genomeString = sc.nextLine();
        String[] genomeStringBuilder = genomeString.split(" ");
        genomes = genomeStringBuilder[0].length();

        String ruleString;
        String newRule;
        String outputString;
        String[] ruleBuilder;
        String[] ruleArrayString;

        Scanner scan = new Scanner(BioComputingMain.class.getResourceAsStream(File));
        scan.nextLine();
        for (int i = 0; i < rulePopulation.length; i++) {
            Rule rule = new Rule(genomes);
            ruleString = scan.nextLine();
            ruleBuilder = ruleString.split(" ");

            newRule = ruleBuilder[0];
            outputString = ruleBuilder[1];

            ruleArrayString = newRule.split("");
            for (int j = 0; j < rule.gene.length; j++) {
                rule.gene[j] = Integer.parseInt(ruleArrayString[j]);
            }

            rule.output = Integer.parseInt(outputString);
            rulePopulation[i] = rule;
            rulePopulation[i] = rulePopulation[i].clone();
        }
        printRulePopulation(rulePopulation);
    }

    public static void generateIndividualPopulation(Individual[] population, int genomes) {

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

    public static void splitPopulation(Individual[] population, Rule[] ruleOffspring) {

        for (int i = 0; i < population.length; i++) {

            Integer[] newArray = new Integer[population[0].gene.length];
            int x = 0;
            for (int value : population[0].gene) {
                newArray[x++] = Integer.valueOf(value);
            }

            List<Integer[]> ruleList = splitArray(newArray, 6);

            for (int j = 0; j < ruleList.size(); j++) {
                Rule rule = new Rule(RULE_GENOMES);
                for (int k = 0; k < ruleList.get(j).length - 1; k++) {
                    rule.gene[k] = ruleList.get(j)[k];
                }
                rule.output = ruleList.get(j)[ruleList.get(j).length - 1];
                ruleOffspring[j] = rule.clone();
            }
        }
        printRulePopulation(ruleOffspring);
    }

    // copied code http://stackoverflow.com/a/30095524/7173635
    private static List<Integer[]> splitArray(Integer[] originalArray, int chunkSize) {
        
        List<Integer[]> listOfArrays = new ArrayList<>();
        int totalSize = originalArray.length;
        if (totalSize < chunkSize) {
            chunkSize = totalSize;
        }
        int from = 0;
        int to = chunkSize;

        while (from < totalSize) {
            Integer[] partArray = Arrays.copyOfRange(originalArray, from, to);
            listOfArrays.add(partArray);

            from += chunkSize;
            to = from + chunkSize;
            if (to > totalSize) {
                to = totalSize;
            }
        }
        return listOfArrays;
    }

    // copied code http://stackoverflow.com/a/14897676/7173635
    public static boolean compareArrays(int[] array1, int[] array2) {
        boolean same = true;
        if (array1 != null && array2 != null) {
            if (array1.length != array2.length) {
                same = false;
            } else {
                for (int i = 0; i < array2.length; i++) {
                    if (array2[i] != array1[i]) {
                        same = false;
                    }
                }
            }
        } else {
            same = false;
        }
        return same;
    }

    public static void fitness(Rule[] population, Rule[] trainingSet) {

        for (int i = 0; i < population.length; i++) {
            for (int j = 0; j < trainingSet.length; j++) {
                if (compareArrays(population[i].gene, trainingSet[j].gene)) {
                    if (population[i].output == trainingSet[j].output) {
                        population[i].fitness++;
                    }
                }
            }
        }
        printRulePopulation(population);
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
        }
    }

    public static void populationFitness(Rule[] population) {

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

    public static void printRulePopulation(Rule[] rulePopulation) {

        for (int i = 0; i < rulePopulation.length; i++) {
            System.out.println("Rule Population " + i + Arrays.toString(rulePopulation[i].gene)
                    + rulePopulation[i].output + rulePopulation[i].fitness);
        }
    }

    public static void printArray(Individual[] population) {

        for (int i = 0; i < population.length; i++) {
            System.out.println("offspring " + i + Arrays.toString(population[i].gene) + population[i].fitness);
        }
        System.out.println("\n");
    }

    public static void ruleCrossover(Rule[] population, Rule[] offspring, double crossoverRate) {

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
        //fitness(offspring);
    }

    public static void ruleMutation(Rule[] population, Rule[] mutatedOffspring, double mutationRate, double mutationProbability) {

        // Iterate through gene, randomly select whether
        // or not to change the value of the genome
        //
        Rule[] offspring = new Rule[RULESET_POPULATION_SIZE];
        Random mutant = new Random();

        for (int i = 0; i < population.length; i++) {
            if (mutationProbability > mutant.nextDouble()) {
                offspring[i] = population[i].clone();
            } else {
                offspring[i] = population[i].mutate(mutationRate);
            }

        }

//        fitness(population);
//        fitness(offspring);
        // deep copy using .clone
        for (int i = 0; i < offspring.length; i++) {
            mutatedOffspring[i] = offspring[i].clone();
        }

//        fitness(mutatedOffspring);
//        // print population
//        printArray(mutatedOffspring);
    }

    public static void keepBestRule(Rule[] population, Rule[] offspring) {

        int weakestIndex = 0;
        int fittestIndex = 0;
        for (int i = 0; i < population.length; i++) {
            if (population[i].output >= population[fittestIndex].output) {
                fittestIndex = i;
            } else if (population[i].output <= population[weakestIndex].output) {
                weakestIndex = i;
            }
        }
//        System.out.println("weakest " + Arrays.toString(population[weakestIndex].gene));
//        System.out.println("fittest " + Arrays.toString(population[fittestIndex].gene));

        // overwrite weakest individual with fittest
        population[weakestIndex] = new Rule(population[fittestIndex].gene, population[fittestIndex].output);

        // Copy offspring array back to population
        for (int i = 0; i < population.length; i++) {
            offspring[i] = new Rule(population[i].gene, population[i].output);
        }

        for (int i = 0; i < offspring.length; i++) {
            if (offspring[i].output == GENOMES) {
                FINISHED = true;
            }
        }
        if (FINISHED) {
            System.out.println("fittest " + Arrays.toString(population[fittestIndex].gene));
        }
    }

    public static void writeToFile(Individual[] population, StringBuilder sb, int generations) {

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
