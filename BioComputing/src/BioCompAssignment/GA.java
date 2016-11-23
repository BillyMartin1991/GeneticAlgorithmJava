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
public class GA {

    private static final int RULE_GENOMES = 5;
    private static final int RULETRAINING_POPULATION_SIZE = 32;
    private static final int RULESET_POPULATION_SIZE = 10;
    private static final int GENOMES = 60;
    private static final int POPULATION_SIZE = 10;
    private static final int GENERATIONS = 50;
    private static final double MUTATION_RATE = 0.01;
    private static final double MUTATION_PROBABILITY = 1;
    private static final double CROSSOVER_RATE = 1;
    private static boolean NOT_FINISHED = true;
    private static final String DATA_SET_1 = "/Resource/data1.txt";
    private static final String DATA_SET_2 = "/Resource/data2.txt";
    private static final String DATA_SET_3 = "/Resource/data3.txt";

    public static void main(String[] args) throws FileNotFoundException {

        GA.runneth();
    }

    public static void runneth() throws FileNotFoundException {

        PrintWriter pw = new PrintWriter(new File("filedump/test2.csv"));
        StringBuilder sb = new StringBuilder();
        Individual[] individualPopulation = new Individual[POPULATION_SIZE];
        Individual[] offspring = new Individual[POPULATION_SIZE];
        Rule[] ruleTrainingPopulation = new Rule[RULETRAINING_POPULATION_SIZE];
        Rule[] rulePopulation = new Rule[RULESET_POPULATION_SIZE];
        int generations = 0;

        generateRulePopulation(ruleTrainingPopulation, DATA_SET_1);
        generateIndividualPopulation(individualPopulation, GENOMES);
        
        System.out.println("\nFitness");
        fitness(individualPopulation, rulePopulation, ruleTrainingPopulation);
        populationAverageFitness(individualPopulation);
        
        initialiseFile(sb);

        // Selection, Xover, Mutation
        selection(individualPopulation, offspring);
        crossover(offspring, individualPopulation, CROSSOVER_RATE);
        mutation(individualPopulation, offspring, MUTATION_RATE, MUTATION_PROBABILITY);

        // Calculate initial fitness
        fitness(offspring, rulePopulation, ruleTrainingPopulation);
        populationAverageFitness(offspring);
        keepBest(offspring, individualPopulation);
        Individual saveFittest = getFittest(individualPopulation);

//        while (NOT_FINISHED) {
        for (int i = 0; i < 500; i++) {

            generations++;

            // Selection, Xover, Mutation
            selection(individualPopulation, offspring);
            crossover(offspring, individualPopulation, CROSSOVER_RATE);
            mutation(individualPopulation, offspring, MUTATION_RATE, MUTATION_PROBABILITY);

            // Calculate Fitness
            fitness(offspring, rulePopulation, ruleTrainingPopulation);
            populationAverageFitness(offspring);
            keepBest(offspring, individualPopulation);

            // Compare to previous fitness
            individualPopulation = compareBest(saveFittest, individualPopulation);
            saveFittest = getFittest(individualPopulation).clone();
            
            // Write data to output file
            writeToFile(individualPopulation, sb, generations);

            finished(individualPopulation, ruleTrainingPopulation, generations);
        }
        pw.write(sb.toString());
        pw.close();
        System.out.println("file exported");
    }

