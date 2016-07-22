#Random-Forest:
#   Iterations: 20,100,200,400
echo start Random-Forest

java weka.classifiers.trees.RandomForest -I 20 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/RandomForest_row3_1.txt
java weka.classifiers.trees.RandomForest -I 100 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/RandomForest_row3_2.txt
java weka.classifiers.trees.RandomForest -I 200 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/RandomForest_row3_3.txt
java weka.classifiers.trees.RandomForest -I 400 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/RandomForest_row3_4.txt

java weka.classifiers.trees.RandomForest -I 20 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/RandomForest_row4_1.txt
java weka.classifiers.trees.RandomForest -I 100 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/RandomForest_row4_2.txt
java weka.classifiers.trees.RandomForest -I 200 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/RandomForest_row4_3.txt
java weka.classifiers.trees.RandomForest -I 400 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/RandomForest_row4_4.txt

java weka.classifiers.trees.RandomForest -I 20 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/RandomForest_row5_1.txt
java weka.classifiers.trees.RandomForest -I 100 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/RandomForest_row5_2.txt
java weka.classifiers.trees.RandomForest -I 200 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/RandomForest_row5_3.txt
java weka.classifiers.trees.RandomForest -I 400 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/RandomForest_row5_4.txt

java weka.classifiers.trees.RandomForest -I 20 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/RandomForest_row6_1.txt
java weka.classifiers.trees.RandomForest -I 100 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/RandomForest_row6_2.txt
java weka.classifiers.trees.RandomForest -I 200 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/RandomForest_row6_3.txt
java weka.classifiers.trees.RandomForest -I 400 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/RandomForest_row6_4.txt

echo end Random-Forest