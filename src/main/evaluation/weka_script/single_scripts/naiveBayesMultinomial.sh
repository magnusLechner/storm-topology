#NaiveBayesMultinomial
echo start NaiveBayesMultinomial

java weka.classifiers.bayes.NaiveBayesMultinomial -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/NaiveBayesMultinomial_row3_1.txt
java weka.classifiers.bayes.NaiveBayesMultinomial -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/NaiveBayesMultinomial_row4_1.txt
java weka.classifiers.bayes.NaiveBayesMultinomial -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/NaiveBayesMultinomial_row5_1.txt
java weka.classifiers.bayes.NaiveBayesMultinomial -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/NaiveBayesMultinomial_row6_1.txt

echo end NaiveBayesMultinomial