    public static void generateRulePopulation(Rule[] rulePopulation, String File) {

        // Get Gonome Length from file
        Scanner sc = new Scanner(GA.class.getResourceAsStream(File));
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

        Scanner scan = new Scanner(GA.class.getResourceAsStream(File));
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
            for (int j = 0; j < indv.gene.length; j++) {                    // Make sure output genes aren't wildcard
                indv.gene[j] = random.nextInt(2);  // for each genome in the new individual's gene array generate a random number between 0-2
            }

            population[i] = indv;
            System.out.println("population gene " + i + "= " + Arrays.toString(population[i].gene));
        }
    }

    public static List<int[]> splitIndividual(Individual individual) {

        List<int[]> listOfIndividuals = new ArrayList<>();
        int chunkSize = RULE_GENOMES + 1;

        int totalSize = individual.gene.length;
        if (totalSize < chunkSize) {
            chunkSize = totalSize;
        }
        int from = 0;
        int to = chunkSize;

        while (from < totalSize) {
            int[] partArray = Arrays.copyOfRange(individual.gene, from, to);
            listOfIndividuals.add(partArray);

            from += chunkSize;
            to = from + chunkSize;
            if (to > totalSize) {
                to = totalSize;
            }
        }
        return listOfIndividuals;
    }

    public static Rule[] createAllRules(List<int[]> listOfArrays) {

        Rule[] ruleOffspring = new Rule[(GENOMES / (RULE_GENOMES + 1)) * POPULATION_SIZE];
        List<Integer[]> ruleList = new ArrayList<>();
        int chunkSize = RULE_GENOMES + 1;

        for (int i = 0; i < listOfArrays.size(); i++) {

            Integer[] newArray = new Integer[chunkSize];
            int x = 0;
            for (int value : listOfArrays.get(i)) {
                newArray[x++] = Integer.valueOf(value);
            }
            ruleList.add(newArray);
        }

        for (int j = 0; j < ruleList.size(); j++) {
            Rule rule = new Rule(RULE_GENOMES);
            for (int k = 0; k < ruleList.get(j).length - 1; k++) {
                rule.gene[k] = ruleList.get(j)[k];
            }
            rule.output = ruleList.get(j)[ruleList.get(j).length - 1];
            ruleOffspring[j] = rule.clone();
        }
        return ruleOffspring;
    }

    public static List<int[]> splitPopulation(Individual[] population) {

        List<int[]> listOfArrays = new ArrayList<>();
        int chunkSize = RULE_GENOMES + 1;

        for (int i = 0; i < population.length; i++) {
            int totalSize = population[i].gene.length;
            if (totalSize < chunkSize) {
                chunkSize = totalSize;
            }
            int from = 0;
            int to = chunkSize;

            while (from < totalSize) {
                int[] partArray = Arrays.copyOfRange(population[i].gene, from, to);
                listOfArrays.add(partArray);

                from += chunkSize;
                to = from + chunkSize;
                if (to > totalSize) {
                    to = totalSize;
                }
            }
        }
        return listOfArrays;

    }

    public static Rule[] createRules(List<int[]> listOfArrays) {

        Rule[] ruleOffspring = new Rule[RULESET_POPULATION_SIZE];
        List<Integer[]> ruleList = new ArrayList<>();
        int chunkSize = RULE_GENOMES + 1;

        for (int i = 0; i < listOfArrays.size(); i++) {

            Integer[] newArray = new Integer[chunkSize];
            int x = 0;
            for (int value : listOfArrays.get(i)) {
                newArray[x++] = Integer.valueOf(value);
            }
            ruleList.add(newArray);
        }

        for (int j = 0; j < ruleList.size(); j++) {
            Rule rule = new Rule(RULE_GENOMES);
            for (int k = 0; k < ruleList.get(j).length - 1; k++) {
                rule.gene[k] = ruleList.get(j)[k];
            }
            rule.output = ruleList.get(j)[ruleList.get(j).length - 1];
            ruleOffspring[j] = rule.clone();
        }
        return ruleOffspring;
    }

    // copied code http://stackoverflow.com/a/14897676/7173635
    public static boolean compareArrays(int[] array1, int[] array2) {

        boolean same = true;
        if (array1 != null && array2 != null) {
            if (array1.length != array2.length) {
                same = false;
            } else {
                for (int i = 0; i < array2.length; i++) {
                    if ((array2[i] != array1[i])) {
                        same = false;

                    }
                }
            }
        } else {
            same = false;
        }
        return same;
    }

    public static void fitness(Individual[] population, Rule[] rulePopulation, Rule[] trainingSet) {

        int match = 0;
        for (int z = 0; z < population.length; z++) {
            //create rules for each individual
            population[z].fitness = 0;
            rulePopulation = createRules(splitIndividual(population[z]));

            for (int i = 0; i < trainingSet.length; i++) {
                for (int j = 0; j < rulePopulation.length; j++) {
                    if (compareArrays(trainingSet[i].gene, rulePopulation[j].gene)) {
                        if (trainingSet[i].output == rulePopulation[j].output) {
                            match++;
                            population[z].fitness++;
                            break;
                        }
                        break;
                    }
                }
            }
        }
        System.out.println("matches " + match);
    }

    public static int totalFitness(Individual[] population, Rule[] trainingSet) {

        Rule[] allGeneratedRules;// = new Rule [(GENOMES / (RULE_GENOMES + 1)) * POPULATION_SIZE];
        allGeneratedRules = createAllRules(splitPopulation(population));
        int totalFitness = 0;

        for (int i = 0; i < trainingSet.length; i++) {
            for (int j = 0; j < allGeneratedRules.length; j++) {
                if (compareArrays(trainingSet[i].gene, allGeneratedRules[j].gene)) {
                    if (trainingSet[i].output == allGeneratedRules[j].output) {
                        totalFitness++;
                        break;
                    }
                    break;
                }
            }
        }
        System.out.println("Total Fitness " + totalFitness);
        return totalFitness;
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

    public static void populationAverageFitness(Individual[] population) {

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
        
        //error
        int error = (RULETRAINING_POPULATION_SIZE - population[fittestIndex].fitness);
        double errorPercentage = (double) error / RULETRAINING_POPULATION_SIZE * 100;
        
        System.out.println("Average Fitness: " + averageFitness);
        System.out.println("Classification error: " + errorPercentage + "%");
    }

    public static int populationTotalFitness(Individual[] population) {

        int populationTotalFitness = 0;
        for (int i = 0; i < population.length; i++) {
            populationTotalFitness = populationTotalFitness + population[i].fitness;
        }
        return populationTotalFitness;
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
    }

    public static void mutation(Individual[] population, Individual[] mutatedOffspring, double mutationRate, double mutationProbability) {

        // Iterate through gene, randomly select whether
        // or not to change the value of the genome
        //
//        System.out.println("\nMUTATION\n");
        Individual[] offspring = new Individual[POPULATION_SIZE];
        Random mutant = new Random();

        for (int i = 0; i < population.length; i++) {
            if (mutationProbability < mutant.nextDouble()) {
                offspring[i] = population[i].clone();
            } else {
                offspring[i] = population[i].mutate(mutationRate);
            }
        }
        // deep copy using .clone
        for (int i = 0; i < offspring.length; i++) {
            mutatedOffspring[i] = offspring[i].clone();
        }
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
    }
    
    public static Individual getFittest(Individual[] population) {

        Individual fitty;
        int weakestIndex = 0;
        int fittestIndex = 0;
        for (int i = 0; i < population.length; i++) {
            if (population[i].fitness >= population[fittestIndex].fitness) {
                fittestIndex = i;
            } else if (population[i].fitness <= population[weakestIndex].fitness) {
                weakestIndex = i;
            }
        }

        fitty = new Individual(population[fittestIndex].gene, population[fittestIndex].fitness);
        System.out.println("\nfittest: " + fitty.fitness);
        return fitty;
    }

    public static Individual getWorst(Individual[] population) {

        Individual weaky;
        int weakestIndex = 0;
        int fittestIndex = 0;
        for (int i = 0; i < population.length; i++) {
            if (population[i].fitness >= population[fittestIndex].fitness) {
                fittestIndex = i;
            } else if (population[i].fitness <= population[weakestIndex].fitness) {
                weakestIndex = i;
            }
        }

        weaky = new Individual(population[weakestIndex].gene, population[weakestIndex].fitness);
        return weaky;
    }
    
    public static Individual[] compareBest(Individual fitty, Individual[] population) {

        Individual[] offspring = new Individual[POPULATION_SIZE];

        int weakestIndex = 0;
        int fittestIndex = 0;
        for (int i = 0; i < population.length; i++) {
            if (population[i].fitness >= population[fittestIndex].fitness) {
                fittestIndex = i;
            } else if (population[i].fitness <= population[weakestIndex].fitness) {
                weakestIndex = i;
            }
        }

        if (population[fittestIndex].fitness < fitty.fitness) {
            population[fittestIndex] = fitty.clone();
        } else {
            population[weakestIndex] = fitty.clone();
        }

        for (int i = 0; i < population.length; i++) {
            offspring[i] = new Individual(population[i].gene, population[i].fitness);
        }
        return offspring;
    }

    public static void finished(Individual[] individualPopulation, Rule[] trainingSet, int generations) {

        for (int i = 0; i < individualPopulation.length; i++) {
            if (individualPopulation[i].fitness == RULETRAINING_POPULATION_SIZE) {
//            if (populationTotalFitness(individualPopulation) == RULETRAINING_POPULATION_SIZE) {
                NOT_FINISHED = false;
            }
        }

//        if(totalFitness(individualPopulation, trainingSet) == RULETRAINING_POPULATION_SIZE){
//           NOT_FINISHED = false; 
//        }
        if (!NOT_FINISHED) {
            System.out.println("finished.\nGeneration " + generations);
            printArray(individualPopulation);
        } else {
            System.out.println("\nGeneration " + generations);
        }
    }
    
    public static void initialiseFile(StringBuilder sb){
        
        sb.append("Best Fitness");
        sb.append(',');
        sb.append("Average Fitness");
        sb.append(',');
        sb.append("Generation");
        sb.append('\n');
        
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
        
        sb.append(population[fittestIndex].fitness);
        sb.append(',');
        sb.append(averageFitness);
        sb.append(',');
        sb.append(generations);
        sb.append('\n');

    }
}
